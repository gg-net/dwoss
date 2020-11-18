package eu.ggnet.dwoss.report.ee.itest;

import java.util.Date;

import javax.ejb.EJB;
import javax.inject.Inject;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.report.ee.ReportAgent;
import eu.ggnet.dwoss.report.ee.ViewReportResult;
import eu.ggnet.dwoss.report.ee.ReportParameter;
import eu.ggnet.dwoss.report.ee.entity.ReportLine;
import eu.ggnet.dwoss.report.ee.itest.support.ArquillianProjectArchive;
import eu.ggnet.dwoss.report.ee.itest.support.ReportLineItHelper;

import static eu.ggnet.dwoss.core.common.values.DocumentType.INVOICE;
import static eu.ggnet.dwoss.core.common.values.PositionType.UNIT;
import static eu.ggnet.dwoss.core.common.values.tradename.TradeName.ONESELF;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

/**
 *
 * @author oliver.guenther
 */
@RunWith(Arquillian.class)
public class ReportAgentPrepareReportOneLineIT extends ArquillianProjectArchive {

    @EJB
    private ReportAgent agent;

    @Inject
    private ReportLineItHelper helper;

    private final static String ID_ONE = "1";

    private final static Date D_BEFORE_1 = ReportLineItHelper.date(2010, 01, 01);

    private final static Date D1 = ReportLineItHelper.date(2010, 01, 05);

    private final static Date D_AFTER_1 = ReportLineItHelper.date(2010, 01, 31);

    private final static Date D2 = ReportLineItHelper.date(2010, 02, 05);

    @Test
    public void testOneLineInTheReport() {
        // One line, that should be in the report
        ReportLine line1 = helper.makeReportLine(D1, ONESELF, INVOICE, UNIT, ID_ONE);
        ReportParameter.Builder reportBuilder = new ReportParameter.Builder()
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

}
