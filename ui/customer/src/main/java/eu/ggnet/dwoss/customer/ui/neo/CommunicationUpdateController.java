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
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;

import org.apache.commons.lang3.StringUtils;

import eu.ggnet.dwoss.customer.ee.entity.Communication;
import eu.ggnet.dwoss.customer.ee.entity.Communication.Type;
import eu.ggnet.saft.core.Ui;
import eu.ggnet.saft.core.ui.*;

import jakarta.enterprise.context.Dependent;

import static javafx.scene.text.FontPosture.ITALIC;

/**
 * Controller class for the editor view of a Communication. Allows the user to
 * change all values of the Communication.
 *
 * @author jens.papenhagen
 */
@Dependent
public class CommunicationUpdateController implements Initializable, FxController, Consumer<Communication>, ResultProducer<Communication> {
    
    public final static String WRONG_PHONE = "Rufnummernformat nicht zulässig.\n"
                        + "Beispiele: +49 1234 123456-12\n"
                        + "  0049 123 12345\n"
                        + "  040 987153";
    
    public final static String WRONG_EMAIL = "E-Mailformat nicht zulässig.\n"
                        + "Format: aaaaaa@bbbbb.ccc\n"
                        + "z.b. max@beispiel.de";

    @FXML
    private ChoiceBox<Type> communicationTypeBox;

    @FXML
    private TextField identifer;

    @FXML
    private TextFlow warning;

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
                Text t = new Text(WRONG_EMAIL);
                t.setFill(Color.RED);
                warning.getChildren().add(t);
                return;
            }
            //check the phone pattern, display Warning (!)
            if ( (communicationTypeBox.getSelectionModel().getSelectedItem().equals(Communication.Type.MOBILE)
                  || communicationTypeBox.getSelectionModel().getSelectedItem().equals(Communication.Type.PHONE)
                  || communicationTypeBox.getSelectionModel().getSelectedItem().equals(Communication.Type.FAX))
                    && !identifer.getText().matches(Communication.PHONE_PATTERN) ) {
                warning.setVisible(true);
                Text t = new Text(WRONG_PHONE);
                t.setFont(Font.font("Verdana", ITALIC, 12));
                t.setFill(Color.RED);                
                warning.getChildren().add(t);
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
    public void accept( Communication communication) {
        this.communication = Objects.requireNonNull(communication,"communication must not be null");
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
