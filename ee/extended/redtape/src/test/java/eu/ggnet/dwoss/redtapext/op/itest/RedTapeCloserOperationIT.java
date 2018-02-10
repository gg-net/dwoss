package eu.ggnet.dwoss.redtapext.op.itest;

import java.util.*;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.inject.Inject;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.customer.opi.CustomerMetaData;
import eu.ggnet.dwoss.customer.ee.CustomerServiceBean;
import eu.ggnet.dwoss.customer.ee.assist.gen.CustomerGeneratorOperation;
import eu.ggnet.dwoss.mandator.api.value.*;
import eu.ggnet.dwoss.receipt.gen.ReceiptGeneratorOperation;
import eu.ggnet.dwoss.redtape.ee.RedTapeAgent;
import eu.ggnet.dwoss.redtape.ee.api.UnitPositionHook;
import eu.ggnet.dwoss.redtape.ee.entity.*;
import eu.ggnet.dwoss.redtapext.ee.RedTapeWorker;
import eu.ggnet.dwoss.redtapext.ee.gen.RedTapeGeneratorOperation;
import eu.ggnet.dwoss.redtapext.ee.reporting.RedTapeCloser;
import eu.ggnet.dwoss.redtapext.op.itest.support.*;
import eu.ggnet.dwoss.report.ReportAgent;
import eu.ggnet.dwoss.report.entity.ReportLine;
import eu.ggnet.dwoss.rules.*;
import eu.ggnet.dwoss.stock.StockAgent;
import eu.ggnet.dwoss.stock.entity.*;
import eu.ggnet.dwoss.uniqueunit.eao.ProductEao;
import eu.ggnet.dwoss.uniqueunit.entity.*;
import eu.ggnet.dwoss.uniqueunit.format.UniqueUnitFormater;
import eu.ggnet.dwoss.util.UserInfoException;
import eu.ggnet.dwoss.util.interactiveresult.Result;

import static eu.ggnet.dwoss.redtape.ee.entity.Document.Condition.CANCELED;
import static eu.ggnet.dwoss.rules.DocumentType.BLOCK;
import static eu.ggnet.dwoss.rules.PositionType.PRODUCT_BATCH;
import static eu.ggnet.dwoss.rules.TradeName.ACER;
import static eu.ggnet.dwoss.uniqueunit.entity.UniqueUnit.Identifier.REFURBISHED_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

/**
 *
 * @author oliver.guenther
 */
@RunWith(Arquillian.class)
public class RedTapeCloserOperationIT extends ArquillianProjectArchive {

    class RedTapeHookStup implements UnitPositionHook {

        @Override
        public Result<List<Position>> elaborateUnitPosition(Position p, long documentId) throws UserInfoException {
            return new Result<>(Arrays.asList(p,
                    Position.builder()
                            .amount(p.getAmount())
                            .price(10.)
                            .tax(p.getTax())
                            .serialNumber(p.getSerial())
                            .refurbishedId(p.getRefurbishedId())
                            .bookingAccount(new Ledger(1000, "DemoLedger"))
                            .type(PRODUCT_BATCH)
                            .tax(TaxType.GENERAL_SALES_TAX_DE_SINCE_2007.getTax())
                            .uniqueUnitId(p.getUniqueUnitId())
                            .uniqueUnitProductId(eao.findByPartNo(WARRANTY_PART_NO).getId())
                            .name("Warranty Position")
                            .description("Warranty Position")
                            .build()
            ));
        }

    }

    public final static String WARRANTY_PART_NO = WarrantyServiceStup.WARRANTY_PART_NO;

    private final static Random R = new Random();

    @Inject
    private CustomerGeneratorOperation customerGenerator;

    @Inject
    private ReceiptGeneratorOperation receiptGenerator;

    @Inject
    private RedTapeGeneratorOperation redTapeGenerator;

    @EJB
    private RedTapeCloser redTapeCloser;

    @EJB
    private ReportAgent reportAgent;

    @EJB
    private RedTapeWorker redTapeWorker;

    @EJB
    private RedTapeAgent redTapeAgent;

    @Inject
    private CustomerServiceBean customerService;

    @Inject
    private ProductEao eao;

    @EJB
    private StockAgent stockAgent;

    @Inject
    private RedTapeCloserOpertaionItBean redTapeCloserOpertaionItBean;

    @Inject
    private PostLedger postLedger;

    private SpecialSystemCustomers systemCustomers;

    private ReceiptCustomers receiptCustomers;

    @Inject
    private DatabaseCleaner cleaner;

    @After
    public void clearDatabase() throws Exception {
        cleaner.clear();
    }

    private void warnIfStockSizeDidNotChange(long stockSize) {
        if ( stockSize == stockAgent.count(StockUnit.class) ) {
            System.err.println("Stock size din't change, this is highly unlikely. To solve:\n"
                    + " - Rerun Test multiple times\n"
                    + " - Increase the amount of generator\n"
                    + " - Manipulate Stock before rollout to ensure the right behavior");
        }
    }

