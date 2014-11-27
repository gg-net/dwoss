package eu.ggnet.dwoss.report.eao;

import java.text.ParseException;
import java.util.*;
import java.util.Map.Entry;

import javax.persistence.*;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.*;

import eu.ggnet.dwoss.report.assist.ReportPu;
import eu.ggnet.dwoss.report.assist.gen.ReportLineGenerator;
import eu.ggnet.dwoss.report.entity.Report;
import eu.ggnet.dwoss.report.entity.ReportLine;
import eu.ggnet.dwoss.report.entity.partial.SimpleReportLine;
import eu.ggnet.dwoss.rules.*;
import eu.ggnet.dwoss.util.DateFormats;

import static eu.ggnet.dwoss.rules.DocumentType.ANNULATION_INVOICE;
import static eu.ggnet.dwoss.rules.DocumentType.INVOICE;
import static eu.ggnet.dwoss.rules.PositionType.UNIT;
import static eu.ggnet.dwoss.rules.SalesChannel.RETAILER;
import static eu.ggnet.dwoss.rules.Step.DAY;
import static eu.ggnet.dwoss.rules.TradeName.*;
import static org.apache.commons.lang3.time.DateUtils.*;
import static org.apache.commons.lang3.time.DateUtils.addDays;
import static org.apache.commons.lang3.time.DateUtils.parseDate;
import static org.junit.Assert.*;

/**
 *
 * @author oliver.guenther
 */
public class ReportLineEaoIT {

    private EntityManagerFactory emf;

    private EntityManager em;

    private final ReportLineGenerator generator = new ReportLineGenerator();

    private static Date startEarly;

    private static Date startMid;

    private static Date startFuture;

