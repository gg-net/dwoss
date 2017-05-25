package eu.ggnet.dwoss.redtape.op.itest;

import java.text.SimpleDateFormat;
import java.util.*;

import javax.ejb.EJB;
import javax.inject.Inject;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.customer.assist.gen.CustomerGeneratorOperation;
import eu.ggnet.dwoss.receipt.gen.ReceiptGeneratorOperation;
import eu.ggnet.dwoss.redtape.*;
import eu.ggnet.dwoss.redtape.entity.*;
import eu.ggnet.dwoss.redtape.op.itest.support.ArquillianProjectArchive;
import eu.ggnet.dwoss.redtape.op.itest.support.SupportBean;
import eu.ggnet.dwoss.rules.DocumentType;
import eu.ggnet.dwoss.rules.PositionType;
import eu.ggnet.dwoss.stock.StockAgent;
import eu.ggnet.dwoss.stock.assist.gen.StockGeneratorOperation;
import eu.ggnet.dwoss.stock.entity.*;
import eu.ggnet.dwoss.uniqueunit.entity.Product;
import eu.ggnet.dwoss.uniqueunit.entity.UniqueUnit;
import eu.ggnet.dwoss.util.UserInfoException;

import static eu.ggnet.dwoss.redtape.op.itest.support.NaivBuilderUtil.*;
import static org.junit.Assert.*;

/**
 *
 * @author bastian.venz
 */
@RunWith(Arquillian.class)
public class RedTapeOperationAnnulationInvoiceIT extends ArquillianProjectArchive {

    @Inject
    private CustomerGeneratorOperation customerGenerator;

    @Inject
    private StockGeneratorOperation stockGenerator;

    @EJB
    private RedTapeWorker redTapeWorker;

    @EJB
    private UnitOverseer unitOverseer;

    @EJB
    private StockAgent stockAgent;

    @EJB
    private RedTapeAgent redTapeAgent;

    @Inject
    private SupportBean support;

    @Inject
    private ReceiptGeneratorOperation receiptGenerator;

    private final String YY = new SimpleDateFormat("YY").format(new Date());

