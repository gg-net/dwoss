package eu.ggnet.dwoss.report.ee.itest;

import eu.ggnet.dwoss.report.ee.itest.support.ReportLineItHelper;
import eu.ggnet.dwoss.report.ee.itest.support.ArquillianProjectArchive;

import java.util.Date;

import javax.ejb.EJB;
import javax.inject.Inject;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.report.ee.ReportAgent;
import eu.ggnet.dwoss.report.ee.ReportAgent.ReportParameter;
import eu.ggnet.dwoss.report.ee.ReportAgent.ViewReportResult;
import eu.ggnet.dwoss.report.ee.entity.ReportLine;

import static eu.ggnet.dwoss.rules.DocumentType.INVOICE;
import static eu.ggnet.dwoss.rules.PositionType.UNIT;
import static eu.ggnet.dwoss.rules.TradeName.ONESELF;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

/**
 *
 * @author oliver.guenther
 */
@RunWith(Arquillian.class)
public class ReportAgentPrepareReporOneLineBeforeIT extends ArquillianProjectArchive {

    @EJB
    private ReportAgent agent;

    @Inject
    private ReportLineItHelper helper;

    private final static String ID_ONE = "1";

    private final static Date D0 = ReportLineItHelper.date(2009, 12, 05);

    private final static Date D_BEFORE_1 = ReportLineItHelper.date(2010, 01, 01);

    private final static Date D1 = ReportLineItHelper.date(2010, 01, 05);

    private final static Date D_AFTER_1 = ReportLineItHelper.date(2010, 01, 31);

    private final static Date D2 = ReportLineItHelper.date(2010, 02, 05);

    @Test
    public void testOneLineInTheReportOneBefore() {
        // One line, that should be in the report
        ReportLine line1 = helper.makeReportLine(D1, ONESELF, INVOICE, UNIT, ID_ONE);
        // Add a line, that is before.
        ReportLine line2 = helper.makeReportLine(D0, ONESELF, INVOICE, UNIT, ID_ONE);
        ReportParameter.ReportParameterBuilder reportBuilder = ReportParameter.builder()
                .reportName("Report2")
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

}
