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
import eu.ggnet.dwoss.rules.AddressType;
import eu.ggnet.saft.api.ui.FxController;
import eu.ggnet.saft.api.ui.ResultProducer;

import static eu.ggnet.dwoss.rules.AddressType.SHIPPING;

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

            if ( invoiceAddressAddressListView.getSelectionModel().isEmpty() ) {
                saveButton.setDisable(true);

            } else if ( invoiceAddressCompanyListView.getSelectionModel().isEmpty()
                    && invoiceAddressContactListView.getSelectionModel().isEmpty() ) {
                saveButton.setDisable(true);

            } else if ( (!invoiceAddressCompanyListView.getSelectionModel().isEmpty())
                    || (!invoiceAddressContactListView.getSelectionModel().isEmpty()) ) {
                saveButton.setDisable(false);

            }
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
                if ( newValue != null )
                    invoiceAddressTextArea.setText(newValue.toHtml());
            }
        });

        this.shippingAddressAddressListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Address>() {
            @Override
            public void changed(ObservableValue<? extends Address> observable, Address oldValue, Address newValue) {
                if ( newValue != null )
                    shippingAddressTextArea.setText(newValue.toHtml());
            }
        });

        this.saveButton.setDisable(true);

        invoiceAddressCompanyListView.getSelectionModel().selectedItemProperty().addListener(saveButtonDisablingListener);
        invoiceAddressContactListView.getSelectionModel().selectedItemProperty().addListener(saveButtonDisablingListener);
        invoiceAddressAddressListView.getSelectionModel().selectedItemProperty().addListener(saveButtonDisablingListener);

    }

    @Override
    public InvoiceAddressLabelWithNullableShippingAddressLabel getResult() {

        return this.addressLabel;

    }

    /**
     * AddressLabel.class allows it's company OR contact field to be null
     *
     * @todo
     * close window etc
     * @param event
     */
    @FXML
    private void handleSaveButtonAction(ActionEvent event) {

        Company invoiceLabelCompany = invoiceAddressCompanyListView.getSelectionModel().getSelectedItem();
        Contact invoiceLabelContact = invoiceAddressContactListView.getSelectionModel().getSelectedItem();
        Address invoiceLabelAddress = invoiceAddressAddressListView.getSelectionModel().getSelectedItem();

        AddressLabel invoiceLabel = new AddressLabel(invoiceLabelCompany, invoiceLabelContact, invoiceLabelAddress, AddressType.INVOICE);

        Address shippingAddress = shippingAddressAddressListView.getSelectionModel().getSelectedItem();
        Company shippingLabelCompany = shippingAddressCompanyListView.getSelectionModel().getSelectedItem();
        Contact shippingLabelContact = shippingAddressContactListView.getSelectionModel().getSelectedItem();

        AddressLabel shippingLabel;
        if ( shippingAddress == null || (shippingLabelCompany == null && shippingLabelContact == null) )
            shippingLabel = null;

        else
            shippingLabel = new AddressLabel(shippingLabelCompany, invoiceLabelContact, shippingAddress, SHIPPING);

        this.addressLabel = new InvoiceAddressLabelWithNullableShippingAddressLabel(shippingLabel, invoiceLabel);

        System.out.println(this.addressLabel);
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

    private Optional<AddressLabel> shippingLabel;

    private AddressLabel invoiceLabel;

    public InvoiceAddressLabelWithNullableShippingAddressLabel(AddressLabel shippingLabel, AddressLabel invoiceLabel) {
        if ( invoiceLabel == null )
            throw new IllegalArgumentException("invoice Label can not be null");

        this.shippingLabel = Optional.ofNullable(shippingLabel);

        this.invoiceLabel = invoiceLabel;
    }

    @Override
    public String toString() {
        return "InvoiceAddressLabelWithNullableShippingAddressLabel{" + "shippingLabel=" + shippingLabel + ", invoiceLabel=" + invoiceLabel + '}';
    }

}
