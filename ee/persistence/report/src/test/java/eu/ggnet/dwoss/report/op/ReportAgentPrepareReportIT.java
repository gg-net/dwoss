package eu.ggnet.dwoss.report.op;

import eu.ggnet.dwoss.rules.TradeName;
import eu.ggnet.dwoss.rules.PositionType;
import eu.ggnet.dwoss.rules.DocumentType;
import eu.ggnet.dwoss.report.assist.Reports;
import eu.ggnet.dwoss.report.assist.gen.ReportLineGenerator;
import eu.ggnet.dwoss.report.assist.ReportPu;

import java.util.*;

import javax.ejb.Stateless;
import javax.ejb.embeddable.EJBContainer;
import javax.inject.Inject;
import javax.naming.NamingException;
import javax.persistence.EntityManager;

import org.junit.*;

import eu.ggnet.dwoss.configuration.SystemConfig;
import eu.ggnet.dwoss.report.ReportAgent;
import eu.ggnet.dwoss.report.ReportAgent.ReportParameter;
import eu.ggnet.dwoss.report.ReportAgent.ViewReportResult;
import eu.ggnet.dwoss.report.entity.ReportLine;

import static eu.ggnet.dwoss.rules.DocumentType.INVOICE;
import static eu.ggnet.dwoss.rules.PositionType.UNIT;
import static eu.ggnet.dwoss.rules.TradeName.ONESELF;
import static java.time.LocalDate.of;
import static java.time.ZoneId.systemDefault;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

/**
 *
 * @author oliver.guenther
 */
public class ReportAgentPrepareReportIT {

    private EJBContainer container;

    @Inject
    private ReportAgent agent;

    @Inject
    private ReportAgentPrepareReportHelper h;

    private final static String ID_ONE = "1";

    private final static Date D0 = date(2009, 12, 05);

    private final static Date D_BEFORE_1 = date(2010, 01, 01);

    private final static Date D1 = date(2010, 01, 05);

    private final static Date D_AFTER_1 = date(2010, 01, 31);

    private final static Date D2 = date(2010, 02, 05);

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
    public void testOneLineInTheReport() {
        // One line, that should be in the report
        ReportLine line1 = h.makeReportLine(D1, ONESELF, INVOICE, UNIT, ID_ONE);
        ReportParameter.ReportParameterBuilder reportBuilder = ReportParameter.builder()
                .reportName("Report1")
                .contractor(ONESELF)
                .start(D_BEFORE_1)
                .end(D_AFTER_1);
        ViewReportResult report = agent.prepareReport(reportBuilder.build(), false);
        assertThat(report.getAllLines().size(), is(equalTo(1)));
        assertThat(report.getAllLines(), hasItem(line1));
        assertThat(report.getLines().keySet(), hasItem(ViewReportResult.Type.INVOICED));
        assertThat(report.getLines().get(ViewReportResult.Type.INVOICED), hasItem(line1));
    }

    @Test
    public void testOneLineInTheReportOneBefore() {
        // One line, that should be in the report
        ReportLine line1 = h.makeReportLine(D1, ONESELF, INVOICE, UNIT, ID_ONE);
        // Add a line, that is before.
        ReportLine line2 = h.makeReportLine(D0, ONESELF, INVOICE, UNIT, ID_ONE);
        ReportParameter.ReportParameterBuilder reportBuilder = ReportParameter.builder()
                .reportName("Report1")
                .contractor(ONESELF)
                .start(D_BEFORE_1)
                .end(D_AFTER_1);
        ViewReportResult report = agent.prepareReport(reportBuilder.build(), false);
        assertThat(report.getAllLines().size(), is(equalTo(1)));
        assertThat(report.getAllLines(), hasItem(line1));
        assertThat(report.getLines().get(ViewReportResult.Type.INVOICED), hasItem(line1));
        assertThat(report.getAllLines(), not(hasItem(line2)));

        // Now it should be in here
        report = agent.prepareReport(reportBuilder.build(), true);
        assertThat(report.getAllLines().size(), is(equalTo(2)));
        assertThat(report.getAllLines(), hasItems(line1, line2));
        assertThat(report.getLines().get(ViewReportResult.Type.INVOICED), hasItems(line1, line2));
    }

    @Test
    public void testOneLineInTheReportOneAfter() {
        // One line, that should be in the report
        ReportLine line1 = h.makeReportLine(D1, ONESELF, INVOICE, UNIT, ID_ONE);
        // Add a line, that is before.
        ReportLine line2 = h.makeReportLine(D2, ONESELF, INVOICE, UNIT, ID_ONE);
        ReportParameter.ReportParameterBuilder reportBuilder = ReportParameter.builder()
                .reportName("Report1")
                .contractor(ONESELF)
                .start(D_BEFORE_1)
                .end(D_AFTER_1);
        ViewReportResult report = agent.prepareReport(reportBuilder.build(), false);
        assertThat(report.getAllLines().size(), is(equalTo(1)));
        assertThat(report.getAllLines(), hasItem(line1));
        assertThat(report.getLines().get(ViewReportResult.Type.INVOICED), hasItem(line1));
        assertThat(report.getAllLines(), not(hasItem(line2)));
    }

    @Stateless
    public static class ReportAgentPrepareReportHelper {

        @Inject
        @Reports
        private EntityManager em;

        private final ReportLineGenerator gen = new ReportLineGenerator();

        public ReportLine makeReportLine(Date reportDate, TradeName contractor, DocumentType doc, PositionType pos, String refurbishId) {
            ReportLine r = gen.makeReportLine();
            r.setReportingDate(reportDate);
            r.setContractor(contractor);
            r.setDocumentType(doc);
            r.setPositionType(pos);
            r.setRefurbishId(refurbishId);
            em.persist(r);
            return r;
        }

    }

    public final static Date date(int year, int month, int day) {
        return Date.from(of(year, month, day).atStartOfDay(systemDefault()).toInstant());
    }
}
