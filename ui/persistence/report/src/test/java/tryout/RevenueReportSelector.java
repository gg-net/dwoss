package tryout;

import org.junit.Test;

import eu.ggnet.dwoss.report.RevenueReportSelectorPane;
import eu.ggnet.dwoss.util.OkCancelStage;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;

/**
 *
 * @author oliver.guenther
 */
public class RevenueReportSelector {

    private boolean complete = false;

    @Test
    public void tryout() throws InterruptedException {
        new JFXPanel();    // To start the platform

        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                RevenueReportSelectorPane selector = new RevenueReportSelectorPane();
                OkCancelStage<RevenueReportSelectorPane> stage = new OkCancelStage<>("Auswählen", selector);
                stage.showAndWait();
                System.out.println("OK=" + stage.isOk());
                System.out.println(selector);
                complete = true;
            }
        });

        while (!complete) {
            Thread.sleep(500);
        }

    }
}
