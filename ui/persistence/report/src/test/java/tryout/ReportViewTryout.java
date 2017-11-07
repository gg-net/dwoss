package tryout;

import java.io.IOException;
import java.util.*;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import org.junit.Test;

import eu.ggnet.dwoss.report.ReportAgent.ReportParameter;
import eu.ggnet.dwoss.report.ReportAgent.ViewReportResult;
import eu.ggnet.dwoss.report.ReportController;
import eu.ggnet.dwoss.report.assist.gen.ReportLineGenerator;
import eu.ggnet.dwoss.report.entity.ReportLine;
import eu.ggnet.dwoss.rules.TradeName;

import static eu.ggnet.dwoss.report.ReportAgent.ViewReportResult.Type.INVOICED;
import static eu.ggnet.dwoss.report.ReportAgent.ViewReportResult.Type.REPAYMENTS;

/**
 *
 * @author pascal.perau
 */
public class ReportViewTryout {

    private TradeName tradeName = TradeName.FUJITSU;

    @Test
    public void show() {
        TryoutUtil.initAndShow();

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

        new JFXPanel();
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(ReportController.loadFxml());
                Stage stage = new Stage();
                stage.setTitle("Report Tryout");
                AnchorPane page = (AnchorPane)loader.load();
                loader.<ReportController>getController().initReportData(result, false);
                Scene scene = new Scene(page, Color.ALICEBLUE);
                stage.setScene(scene);
                stage.showAndWait();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        TryoutUtil.waitForClose();
    }

}
