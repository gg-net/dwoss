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
package eu.ggnet.dwoss.customer.ui.neo.mainView;

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
import eu.ggnet.dwoss.customer.ui.neo.listView.*;
import eu.ggnet.saft.api.ui.*;

/**
 * FXML Controller class
 *
 * @author jens.papenhagen
 */
@Title("Firmen eintragen")
public class CustomerCompanyController implements Initializable, FxController, Consumer<Company>, ResultProducer<Company> {

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

    ObservableList<Contact> contactList = FXCollections.observableArrayList();

    ObservableList<Address> addressList = FXCollections.observableArrayList();

    ObservableList<Communication> communicationList = FXCollections.observableArrayList();

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

            contactList.addAll(company.getContacts());
            addressList.addAll(company.getAddresses());
            communicationList.addAll(company.getCommunications());

            ContactListedView ContactListedView = new ContactListedView();
            ContactListedView.fillList(contactList);
            contactBox.getChildren().add(ContactListedView.getVbox());

            AddressListedView addressListedView = new AddressListedView();
            addressListedView.fillList(addressList);
            addressBox.getChildren().add(addressListedView.getVbox());

            CommunicationListedView communicationListedView = new CommunicationListedView();
            communicationListedView.fillList(communicationList);
            communicationsBox.getChildren().add(communicationListedView.getVbox());
        }
    }

    @Override
    public Company getResult() {
        return this.company;
    }
}