    /**
     * Tests if something gets closed and if the appropriated stock units are gone.
     * <p>
     * @throws UserInfoException
     */
    @Test
    public void testDayClosing() throws UserInfoException {
        assertFalse(customerGenerator.makeCustomers(10).isEmpty());
        receiptCustomers = customerGenerator.makeReceiptCustomers(ACER);
        systemCustomers = customerGenerator.makeSpecialCustomers(BLOCK);
        assertFalse(systemCustomers == null);
        assertFalse(receiptCustomers == null);
        assertFalse(receiptGenerator.makeUniqueUnits(200, true, true).isEmpty());
        assertFalse(redTapeGenerator.makeSalesDossiers(30).isEmpty());

        //dossier ids from created blockers
        List<Long> blockerIds = buildBlocker().stream().mapToLong(d -> d.getId()).boxed().collect(Collectors.toList());

        assertThat(
                stockAgent.findAllEager(StockTransaction.class)
                        .stream()
                        .map(StockTransaction::getPositions)
                        .flatMap(Collection::stream)
                        .anyMatch(t -> t.getStockUnit() != null)
        )
                .overridingErrorMessage("Their exist a StockTransaction, which is not complete (blocking a stockUnit), impossible!")
                .isFalse();

        long stockUnits = stockAgent.count(StockUnit.class);
        assertThat(stockUnits).isPositive();

        List<LogicTransaction> allLts = stockAgent.findAllEager(LogicTransaction.class);
        assertThat(allLts.size())
                .overridingErrorMessage("No LogicTransactions exist, impossible!")
                .isPositive();

        redTapeCloser.executeManual("Junit");

        List<Dossier> blockerDossiers = new ArrayList<>();
        for (Long blockerId : blockerIds) {
            blockerDossiers.add(redTapeAgent.findByIdEager(Dossier.class, blockerId));
        }
        assertEquals("More/Less Blockers than expected passed closing", 3, blockerDossiers.stream().filter(d -> d.isClosed()).collect(Collectors.toList()).size());

        warnIfStockSizeDidNotChange(stockUnits);

        List<Dossier> allDossiers = redTapeAgent.findAllEager(Dossier.class);
        for (Dossier dos : allDossiers) {
            if ( dos.isClosed() ) {
                for (Document doc : dos.getActiveDocuments()) {
                    if ( doc.containsAny(CANCELED) ) continue; // These are just ignored.
                    for (Integer uuId : doc.getPositionsUniqueUnitIds()) {
                        StockUnit su = stockAgent.findStockUnitByUniqueUnitIdEager(uuId);

                        assertNull("There is a StockUnit for a closed Dossier (doc.id= " + doc.getId() + "):\n"
                                + dos.toMultiLine() + "\n\n"
                                + su + "\n\n"
                                + "Original LTS: " + allLts.stream().filter(x -> x.getUnits().contains(su)).findAny().orElse(null), su);
                    }
                }
            } else {
                for (Integer uuId : dos.getRelevantUniqueUnitIds()) {
                    StockUnit su = stockAgent.findStockUnitByUniqueUnitIdEager(uuId);
                    assertNotNull("There is no StockUnit for an open Dossier\n" + dos, su);
                }
            }
        }

        long reportSize = reportAgent.count(ReportLine.class);
        assertFalse(reportSize == 0);

        redTapeCloser.executeManual("Junit");
        assertEquals("Second call should not add anything new", reportSize, reportAgent.count(ReportLine.class));
    }

    @Test
    public void testDayClosingWarrenty() throws UserInfoException {
        long customerId = customerGenerator.makeCustomer();
        UniqueUnit uu = receiptGenerator.makeUniqueUnits(1, true, true).get(0);
        Product p = redTapeCloserOpertaionItBean.makeWarrantyProduct();
        CustomerMetaData metaCustomer = customerService.asCustomerMetaData(customerId);

        assertFalse("no customer in database", customerId == 0);
        assertFalse("bo unique unit in database", uu == null);
        assertFalse("no customer meta data found", metaCustomer == null);
        assertFalse("no warranty product in database", p == null);

        // Create a dossier on a random customer.
        Dossier dos = redTapeWorker.create(customerId, false, "Generated by RedTapeGeneratorOperation.makeSalesDossiers()");
        Document doc = dos.getActiveDocuments(DocumentType.ORDER).get(0);
        assertThat(doc).overridingErrorMessage("Expected active document Order, got null. Dossier: " + dos.toMultiLine()).isNotNull();

        double price = uu.getPrice(PriceType.CUSTOMER);
        if ( price < 0.001 ) price = uu.getPrice(PriceType.RETAILER);
        if ( price < 0.001 ) price = 1111.11;
        Position pos = Position.builder()
                .amount(1)
                .type(PositionType.UNIT)
                .uniqueUnitId(uu.getId())
                .uniqueUnitProductId(uu.getProduct().getId())
                .price(price)
                .tax(doc.getSingleTax())
                .description(UniqueUnitFormater.toDetailedDiscriptionLine(uu))
                .name(UniqueUnitFormater.toPositionName(uu))
                .build();
        pos.setRefurbishedId(uu.getRefurbishId());
        doc.appendAll(new RedTapeHookStup().elaborateUnitPosition(pos, doc.getId()).getPayload());

        doc = redTapeWorker.update(doc, null, "JUnit");
        doc.add(Document.Condition.PAID);
        doc.add(Document.Condition.PICKED_UP);
        doc.setType(DocumentType.INVOICE);

        doc = redTapeWorker.update(doc, null, "JUnit");
        redTapeCloser.executeManual("Junit");

        doc = redTapeAgent.findByIdEager(Document.class, doc.getId());
        doc.setType(DocumentType.COMPLAINT);
        doc.setDirective(Document.Directive.WAIT_FOR_COMPLAINT_COMPLETION);
        doc = redTapeWorker.update(doc, null, "JUnit");

        redTapeCloser.executeManual("Junit");

        redTapeCloserOpertaionItBean.checkReferences(dos.getId());
    }

