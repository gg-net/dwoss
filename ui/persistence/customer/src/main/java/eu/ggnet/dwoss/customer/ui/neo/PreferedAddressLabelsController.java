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

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import eu.ggnet.dwoss.customer.entity.*;
import eu.ggnet.saft.api.ui.FxController;
import eu.ggnet.saft.api.ui.ResultProducer;

/**
 *
 * @author jacob.weinhold
 */
public class PreferedAddressLabelsController implements Initializable, FxController, Consumer<Customer>, ResultProducer<Customer> {

    @FXML
    private ListView<Company> invoiceAdressCompanyListView;

    @FXML
    private ListView<Contact> invoiceAdressContactListView;

    @FXML
    private ListView<Address> invoiceAdressAdressListView;

    @FXML
    private ListView<Company> shippingAdressCompanyListView;

    @FXML
    private ListView<Contact> shippingAdressContactListView;

    @FXML
    private ListView<Address> shippingAdressAdressListView;

    @FXML
    private Button invoiceAdressClearButton;

    @FXML
    private Button shippingAdressClearButton;

    @FXML
    private Button saveButton;

    @FXML
    private Button cancelButton;

    private Customer customer;

    @FXML
    private TextArea invoiceAdressTextArea;

    @FXML
    private TextArea shippingAdressTextArea;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    private void handleinvoiceAdressClearButtonAction(ActionEvent event) {
    }

    @FXML
    private void handleshippingAdressClearButtonAction(ActionEvent event) {
    }

    @Override
    public void accept(Customer inputCustomer) {
        this.customer = inputCustomer;

        invoiceAdressCompanyListView.getItems().addAll(this.customer.getCompanies());
        this.invoiceAdressCompanyListView.getItems().forEach(company -> this.invoiceAdressContactListView.getItems().addAll(company.getContacts()));
        this.invoiceAdressContactListView.getItems().forEach(contact -> invoiceAdressAdressListView.getItems().addAll(contact.getAddresses()));

        shippingAdressCompanyListView.getItems().addAll(this.customer.getCompanies());
        this.shippingAdressCompanyListView.getItems().forEach(company -> this.shippingAdressContactListView.getItems().addAll(company.getContacts()));
        this.shippingAdressContactListView.getItems().forEach(contact -> shippingAdressAdressListView.getItems().addAll(contact.getAddresses()));

        this.invoiceAdressAdressListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Address>() {
            @Override
            public void changed(ObservableValue<? extends Address> observable, Address oldValue, Address newValue) {
                invoiceAdressTextArea.setText(newValue.toHtml());
            }
        });

        this.shippingAdressAdressListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Address>() {
            @Override
            public void changed(ObservableValue<? extends Address> observable, Address oldValue, Address newValue) {
                shippingAdressTextArea.setText(newValue.toHtml());
            }
        });

    }

    @Override
    public Customer getResult() {
        return this.customer;

    }

}
