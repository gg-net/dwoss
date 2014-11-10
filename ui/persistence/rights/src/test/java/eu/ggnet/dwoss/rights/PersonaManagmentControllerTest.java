package eu.ggnet.dwoss.rights;

import java.awt.GraphicsEnvironment;
import java.io.IOException;

import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 *
 * @author pascal.perau
 */
public class PersonaManagmentControllerTest {

    @Test
    public void testResource() {
        assertThat(PersonaManagmentController.loadFxml()).isNotNull();
    }

    @Test
    public void testJavaFxFxml() throws IOException {
        if ( GraphicsEnvironment.isHeadless() ) return;
        new JFXPanel(); // Implizit start of JavaFx.
        FXMLLoader loader = new FXMLLoader(PersonaManagmentController.loadFxml());
        loader.load();
        assertThat((PersonaManagmentController)loader.getController()).isNotNull();
    }

}
