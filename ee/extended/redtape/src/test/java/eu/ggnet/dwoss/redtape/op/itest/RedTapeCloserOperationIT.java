package eu.ggnet.dwoss.redtape.op.itest;

import eu.ggnet.dwoss.redtape.op.itest.support.RedTapeCloserOpertaionItBean;

import java.util.*;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.inject.Inject;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.*;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.configuration.GlobalConfig;
import eu.ggnet.dwoss.customer.api.CustomerMetaData;
import eu.ggnet.dwoss.customer.assist.gen.CustomerGeneratorOperation;
import eu.ggnet.dwoss.customer.op.CustomerServiceBean;
import eu.ggnet.dwoss.mandator.api.value.*;
import eu.ggnet.dwoss.receipt.gen.ReceiptGeneratorOperation;
import eu.ggnet.dwoss.redtape.RedTapeAgent;
import eu.ggnet.dwoss.redtape.RedTapeWorker;
import eu.ggnet.dwoss.redtape.api.UnitPositionHook;
import eu.ggnet.dwoss.redtape.entity.*;
import eu.ggnet.dwoss.redtape.gen.RedTapeGeneratorOperation;
import eu.ggnet.dwoss.redtape.op.itest.support.*;
import eu.ggnet.dwoss.redtape.reporting.RedTapeCloser;
import eu.ggnet.dwoss.report.ReportAgent;
import eu.ggnet.dwoss.report.entity.ReportLine;
import eu.ggnet.dwoss.rules.DocumentType;
import eu.ggnet.dwoss.rules.PositionType;
import eu.ggnet.dwoss.stock.StockAgent;
import eu.ggnet.dwoss.stock.entity.*;
import eu.ggnet.dwoss.uniqueunit.eao.ProductEao;
import eu.ggnet.dwoss.uniqueunit.entity.*;
import eu.ggnet.dwoss.uniqueunit.format.UniqueUnitFormater;
import eu.ggnet.dwoss.util.MathUtil;
import eu.ggnet.dwoss.util.UserInfoException;
import eu.ggnet.dwoss.util.interactiveresult.Result;

import static eu.ggnet.dwoss.redtape.entity.Document.Condition.CANCELED;
import static eu.ggnet.dwoss.rules.DocumentType.BLOCK;
import static eu.ggnet.dwoss.rules.PositionType.PRODUCT_BATCH;
import static eu.ggnet.dwoss.rules.TradeName.ACER;
import static eu.ggnet.dwoss.uniqueunit.entity.UniqueUnit.Identifier.REFURBISHED_ID;
import static org.fest.assertions.api.Assertions.assertThat;
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
                            .afterTaxPrice(MathUtil.roundedApply(10, GlobalConfig.TAX, 0.))
                            .serialNumber(p.getSerial())
                            .refurbishedId(p.getRefurbishedId())
                            .bookingAccount(1000)
                            .type(PRODUCT_BATCH)
                            .tax(GlobalConfig.TAX)
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
    @Ignore
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
        double price = uu.getPrice(PriceType.CUSTOMER);
        if ( price < 0.001 ) price = uu.getPrice(PriceType.RETAILER);
        if ( price < 0.001 ) price = 1111.11;
        Position pos = new PositionBuilder()
                .setType(PositionType.UNIT)
                .setUniqueUnitId(uu.getId())
                .setUniqueUnitProductId(uu.getProduct().getId())
                .setPrice(price)
                .setTax(GlobalConfig.TAX)
                .setAfterTaxPrice(MathUtil.roundedApply(price, GlobalConfig.TAX, 0.))
                .setDescription(UniqueUnitFormater.toDetailedDiscriptionLine(uu))
                .setName(UniqueUnitFormater.toPositionName(uu))
                .setBookingAccount(-1)
                .createPosition();
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
                .bookingAccount(postLedger.get(PositionType.COMMENT).orElse(-1))
                .build());

        Dossier d2 = redTapeWorker.create(customerId, R.nextBoolean(), "JUNIT");
        d2.getActiveDocuments(BLOCK).get(0).append(Position.builder()
                .amount(1)
                .type(PositionType.COMMENT)
                .name("Comment")
                .description("Comment")
                .bookingAccount(postLedger.get(PositionType.COMMENT).orElse(-1))
                .build());
        d2.getActiveDocuments(BLOCK).get(0).append(Position.builder()
                .type(PositionType.SERVICE)
                .price(100)
                .tax(GlobalConfig.TAX)
                .afterTaxPrice(MathUtil.roundedApply(100, GlobalConfig.TAX, 0.))
                .name("Service")
                .description("Service")
                .amount(1)
                .bookingAccount(postLedger.get(PositionType.SERVICE).orElse(-1))
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
                .tax(GlobalConfig.TAX)
                .afterTaxPrice(MathUtil.roundedApply(unit1.getPrice(PriceType.SALE), GlobalConfig.TAX, 0.))
                .description(UniqueUnitFormater.toDetailedDiscriptionLine(unit1))
                .name(UniqueUnitFormater.toPositionName(unit1))
                .bookingAccount(-1)
                .refurbishedId(unit1.getIdentifier(REFURBISHED_ID))
                .build());

        Dossier d4 = redTapeWorker.create(customerId, R.nextBoolean(), "JUNIT");
        d4.getActiveDocuments(BLOCK).get(0).append(Position.builder()
                .amount(1)
                .type(PositionType.UNIT)
                .uniqueUnitId(unit2.getId())
                .uniqueUnitProductId(unit2.getProduct().getId())
                .price(unit2.getPrice(PriceType.SALE))
                .tax(GlobalConfig.TAX)
                .afterTaxPrice(MathUtil.roundedApply(unit2.getPrice(PriceType.SALE), GlobalConfig.TAX, 0.))
                .description(UniqueUnitFormater.toDetailedDiscriptionLine(unit2))
                .name(UniqueUnitFormater.toPositionName(unit2))
                .bookingAccount(-1)
                .refurbishedId(unit2.getIdentifier(REFURBISHED_ID))
                .build());

        Dossier d5 = redTapeWorker.create(receiptId, R.nextBoolean(), "JUNIT");
        d5.getActiveDocuments(BLOCK).get(0).append(Position.builder()
                .amount(1)
                .type(PositionType.COMMENT)
                .name("Comment")
                .description("Comment")
                .bookingAccount(postLedger.get(PositionType.COMMENT).orElse(-1))
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
