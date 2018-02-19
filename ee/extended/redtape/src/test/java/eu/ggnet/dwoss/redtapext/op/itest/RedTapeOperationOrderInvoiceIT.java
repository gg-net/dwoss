package eu.ggnet.dwoss.redtapext.op.itest;

import java.text.SimpleDateFormat;
import java.util.*;

import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.time.DateUtils;
import org.bouncycastle.util.Strings;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.customer.ee.assist.gen.CustomerGeneratorOperation;
import eu.ggnet.dwoss.receipt.ee.gen.ReceiptGeneratorOperation;
import eu.ggnet.dwoss.redtape.ee.RedTapeAgent;
import eu.ggnet.dwoss.redtape.ee.entity.*;
import eu.ggnet.dwoss.redtapext.ee.RedTapeWorker;
import eu.ggnet.dwoss.redtapext.ee.UnitOverseer;
import eu.ggnet.dwoss.redtapext.ee.sage.SageExporter;
import eu.ggnet.dwoss.redtapext.op.itest.support.*;
import eu.ggnet.dwoss.rules.*;
import eu.ggnet.dwoss.stock.ee.StockAgent;
import eu.ggnet.dwoss.stock.ee.eao.LogicTransactionEao;
import eu.ggnet.dwoss.stock.ee.entity.LogicTransaction;
import eu.ggnet.dwoss.stock.ee.entity.StockUnit;
import eu.ggnet.dwoss.uniqueunit.ee.entity.Product;
import eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnit;
import eu.ggnet.dwoss.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

/**
 *
 * @author bastian.venz
 */
@RunWith(Arquillian.class)
public class RedTapeOperationOrderInvoiceIT extends ArquillianProjectArchive {

    @EJB
    private UnitOverseer unitOverseer;

    @EJB
    private RedTapeWorker redTapeWorker;

    @EJB
    private SageExporter sageExporter;

    @Inject
    private CustomerGeneratorOperation customerGenerator;

    @Inject
    private LogicTransactionEao logicTransactionEao;

    @EJB
    private RedTapeAgent redTapeAgent;

    @EJB
    private StockAgent stockAgent;

    @Inject
    private SupportBean manipulator;

    @Inject
    private ReceiptGeneratorOperation receiptGenerator;

    @Inject
    private DatabaseCleaner cleaner;

    @After
    public void clearDatabase() throws Exception {
        cleaner.clear();
    }

