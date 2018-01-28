package eu.ggnet.dwoss.rights;

import eu.ggnet.dwoss.rights.OperatorManagmentController;

import java.awt.GraphicsEnvironment;
import java.io.IOException;

import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author pascal.perau
 */
public class OperatorManagmentControllerTest {

    @Test
    public void testResource() {
        assertThat(OperatorManagmentController.loadFxml()).isNotNull();
    }

    @Test
    public void testJavaFxFxml() throws IOException {
        if ( GraphicsEnvironment.isHeadless() ) return;
        new JFXPanel(); // Implizit start of JavaFx.
        FXMLLoader loader = new FXMLLoader(OperatorManagmentController.loadFxml());
        loader.load();
        assertThat((OperatorManagmentController)loader.getController()).isNotNull();
    }

}
