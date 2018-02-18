package eu.ggnet.dwoss.receipt.itest;

import eu.ggnet.dwoss.stock.ee.entity.StockTransaction;
import eu.ggnet.dwoss.stock.ee.entity.Shipment;
import eu.ggnet.dwoss.stock.ee.entity.Stock;
import eu.ggnet.dwoss.stock.ee.entity.LogicTransaction;
import eu.ggnet.dwoss.stock.ee.entity.StockUnit;
import eu.ggnet.dwoss.redtape.ee.entity.Dossier;
import eu.ggnet.dwoss.redtape.ee.entity.Position;
import eu.ggnet.dwoss.redtape.ee.entity.Document;

import java.util.*;

import javax.ejb.EJB;
import javax.inject.Inject;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.customer.ee.assist.gen.CustomerGeneratorOperation;
import eu.ggnet.dwoss.mandator.api.value.Contractors;
import eu.ggnet.dwoss.mandator.api.value.ReceiptCustomers;
import eu.ggnet.dwoss.receipt.ee.UnitProcessor;
import eu.ggnet.dwoss.receipt.ee.gen.ReceiptGeneratorOperation;
import eu.ggnet.dwoss.receipt.itest.support.ArquillianProjectArchive;
import eu.ggnet.dwoss.redtape.ee.RedTapeAgent;
import eu.ggnet.dwoss.rules.*;
import eu.ggnet.dwoss.spec.ee.entity.ProductSpec;
import eu.ggnet.dwoss.stock.ee.StockAgent;
import eu.ggnet.dwoss.stock.ee.assist.gen.StockGeneratorOperation;
import eu.ggnet.dwoss.stock.ee.emo.StockTransactionEmo;
import eu.ggnet.dwoss.uniqueunit.ee.UniqueUnitAgent;
import eu.ggnet.dwoss.uniqueunit.ee.assist.gen.UniqueUnitGenerator;
import eu.ggnet.dwoss.uniqueunit.ee.entity.Product;
import eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnit;
import eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnit.Identifier;

import static eu.ggnet.dwoss.rules.ReceiptOperation.*;
import static org.junit.Assert.*;

/**
 *
 * @author bastian.venz, oliver.guenther
 */
@RunWith(Arquillian.class)
public class ReceiptUnitOperationIT extends ArquillianProjectArchive {

    @EJB
    private UnitProcessor unitProcessor;

    @EJB
    private UniqueUnitAgent uniqueUnitAgent;

    @Inject
    private StockGeneratorOperation stockGenerator;

    @Inject
    private CustomerGeneratorOperation customerGenerator;

    @Inject
    private ReceiptGeneratorOperation receiptGenerator;

    @EJB
    private StockAgent stockAgent;

    @EJB
    private RedTapeAgent redTapeAgent;

    @Inject
    private ReceiptCustomers receiptCustomers;

    @Inject
    private Contractors contractors;

    @Inject
    private StockTransactionEmo stockTransactionEmo;

    private final UniqueUnitGenerator unitGenerator = new UniqueUnitGenerator();

    @Test
    public void testReceiptAndUpdate() throws InterruptedException {
        customerGenerator.makeSystemCustomers(contractors.all().toArray(new TradeName[0]));
        // Constants ,later permutate throug all
        Stock stock = stockGenerator.makeStocksAndLocations(2).get(0);

        List<ReceiptOperation> operations = Arrays.asList(SALEABLE, INTERNAL_REWORK, MISSING_PARTS, REPAIR);

        // Receipt a Unit
        ProductSpec productSpec = receiptGenerator.makeProductSpec();
        Product product = uniqueUnitAgent.findById(Product.class, productSpec.getProductId());
        StockTransaction stockTransaction = stockTransactionEmo.requestRollInPrepared(stock.getId(), "No User", "Rollin via ReceiptUnitOperationHelper.findOrCreateRollInTransaction");

        for (TradeName contractor : contractors.all()) {
            Shipment productShipment = stockAgent.persist(new Shipment("SHIPMENTNAME_" + contractor, contractor, TradeName.ACER, Shipment.Status.OPENED));
            for (ReceiptOperation receiptOperation : operations) {
                UniqueUnit receiptUnit = unitGenerator.makeUniqueUnit(contractor, product);
                unitProcessor.receipt(receiptUnit, product, productShipment, stockTransaction, receiptOperation, "Receipt Operation from Test", "Testuser");

                asserts(receiptUnit, stockTransaction, receiptOperation, contractor);
                for (ReceiptOperation updateOperation : operations) {
                    UniqueUnit uniqueUnit = uniqueUnitAgent.findUnitByIdentifierEager(Identifier.REFURBISHED_ID, receiptUnit.getIdentifier(Identifier.REFURBISHED_ID));
                    unitProcessor.update(uniqueUnit, product, updateOperation, "Update Operation from Test", "Testuser");
                    assertsUpdate(receiptUnit, stockTransaction, updateOperation, contractor);
                }
            }
        }
        Thread.sleep(3000);
    }

