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
package eu.ggnet.dwoss.customer.ui.neo.listView;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.VBox;

import eu.ggnet.dwoss.customer.entity.*;
import eu.ggnet.dwoss.customer.ui.CustomerTask;
import eu.ggnet.saft.Ui;
import eu.ggnet.saft.api.ui.ClosedListener;
import eu.ggnet.saft.api.ui.FxController;
import eu.ggnet.saft.core.ui.FxSaft;

/**
 * FXML Controller class
 *
 * @author jens.papenhagen
 */
public class CustomerCompanyController implements Initializable, FxController {
    
    private final Pattern decimalPattern = Pattern.compile("-?\\d*(\\,\\d{0,2})?");

    private final CustomerTask LOADING_TASK = new CustomerTask();

    @FXML
    TextField nameField;

    @FXML
    TextField ledgerField;

    @FXML
    TextField taxIdField;

    @FXML
    VBox contactBox;

    @FXML
    VBox addressBox;

    @FXML
    VBox communicationsBox;

    Company uicomppany;
    
    ObservableList<Contact> contactList = FXCollections.observableArrayList();

    ObservableList<Address> addressList = FXCollections.observableArrayList();

    ObservableList<Communication> communicationList = FXCollections.observableArrayList();

    public CustomerCompanyController(Company comppany) {
        this.uicomppany = comppany;
        fillUI();
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO

        Ui.progress().observe(LOADING_TASK);
        Ui.exec(LOADING_TASK);
    }

    
    private void fillUI(){
        
        nameField.setText(uicomppany.getName());
        ledgerField.setText(""+ uicomppany.getLedger());
        taxIdField.setText(uicomppany.getTaxId());
        
        // force the field to be numeric only
        ledgerField.textFormatterProperty().set(new TextFormatter<>(changeed -> {
            if ( decimalPattern.matcher(changeed.getControlNewText()).matches() ) {
                return changeed;
            } else {
                return null;
            }
        }));
        
        contactList.addAll(uicomppany.getContacts());
        addressList.addAll(uicomppany.getAddresses());
        communicationList.addAll(uicomppany.getCommunications());        
        
        CustomerContactListController customerContactListController = new CustomerContactListController(contactList);
        contactBox.getChildren().add(customerContactListController.getVbox());
        
        CustomerAddressListController customerAddressListController = new CustomerAddressListController(addressList);
        addressBox.getChildren().add(customerAddressListController.getVbox());

        CustomerCommunicationListController customerCommunicationListController = new CustomerCommunicationListController(communicationList);
        communicationsBox.getChildren().add(customerCommunicationListController.getVbox());        
        
    }
}
