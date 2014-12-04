/* 
 * Copyright (C) 2014 GG-Net GmbH - Oliver Günther
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.ggnet.dwoss.rights.action;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.IOException;

import eu.ggnet.saft.core.Workspace;
import eu.ggnet.saft.core.authorisation.AccessableAction;

import eu.ggnet.dwoss.rights.RightsManagmentController;

import eu.ggnet.dwoss.common.DwOssCore;

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
                    DwOssCore.show(mainFrame, exception);
                }
            }

        });
    }

}