    @Test
    public void testCreditMemo() throws UserInfoException {
        List<Stock> allStocks = stockGenerator.makeStocksAndLocations(2); // We need two stocks at least.
        long customerId = customerGenerator.makeCustomer();
        List<UniqueUnit> uus = receiptGenerator.makeUniqueUnits(4, true, true);
        UniqueUnit uu1 = uus.get(0);
        UniqueUnit uu2 = uus.get(1);
        UniqueUnit uu3 = uus.get(2);
        UniqueUnit uu4 = uus.get(3);

        Product uuProduct1 = uu1.getProduct();

        int stockIdOfUU1 = stockAgent.findStockUnitByUniqueUnitIdEager(uu1.getId()).getStock().getId();
        int alternateStockId = allStocks.stream().map(Stock::getId).filter(id -> id != stockIdOfUU1).findFirst()
                .orElseThrow(() -> new RuntimeException("No alternate StockId found, impossible"));

        Dossier dos = redTapeWorker.create(customerId, true, "Me");
        Document doc = dos.getActiveDocuments(DocumentType.ORDER).get(0);
        assertTrue(doc.equalsContent(dos.getActiveDocuments(DocumentType.ORDER).get(0)));

        doc.append(unit(uu1));
        doc.append(unit(uu2));
        doc.append(unit(uu3));
        doc.append(comment());
        doc.append(service());
        doc.append(batch(uuProduct1));
        doc.append(shippingcost());

        //add units to LogicTransaction
        unitOverseer.lockStockUnit(dos.getId(), uu1.getIdentifier(UniqueUnit.Identifier.REFURBISHED_ID));
        unitOverseer.lockStockUnit(dos.getId(), uu2.getIdentifier(UniqueUnit.Identifier.REFURBISHED_ID));
        unitOverseer.lockStockUnit(dos.getId(), uu3.getIdentifier(UniqueUnit.Identifier.REFURBISHED_ID));
        unitOverseer.lockStockUnit(dos.getId(), uu4.getIdentifier(UniqueUnit.Identifier.REFURBISHED_ID));

        doc = redTapeWorker.update(doc, null, "JUnit");

        doc.add(Document.Condition.PAID);
        doc.add(Document.Condition.PICKED_UP);
        doc.setType(DocumentType.INVOICE);

        doc = redTapeWorker.update(doc, null, "JUnit");

        LogicTransaction lt = support.findByDossierId(doc.getDossier().getId());
        assertNotNull("A LogicTrasaction must exists", lt);
        assertEquals("The Size of the LogicTransaction", 3, lt.getUnits().size());

        //A CreditMemo for Unit1, negate prices on Annulation Invoice.
        for (Position pos : new ArrayList<>(doc.getPositions().values())) {
            if ( pos.getUniqueUnitId() == uu1.getId() ) {
                pos.setPrice(pos.getPrice() * -1);
                pos.setPrice(pos.getAfterTaxPrice() * -1);
            } else {
                doc.remove(pos);
            }
        }

        assertEquals("Document should have exactly one possition", 1, doc.getPositions().size());
        assertEquals("Position is exactly the same UniqueUnitId", uu1.getId(), doc.getPositions().get(1).getUniqueUnitId());
        doc.setType(DocumentType.ANNULATION_INVOICE);

        // Setting the Stock by force, cause we want to see if on update, the unit is moved to the original stock.
        support.changeStock(uu1.getId(), alternateStockId);

        doc = redTapeWorker.update(doc, stockIdOfUU1, "JUnit Test");
        // Asserting Everything
        assertEquals("The Identifier of CreditMemo", "SR" + YY + "_00001", doc.getIdentifier());

        lt = support.findByDossierId(doc.getDossier().getId());

        assertNotNull("A LogicTrasaction must still exists", lt);
        assertEquals("The Size of the LogicTransaction", 2, lt.getUnits().size());
        for (StockUnit stockUnit : lt.getUnits()) {
            if ( stockUnit.getUniqueUnitId() == uu1.getId() ) {
                fail("The StockUnit of the CreditMemo should not be on the LogicTransaction of the Dossier");
            }
        }
        List<StockTransaction> sto = stockAgent.findStockTransactionEager(StockTransactionType.EXTERNAL_TRANSFER, StockTransactionStatusType.COMPLETED);
        assertEquals("One External Transfer Transaction", 1, sto.size());
        assertEquals("Only One Position on the Transaction should exist", 1, sto.get(0).getPositions().size());
        assertEquals("The One Position should reference to the UniqueUnit of the CreditMemo", uu1.getId(), sto.get(0).getPositions().get(0).getUniqueUnitId().intValue());
        assertEquals("The Transaction should contain exactlly one shadow of the UniqueUnit", 1, sto.size());

        StockUnit stockUnit1 = stockAgent.findStockUnitByUniqueUnitIdEager(uu1.getId());
        assertEquals("The Stock of the StockUnit", stockIdOfUU1, stockUnit1.getStock().getId());
        assertNotNull("StockUnit should be on a LogicTransaction", stockUnit1.getLogicTransaction());

        Dossier dossier = redTapeAgent.findByIdEager(Dossier.class, stockUnit1.getLogicTransaction().getDossierId());
        assertNotNull("A Dossier on the SystemCustomer must exist", dossier);
        assertFalse(dossier.getActiveDocuments().isEmpty());
        assertFalse(dossier.getActiveDocuments().get(0).getPositions().isEmpty());
        assertEquals(2, dossier.getActiveDocuments().get(0).getPositions().size());
        boolean unit1Found = false;
        boolean commentFound = false;
        for (Position pos : dossier.getActiveDocuments().get(0).getPositions().values()) {
            if ( pos.getType() == PositionType.UNIT ) {
                assertEquals(uu1.getId(), pos.getUniqueUnitId());
                unit1Found = true;
            } else if ( pos.getType() == PositionType.COMMENT ) {
                commentFound = true;
            }
        }
        assertTrue(unit1Found);
        assertTrue(commentFound);

        Document invoice = doc.getDossier().getActiveDocuments(DocumentType.INVOICE).get(0);

        //A CreditMemo for a Unit, which is Rolled Out before.
        for (Position pos : new ArrayList<>(invoice.getPositions().values())) {
            if ( pos.getType() != PositionType.UNIT ) invoice.remove(pos);
            else if ( pos.getUniqueUnitId() != uu2.getId() ) invoice.remove(pos);
            else {
                pos.setPrice(pos.getPrice() * -1);
                pos.setPrice(pos.getAfterTaxPrice() * -1);
            }
        }

        assertEquals("Document should have exactly one possition", 1, invoice.getPositions().size());
        assertEquals("Position is exactly the same UniqueUnitId", uu2.getId(), invoice.getPositions().get(1).getUniqueUnitId());
        invoice.setType(DocumentType.ANNULATION_INVOICE);

        // Lets roll Out the Unit
        support.rollOut(uu2.getId());

        // Verify it is not in stock
        StockUnit stockUnit2 = stockAgent.findStockUnitByUniqueUnitIdEager(uu2.getId());
        assertNull("StockUnit should not exist: " + stockUnit2, stockUnit2);
        // Do the second credit Memo and check if the Unit is back in the stock.
        doc = redTapeWorker.update(invoice, stockIdOfUU1, "JUnit");

        // Assert Everything
        // TODO: this is Mandatorspecific, pickup there.
        assertEquals("The Identifier of CreditMemo", "SR" + YY + "_00002", doc.getIdentifier());

        stockUnit2 = stockAgent.findStockUnitByUniqueUnitIdEager(uu2.getId());
        assertNotNull("StockUnit exists", stockUnit2);
        assertNotNull("StockUnit should have LogicTransaction", stockUnit2.getLogicTransaction());
        assertEquals("StockUnit is not the correct one", uu2.getId(), stockUnit2.getUniqueUnitId().intValue());

        dossier = redTapeAgent.findByIdEager(Dossier.class, stockUnit2.getLogicTransaction().getDossierId());
        assertNotNull("A Dossier on the SystemCustomer must exist", dossier);
        assertFalse(dossier.getActiveDocuments().isEmpty());
        assertFalse(dossier.getActiveDocuments().get(0).getPositions().isEmpty());
        assertEquals(2, dossier.getActiveDocuments().get(0).getPositions().size());
        unit1Found = false;
        commentFound = false;
        for (Position pos : dossier.getActiveDocuments().get(0).getPositions().values()) {
            if ( pos.getType() == PositionType.UNIT ) {
                assertEquals(uu2.getId(), pos.getUniqueUnitId());
                unit1Found = true;
            } else if ( pos.getType() == PositionType.COMMENT ) {
                commentFound = true;
            }
        }
        assertTrue(unit1Found);
        assertTrue(commentFound);

    }

}
