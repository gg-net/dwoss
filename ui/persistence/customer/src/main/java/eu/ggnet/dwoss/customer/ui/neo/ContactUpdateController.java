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

import eu.ggnet.dwoss.customer.ui.neo.list.AddressList;
import eu.ggnet.dwoss.customer.ui.neo.list.CommunicationList;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import eu.ggnet.dwoss.customer.entity.*;
import eu.ggnet.saft.api.ui.*;

/**
 * FXML Controller class
 *
 * @author jens.papenhagen
 */
@Title("Kontakt eintragen")
public class ContactUpdateController implements Initializable, FxController, Consumer<Contact>, ResultProducer<Contact> {

    @FXML
    private ChoiceBox genderBox;

    @FXML
    private TextField titleField;

    @FXML
    private TextField firstnameField;

    @FXML
    private TextField lastnameField;

    @FXML
    private VBox addressBox;

    @FXML
    private VBox communicationsBox;

    private Contact contact;

    ObservableList<Address> address = FXCollections.observableArrayList();

    ObservableList<Communication> communications = FXCollections.observableArrayList();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //fill the UI with default values
        genderBox.getItems().addAll(Contact.Sex.values());
        genderBox.getSelectionModel().selectFirst();

    }

    @Override
    public void accept(Contact contact) {
        this.contact = contact;

        if ( contact != null ) {
            titleField.setText(contact.getTitle());

            firstnameField.setText(contact.getFirstName());
            lastnameField.setText(contact.getLastName());

            address.addAll(contact.getAddresses());
            communications.addAll(contact.getCommunications());

            AddressList addressList = new AddressList(address);
            addressBox.getChildren().add(addressList.getList());

            CommunicationList communicationList = new CommunicationList(communications);
            communicationsBox.getChildren().add(communicationList.getList());

        }
    }

    @Override
    public Contact getResult() {
        return this.contact;
    }

}
