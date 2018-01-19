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
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import eu.ggnet.dwoss.customer.entity.*;
import eu.ggnet.dwoss.customer.entity.projection.AddressLabel;
import eu.ggnet.saft.api.ui.FxController;
import eu.ggnet.saft.api.ui.ResultProducer;

/**
 *
 * @author jacob.weinhold
 *
 */
public class PreferedAddressLabelsController implements Initializable, FxController, Consumer<Customer>, ResultProducer<InvoiceAddressLabelWithNullableShippingAddressLabel> {

    @FXML
    private ListView<Company> invoiceAddressCompanyListView;

    @FXML
    private ListView<Contact> invoiceAddressContactListView;

    @FXML
    private ListView<Address> invoiceAddressAddressListView;

    @FXML
    private ListView<Company> shippingAddressCompanyListView;

    @FXML
    private ListView<Contact> shippingAddressContactListView;

    @FXML
    private ListView<Address> shippingAddressAddressListView;

    @FXML
    private Button invoiceAddressClearButton;

    @FXML
    private Button shippingAddressClearButton;

    @FXML
    private Button saveButton;

    @FXML
    private Button cancelButton;

    @FXML
    private TextArea invoiceAddressTextArea;

    @FXML
    private TextArea shippingAddressTextArea;

    private InvoiceAddressLabelWithNullableShippingAddressLabel addressLabel;

    private Customer customer;

    InvalidationListener saveButtonDisablingListener = new InvalidationListener() {

        @Override
        public void invalidated(Observable observable) {

            if ( invoiceAddressAddressListView.getSelectionModel().isEmpty() )
                saveButton.setDisable(true);

            else if ( invoiceAddressCompanyListView.getSelectionModel().isEmpty()
                    && invoiceAddressContactListView.getSelectionModel().isEmpty() )
                saveButton.setDisable(true);

            else if ( !invoiceAddressCompanyListView.getSelectionModel().isEmpty()
                    || !invoiceAddressContactListView.getSelectionModel().isEmpty() )
                saveButton.setDisable(false);
        }

    };

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @Override
    public void accept(Customer inputCustomer) {
        this.customer = inputCustomer;

        invoiceAddressCompanyListView.getItems().addAll(this.customer.getCompanies());
        this.invoiceAddressCompanyListView.getItems().forEach(company -> this.invoiceAddressContactListView.getItems().addAll(company.getContacts()));
        invoiceAddressContactListView.getItems().addAll(this.customer.getContacts());
        this.invoiceAddressContactListView.getItems().forEach(contact -> invoiceAddressAddressListView.getItems().addAll(contact.getAddresses()));

        shippingAddressCompanyListView.getItems().addAll(this.customer.getCompanies());
        this.shippingAddressCompanyListView.getItems().forEach(company -> this.shippingAddressContactListView.getItems().addAll(company.getContacts()));
        shippingAddressContactListView.getItems().addAll(this.customer.getContacts());
        this.shippingAddressContactListView.getItems().forEach(contact -> shippingAddressAddressListView.getItems().addAll(contact.getAddresses()));

        this.invoiceAddressAddressListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Address>() {
            @Override
            public void changed(ObservableValue<? extends Address> observable, Address oldValue, Address newValue) {
                invoiceAddressTextArea.setText(newValue.toHtml());
            }
        });

        this.shippingAddressAddressListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Address>() {
            @Override
            public void changed(ObservableValue<? extends Address> observable, Address oldValue, Address newValue) {
                shippingAddressTextArea.setText(newValue.toHtml());
            }
        });

        this.saveButton.setDisable(true);

        invoiceAddressCompanyListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Company>() {

            @Override
            public void changed(ObservableValue<? extends Company> observable, Company oldValue, Company newValue) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });

        invoiceAddressCompanyListView.selectionModelProperty().addListener(saveButtonDisablingListener);
        invoiceAddressContactListView.selectionModelProperty().addListener(saveButtonDisablingListener);
        invoiceAddressAddressListView.selectionModelProperty().addListener(saveButtonDisablingListener);

    }

    @Override
    public InvoiceAddressLabelWithNullableShippingAddressLabel getResult() {

        return this.addressLabel;

    }

    /**
     * AddressLabel.class allows it's company OR contact field to be null
     *
     * @param event
     */
    @FXML
    private void handleSaveButtonAction(ActionEvent event) {

    }

    @FXML
    private void handleCancelButtonAction(ActionEvent event) {
    }

    @FXML
    private void handleinvoiceAddressClearButtonAction(ActionEvent event) {
        invoiceAddressCompanyListView.getSelectionModel().clearSelection();
        invoiceAddressContactListView.getSelectionModel().clearSelection();
        invoiceAddressAddressListView.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleshippingAddressClearButtonAction(ActionEvent event) {
        shippingAddressCompanyListView.getSelectionModel().clearSelection();
        shippingAddressContactListView.getSelectionModel().clearSelection();
        shippingAddressAddressListView.getSelectionModel().clearSelection();
    }
}

class InvoiceAddressLabelWithNullableShippingAddressLabel {

    Optional<AddressLabel> shippingLabel;

    AddressLabel invoiceLabel;

    public InvoiceAddressLabelWithNullableShippingAddressLabel(AddressLabel shippingLabel, AddressLabel invoiceLabel) {
        if ( invoiceLabel == null )
            throw new IllegalArgumentException("invoice Label can not be null");

        this.shippingLabel = Optional.ofNullable(shippingLabel);

        this.invoiceLabel = invoiceLabel;
    }

}
