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

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;

import eu.ggnet.dwoss.customer.entity.*;
import eu.ggnet.saft.api.ui.FxController;
import eu.ggnet.saft.api.ui.ResultProducer;

/**
 * FXML Controller class
 *
 * @author jacob.weinhold
 */
public class CompanyPopUpController implements Initializable, FxController, Consumer<Company>, ResultProducer<Company> {

//    private final Pattern decimalPattern = Pattern.compile("-?\\d*(\\,\\d{0,2})?");
    @FXML
    private Label idLabel;

    @FXML
    private TextField nameTextField;

    @FXML
    private TextField taxIdTextField;

    @FXML
    private TextField ledgerTextField;

    @FXML
    private FlowPane contactsPane;

    @FXML
    private FlowPane addressesPane;

    @FXML
    private FlowPane communicationsPane;

    private Company company;

    ObservableList<Contact> contacts = FXCollections.observableArrayList();

    ObservableList<Address> address = FXCollections.observableArrayList();

    ObservableList<Communication> communications = FXCollections.observableArrayList();

    @FXML
    private Button testButton;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @Override
    public void accept(Company t) {
//        this.company = company;
//        if ( company != null ) {
//
//            nameTextField.setText(company.getName());
//            ledgerTextField.setText("" + company.getLedger());
//            taxIdTextField.setText(company.getTaxId());
//
//            // force the field to be numeric only
//            ledgerTextField.textFormatterProperty().set(new TextFormatter<>(changeed -> {
//                if ( decimalPattern.matcher(changeed.getControlNewText()).matches() ) {
//                    return changeed;
//                } else {
//                    return null;
//                }
//            }));
//
//            contacts.addAll(company.getContacts());
//            address.addAll(company.getAddresses());
//            communications.addAll(company.getCommunications());
//
//            ContactList contactList = new ContactList(contacts);
//            contactsPane.getChildren().add(contactList.getList());
//
//            AddressList addressList = new AddressList(address);
//            addressesPane.getChildren().add(addressList.getList());
//
//            CommunicationList communicationList = new CommunicationList(communications);
//            communicationsPane.getChildren().add(communicationList.getList());
    }

    @Override
    public Company getResult() {
        return this.company;
    }

    @FXML
    private void handleTestButtonAction(ActionEvent event) {
//        System.out.println(contactsPane.getChildren().size());
//        contactsPane.getChildren().forEach(e -> System.out.println(e));
    }

}
