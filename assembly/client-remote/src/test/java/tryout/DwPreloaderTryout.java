package tryout;

import java.util.concurrent.CountDownLatch;

import org.junit.Test;

import eu.ggnet.dwoss.assembly.remote.DwPreloader;

import javafx.application.Platform;
import javafx.application.Preloader.StateChangeNotification;
import javafx.embed.swing.JFXPanel;
import javafx.stage.*;

/**
 *
 * @author oliver.guenther
 */
public class DwPreloaderTryout {

    @Test
    public void tryoutPreloader() throws InterruptedException {
        JFXPanel p = new JFXPanel();

        final CountDownLatch cdl = new CountDownLatch(1);
        DwPreloader pre = new DwPreloader();
        Platform.runLater(() -> {
            try {
                Stage stage = new Stage();
                stage.initStyle(StageStyle.UNDECORATED);
                pre.start(stage);
                cdl.countDown();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        cdl.await();
        Thread.sleep(2000);

        final CountDownLatch cdl2 = new CountDownLatch(1);
        Platform.runLater(() -> {
            pre.handleStateChangeNotification(new StateChangeNotification(StateChangeNotification.Type.BEFORE_START));
            cdl2.countDown();
        });
        cdl2.await();
        Thread.sleep(2000);

    }

}
