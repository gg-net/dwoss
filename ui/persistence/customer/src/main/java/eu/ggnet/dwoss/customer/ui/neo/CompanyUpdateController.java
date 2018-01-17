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

import eu.ggnet.dwoss.customer.ui.neo.AddressList;
import eu.ggnet.dwoss.customer.ui.neo.ContactList;
import eu.ggnet.dwoss.customer.ui.neo.CommunicationList;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.VBox;

import eu.ggnet.dwoss.customer.entity.*;
import eu.ggnet.saft.api.ui.*;

/**
 * FXML Controller class
 *
 * @author jens.papenhagen
 */
@Title("Firmen eintragen")
public class CompanyUpdateController implements Initializable, FxController, Consumer<Company>, ResultProducer<Company> {

    private final Pattern decimalPattern = Pattern.compile("-?\\d*(\\,\\d{0,2})?");

    @FXML
    private TextField nameField;

    @FXML
    private TextField ledgerField;

    @FXML
    private TextField taxIdField;

    @FXML
    private VBox contactBox;

    @FXML
    private VBox addressBox;

    @FXML
    private VBox communicationsBox;

    private Company company;

    ObservableList<Contact> contacts = FXCollections.observableArrayList();

    ObservableList<Address> address = FXCollections.observableArrayList();

    ObservableList<Communication> communications = FXCollections.observableArrayList();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    @Override
    public void accept(Company company) {
        this.company = company;
        if ( company != null ) {

            nameField.setText(company.getName());
            ledgerField.setText("" + company.getLedger());
            taxIdField.setText(company.getTaxId());

            // force the field to be numeric only
            ledgerField.textFormatterProperty().set(new TextFormatter<>(changeed -> {
                if ( decimalPattern.matcher(changeed.getControlNewText()).matches() ) {
                    return changeed;
                } else {
                    return null;
                }
            }));

            contacts.addAll(company.getContacts());
            address.addAll(company.getAddresses());
            communications.addAll(company.getCommunications());

            ContactList contactList = new ContactList(contacts);
            contactBox.getChildren().add(contactList.getList());

            AddressList addressList = new AddressList(address);
            addressBox.getChildren().add(addressList.getList());

            CommunicationList communicationList = new CommunicationList(communications);
            communicationsBox.getChildren().add(communicationList.getList());
        }
    }

    @Override
    public Company getResult() {
        return this.company;
    }
}
