package eu.ggnet.dwoss.report;

import eu.ggnet.dwoss.report.ReportAgent;

import java.text.ParseException;
import java.util.*;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.embeddable.EJBContainer;
import javax.inject.Inject;
import javax.naming.NamingException;
import javax.persistence.EntityManager;

import org.junit.*;

import eu.ggnet.dwoss.configuration.SystemConfig;
import eu.ggnet.dwoss.report.ReportAgent.SearchParameter;
import eu.ggnet.dwoss.report.assist.ReportPu;
import eu.ggnet.dwoss.report.assist.Reports;
import eu.ggnet.dwoss.report.assist.gen.ReportLineGenerator;
import eu.ggnet.dwoss.report.entity.Report;
import eu.ggnet.dwoss.report.entity.ReportLine;
import eu.ggnet.dwoss.report.entity.partial.SimpleReportLine;

import eu.ggnet.dwoss.util.DateFormats;

import static eu.ggnet.dwoss.rules.DocumentType.*;
import static eu.ggnet.dwoss.rules.PositionType.*;
import static eu.ggnet.dwoss.rules.TradeName.*;
import static java.util.Arrays.asList;
import static org.junit.Assert.*;

/**
 *
 * @author oliver.guenther
 */
public class ReportAgentIT {

    private final static Date D0 = parse("2012-01-01");

    private final static Date D1 = parse("2013-01-01");

    private final static Date D2 = parse("2013-01-15");

    private EJBContainer container;

    @EJB
    private ReportAgent agent;

    @Inject
    private Helper helper;

    @Before
    public void setUp() throws NamingException {
        Map<String, Object> c = new HashMap<>();
        c.putAll(ReportPu.CMP_IN_MEMORY);
        c.putAll(SystemConfig.OPENEJB_EJB_XML_DISCOVER);
        c.putAll(SystemConfig.OPENEJB_LOG_TESTING);
        container = EJBContainer.createEJBContainer(c);
        container.getContext().bind("inject", this);
    }

    @After
    public void tearDown() {
        container.close();
    }

    @Test
    public void testDanglingCloser() {
        long idDangling = helper.prepareDangling();
        List<ReportLine> lines = agent.findAllEager(ReportLine.class);
        assertEquals("Size of prepared elements has changed, verifiy Test expectations", 6, lines.size());

        List<Report> reports = agent.findAll(Report.class);
        assertEquals("Should only have one report", 1, reports.size());

        Set<ReportLine> attached = agent.attachDanglingComplaints(LENOVO, D2);
        assertEquals("Unexpected ammount of dangling complaints", 1, attached.size());
        assertEquals("Unexpected ReportLine.id of dangling complaint", idDangling, attached.iterator().next().getId());

        Set<ReportLine> unreported2 = agent.prepareReport(
                ReportAgent.ReportParameter.builder().contractor(LENOVO).start(D0).end(D2).reportName("A Report").build(),
                true).getAllLines();
        assertNull("The unreported lines does still contain the dangling complaint", filter(unreported2, idDangling));

        NavigableSet<ReportLine> reportlines = agent.findReportResult(reports.get(0).getId()).getAllLines();
        assertNotNull("The original report does not contain the dangling complaint", filter(reportlines, idDangling));

    }

    @Test
    public void testSearch() {
        String refurbishId = helper.prepareSearch();

        long count = agent.count(new SearchParameter(refurbishId));
        assertTrue(count > 1);

        List<SimpleReportLine> find = agent.findSimple(new SearchParameter(refurbishId), 0, 100);
        assertEquals(count, find.size());
    }

    @Stateless
    public static class Helper {

        @Inject
        @Reports
        private EntityManager em;

        private final ReportLineGenerator generator = new ReportLineGenerator();

