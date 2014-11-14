/*
 * Copyright (C) 2014 GG-Net GmbH
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
package eu.ggnet.dwoss.misc.action;

import java.awt.event.ActionEvent;
import java.util.Objects;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import eu.ggnet.dwoss.common.ExceptionUtil;
import eu.ggnet.dwoss.misc.ResolveRepaymentController;
import eu.ggnet.dwoss.rules.TradeName;
import eu.ggnet.saft.api.Authorisation;
import eu.ggnet.saft.core.Workspace;
import eu.ggnet.saft.core.authorisation.AccessableAction;

import static eu.ggnet.dwoss.rights.api.AtomicRight.RESOLVE_REPAYMENT;
import static eu.ggnet.saft.core.Client.lookup;

/**
 *
 * @author bastian.venz
 */
public class ResolveRepaymentAction extends AccessableAction {

    public ResolveRepaymentAction() {
        super(RESOLVE_REPAYMENT);
    }

    private boolean cancel = false;

    @Override
    public void actionPerformed(ActionEvent e) {
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                try {

                    Stage choose = new Stage();
                    choose.setTitle("Contractor auswählen");
                    ComboBox<TradeName> comboBox = new ComboBox<>(FXCollections.observableArrayList(TradeName.values()));
                    comboBox.getSelectionModel().select(TradeName.values()[0]);
                    Button button = new Button("Ok");
                    button.setOnAction((t) -> {
                        choose.hide();
                    });
                    Button buttonCancel = new Button("Abbrechen");
                    buttonCancel.setOnAction((t) -> {
                        cancel = true;
                        choose.hide();
                    });

                    VBox box = new VBox(comboBox, button, buttonCancel);
                    choose.setScene(new Scene(box, Color.ALICEBLUE));
                    choose.showAndWait();

                    if ( cancel ) return;

                    Stage stage = new Stage();
                    stage.setTitle("Resolve Repayment");
                    FXMLLoader loader = new FXMLLoader(ResolveRepaymentController.loadFxml());
                    GridPane page = (GridPane)loader.load();
                    ResolveRepaymentController controller = Objects.requireNonNull(loader.getController());
                    controller.setContractor(comboBox.getValue());
                    Scene scene = new Scene(page, Color.ALICEBLUE);
                    stage.setScene(scene);
                    stage.show();
                } catch (Exception ex) {
                    ExceptionUtil.show(lookup(Workspace.class).getMainFrame(), ex);
                }
            }
        });
    }

}
