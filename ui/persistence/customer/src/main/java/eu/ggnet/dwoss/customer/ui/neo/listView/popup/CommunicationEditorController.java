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
package eu.ggnet.dwoss.customer.ui.neo.listView.popup;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import org.apache.commons.lang3.StringUtils;

import eu.ggnet.dwoss.customer.entity.Communication;
import eu.ggnet.saft.Ui;
import eu.ggnet.saft.UiAlert;
import eu.ggnet.saft.api.ui.*;
import eu.ggnet.saft.core.ui.UiAlertBuilder;

/**
 * FXML Controller class
 *
 * @author jens.papenhagen
 */
public class CommunicationEditorController implements Initializable, FxController, Consumer<Communication>, ResultProducer<Communication> {

    @FXML
    Button saveButton;

    @FXML
    Button closeButton;

    @FXML
    ChoiceBox commtypbox;

    @FXML
    TextField identifer;

    @FXML
    Label warning;

    private Communication communication;

    @FXML
    /**
     * Close the Editor window and discard all changes.
     */
    private void cancel(ActionEvent event) {
        Ui.closeWindowOf(identifer);
    }

    @FXML
    /**
     * Close the Editor window and discard all changes.
     *
     * @todo
     * objekte passen mit saft
     */
    private void save(ActionEvent event) {
        warning.setVisible(false);

        if ( !StringUtils.isBlank(identifer.getText()) ) {
            //check the email pattern, display Warning (!)
            if ( commtypbox.getSelectionModel().getSelectedItem().equals(Communication.Type.EMAIL)
                    && !identifer.getText().matches(Communication.EMAIL_PATTERN) ) {

                warning.setVisible(true);
                return;
            }
            //check the phone pattern, display Warning (!)
            if ( (commtypbox.getSelectionModel().getSelectedItem().equals(Communication.Type.MOBILE)
                  || commtypbox.getSelectionModel().getSelectedItem().equals(Communication.Type.PHONE)
                  || commtypbox.getSelectionModel().getSelectedItem().equals(Communication.Type.FAX))
                    && !identifer.getText().matches(Communication.PHONE_PATTERN) ) {
                warning.setVisible(true);
                return;
            }

        }

        if ( StringUtils.isBlank(identifer.getText()) ) {
            UiAlert.message("Es muss das Feld gesetzt werden").show(UiAlertBuilder.Type.WARNING);
            return;
        }

        Ui.closeWindowOf(identifer);
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    private void start() {
        identifer.setText(communication.getIdentifier());

        if ( communication != null ) {
            commtypbox.getSelectionModel().select(communication.getType());
        }
    }
    
    @Override
    public void accept(Communication communication) {
        this.communication = communication;
        start();
    }

    @Override
    public Communication getResult() {
         if ( communication == null ) {
            return null;
        }
        return communication;
    }

}
