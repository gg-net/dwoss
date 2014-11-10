package eu.ggnet.dwoss.redtape.reporting;

import eu.ggnet.dwoss.rules.DocumentType;
import eu.ggnet.dwoss.rules.PositionType;
import eu.ggnet.dwoss.rules.TradeName;
import eu.ggnet.dwoss.util.MathUtil;
import eu.ggnet.dwoss.util.UserInfoException;
import eu.ggnet.dwoss.redtape.entity.PositionBuilder;
import eu.ggnet.dwoss.redtape.entity.Position;
import eu.ggnet.dwoss.redtape.entity.Document;
import eu.ggnet.dwoss.redtape.entity.Dossier;
import eu.ggnet.dwoss.stock.entity.LogicTransaction;
import eu.ggnet.dwoss.stock.entity.StockTransaction;
import eu.ggnet.dwoss.stock.entity.StockUnit;
import eu.ggnet.dwoss.uniqueunit.entity.UniqueUnit;
import eu.ggnet.dwoss.uniqueunit.entity.PriceType;
import eu.ggnet.dwoss.uniqueunit.entity.Product;

import java.util.*;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.embeddable.EJBContainer;
import javax.inject.Inject;
import javax.naming.NamingException;
import javax.persistence.EntityManager;

import org.junit.*;

import eu.ggnet.dwoss.configuration.GlobalConfig;
import eu.ggnet.dwoss.configuration.SystemConfig;
import eu.ggnet.dwoss.customer.api.CustomerMetaData;
import eu.ggnet.dwoss.customer.api.CustomerService;
import eu.ggnet.dwoss.customer.assist.CustomerPu;
import eu.ggnet.dwoss.customer.assist.gen.CustomerGeneratorOperation;

import eu.ggnet.dwoss.mandator.api.service.WarrantyService;

import eu.ggnet.dwoss.receipt.gen.ReceiptGeneratorOperation;
import eu.ggnet.dwoss.redtape.RedTapeAgent;
import eu.ggnet.dwoss.redtape.api.RedTapeHookService;
import eu.ggnet.dwoss.redtape.assist.RedTapePu;

import eu.ggnet.dwoss.redtape.RedTapeWorker;
import eu.ggnet.dwoss.redtape.gen.RedTapeGeneratorOperation;

import eu.ggnet.dwoss.report.ReportAgent;
import eu.ggnet.dwoss.report.assist.ReportPu;
import eu.ggnet.dwoss.report.eao.ReportLineEao;
import eu.ggnet.dwoss.report.entity.ReportLine;


import eu.ggnet.dwoss.spec.assist.SpecPu;
import eu.ggnet.dwoss.stock.StockAgent;
import eu.ggnet.dwoss.stock.assist.StockPu;
import eu.ggnet.dwoss.uniqueunit.assist.UniqueUnitPu;
import eu.ggnet.dwoss.uniqueunit.assist.UniqueUnits;
import eu.ggnet.dwoss.uniqueunit.eao.ProductEao;
import eu.ggnet.dwoss.uniqueunit.format.UniqueUnitFormater;

import eu.ggnet.dwoss.util.interactiveresult.Result;

import static eu.ggnet.dwoss.redtape.entity.Document.Condition.CANCELED;
import static eu.ggnet.dwoss.report.entity.ReportLine.SingleReferenceType.WARRANTY;
import static eu.ggnet.dwoss.rules.PositionType.*;
import static eu.ggnet.dwoss.rules.ProductGroup.COMMENTARY;
import static eu.ggnet.dwoss.rules.TradeName.HP;
import static eu.ggnet.dwoss.rules.TradeName.ONESELF;
import static org.fest.assertions.api.Assertions.*;
import static org.junit.Assert.*;

/**
 *
 * @author oliver.guenther
 */
public class RedTapeCloserOperationIT {

    class RedTapeHookStup implements RedTapeHookService {

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

        @Override
        public Result<List<Position>> addWarrantyForUnitPosition(Position p, long documentId) throws UserInfoException {
            return null;
        }
    }

    static class WarrantyServiceStup implements WarrantyService {

        @Override
        public boolean isWarranty(String partNo) {
            return WARRANTY_PART_NO.equals(partNo);
        }

        @Override
        public TradeName warrantyContractor(String partNo) {
            return ONESELF;
        }

    };

    static final String WARRANTY_PART_NO = "DEH2381234";

    private EJBContainer container;

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
    private CustomerService customerService;

    @Inject
    private ProductEao eao;

    @Inject
    private StockAgent stockAgent;

    @Inject
    private RedTapeCloserOpertaionItBean redTapeCloserOpertaionItBean;

    @Before
    public void setUp() throws NamingException {
        Map<String, Object> c = new HashMap<>();
        c.putAll(CustomerPu.CMP_IN_MEMORY);
        c.putAll(SpecPu.CMP_IN_MEMORY);
        c.putAll(UniqueUnitPu.CMP_IN_MEMORY);
        c.putAll(StockPu.CMP_IN_MEMORY);
        c.putAll(RedTapePu.CMP_IN_MEMORY);
        c.putAll(ReportPu.CMP_IN_MEMORY);
        c.putAll(SystemConfig.OPENEJB_EJB_XML_DISCOVER);
        c.putAll(SystemConfig.OPENEJB_LOG_WARN);
        container = EJBContainer.createEJBContainer(c);
        container.getContext().bind("inject", this);
    }

    @After
    public void tearDown() {
        container.close();
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
        assertFalse(receiptGenerator.makeUniqueUnits(200, true, true).isEmpty());
        assertFalse(redTapeGenerator.makeSalesDossiers(30).isEmpty());

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

    @Stateless
    public static class RedTapeCloserOpertaionItBean {

        @Inject
        private ReportLineEao reportEao;

        @Inject
        @UniqueUnits
        private EntityManager uuEm;

        public void checkReferences(long dossierId) {
            List<ReportLine> allLines = reportEao.findAll();

            List<ReportLine> collect = allLines.stream().filter((line) -> {
                return line.getPositionType().equals(PositionType.PRODUCT_BATCH)
                        && line.getDossierId() == dossierId;
            }).collect(Collectors.toList());
            assertEquals("Assert ten warranties to be present", 2, collect.size());

            for (ReportLine line : collect) {
                ReportLine reference = line.getReference(WARRANTY);
                assertFalse("Line has no unit reference " + line, reference == null);
                assertEquals("Assert equal dossier id in reference", reference.getDossierId(), dossierId);
                assertFalse("Contractor has not been set", reference.getContractor() == null);
            }
        }

        public Product makeWarrantyProduct() {
            Product p = new Product(COMMENTARY, HP, WARRANTY_PART_NO, "Warranty Product");
            uuEm.persist(p);
            return p;
        }

    }
}
