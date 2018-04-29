package eu.ggnet.dwoss.rights.ui;

import java.awt.GraphicsEnvironment;
import java.io.IOException;

import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;

import org.junit.Test;

import eu.ggnet.dwoss.rights.ee.RightsAgent;
import eu.ggnet.saft.Dl;

import tryout.stub.RightsAgentStub;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author pascal.perau
 */
public class RightsManagmentControllerTest {

    @Test
    public void testResource() {
        assertThat(RightsManagmentController.loadFxml()).isNotNull();
    }

    @Test
    public void testJavaFxFxml() throws IOException {
        if ( GraphicsEnvironment.isHeadless() ) return;
        new JFXPanel(); // Implizit start of JavaFx.
        Dl.remote().add(RightsAgent.class, new RightsAgentStub());
        FXMLLoader loader = new FXMLLoader(RightsManagmentController.loadFxml());
        loader.load();
        assertThat((RightsManagmentController)loader.getController()).isNotNull();
    }

}
