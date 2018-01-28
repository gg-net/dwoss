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

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import org.apache.commons.lang3.StringUtils;

import eu.ggnet.dwoss.customer.ee.entity.Communication;
import eu.ggnet.dwoss.customer.ee.entity.Communication.Type;
import eu.ggnet.saft.Ui;
import eu.ggnet.saft.UiAlert;
import eu.ggnet.saft.api.ui.*;
import eu.ggnet.saft.core.ui.UiAlertBuilder;

/**
 * FXML Controller class
 *
 * @author jens.papenhagen
 */
public class CommunicationUpdateController implements Initializable, FxController, Consumer<Communication>, ResultProducer<Communication> {

    @FXML
    private ChoiceBox<Type> commtypbox;

    @FXML
    private TextField identifer;

    @FXML
    private Label warning;

    private Communication communication;

    @FXML
    /**
     * Close the Editor window and discard all changes.
     */
    private void handleCloseButtonAction(ActionEvent event) {
        this.communication = null;
        Ui.closeWindowOf(identifer);
    }

    @FXML
    /**
     * Close the Editor window and discard all changes.
     * <p>
     */
    private void handleSaveButtonAction(ActionEvent event) {
        warning.setVisible(false);

        if ( !StringUtils.isBlank(identifer.getText()) ) {
            //check the email pattern, display Warning (!)
            if ( commtypbox.getSelectionModel().getSelectedItem().equals(Communication.Type.EMAIL)
                    && !identifer.getText().matches(Communication.EMAIL_PATTERN) ) {

                warning.setVisible(true);
                warning.setText("Bitte die E-Mail überprüfen.");
                return;
            }
            //check the phone pattern, display Warning (!)
            if ( (commtypbox.getSelectionModel().getSelectedItem().equals(Communication.Type.MOBILE)
                  || commtypbox.getSelectionModel().getSelectedItem().equals(Communication.Type.PHONE)
                  || commtypbox.getSelectionModel().getSelectedItem().equals(Communication.Type.FAX))
                    && !identifer.getText().matches(Communication.PHONE_PATTERN) ) {
                warning.setVisible(true);
                warning.setText("Bitte nur Zahlen eingeben.");
                return;
            }

        }

        if ( StringUtils.isBlank(identifer.getText()) ) {
            UiAlert.message("Es muss das Feld gesetzt werden").show(UiAlertBuilder.Type.WARNING);
            return;
        } else {
            getCommunication();
        }

        Ui.closeWindowOf(identifer);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        commtypbox.getItems().addAll(Communication.Type.values());
    }

    @Override
    public void accept(Communication a) {
        if ( a != null ) {
            communication = a;
            setCommunication(communication);
        } else {
            UiAlert.message("Kommunikationsweg ist inkompatibel").show(UiAlertBuilder.Type.WARNING);
        }

    }

    @Override
    public Communication getResult() {
        if ( communication == null ) {
            return null;
        }
        return communication;
    }

    /**
     * Set the Communication for the Edit
     *
     * @param a is the Communication
     */
    private void setCommunication(Communication com) {
        identifer.setText(com.getIdentifier());
        commtypbox.getSelectionModel().select(com.getType());
    }

    /**
     * Get the Communication back
     */
    private void getCommunication() {
        communication.setType(commtypbox.getSelectionModel().getSelectedItem());
        communication.setIdentifier(identifer.getText());
    }

}
