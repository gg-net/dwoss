package tryout;

import java.io.IOException;

import org.junit.Test;

import eu.ggnet.saft.core.Client;

import eu.ggnet.dwoss.rights.RightsAgent;

import eu.ggnet.dwoss.rights.RightsManagmentController;

import tryout.stub.RightsAgentStub;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 *
 * @author Bastian Venz
 */
public class RightsManagmentControllerTryout {

    boolean complete = false;

    @Test
    public void testSomeMethod() throws InterruptedException {
        new JFXPanel();    // To start the platform 
        Client.addSampleStub(RightsAgent.class, new RightsAgentStub());
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    Stage stage = new Stage();
                    stage.setTitle("Rechte Managment");
                    AnchorPane page = (AnchorPane)FXMLLoader.load(RightsManagmentController.loadFxml());
                    Scene scene = new Scene(page, Color.ALICEBLUE);
                    stage.setScene(scene);
                    stage.showAndWait();
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
                complete = true;
            }
        });
        while (!complete) {
            Thread.sleep(500);
        }
    }

}
