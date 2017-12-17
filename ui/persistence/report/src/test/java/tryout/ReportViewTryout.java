package tryout;

import java.util.*;

import javax.swing.JLabel;

import eu.ggnet.dwoss.report.ReportAgent.ReportParameter;
import eu.ggnet.dwoss.report.ReportAgent.ViewReportResult;
import eu.ggnet.dwoss.report.ui.main.ReportController;
import eu.ggnet.dwoss.report.assist.gen.ReportLineGenerator;
import eu.ggnet.dwoss.report.entity.ReportLine;
import eu.ggnet.dwoss.rules.TradeName;
import eu.ggnet.saft.Ui;
import eu.ggnet.saft.UiCore;

import static eu.ggnet.dwoss.report.ReportAgent.ViewReportResult.Type.INVOICED;
import static eu.ggnet.dwoss.report.ReportAgent.ViewReportResult.Type.REPAYMENTS;

/**
 *
 * @author pascal.perau
 */
public class ReportViewTryout {

    public static void main(String[] args) {
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

        Ui.fxml().show(() -> new ReportController.In(result, false), ReportController.class);
    }

}