    private void asserts(UniqueUnit receiptUnit, StockTransaction stockTransaction, ReceiptOperation receiptOperation, TradeName contractor) {
        String head = "(" + contractor + "," + receiptOperation + "):";
        // Verify the UniqueUnit
        UniqueUnit uniqueUnit = uniqueUnitAgent.findUnitByIdentifierEager(Identifier.REFURBISHED_ID, receiptUnit.getIdentifier(Identifier.REFURBISHED_ID));
        assertNotNull(head + "Receipt Unit should exist", uniqueUnit);
        assertEquals(head + "Serial not equal", receiptUnit.getIdentifier(Identifier.SERIAL), uniqueUnit.getIdentifier(Identifier.SERIAL));

        // Verify the StockUnit
        StockUnit stockUnit = stockAgent.findStockUnitByUniqueUnitIdEager(uniqueUnit.getId());
        assertNotNull(head + "StockUnit should exist", stockUnit);
        assertEquals(head + "RefurbishId of UniqueUnit and StockUnit must be equal", receiptUnit.getIdentifier(Identifier.REFURBISHED_ID), stockUnit.getRefurbishId());
        assertEquals(head + "StockTransaction must be the same", stockTransaction.getId(), stockUnit.getTransaction().getId());

        if ( !ReceiptOperation.valuesBackedByCustomer().contains(receiptOperation) ) {
            // If unspecial Operation, no more verification needed.
            assertNull(head + "StockUnit.logicTransaction for " + receiptOperation, stockUnit.getLogicTransaction());
            return;
        }

        // Verify RedTape
        LogicTransaction logicTransaction = stockUnit.getLogicTransaction();
        assertNotNull(head + "StockUnit.logicTransaction for " + receiptOperation, logicTransaction);
        Dossier dossier = redTapeAgent.findByIdEager(Dossier.class, stockUnit.getLogicTransaction().getDossierId());
        assertNotNull(head + "Dossier for LogicTransaction must exist", dossier);
        assertEquals(head + "Dossier.customerId for " + contractor + " with " + receiptOperation,
                receiptCustomers.getCustomerId(contractor, receiptOperation), dossier.getCustomerId());
        assertEquals(head + "Dossier.activeDocuments", 1, dossier.getActiveDocuments().size());
        Document document = dossier.getActiveDocuments().get(0);
        assertEquals(head + "Document.type", DocumentType.BLOCK, document.getType());
        assertEquals(head + "LogicTransaction.stockUnits and Document.positions.uniqueUnitIds",
                toUniqueUnitIds(logicTransaction), document.getPositionsUniqueUnitIds());
    }

    private void assertsUpdate(UniqueUnit receiptUnit, StockTransaction stockTransaction, ReceiptOperation receiptOperation, TradeName contractor) {
        asserts(receiptUnit, stockTransaction, receiptOperation, contractor);
        List<Dossier> allDossiers = redTapeAgent.findAllEager(Dossier.class);
        List<Document> allDocumentsWithUnit = new ArrayList<>();
        for (Dossier dossier : allDossiers) {
            for (Document document : dossier.getActiveDocuments()) {
                for (Position position : document.getPositions(PositionType.UNIT).values()) {
                    if ( position.getUniqueUnitId() == receiptUnit.getId() ) {
                        allDocumentsWithUnit.add(document);
                    }
                }
            }
        }
        List<Long> documentIds = new ArrayList<>();
        for (Document document : allDocumentsWithUnit) {
            documentIds.add(document.getId());
        }
        assertTrue("The UniqueUnit " + receiptUnit.getId() + " is in more than one document. DocumentsIds: " + documentIds, documentIds.size() <= 1);

    }

    private Set<Integer> toUniqueUnitIds(LogicTransaction lt) {
        Set<Integer> result = new HashSet<>();
        for (StockUnit unit : lt.getUnits()) {
            result.add(unit.getUniqueUnitId());
        }
        return result;

    }

}
