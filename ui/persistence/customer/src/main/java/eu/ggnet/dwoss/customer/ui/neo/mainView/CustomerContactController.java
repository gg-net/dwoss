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

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import eu.ggnet.dwoss.customer.entity.*;
import eu.ggnet.dwoss.customer.ui.CustomerTask;
import eu.ggnet.dwoss.customer.ui.neo.listView.CustomerAddressListController;
import eu.ggnet.dwoss.customer.ui.neo.listView.CustomerCommunicationListController;
import eu.ggnet.saft.Ui;
import eu.ggnet.saft.api.ui.FxController;

/**
 * FXML Controller class
 *
 * @author jens.papenhagen
 */
public class CustomerContactController implements Initializable, FxController {

    private final CustomerTask LOADING_TASK = new CustomerTask();

    @FXML
    ChoiceBox genderBox;

    @FXML
    TextField titleField;

    @FXML
    TextField firstnameField;

    @FXML
    TextField lastnameField;

    @FXML
    VBox addressBox;

    @FXML
    VBox communicationsBox;

    Contact uicontact;

    ObservableList<Address> addressList = FXCollections.observableArrayList();

    ObservableList<Communication> communicationList = FXCollections.observableArrayList();

     public CustomerContactController(Contact contact) {
        this.uicontact = contact;
        fillUI();
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //fill the UI with default values
        genderBox.getItems().addAll(Contact.Sex.values());
        genderBox.getSelectionModel().selectFirst();

        // TODO
        Ui.progress().observe(LOADING_TASK);
        Ui.exec(LOADING_TASK);
    }

    private void fillUI() {
        titleField.setText(uicontact.getTitle());

        firstnameField.setText(uicontact.getFirstName());
        lastnameField.setText(uicontact.getLastName());
        
        addressList.addAll(uicontact.getAddresses());
        communicationList.addAll(uicontact.getCommunications());
        

        CustomerAddressListController customerAddressListController = new CustomerAddressListController(addressList);
        addressBox.getChildren().add(customerAddressListController.getVbox());

        CustomerCommunicationListController customerCommunicationListController = new CustomerCommunicationListController(communicationList);
        communicationsBox.getChildren().add(customerCommunicationListController.getVbox());

    }

}
