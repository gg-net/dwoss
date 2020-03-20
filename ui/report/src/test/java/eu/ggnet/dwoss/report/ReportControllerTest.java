package eu.ggnet.dwoss.report;

import eu.ggnet.dwoss.report.ui.main.ReportController;

import java.awt.GraphicsEnvironment;
import java.io.IOException;

import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author oliver.guenther
 */
public class ReportControllerTest {

    @Test
    public void testResource() {
        assertThat(ReportController.loadFxml()).isNotNull();
    }

    @Test
    public void testJavaFxFxml() throws IOException {
        if ( GraphicsEnvironment.isHeadless() ) return;
        new JFXPanel(); // Implizit start of JavaFx.
        FXMLLoader loader = new FXMLLoader(ReportController.loadFxml());
        loader.load();
        assertThat((ReportController)loader.getController()).isNotNull();
    }

}
