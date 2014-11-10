package eu.ggnet.dwoss.rights.action;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.IOException;

import eu.ggnet.saft.core.Workspace;
import eu.ggnet.saft.core.authorisation.AccessableAction;

import eu.ggnet.dwoss.rights.RightsManagmentController;

import eu.ggnet.dwoss.common.ExceptionUtil;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import static eu.ggnet.saft.core.Client.lookup;
import static eu.ggnet.dwoss.rights.api.AtomicRight.CREATE_UPDATE_RIGHTS;

/**
 *
 * @author Bastian Venz
 */
public class RightsManagmentAction extends AccessableAction {

    public RightsManagmentAction() {
        super(CREATE_UPDATE_RIGHTS);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Window mainFrame = lookup(Workspace.class).getMainFrame();
                try {
                    Stage stage = new Stage();
                    stage.setTitle("Rechte Managment");
                    AnchorPane page = (AnchorPane)FXMLLoader.load(RightsManagmentController.class.getResource("RightsManagmentView.fxml"));
                    Scene scene = new Scene(page, Color.ALICEBLUE);
                    stage.setScene(scene);
                    stage.show();
                } catch (IOException exception) {
                    ExceptionUtil.show(mainFrame, exception);
                }
            }

        });
    }

}