    /**
     * Test of create method, of class RedTapeWorkerOperation.
     * <p>
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testCreateDossier() throws InterruptedException {

        long customerId = customerGenerator.makeCustomer();
        Dossier dos1 = redTapeWorker.create(customerId, true, "Me");

        // Dossier value tests
        assertThat(dos1.getCustomerId()).as("dossier.customerId").isEqualTo(customerId);
        assertTrue(dos1.isDispatch());
        assertTrue(dos1.getId() > 0);

        // Document value tests
        assertNotNull(dos1.getDocuments());
        assertTrue(!dos1.getDocuments().isEmpty());
        assertTrue(dos1.getDocuments().iterator().next().isActive());
        assertTrue(dos1.getActiveDocuments(DocumentType.ORDER).get(0).getId() > 0);
        List<Address> all = redTapeAgent.findAll(Address.class);
        assertThat(all)
                .overridingErrorMessage("Unexspected Addesses in Database:" + all)
                .isNotEmpty();

        assertThat(all.size())
                .overridingErrorMessage("Should be one or two Addresses, but size()=" + all.size() + ", elements=" + all)
                .isLessThan(3);
    }

    @Test
    public void testUpdateDocument() throws UserInfoException {
        long customerId = customerGenerator.makeCustomer();
        List<UniqueUnit> uus = receiptGenerator.makeUniqueUnits(4, true, true);
        UniqueUnit uu1 = uus.get(0);
        UniqueUnit uu2 = uus.get(1);
        UniqueUnit uu3 = uus.get(2);
        UniqueUnit uu4 = uus.get(3);

        Product uuProduct1 = uu1.getProduct();

        customerGenerator.scrambleAddress(customerId, AddressType.SHIPPING);

        //Generate Dossier
        Dossier dos1 = redTapeWorker.create(customerId, false, "Me");
        Document doc1 = dos1.getActiveDocuments(DocumentType.ORDER).get(0);
        assertThat(doc1).overridingErrorMessage("Expected active document Order, got null. Dossier: " + dos1.toMultiLine()).isNotNull();

        //Commit explicit date to document for assertion
        doc1 = manipulator.changeActual(doc1, new GregorianCalendar(2012, 3, 15).getTime());

        //Create Positions
        Position p1 = NaivBuilderUtil.unit(uu1);
        Position p2 = NaivBuilderUtil.unit(uu2);
        Position p3 = NaivBuilderUtil.unit(uu3);
        Position p4 = NaivBuilderUtil.comment();
        Position p5 = NaivBuilderUtil.service();
        Position p6 = NaivBuilderUtil.batch(uuProduct1);
        Position p7 = NaivBuilderUtil.shippingcost();

        doc1.append(p1);
        doc1.append(p2);
        doc1.append(p3);
        doc1.append(p4);
        doc1.append(p5);
        doc1.append(p6);
        doc1.append(p7);

        //add units to LogicTransaction
        unitOverseer.lockStockUnit(dos1.getId(), uu1.getIdentifier(UniqueUnit.Identifier.REFURBISHED_ID));
        unitOverseer.lockStockUnit(dos1.getId(), uu2.getIdentifier(UniqueUnit.Identifier.REFURBISHED_ID));
        unitOverseer.lockStockUnit(dos1.getId(), uu3.getIdentifier(UniqueUnit.Identifier.REFURBISHED_ID));
        unitOverseer.lockStockUnit(dos1.getId(), uu4.getIdentifier(UniqueUnit.Identifier.REFURBISHED_ID));

        //check if LogicTransaction is created correctly
        assertEquals("Amount of LogicTransactions", 1, logicTransactionEao.findAll().size());
        assertEquals("The LogicTransaction should have only 4 units", 4, logicTransactionEao.findAll().get(0).getUnits().size());

        //update document
        Date date = doc1.getActual();
        doc1 = redTapeWorker.update(doc1, null, "Me");
        assertEquals("Same actual dates expected", date, doc1.getActual());

        assertEquals("Only one Active Document expected", 1, doc1.getDossier().getActiveDocuments().size());
        assertTrue(logicTransactionEao.findAll().get(0).getUnits().size() == 3);

        assertEquals("Ammount of Documents", 2, redTapeAgent.findAll(Document.class).size());

        dos1 = redTapeAgent.findByIdEager(Dossier.class, dos1.getId());
        assertEquals("Ammount of Posistions", 7, doc1.getPositions().size());

        //address setting test
        assertFalse(doc1.getInvoiceAddress().equals(doc1.getShippingAddress()));

        //Test update with document changes
        Position p8 = Position.builder().amount(1).type(PositionType.SERVICE).price(50.).tax(1.).name("Position 8").description("Position from update2").build();
        doc1.append(p8);
        doc1.removeAt(6);

        Document doc2 = redTapeWorker.update(doc1, null, "Me");

        assertEquals("Only one Active Document expected", 1, doc2.getDossier().getActiveDocuments().size());

        assertEquals("Ammount of Documents", 3, redTapeAgent.findAll(Document.class).size());
        dos1 = redTapeAgent.findByIdEager(Dossier.class, dos1.getId());
        assertEquals("Id of Predecessor Document", doc1.getId(), dos1.getActiveDocuments(DocumentType.ORDER).get(0).getPredecessor().getId());
        assertEquals("Ammount of Posistions", 7, dos1.getActiveDocuments(DocumentType.ORDER).get(0).getPositions().size());

        //Test date changes when updating document type
        doc2.setType(DocumentType.INVOICE);
        Document doc3 = redTapeWorker.update(doc2, null, "JUnit");
        assertFalse(doc3.getActual().equals(doc2.getActual()));
    }

    @Test
    public void testRevertCreate() throws UserInfoException {
        long customerId = customerGenerator.makeCustomer();
        List<UniqueUnit> uus = receiptGenerator.makeUniqueUnits(4, true, true);
        UniqueUnit uu1 = uus.get(0);
        UniqueUnit uu2 = uus.get(1);
        UniqueUnit uu3 = uus.get(2);
        UniqueUnit uu4 = uus.get(3);

        Product uuProduct1 = uu1.getProduct();

        //Generate Dossier
        Dossier dos1 = redTapeWorker.create(customerId, true, "Me");
        Document doc1 = dos1.getActiveDocuments(DocumentType.ORDER).get(0);
        assertThat(doc1).overridingErrorMessage("Expected active document Order, got null. Dossier: " + dos1.toMultiLine()).isNotNull();

        //Create Positions
        Position p1 = NaivBuilderUtil.unit(uu1);
        Position p2 = NaivBuilderUtil.unit(uu2);
        Position p4 = NaivBuilderUtil.comment();
        Position p5 = NaivBuilderUtil.service();
        Position p6 = NaivBuilderUtil.batch(uuProduct1);

        doc1.append(p1);
        doc1.append(p2);
        doc1.append(p4);
        doc1.append(p5);
        doc1.append(p6);

        //add units to LogicTransaction
        unitOverseer.lockStockUnit(dos1.getId(), uu1.getIdentifier(UniqueUnit.Identifier.REFURBISHED_ID));
        unitOverseer.lockStockUnit(dos1.getId(), uu2.getIdentifier(UniqueUnit.Identifier.REFURBISHED_ID));

        //update document
        doc1 = redTapeWorker.update(doc1, null, "Me");

        //add units to LogicTransaction without update
        unitOverseer.lockStockUnit(dos1.getId(), uu3.getIdentifier(UniqueUnit.Identifier.REFURBISHED_ID));
        unitOverseer.lockStockUnit(dos1.getId(), uu4.getIdentifier(UniqueUnit.Identifier.REFURBISHED_ID));
        assertEquals("LogicTransaction size", 4, logicTransactionEao.findByDossierId(dos1.getId()).getUnits().size());

        //test revertCreate operation
        redTapeWorker.revertCreate(doc1);

        assertEquals("LogicTransaction cleanup", 2, logicTransactionEao.findByDossierId(dos1.getId()).getUnits().size());
        assertNull(stockAgent.findStockUnitByUniqueUnitIdEager(uu3.getId()).getLogicTransaction());
        assertNull(stockAgent.findStockUnitByUniqueUnitIdEager(uu3.getId()).getLogicTransaction());

        long customerId2 = customerGenerator.makeCustomer();

        Dossier dos2 = redTapeWorker.create(customerId2, true, "Me");
        assertEquals("Dossiers in database", 2, redTapeAgent.findAll(Dossier.class).size());

        redTapeWorker.revertCreate(dos2.getActiveDocuments(DocumentType.ORDER).get(0));
        assertEquals("Dossiers in database", 1, redTapeAgent.findAll(Dossier.class).size());
    }

    @Test
    public void testInvoiceDocument() throws UserInfoException {
        long customerId = customerGenerator.makeCustomer();
        List<UniqueUnit> uus = receiptGenerator.makeUniqueUnits(4, true, true);
        UniqueUnit uu1 = uus.get(0);
        UniqueUnit uu2 = uus.get(1);
        UniqueUnit uu3 = uus.get(2);
        UniqueUnit uu4 = uus.get(3);

        Product uuProduct1 = uu1.getProduct();

        //Generate Dossier
        Dossier dos = redTapeWorker.create(customerId, true, "Me");
        Document doc1 = dos.getActiveDocuments(DocumentType.ORDER).get(0);
        assertThat(doc1).overridingErrorMessage("Expected active document Order, got null. Dossier: " + dos.toMultiLine()).isNotNull();

        doc1.add(Document.Flag.CUSTOMER_BRIEFED);
        doc1.add(Document.Flag.CUSTOMER_EXACTLY_BRIEFED);

        //Create Positions
        Position p1 = NaivBuilderUtil.unit(uu1);
        Position p2 = NaivBuilderUtil.unit(uu2);
        Position p4 = NaivBuilderUtil.comment();
        Position p5 = NaivBuilderUtil.service();
        Position p6 = NaivBuilderUtil.batch(uuProduct1);
        Position p7 = NaivBuilderUtil.shippingcost();

        doc1.append(p1);
        doc1.append(p2);
        doc1.append(p4);
        doc1.append(p5);
        doc1.append(p6);
        doc1.append(p7);

        Position p3 = NaivBuilderUtil.unit(uu3);
        Position p8 = NaivBuilderUtil.unit(uu4);

        Dossier dos2 = redTapeWorker.create(customerId, false, "Me2");
        Document doc2 = dos2.getActiveDocuments(DocumentType.ORDER).get(0);
        assertThat(doc2).overridingErrorMessage("Expected active document Order, got null. Dossier: " + dos2.toMultiLine()).isNotNull();

        doc2.append(p3);
        doc2.append(p8);

        //add units to LogicTransaction
        unitOverseer.lockStockUnit(dos.getId(), uu1.getIdentifier(UniqueUnit.Identifier.REFURBISHED_ID));
        unitOverseer.lockStockUnit(dos.getId(), uu2.getIdentifier(UniqueUnit.Identifier.REFURBISHED_ID));
        unitOverseer.lockStockUnit(dos.getId(), uu3.getIdentifier(UniqueUnit.Identifier.REFURBISHED_ID));
        unitOverseer.lockStockUnit(dos.getId(), uu4.getIdentifier(UniqueUnit.Identifier.REFURBISHED_ID));

        doc1 = redTapeWorker.update(doc1, null, "Junit Test");

        doc1.add(Document.Condition.PAID);
        doc1.add(Document.Condition.PICKED_UP);
        doc1.setType(DocumentType.INVOICE);
        doc1 = redTapeWorker.update(doc1, null, "tester");
        String format = new SimpleDateFormat("YY").format(new Date());

        assertEquals("The Identifier of Invoice", "RS" + format + "_00001", doc1.getIdentifier());
        assertFalse("Document must not contain " + Document.Flag.CUSTOMER_BRIEFED, doc1.getFlags().contains(Document.Flag.CUSTOMER_BRIEFED));
        assertFalse("Document must not contain " + Document.Flag.CUSTOMER_EXACTLY_BRIEFED, doc1.getFlags().contains(Document.Flag.CUSTOMER_EXACTLY_BRIEFED));

        LogicTransaction lt = logicTransactionEao.findByDossierId(doc1.getDossier().getId());
        assertNotNull("A LogicTrasaction must exists", lt);
        assertEquals("The Size of the LogicTransaction", 2, lt.getUnits().size());

        doc2.setType(DocumentType.INVOICE);
        Document update2 = redTapeWorker.update(doc2, null, "Junit");
        assertEquals("RS" + format + "_00002", update2.getIdentifier());

        Date now = new Date();
        Date start = DateUtils.addDays(now, -1);
        Date end = DateUtils.addDays(now, 1);

        FileJacket fj = sageExporter.toXml(start, end);
        String result = Strings.fromByteArray(fj.getContent());
        assertThat(result).as("SageXml spot Test")
                .isNotBlank()
                .contains(dos.getIdentifier(), dos2.getIdentifier())
                .contains(Double.toString(TwoDigits.round(p7.getPrice() * p7.getAmount())).replace(".", ","))
                .contains(Double.toString(TwoDigits.round(p6.getPrice() * p6.getAmount())).replace(".", ","));
    }

    @Test
    public void testCancelDocument() throws UserInfoException {
        long customerId = customerGenerator.makeCustomer();
        List<UniqueUnit> uus = receiptGenerator.makeUniqueUnits(4, true, true);
        UniqueUnit uu1 = uus.get(0);
        UniqueUnit uu2 = uus.get(1);
        UniqueUnit uu3 = uus.get(2);
        UniqueUnit uu4 = uus.get(3);

        Product uuProduct1 = uu1.getProduct();

        //Generate Dossier
        Dossier dos = redTapeWorker.create(customerId, true, "Me");
        Document doc1 = dos.getActiveDocuments(DocumentType.ORDER).get(0);
        assertThat(doc1).overridingErrorMessage("Expected active document Order, got null. Dossier: " + dos.toMultiLine()).isNotNull();

        //Create Positions
        Position p1 = NaivBuilderUtil.unit(uu1);
        Position p2 = NaivBuilderUtil.unit(uu2);
        Position p4 = NaivBuilderUtil.comment();
        Position p5 = NaivBuilderUtil.service();
        Position p6 = NaivBuilderUtil.batch(uuProduct1);
        Position p7 = NaivBuilderUtil.shippingcost();
        doc1.append(p1);
        doc1.append(p2);
        doc1.append(p4);
        doc1.append(p5);
        doc1.append(p6);
        doc1.append(p7);

        Position p3 = NaivBuilderUtil.unit(uu3);
        Position p8 = NaivBuilderUtil.unit(uu4);

        Dossier dos2 = redTapeWorker.create(customerId, false, "Me2");

        Document doc2 = dos2.getActiveDocuments(DocumentType.ORDER).get(0);
        assertThat(doc2).overridingErrorMessage("Expected active document Order, got null. Dossier: " + dos2.toMultiLine()).isNotNull();


        doc2.add(Document.Condition.CONFIRMED);
        doc2.add(Document.Condition.PAID);
        doc2.append(p3);
        doc2.append(p8);

        //add units to LogicTransaction
        unitOverseer.lockStockUnit(dos.getId(), uu1.getIdentifier(UniqueUnit.Identifier.REFURBISHED_ID));
        unitOverseer.lockStockUnit(dos.getId(), uu2.getIdentifier(UniqueUnit.Identifier.REFURBISHED_ID));
        unitOverseer.lockStockUnit(dos.getId(), uu3.getIdentifier(UniqueUnit.Identifier.REFURBISHED_ID));
        unitOverseer.lockStockUnit(dos.getId(), uu4.getIdentifier(UniqueUnit.Identifier.REFURBISHED_ID));

        doc1.add(Document.Condition.CONFIRMED);
        doc1.setType(DocumentType.ORDER);
        doc1 = redTapeWorker.update(doc1, null, "Junit Test");

        doc1.add(Document.Condition.CANCELED);
        doc1 = redTapeWorker.update(doc1, null, "tester");
        assertTrue("Document doesent contain the Condition Canceled", doc1.getConditions().contains(Document.Condition.CANCELED));

        for (Position position : doc1.getPositions(PositionType.UNIT).values()) {
            StockUnit su = stockAgent.findStockUnitByUniqueUnitIdEager(position.getUniqueUnitId());
            assertNull(su + " should not have LogicTransaction", su.getLogicTransaction());
        }
    }

}