        /**
         * Generates some random Data, but ensures, that at least one line has the characteristic of a Dangling Complaint.
         * <p>
         * @return some random Data, but ensures, that at least one line has the characteristic of a Dangling Complaint.
         */
        public long prepareDangling() {
            ReportLine invoice1 = generator.makeReportLine(asList(LENOVO), D1, 5, asList(UNIT), asList(INVOICE));
            ReportLine invoice2 = generator.makeReportLine(asList(LENOVO), D1, 5, asList(UNIT), asList(INVOICE));
            ReportLine annulationInvoiceFor2 = generator.makeReportLine(asList(LENOVO), D1, 5, asList(UNIT), asList(ANNULATION_INVOICE));
            annulationInvoiceFor2.setRefurbishId(invoice2.getRefurbishId());
            annulationInvoiceFor2.setUniqueUnitId(invoice2.getUniqueUnitId());
            invoice2.add(annulationInvoiceFor2);

            ReportLine invoice3 = generator.makeReportLine(asList(LENOVO), D1, 5, asList(UNIT), asList(INVOICE));
            ReportLine invoice4 = generator.makeReportLine(asList(LENOVO), D1, 5, asList(UNIT), asList(INVOICE));
            em.persist(invoice1);
            em.persist(invoice2);
            em.persist(annulationInvoiceFor2);
            em.persist(invoice3);
            em.persist(invoice4);
            Report r = new Report("Lenovo", LENOVO, D1, D2);
            r.add(invoice1);
            r.add(invoice2);
            r.add(annulationInvoiceFor2);
            em.persist(r);

            ReportLine danglingComplaintFor2 = generator.makeReportLine(asList(LENOVO), D1, 5, asList(UNIT), asList(COMPLAINT));
            danglingComplaintFor2.setRefurbishId(invoice2.getRefurbishId());
            danglingComplaintFor2.setUniqueUnitId(invoice2.getUniqueUnitId());
            invoice2.add(danglingComplaintFor2);
            annulationInvoiceFor2.add(danglingComplaintFor2);
            em.persist(danglingComplaintFor2);
            return danglingComplaintFor2.getId();
        }

        /**
         * Generates some reportlines. The returned refurbishId has references, but with different refurbishIds.
         * <p>
         * @return refurbishId has references, but with different refurbishIds.
         */
        public String prepareSearch() {
            ReportLine invoice1 = generator.makeReportLine(asList(LENOVO), D1, 5, asList(UNIT), asList(INVOICE));
            ReportLine invoice2 = generator.makeReportLine(asList(LENOVO), D1, 5, asList(UNIT), asList(INVOICE));
            ReportLine annulationInvoiceFor2 = generator.makeReportLine(asList(LENOVO), D1, 5, asList(UNIT), asList(ANNULATION_INVOICE));
            annulationInvoiceFor2.setRefurbishId(invoice2.getRefurbishId());
            annulationInvoiceFor2.setUniqueUnitId(invoice2.getUniqueUnitId());
            invoice2.add(annulationInvoiceFor2);

            ReportLine invoice3 = generator.makeReportLine(asList(LENOVO), D1, 5, asList(UNIT), asList(INVOICE));
            ReportLine invoice4 = generator.makeReportLine(asList(LENOVO), D1, 5, asList(UNIT), asList(INVOICE));
            ReportLine complaintFor2 = generator.makeReportLine(asList(LENOVO), D1, 5, asList(UNIT), asList(COMPLAINT));
            complaintFor2.setRefurbishId(invoice2.getRefurbishId());
            complaintFor2.setUniqueUnitId(invoice2.getUniqueUnitId());
            invoice2.add(complaintFor2);
            annulationInvoiceFor2.add(complaintFor2);

            ReportLine differentFor2 = generator.makeReportLine(asList(LENOVO), D1, 5, asList(UNIT), asList(INVOICE));
            differentFor2.setUniqueUnitId(invoice2.getUniqueUnitId());
            invoice2.add(differentFor2);
            ReportLine different2For2 = generator.makeReportLine(asList(LENOVO), D1, 5, asList(UNIT), asList(INVOICE));
            different2For2.setUniqueUnitId(invoice2.getUniqueUnitId());
            invoice2.add(different2For2);

            em.persist(invoice1);
            em.persist(invoice2);
            em.persist(annulationInvoiceFor2);
            em.persist(invoice3);
            em.persist(invoice4);
            em.persist(differentFor2);
            em.persist(different2For2);

            return invoice2.getRefurbishId();
        }

    }

    private ReportLine filter(Collection<ReportLine> lines, long id) {
        for (ReportLine line : lines) {
            if ( line.getId() == id ) return line;
        }
        return null;
    }

    private static Date parse(String dateString) {
        try {
            return DateFormats.ISO.parse(dateString);
        } catch (ParseException ex) {
            throw new RuntimeException(ex);
        }
    }
}
