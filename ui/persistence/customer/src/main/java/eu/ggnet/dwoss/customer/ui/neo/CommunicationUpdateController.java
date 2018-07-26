/*
 * Copyright (C) 2018 GG-Net GmbH
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
package eu.ggnet.dwoss.customer.ui.neo;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import org.apache.commons.lang3.StringUtils;

import eu.ggnet.dwoss.customer.ee.entity.Communication;
import eu.ggnet.dwoss.customer.ee.entity.Communication.Type;
import eu.ggnet.saft.core.Ui;
import eu.ggnet.saft.core.ui.FxController;
import eu.ggnet.saft.core.ui.ResultProducer;
import eu.ggnet.saft.core.ui.AlertType;

import lombok.NonNull;

/**
 * Controller class for the editor view of a Communication. Allows the user to
 * change all values of the Communication.
 *
 * @author jens.papenhagen
 */
public class CommunicationUpdateController implements Initializable, FxController, Consumer<Communication>, ResultProducer<Communication> {

    @FXML
    private ChoiceBox<Type> communicationTypeBox;

    @FXML
    private TextField identifer;

    @FXML
    private Label warning;

    private Communication communication;

    @FXML
    private Button saveButton;

    private boolean isCanceled = true;

    @FXML
    /**
     * Close the Editor window and discard all changes.
     */
    private void handleCloseButtonAction() {
        isCanceled = true;
        Ui.closeWindowOf(identifer);
    }

    @FXML
    /**
     * Close the Editor window and discard all changes.
     * <p>
     */
    private void handleSaveButtonAction() {
        warning.setVisible(false);
        updateCommunication();

        if ( !StringUtils.isBlank(identifer.getText()) ) {
            //check the email pattern, display Warning (!)
            if ( communicationTypeBox.getSelectionModel().getSelectedItem().equals(Communication.Type.EMAIL)
                    && !identifer.getText().matches(Communication.EMAIL_PATTERN) ) {

                warning.setVisible(true);
                warning.setText("Bitte die E-Mail überprüfen.");
                return;
            }
            //check the phone pattern, display Warning (!)
            if ( (communicationTypeBox.getSelectionModel().getSelectedItem().equals(Communication.Type.MOBILE)
                  || communicationTypeBox.getSelectionModel().getSelectedItem().equals(Communication.Type.PHONE)
                  || communicationTypeBox.getSelectionModel().getSelectedItem().equals(Communication.Type.FAX))
                    && !identifer.getText().matches(Communication.PHONE_PATTERN) ) {
                warning.setVisible(true);
                warning.setText("Bitte nur Zahlen eingeben.");
                return;
            }

        }

        //only get valid object out
        if ( communication.getViolationMessage() != null ) {
            Ui.build(communicationTypeBox).alert().message("Kommunikationsweg ist inkompatibel: " + communication.getViolationMessage()).show(AlertType.WARNING);
            return;
        }
        isCanceled = false;
        Ui.closeWindowOf(identifer);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        communicationTypeBox.getItems().addAll(Communication.Type.values());
        communicationTypeBox.getSelectionModel().selectFirst();

        //get overwriten in accept()
        identifer.setText("");

        //enable the save button only on filled TextFields
        saveButton.disableProperty().bind(
                Bindings.createBooleanBinding(()
                        -> identifer.getText().trim().isEmpty(), identifer.textProperty()
                )
        );
    }

    @Override
    public Communication getResult() {
        if ( isCanceled ) return null;
        return communication;
    }

    @Override
    public void accept(@NonNull Communication communication) {
        this.communication = communication;
        communicationTypeBox.getSelectionModel().select(communication.getType());
        communicationTypeBox.setDisable(true);
        if ( communication.getIdentifier() != null ) identifer.setText(communication.getIdentifier());
    }

    /**
     * Get the Communication back
     */
    private void updateCommunication() {
        if ( communication == null ) communication = new Communication();
        communication.setType(communicationTypeBox.getSelectionModel().getSelectedItem());
        communication.setIdentifier(identifer.getText());
    }

}