    /**
     * builds 4 blocker dossiers in the following manner:
     * - 1 comment only blocker
     * - 1 comment plus non unit/comment blocker
     * - 1 unit with stockunit blocker
     * - 1 unit without stockunit blocker
     * */
    private List<Dossier> buildBlocker() {
        Long customerId = (Long)systemCustomers.getSpecialCustomers().keySet().toArray()[0];
        Long receiptId = (Long)receiptCustomers.getReceiptCustomers().values().toArray()[0];

        Dossier d1 = redTapeWorker.create(customerId, R.nextBoolean(), "JUNIT");
        d1.getActiveDocuments(BLOCK).get(0).append(Position.builder()
                .amount(1)
                .type(PositionType.COMMENT)
                .name("Comment")
                .description("Comment")
                .build());

        Dossier d2 = redTapeWorker.create(customerId, R.nextBoolean(), "JUNIT");
        Document doc2 = d2.getActiveDocuments(BLOCK).get(0);
        doc2.append(Position.builder()
                .amount(1)
                .type(PositionType.COMMENT)
                .name("Comment")
                .description("Comment")
                .build());

        doc2.append(Position.builder()
                .type(PositionType.SERVICE)
                .price(100)
                .tax(doc2.getSingleTax())
                .name("Service")
                .description("Service")
                .amount(1)
                .bookingAccount(postLedger.get(PositionType.SERVICE, doc2.getTaxType()).orElse(null))
                .build());

        UniqueUnit unit1 = receiptGenerator.makeUniqueUnits(1, true, true).get(0);
        UniqueUnit unit2 = receiptGenerator.makeUniqueUnits(1, true, true).get(0);

        Dossier d3 = redTapeWorker.create(customerId, R.nextBoolean(), "JUNIT");
        d3.getActiveDocuments(BLOCK).get(0).append(Position.builder()
                .amount(1)
                .type(PositionType.UNIT)
                .uniqueUnitId(unit1.getId())
                .uniqueUnitProductId(unit1.getProduct().getId())
                .price(unit1.getPrice(PriceType.SALE))
                .tax(d3.getActiveDocuments(BLOCK).get(0).getSingleTax())
                .description(UniqueUnitFormater.toDetailedDiscriptionLine(unit1))
                .name(UniqueUnitFormater.toPositionName(unit1))
                .refurbishedId(unit1.getIdentifier(REFURBISHED_ID))
                .build());

        Dossier d4 = redTapeWorker.create(customerId, R.nextBoolean(), "JUNIT");
        d4.getActiveDocuments(BLOCK).get(0).append(Position.builder()
                .amount(1)
                .type(PositionType.UNIT)
                .uniqueUnitId(unit2.getId())
                .uniqueUnitProductId(unit2.getProduct().getId())
                .price(unit2.getPrice(PriceType.SALE))
                .tax(d4.getActiveDocuments(BLOCK).get(0).getSingleTax())
                .description(UniqueUnitFormater.toDetailedDiscriptionLine(unit2))
                .name(UniqueUnitFormater.toPositionName(unit2))
                .refurbishedId(unit2.getIdentifier(REFURBISHED_ID))
                .build());

        Dossier d5 = redTapeWorker.create(receiptId, R.nextBoolean(), "JUNIT");
        d5.getActiveDocuments(BLOCK).get(0).append(Position.builder()
                .amount(1)
                .type(PositionType.COMMENT)
                .name("Comment")
                .description("Comment")
                .build());

        redTapeWorker.update(d1.getActiveDocuments(BLOCK).get(0), null, "JUNIT");
        redTapeWorker.update(d2.getActiveDocuments(BLOCK).get(0), null, "JUNIT");
        redTapeWorker.update(d3.getActiveDocuments(BLOCK).get(0), null, "JUNIT");
        redTapeWorker.update(d4.getActiveDocuments(BLOCK).get(0), null, "JUNIT");
        redTapeWorker.update(d5.getActiveDocuments(BLOCK).get(0), null, "JUNIT");

        redTapeCloserOpertaionItBean.deleteStockUnit(stockAgent.findStockUnitByRefurbishIdEager(unit2.getIdentifier(REFURBISHED_ID)).getId());

        return Arrays.asList(d1, d2, d3, d4, d5);
    }
}
