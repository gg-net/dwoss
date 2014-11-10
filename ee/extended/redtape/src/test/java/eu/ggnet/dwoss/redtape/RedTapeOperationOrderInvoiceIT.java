package eu.ggnet.dwoss.redtape;

import eu.ggnet.dwoss.rules.DocumentType;
import eu.ggnet.dwoss.rules.PaymentMethod;
import eu.ggnet.dwoss.rules.PositionType;
import eu.ggnet.dwoss.rules.AddressType;
import eu.ggnet.dwoss.customer.assist.CustomerPu;
import eu.ggnet.dwoss.redtape.entity.PositionBuilder;
import eu.ggnet.dwoss.redtape.entity.Position;
import eu.ggnet.dwoss.redtape.entity.Address;
import eu.ggnet.dwoss.redtape.assist.RedTapePu;
import eu.ggnet.dwoss.redtape.assist.RedTapes;
import eu.ggnet.dwoss.redtape.entity.Dossier;
import eu.ggnet.dwoss.redtape.entity.Document;
import eu.ggnet.dwoss.stock.entity.LogicTransaction;
import eu.ggnet.dwoss.stock.StockAgent;
import eu.ggnet.dwoss.stock.assist.Stocks;
import eu.ggnet.dwoss.stock.entity.StockUnit;
import eu.ggnet.dwoss.stock.assist.StockPu;
import eu.ggnet.dwoss.uniqueunit.entity.UniqueUnit;
import eu.ggnet.dwoss.uniqueunit.entity.Product;

import java.text.SimpleDateFormat;
import java.util.*;

import javax.ejb.*;
import javax.ejb.embeddable.EJBContainer;
import javax.inject.Inject;
import javax.naming.NamingException;
import javax.persistence.EntityManager;

import org.junit.*;

import eu.ggnet.dwoss.configuration.SystemConfig;
import eu.ggnet.dwoss.customer.assist.gen.CustomerGeneratorOperation;

import eu.ggnet.dwoss.receipt.gen.ReceiptGeneratorOperation;

import eu.ggnet.dwoss.redtape.RedTapeAgent;
import eu.ggnet.dwoss.redtape.eao.DocumentEao;


import eu.ggnet.dwoss.spec.assist.SpecPu;
import eu.ggnet.dwoss.stock.eao.LogicTransactionEao;
import eu.ggnet.dwoss.uniqueunit.assist.UniqueUnitPu;

import eu.ggnet.dwoss.util.UserInfoException;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.*;

/**
 *
 * @author bastian.venz
 */
public class RedTapeOperationOrderInvoiceIT {

    @Stateless
    public static class DocumentManipulator {

        @Inject
        @RedTapes
        private EntityManager redTapeEm;

        public Document changeActual(Document doc, Date date) {
            doc = new DocumentEao(redTapeEm).findById(doc.getId());
            doc.setActual(date);
            return doc;
        }
    }

    @Stateless
    public static class LogicTransactionEaoWrapper {

        @Inject
        @Stocks
        private EntityManager stockEm;

        public List<LogicTransaction> findAll() {
            return new LogicTransactionEao(stockEm).findAll();
        }

        public LogicTransaction findByDossierId(long dossierId) {
            return new LogicTransactionEao(stockEm).findByDossierId(dossierId);
        }
    }

    private EJBContainer container;

    @EJB
    private UnitOverseer unitOverseer;

    @EJB
    private RedTapeWorker redTapeWorker;

    @Inject
    private CustomerGeneratorOperation customerGenerator;

    @Inject
    private LogicTransactionEaoWrapper logicTransactionEao;

    @EJB
    private RedTapeAgent redTapeAgent;

    @EJB
    private StockAgent stockAgent;

    @Inject
    private DocumentManipulator manipulator;

    @Inject
    private ReceiptGeneratorOperation receiptGenerator;

    @Before
    public void setUp() throws NamingException {
        Map<String, Object> c = new HashMap<>();
        c.putAll(SpecPu.CMP_IN_MEMORY);
        c.putAll(UniqueUnitPu.CMP_IN_MEMORY);
        c.putAll(CustomerPu.CMP_IN_MEMORY);
        c.putAll(StockPu.CMP_IN_MEMORY);
        c.putAll(RedTapePu.CMP_IN_MEMORY);
        c.putAll(SystemConfig.OPENEJB_EJB_XML_DISCOVER);
        c.putAll(SystemConfig.OPENEJB_LOG_WARN);
        container = EJBContainer.createEJBContainer(c);
        container.getContext().bind("inject", this);
    }

    @After
    public void tearDown() {
        container.close();
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
        assertEquals(dos1.getCustomerId(), customerId);
        assertEquals(dos1.getPaymentMethod(), PaymentMethod.ADVANCE_PAYMENT);
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
        assertTrue(doc1.equalsContent(dos1.getActiveDocuments(DocumentType.ORDER).get(0)));

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
        Position p8 = new PositionBuilder().setType(PositionType.SERVICE).setPrice(50.).
                setTax(1.).setAfterTaxPrice(1.).setName("Position 8").setDescription("Position from update2").createPosition();
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
        assertTrue(doc1.equalsContent(dos.getActiveDocuments(DocumentType.ORDER).get(0)));

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
        assertTrue(doc1.equalsContent(dos.getActiveDocuments(DocumentType.ORDER).get(0)));

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
