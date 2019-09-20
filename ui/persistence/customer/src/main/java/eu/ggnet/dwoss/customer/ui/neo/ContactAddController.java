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

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*; 
import javafx.util.StringConverter;

import eu.ggnet.dwoss.customer.ee.entity.Contact;
import eu.ggnet.dwoss.customer.ee.entity.Contact.Sex;
import eu.ggnet.saft.core.Ui;
import eu.ggnet.saft.core.ui.*;

/**
 * Controller class for the editor view of a Contact. Allows the user to
 * change all values of the Contact.
 * <p>
 * import static javafx.stage.Modality.WINDOW_MODAL;
 *
 * @author jens.papenhagen
 */
@Title("Kontakt eintragen")
public class ContactAddController implements Initializable, FxController, ResultProducer<Contact> {

    @FXML
    private TextField firstNameTextField;

    @FXML
    private TextField lastNameTextField;

    @FXML
    private TextField titleTextField;

    @FXML
    private ChoiceBox<Sex> genderBox;

    @FXML
    private Button saveButton;

    private boolean isCanceled = true;

    private Contact contact;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        //get overwriten in accept()
        lastNameTextField.setText("");

        //enable the save and "saveAndClose" button only on filled TextFields
        saveButton.disableProperty().bind(lastNameTextField.textProperty().isEmpty());

        saveButton.setOnAction((e) -> {
            updateContact();
            if ( contact.getViolationMessage() != null ) {
                Ui.build().alert().message("Kontakt ist invalid: " + contact.getViolationMessage()).show(AlertType.ERROR);
            } else {
                isCanceled = false;
                Ui.closeWindowOf(lastNameTextField);
            }
        });

        //fill the UI with default values
        genderBox.setConverter(new StringConverter<Sex>() {
            @Override
            public Sex fromString(String string) {
                throw new UnsupportedOperationException("Invalid operation for Convert a String into a Sex.");
            }

            @Override
            public String toString(Sex myClassinstance) {
                return myClassinstance.getSign();
            }
        });
        genderBox.getItems().addAll(Contact.Sex.values());

    }

    @FXML
    private void clickCancelButton() {
        isCanceled = true;
        Ui.closeWindowOf(lastNameTextField);
    }

    @Override
    public Contact getResult() {
        if ( isCanceled ) return null;
        return contact;
    }

    /**
     * update the contact before, consider doing this on element change.
     */
    private void updateContact() {
        if ( contact == null ) contact = new Contact();
        contact.setTitle(titleTextField.getText());
        contact.setFirstName(firstNameTextField.getText());
        contact.setLastName(lastNameTextField.getText());
        contact.setSex(genderBox.getSelectionModel().getSelectedItem());
    }

}
