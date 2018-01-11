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

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import eu.ggnet.dwoss.customer.entity.Address;
import eu.ggnet.dwoss.customer.entity.Contact;
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
public class CustomerContactController implements Initializable, FxController, ClosedListener {

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

    @Override
    public void closed() {
        FxSaft.dispatch(() -> {
            if ( LOADING_TASK.isRunning() ) LOADING_TASK.cancel();
            return null;
        });
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        genderBox.getItems().addAll(Contact.Sex.values());
        genderBox.getSelectionModel().selectFirst();

        ObservableList<Address> list = FXCollections.observableArrayList();

        CustomerAddressListController customerAddressListController = new CustomerAddressListController(list);
        addressBox.getChildren().add(customerAddressListController.getVbox());

        // TODO
        Ui.progress().observe(LOADING_TASK);
        Ui.exec(LOADING_TASK);
    }

}