    static {
        try {
            startEarly = DateFormats.ISO.parse("2012-01-01");
            startMid = DateFormats.ISO.parse("2012-01-14");
            startFuture = DateFormats.ISO.parse("20-01-28");
        } catch (ParseException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Before
    public void setUp() {
        Map<String, Object> c = new HashMap<>();
        c.putAll(ReportPu.JPA_IN_MEMORY);
        emf = Persistence.createEntityManagerFactory(ReportPu.NAME, c);
        em = emf.createEntityManager();

    }

    @After
    public void after() {
        if ( em != null && em.isOpen() ) em.close();
        if ( emf != null && emf.isOpen() ) emf.close();
    }

    @Test
    public void testFindAllSimple() throws ParseException {
        em.getTransaction().begin();

        for (int i = 0; i < 300; i++) {
            ReportLine l = generator.makeReportLine(Arrays.asList(TradeName.DELL), startEarly, 7, Arrays.asList(PositionType.UNIT), Arrays.asList(DocumentType.INVOICE));
            em.persist(l);
        }
        em.getTransaction().commit();

        em.getTransaction().begin();
        List<ReportLine> findAll = new ReportLineEao(em).findAll();
        assertEquals(300, findAll.size());
        List<SimpleReportLine> findAll2 = new ReportLineEao(em).findAllSimple();
        assertEquals(300, findAll2.size());
        em.getTransaction().commit();
    }

    @Test
    public void testFindProductIdMissingContractorPartNo() throws ParseException {
        em.getTransaction().begin();

        final Random R = new Random();

        final long PRODUCT_ID = 10;

        final TradeName CONTRACTOR = HP;

        em.persist(make(DELL, PRODUCT_ID + 5, null)); // Different contarctor and productId
        em.persist(make(DELL, PRODUCT_ID, null)); // Different contarctor
        em.persist(make(CONTRACTOR, PRODUCT_ID + 5, null)); // Different productId
        em.persist(make(CONTRACTOR, PRODUCT_ID, "123556")); // has already a contracotrPartNo

        // Two matching Lines
        ReportLine l1 = make(CONTRACTOR, PRODUCT_ID, null);
        ReportLine l2 = make(CONTRACTOR, PRODUCT_ID, null);

        em.persist(l1);
        em.persist(l2);

        em.getTransaction().commit();

        em.getTransaction().begin();
        List<ReportLine> missing = new ReportLineEao(em).findByProductIdMissingContractorPartNo(PRODUCT_ID, CONTRACTOR);
        assertEquals(2, missing.size());
        assertTrue(missing.contains(l1));
        assertTrue(missing.contains(l2));
        em.getTransaction().commit();
    }

    @Test
    public void testFindLastReported() throws ParseException {
        em.getTransaction().begin();

        for (int i = 0; i < 300; i++) {
            ReportLine l = generator.makeReportLine(Arrays.asList(TradeName.DELL), startEarly, 7, Arrays.asList(PositionType.UNIT), Arrays.asList(DocumentType.INVOICE));
            em.persist(l);
        }
        for (int i = 0; i < 300; i++) {
            ReportLine l = generator.makeReportLine(Arrays.asList(TradeName.DELL), startMid, 7, Arrays.asList(PositionType.UNIT), Arrays.asList(DocumentType.INVOICE));
            em.persist(l);
        }
        for (int i = 0; i < 300; i++) {
            ReportLine l = generator.makeReportLine(Arrays.asList(TradeName.DELL), startMid, 7, Arrays.asList(PositionType.UNIT), Arrays.asList(DocumentType.INVOICE));
            em.persist(l);
        }

        for (int i = 0; i < 300; i++) {
            ReportLine l = generator.makeReportLine(Arrays.asList(TradeName.DELL), startFuture, 7, Arrays.asList(PositionType.UNIT), Arrays.asList(DocumentType.INVOICE));
            em.persist(l);
        }

        for (int i = 0; i < 50; i++) {
            ReportLine l = generator.makeReportLine(Arrays.asList(TradeName.HP), startMid, 7, Arrays.asList(PositionType.UNIT), Arrays.asList(DocumentType.INVOICE));
            em.persist(l);
        }

        for (int i = 0; i < 300; i++) {
            ReportLine l = generator.makeReportLine(Arrays.asList(TradeName.DELL), startMid, 7, Arrays.asList(PositionType.UNIT), Arrays.asList(DocumentType.INVOICE));
            em.persist(l);
            Report r = new Report("Report Test " + l.getId(), DELL, addDays(l.getReportingDate(), -1), addDays(l.getReportingDate(), 1));
            r.add(l);
            em.persist(r);
        }
        em.getTransaction().commit();
        Date max = DateFormats.ISO.parse("2012-01-20");

        em.getTransaction().begin();
        Date d2 = new ReportLineEao(em).findLastReported();
        assertTrue("Date " + d2 + " is not the expected " + max, DateUtils.isSameDay(max, d2));
        em.getTransaction().commit();
    }

    @Test
    public void testFindByUniqueUnitId() throws ParseException {
        String ISO = "yyyy-MM-dd";
        Date d1 = DateUtils.parseDate("2010-01-01", ISO);

        ReportLine line1 = new ReportLine("PersName1", "This is a TestDescription1", 137, "DW0037", 3, "RE0008", PositionType.UNIT,
                DocumentType.INVOICE, 2, 1, 0.19, 100, 119, 37, "This is the Invoice Address", "123", 2, "SERIALNUMBER", new Date(), 3, "PArtNo", "test@gg-net.de");
        line1.setReportingDate(DateUtils.parseDate("2009-01-01", ISO));
        line1.setUniqueUnitId(10);

        ReportLine line2 = new ReportLine("PersName2", "This is a TestDescription2", 1337, "DW0013", 3, "RE001", PositionType.UNIT,
                DocumentType.INVOICE, 2, 1, 0.19, 100, 119, 37, "This is the Invoice Address", "123", 2, "SERIALNUMBER", new Date(), 3, "PArtNo", "test@gg-net.de");
        line2.setReportingDate(DateUtils.parseDate("2009-07-10", ISO));
        line2.setUniqueUnitId(10);

        ReportLine line3 = new ReportLine("PersName3", "This is a TestDescription3", 13, "DW1337", 3, "RE0003", PositionType.UNIT,
                DocumentType.INVOICE, 2, 1, 0.19, 100, 119, 37, "This is the Invoice Address", "123", 2, "SERIALNUMBER", new Date(), 3, "PArtNo", "test@gg-net.de");
        line3.setReportingDate(d1);

        em.getTransaction().begin();
        em.persist(line1);
        em.persist(line2);
        em.persist(line3);
        em.getTransaction().commit();

        em.getTransaction().begin();
        List<ReportLine> rls = new ReportLineEao(em).findByUniqueUnitId(10);
        assertEquals(2, rls.size());
        rls = new ReportLineEao(em).findByUniqueUnitId(1);
        assertTrue(rls.isEmpty());
        em.getTransaction().commit();
    }

    @Test
    public void testFindUnreported() throws ParseException {
        em.getTransaction().begin();

        for (int i = 0; i < 300; i++) {
            ReportLine l = generator.makeReportLine(Arrays.asList(TradeName.DELL), startEarly, 7, Arrays.asList(PositionType.UNIT), Arrays.asList(DocumentType.INVOICE));
            em.persist(l);
        }
        for (int i = 0; i < 300; i++) {
            ReportLine l = generator.makeReportLine(Arrays.asList(TradeName.DELL), startMid, 7, Arrays.asList(PositionType.UNIT), Arrays.asList(DocumentType.INVOICE));
            em.persist(l);
        }
        for (int i = 0; i < 300; i++) {
            ReportLine l = generator.makeReportLine(Arrays.asList(TradeName.DELL), startMid, 7, Arrays.asList(PositionType.COMMENT), Arrays.asList(DocumentType.INVOICE));
            em.persist(l);
        }

        for (int i = 0; i < 300; i++) {
            ReportLine l = generator.makeReportLine(Arrays.asList(TradeName.DELL), startFuture, 7, Arrays.asList(PositionType.UNIT), Arrays.asList(DocumentType.INVOICE));
            em.persist(l);
        }

        for (int i = 0; i < 50; i++) {
            ReportLine l = generator.makeReportLine(Arrays.asList(TradeName.HP), startMid, 7, Arrays.asList(PositionType.UNIT), Arrays.asList(DocumentType.INVOICE));
            em.persist(l);
        }

        for (int i = 0; i < 300; i++) {
            ReportLine l = generator.makeReportLine(Arrays.asList(TradeName.DELL), startMid, 7, Arrays.asList(PositionType.UNIT), Arrays.asList(DocumentType.INVOICE));
            em.persist(l);
            Report r = new Report("Report Test " + l.getId(), DELL, DateUtils.addDays(l.getReportingDate(), -1), DateUtils.addDays(l.getReportingDate(), 1));
            r.add(l);
            em.persist(r);
        }
        em.getTransaction().commit();
        em.getTransaction().begin();
        List<ReportLine> rls = new ReportLineEao(em).findUnreported(DELL, DateFormats.ISO.parse("2012-01-14"), DateFormats.ISO.parse("2012-01-27"));
        assertEquals(600, rls.size());// Units, Comments, ShipmentCost
        rls = new ReportLineEao(em).findUnreported(DELL, DateFormats.ISO.parse("2012-01-14"), DateFormats.ISO.parse("2012-01-27"), PositionType.UNIT, PositionType.UNIT_ANNEX);
        assertEquals(300, rls.size());
        em.getTransaction().commit();
    }

    @Test
    public void testFindUnreportedUnit() throws ParseException {
        em.getTransaction().begin();

        for (int i = 0; i < 300; i++) {
            ReportLine l = generator.makeReportLine(Arrays.asList(TradeName.DELL), startEarly, 7, Arrays.asList(PositionType.UNIT), Arrays.asList(DocumentType.INVOICE));
            em.persist(l);
        }
        for (int i = 0; i < 300; i++) {
            ReportLine l = generator.makeReportLine(Arrays.asList(TradeName.DELL), startMid, 7, Arrays.asList(PositionType.UNIT), Arrays.asList(DocumentType.INVOICE));
            em.persist(l);
        }
        for (int i = 0; i < 300; i++) {
            ReportLine l = generator.makeReportLine(Arrays.asList(TradeName.DELL), startMid, 7, Arrays.asList(PositionType.COMMENT), Arrays.asList(DocumentType.INVOICE));
            em.persist(l);
        }

        for (int i = 0; i < 300; i++) {
            ReportLine l = generator.makeReportLine(Arrays.asList(TradeName.DELL), startFuture, 7, Arrays.asList(PositionType.UNIT), Arrays.asList(DocumentType.INVOICE));
            em.persist(l);
        }

        for (int i = 0; i < 50; i++) {
            ReportLine l = generator.makeReportLine(Arrays.asList(TradeName.HP), startMid, 7, Arrays.asList(PositionType.UNIT), Arrays.asList(DocumentType.INVOICE));
            em.persist(l);
        }

        for (int i = 0; i < 300; i++) {
            ReportLine l = generator.makeReportLine(Arrays.asList(TradeName.DELL), startMid, 7, Arrays.asList(PositionType.UNIT), Arrays.asList(DocumentType.INVOICE));
            em.persist(l);
            Report r = new Report("Report Test " + l.getId(), DELL, DateUtils.addDays(l.getReportingDate(), -1), DateUtils.addDays(l.getReportingDate(), 1));
            r.add(l);
            em.persist(r);
        }
        em.getTransaction().commit();
        ReportLineEao reportLineEao = new ReportLineEao(em);
        em.getTransaction().begin();
        List<ReportLine> rls = reportLineEao.findUnreportedUnits(DELL, DateFormats.ISO.parse("2012-01-14"), DateFormats.ISO.parse("2012-01-27"));
        assertEquals(300, rls.size());
        em.getTransaction().commit();

    }

    @Test
    public void testFindFromTillUnreportedUnit() throws ParseException {
        String ISO = "yyyy-MM-dd";

        ReportLine line1 = new ReportLine("PersName1", "This is a TestDescription1", 137, "DW0037", 3, "RE0008", PositionType.UNIT,
                DocumentType.INVOICE, 2, 1, 0.19, 100, 119, 37, "This is the Invoice Address", "123", 2, "SERIALNUMBER", new Date(), 3, "PArtNo", "test@gg-net.de");
        line1.setReportingDate(DateUtils.parseDate("2009-01-01", ISO));
        line1.setUniqueUnitId(10);
        line1.setContractor(TradeName.DELL);

        ReportLine line2 = new ReportLine("PersName2", "This is a TestDescription2", 1337, "DW0013", 3, "RE001", PositionType.UNIT,
                DocumentType.INVOICE, 2, 1, 0.19, 100, 119, 37, "This is the Invoice Address", "123", 2, "SERIALNUMBER", new Date(), 3, "PArtNo", "test@gg-net.de");
        line2.setReportingDate(DateUtils.parseDate("2010-01-01", ISO));
        line2.setUniqueUnitId(10);
        line2.setContractor(TradeName.DELL);

        ReportLine line3 = new ReportLine("PersName3", "This is a TestDescription3", 13, "DW1337", 3, "RE0003", PositionType.UNIT,
                DocumentType.INVOICE, 2, 1, 0.19, 100, 119, 37, "This is the Invoice Address", "123", 2, "SERIALNUMBER", new Date(), 3, "PArtNo", "test@gg-net.de");
        line3.setReportingDate(DateUtils.parseDate("2011-01-01", ISO));
        line3.setContractor(TradeName.DELL);

        ReportLine line4 = new ReportLine("PersName3", "This is a TestDescription3", 13, "DW1337", 3, "RE0003", PositionType.UNIT,
                DocumentType.INVOICE, 2, 1, 0.19, 100, 119, 37, "This is the Invoice Address", "123", 2, "SERIALNUMBER", new Date(), 3, "PArtNo", "test@gg-net.de");
        line4.setReportingDate(DateUtils.parseDate("2012-01-01", ISO));
        line4.setContractor(TradeName.OTTO);

        Report r = new Report("KW201301", DELL, DateUtils.parseDate("2009-01-01", ISO), DateUtils.parseDate("2009-01-07", ISO));
        r.add(line3);

        em.getTransaction().begin();
        em.persist(line1);
        em.persist(line2);
        em.persist(line3);
        em.persist(line4);
        em.persist(r);
        em.getTransaction().commit();
        ReportLineEao reportLineEao = new ReportLineEao(em);
        em.getTransaction().begin();
        List<ReportLine> rls = reportLineEao.findUnreportedUnits(DELL, DateUtils.parseDate("2008-12-31", ISO), DateUtils.parseDate("2010-12-31", ISO));
        assertEquals(2, rls.size());
        rls = reportLineEao.findUnreportedUnits(OTTO, DateUtils.parseDate("2009-12-31", ISO), DateUtils.parseDate("2013-12-31", ISO));
        assertEquals(1, rls.size());
        em.getTransaction().commit();

    }

    @Test
    public void testRevenue() throws ParseException {
        ReportLineEao reportLineEao = new ReportLineEao(em);

        em.getTransaction().begin();
        em.persist(make("2010-06-01", INVOICE, UNIT, 100));
        em.persist(make("2010-06-02", INVOICE, UNIT, 100));
        em.persist(make("2010-06-03", INVOICE, UNIT, 100));
        em.persist(make("2010-06-04", INVOICE, UNIT, 100));
        em.persist(make("2010-06-04", ANNULATION_INVOICE, UNIT, -50));
        em.persist(make("2010-06-05", INVOICE, UNIT, 100));

        em.persist(make("2010-07-05", INVOICE, UNIT, 100));
        em.getTransaction().commit();

        // Month and count
        em.getTransaction().begin();
        NavigableMap<Date, Revenue> result = reportLineEao.revenueByPositionTypesAndDate(Arrays.asList(UNIT), parseDate("2010-06-01", "yyyy-MM-dd"), parseDate("2010-06-05", "yyyy-MM-dd"), DAY, true);
        for (Entry<Date, Revenue> e : result.entrySet()) {
            assertEquals(100.0, e.getValue().sumBy(INVOICE), 0.0001);
//            System.out.println(DateFormats.ISO.format(e.getKey()) + " - I:" + e.getValue().sum(INVOICE) + " A:" + e.getValue().sum(ANNULATION_INVOICE) + " S:" + e.getValue().sum());
        }
        result = reportLineEao.revenueByPositionTypesAndDate(Arrays.asList(UNIT), parseDate("2010-06-01", "yyyy-MM-dd"), parseDate("2010-06-05", "yyyy-MM-dd"), Step.MONTH, true);
        assertEquals(1, result.size());
        assertEquals(500.0, result.firstEntry().getValue().sumBy(INVOICE), 0.0001);
        assertEquals(-50.0, result.firstEntry().getValue().sumBy(ANNULATION_INVOICE), 0.0001);
        assertEquals(450.0, result.firstEntry().getValue().sum(), 0.0001);

//        result = reportLineEao.revenueByPositionTypesAndDate(Arrays.asList(UNIT), parseDate("2010-06-01", "yyyy-MM-dd"), parseDate("2010-07-30", "yyyy-MM-dd"), DAY);
//        for (Entry<Date, Revenue> e : result.entrySet()) {
//            System.out.println(DateFormats.ISO.format(e.getKey()) + " - I:" + e.getValue().sum(INVOICE) + " A:" + e.getValue().sum(ANNULATION_INVOICE) + " S:" + e.getValue().sum());
//        }
        System.out.println(" -- ");
        result = reportLineEao.revenueByPositionTypesAndDate(Arrays.asList(UNIT), parseDate("2010-06-01", "yyyy-MM-dd"), parseDate("2010-07-30", "yyyy-MM-dd"), Step.MONTH, true);
        assertEquals(2, result.size());
        assertEquals(500.0, result.firstEntry().getValue().sumBy(INVOICE), 0.0001);
        assertEquals(-50.0, result.firstEntry().getValue().sumBy(ANNULATION_INVOICE), 0.0001);
        assertEquals(450.0, result.firstEntry().getValue().sum(), 0.0001);
        assertEquals(100.0, result.lastEntry().getValue().sumBy(INVOICE), 0.0001);
        assertEquals(0.0, result.lastEntry().getValue().sumBy(ANNULATION_INVOICE), 0.0001);
        assertEquals(100.0, result.lastEntry().getValue().sum(), 0.0001);

        for (Step step : Step.values()) {
            // Shortcut to test all steps.
//            System.out.println("-----");
//            System.out.println(step);
//            System.out.println("-----");
            result = reportLineEao.revenueByPositionTypesAndDate(Arrays.asList(UNIT), parseDate("2010-01-01", "yyyy-MM-dd"), parseDate("2010-12-31", "yyyy-MM-dd"), step, true);
//            for (Entry<Date, Revenue> e : result.entrySet()) {
//                System.out.println(step.format(e.getKey()) + "|" + DateFormats.ISO.format(e.getKey()) + " - " + e.getValue());
//            }
        }

        em.getTransaction().commit();

    }

    private ReportLine make(String isoDate, DocumentType docType, PositionType posType, double price) throws ParseException {
        Date date = DateUtils.parseDate(isoDate, "yyyy-MM-dd");

        ReportLine line = ReportLine.builder()
                .name("PositionName")
                .description("PositionDescription")
                .dossierId(1)
                .dossierIdentifier("DW1")
                .documentType(docType)
                .documentId(1)
                .documentIdentifier("RE1")
                .positionType(posType)
                .customerId(1)
                .amount(1)
                .tax(0.19)
                .price(price)
                .afterTaxPrice(price * 0.19)
                .uniqueUnitId(1).build();
        line.setActual(date);
        line.setReportingDate(date);
        line.setContractor(DELL);
        line.setSalesChannel(RETAILER);
        return line;
    }

    private ReportLine make(TradeName contractor, long productId, String contractorPartNo) {
        ReportLine line = generator.makeReportLine();
        line.setPositionType(PositionType.UNIT);
        line.setContractor(contractor);
        line.setProductId(productId);
        line.setContractorPartNo(contractorPartNo);
        return line;
    }
}
