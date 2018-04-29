package tryout;

import eu.ggnet.saft.core.Dl;
import eu.ggnet.saft.core.Ui;
import eu.ggnet.saft.core.UiCore;

import java.util.*;

import javax.swing.JLabel;

import eu.ggnet.dwoss.report.ee.ReportAgent.ReportParameter;
import eu.ggnet.dwoss.report.ee.ReportAgent.ViewReportResult;
import eu.ggnet.dwoss.report.ee.assist.gen.ReportLineGenerator;
import eu.ggnet.dwoss.report.ee.entity.ReportLine;
import eu.ggnet.dwoss.report.ui.main.ReportController;
import eu.ggnet.dwoss.common.api.values.TradeName;
import eu.ggnet.saft.core.dl.RemoteLookup;

import static eu.ggnet.dwoss.report.ee.ReportAgent.ViewReportResult.Type.INVOICED;
import static eu.ggnet.dwoss.report.ee.ReportAgent.ViewReportResult.Type.REPAYMENTS;

/**
 *
 * @author pascal.perau
 */
public class ReportViewTryout {

    public static void main(String[] args) {
        Dl.local().add(RemoteLookup.class, new RemoteLookup() {
            @Override
            public <T> boolean contains(Class<T> clazz) {
                return false;
            }

            @Override
            public <T> T lookup(Class<T> clazz) {
                return null;
            }
        });

        TradeName tradeName = TradeName.FUJITSU;
        UiCore.startSwing(() -> new JLabel("Main Applikation"));

        EnumMap<ViewReportResult.Type, NavigableSet<ReportLine>> lines = new EnumMap<>(ViewReportResult.Type.class);
        ReportLineGenerator op = new ReportLineGenerator();
        NavigableSet<ReportLine> invoicedLines = new TreeSet<>();
        NavigableSet<ReportLine> repayedLinesLines = new TreeSet<>();
        for (int i = 0; i < 30; i++) {
            invoicedLines.add(op.makeReportLine(Arrays.asList(tradeName), new Date(), i));
            repayedLinesLines.add(op.makeReportLine(Arrays.asList(tradeName), new Date(), i));

        }
        lines.put(INVOICED, invoicedLines);
        lines.put(REPAYMENTS, repayedLinesLines);
        ViewReportResult result = new ViewReportResult(lines,
                ReportParameter.builder()
                        .reportName("JUnit " + tradeName + " Report")
                        .contractor(tradeName)
                        .start(new Date())
                        .end(new Date())
                        .build());

        Ui.build().fxml().show(() -> new ReportController.In(result, false), ReportController.class);
    }

}
