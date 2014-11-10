package eu.ggnet.dwoss.receipt;

import eu.ggnet.dwoss.rules.DocumentType;
import eu.ggnet.dwoss.rules.ReceiptOperation;
import eu.ggnet.dwoss.rules.PositionType;
import eu.ggnet.dwoss.rules.TradeName;
import eu.ggnet.dwoss.receipt.UnitProcessor;
import eu.ggnet.dwoss.redtape.entity.Dossier;
import eu.ggnet.dwoss.redtape.entity.Position;
import eu.ggnet.dwoss.redtape.entity.Document;
import eu.ggnet.dwoss.stock.assist.Stocks;
import eu.ggnet.dwoss.stock.entity.Stock;
import eu.ggnet.dwoss.stock.entity.LogicTransaction;
import eu.ggnet.dwoss.stock.entity.StockTransaction;
import eu.ggnet.dwoss.stock.assist.StockPu;
import eu.ggnet.dwoss.stock.entity.StockUnit;
import eu.ggnet.dwoss.stock.entity.Shipment;
import eu.ggnet.dwoss.uniqueunit.entity.Product;
import eu.ggnet.dwoss.uniqueunit.entity.UniqueUnit;

import java.util.*;

import javax.ejb.*;
import javax.ejb.embeddable.EJBContainer;
import javax.inject.Inject;
import javax.naming.NamingException;
import javax.persistence.EntityManager;

import org.junit.*;

import eu.ggnet.dwoss.configuration.SystemConfig;
import eu.ggnet.dwoss.mandator.api.value.Contractors;
import eu.ggnet.dwoss.mandator.api.value.ReceiptCustomers;
import eu.ggnet.dwoss.redtape.RedTapeAgent;
import eu.ggnet.dwoss.redtape.assist.RedTapePu;


import eu.ggnet.dwoss.customer.assist.CustomerPu;
import eu.ggnet.dwoss.customer.assist.gen.CustomerGeneratorOperation;
import eu.ggnet.dwoss.spec.assist.SpecPu;
import eu.ggnet.dwoss.spec.entity.ProductSpec;
import eu.ggnet.dwoss.stock.StockAgent;
import eu.ggnet.dwoss.stock.assist.gen.StockGeneratorOperation;
import eu.ggnet.dwoss.stock.emo.StockTransactionEmo;
import eu.ggnet.dwoss.uniqueunit.UniqueUnitAgent;
import eu.ggnet.dwoss.uniqueunit.assist.UniqueUnitPu;
import eu.ggnet.dwoss.uniqueunit.assist.gen.UniqueUnitGenerator;
import eu.ggnet.dwoss.uniqueunit.entity.UniqueUnit.Identifier;
import eu.ggnet.dwoss.receipt.gen.ReceiptGeneratorOperation;

import static eu.ggnet.dwoss.rules.ReceiptOperation.*;
import static org.junit.Assert.*;

/**
 *
 * @author bastian.venz, oliver.guenther
 */
public class ReceiptUnitOperationIT {

    //<editor-fold defaultstate="collapsed" desc="properties">
    private EJBContainer container;

    @Inject
    private UnitProcessor unitProcessor;

    @Inject
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
    private ReceiptUnitOperationHelper helper;

    @Inject
    private UniqueUnitGenerator unitGenerator;

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="before/after">
    @Before
    public void setUp() throws NamingException {
        Map<String, Object> c = new HashMap<>();
        c.putAll(SpecPu.CMP_IN_MEMORY);
        c.putAll(UniqueUnitPu.CMP_IN_MEMORY);
        c.putAll(CustomerPu.CMP_IN_MEMORY);
        c.putAll(StockPu.CMP_IN_MEMORY);
        c.putAll(RedTapePu.CMP_IN_MEMORY);
        c.putAll(SystemConfig.OPENEJB_EJB_XML_DISCOVER);
        c.putAll(SystemConfig.OPENEJB_LOG_TESTING);
        container = EJBContainer.createEJBContainer(c);
        container.getContext().bind("inject", this);
        customerGenerator.makeSystemCustomers(contractors.all().toArray(new TradeName[0]));
        unitGenerator = new UniqueUnitGenerator();
    }

    @After
    public void tearDown() {
        container.close();
    }

    //</editor-fold>
    @Test
    public void testReceiptAndUpdate() {
        // Constants ,later permutate throug all
        Stock stock = stockGenerator.makeStocksAndLocations(2).get(0);

        List<ReceiptOperation> operations = Arrays.asList(SALEABLE, INTERNAL_REWORK, MISSING_PARTS, REPAIR);

        // Receipt a Unit
        ProductSpec productSpec = receiptGenerator.makeProductSpec();
        Product product = uniqueUnitAgent.findById(Product.class, productSpec.getProductId());
        StockTransaction stockTransaction = helper.findOrCreateRollInTransaction(stock.getId(), "No User");

        for (TradeName contractor : contractors.all()) {
            Shipment productShipment = helper.persist(new Shipment("SHIPMENTNAME_" + contractor, contractor, TradeName.ACER, Shipment.Status.OPENED));
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

    @Stateless
    public static class ReceiptUnitOperationHelper {

        @Inject
        @Stocks
        private EntityManager em;

        @Inject
        private StockTransactionEmo stockTransactionEmo;

        @Inject
        private StockAgent agent;

        public ReceiptUnitOperationHelper() {
        }

        public StockTransaction findOrCreateRollInTransaction(int stockId, String user) {
            return stockTransactionEmo.requestRollInPrepared(stockId, user,
                    "Rollin via ReceiptUnitOperationHelper.findOrCreateRollInTransaction");
        }

        public <T> T persist(T elem) {
            agent.persist(elem);
            return elem;
        }
    }
}
