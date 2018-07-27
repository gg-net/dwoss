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
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.web.WebView;

import eu.ggnet.dwoss.customer.ee.entity.*;
import eu.ggnet.dwoss.customer.ee.entity.projection.AddressLabel;
import eu.ggnet.dwoss.customer.ui.neo.PreferedAddressLabelsController.InvoiceAddressLabelWithNullableShippingAddressLabel;
import eu.ggnet.dwoss.common.api.values.AddressType;
import eu.ggnet.saft.core.Ui;
import eu.ggnet.saft.core.ui.FxController;
import eu.ggnet.saft.core.ui.ResultProducer;

import lombok.Getter;

import static eu.ggnet.dwoss.common.api.values.AddressType.SHIPPING;

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
    private WebView invoiceAddressWebView;

    @FXML
    private WebView shippingAddressWebView;

    private InvoiceAddressLabelWithNullableShippingAddressLabel resultAdressLabel;

    private AddressLabel invoiceLabel;

    private AddressLabel shippingLabel;

    private boolean isCanceled = true;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        saveButton.setDisable(true);

        invoiceAddressCompanyListView.setCellFactory(cb -> new CompanyListCell());
        invoiceAddressContactListView.setCellFactory(cb -> new ContactListCell());
        invoiceAddressAddressListView.setCellFactory(cb -> new AddressListCell());

        shippingAddressCompanyListView.setCellFactory(cb -> new CompanyListCell());
        shippingAddressContactListView.setCellFactory(cb -> new ContactListCell());
        shippingAddressAddressListView.setCellFactory(cb -> new AddressListCell());

        InvalidationListener invoiceWebViewListener = (Observable observable) -> {
            if ( !invoiceAddressAddressListView.getSelectionModel().isEmpty() ) {
                AddressLabel addressLabel = new AddressLabel(invoiceAddressCompanyListView.getSelectionModel().getSelectedItem(),
                        invoiceAddressContactListView.getSelectionModel().getSelectedItem(),
                        invoiceAddressAddressListView.getSelectionModel().getSelectedItem(), AddressType.INVOICE);
                invoiceAddressWebView.getEngine().loadContent(
                        addressLabel.toHtml());

            } else
                invoiceAddressWebView.getEngine().loadContent("");
        };

        this.invoiceAddressAddressListView.getSelectionModel().selectedIndexProperty().addListener(invoiceWebViewListener);
        this.invoiceAddressCompanyListView.getSelectionModel().selectedIndexProperty().addListener(invoiceWebViewListener);
        this.invoiceAddressContactListView.getSelectionModel().selectedIndexProperty().addListener(invoiceWebViewListener);

        InvalidationListener shippingWebViewListener = (Observable observable) -> {
            if ( !shippingAddressAddressListView.getSelectionModel().isEmpty() ) {
                AddressLabel addressLabel = new AddressLabel(shippingAddressCompanyListView.getSelectionModel().getSelectedItem(),
                        shippingAddressContactListView.getSelectionModel().getSelectedItem(),
                        shippingAddressAddressListView.getSelectionModel().getSelectedItem(), AddressType.SHIPPING);
                shippingAddressWebView.getEngine().loadContent(
                        addressLabel.toHtml());

            } else
                shippingAddressWebView.getEngine().loadContent("");
        };

        this.shippingAddressAddressListView.getSelectionModel().selectedIndexProperty().addListener(shippingWebViewListener);
        this.shippingAddressCompanyListView.getSelectionModel().selectedIndexProperty().addListener(shippingWebViewListener);
        this.shippingAddressContactListView.getSelectionModel().selectedIndexProperty().addListener(shippingWebViewListener);

        InvalidationListener saveButtonDisablingListener = (Observable observable) -> {
            boolean isInvoiceAddressValid = (!invoiceAddressAddressListView.getSelectionModel().isEmpty())
                    && ((!invoiceAddressCompanyListView.getSelectionModel().isEmpty())
                        || (!invoiceAddressContactListView.getSelectionModel().isEmpty()));

            boolean isShippingAddressValid = (shippingAddressAddressListView.getSelectionModel().isEmpty()
                                              && shippingAddressCompanyListView.getSelectionModel().isEmpty()
                                              && shippingAddressContactListView.getSelectionModel().isEmpty())
                    || ((!shippingAddressAddressListView.getSelectionModel().isEmpty())
                        && (!shippingAddressCompanyListView.getSelectionModel().isEmpty() || !shippingAddressContactListView.getSelectionModel().isEmpty()));

            if ( isInvoiceAddressValid && isShippingAddressValid )
                saveButton.setDisable(false);

            else
                saveButton.setDisable(true);
        };
        invoiceAddressCompanyListView.getSelectionModel().selectedItemProperty().addListener(saveButtonDisablingListener);
        invoiceAddressContactListView.getSelectionModel().selectedItemProperty().addListener(saveButtonDisablingListener);
        invoiceAddressAddressListView.getSelectionModel().selectedItemProperty().addListener(saveButtonDisablingListener);

        shippingAddressCompanyListView.getSelectionModel().selectedItemProperty().addListener(saveButtonDisablingListener);
        shippingAddressContactListView.getSelectionModel().selectedItemProperty().addListener(saveButtonDisablingListener);
        shippingAddressAddressListView.getSelectionModel().selectedItemProperty().addListener(saveButtonDisablingListener);
    }

    @Override
    public void accept(Customer customer) {

        invoiceAddressCompanyListView.getItems().addAll(customer.getCompanies());
        shippingAddressCompanyListView.getItems().addAll(customer.getCompanies());

        List<Contact> allContacts = Stream.concat(
                customer.getContacts().stream(),
                customer.getCompanies().stream().flatMap((com) -> com.getContacts().stream()))
                .collect(Collectors.toList());

        invoiceAddressContactListView.getItems().addAll(allContacts);
        shippingAddressContactListView.getItems().addAll(allContacts);

        List<Address> allAddresses = Stream.concat(
                allContacts.stream().flatMap((com) -> com.getAddresses().stream()),
                customer.getCompanies().stream().flatMap((com) -> com.getAddresses().stream())).collect(Collectors.toList());

        invoiceAddressAddressListView.getItems().addAll(allAddresses);
        shippingAddressAddressListView.getItems().addAll(allAddresses);

        invoiceLabel = customer.getAddressLabels()
                .stream()
                .filter(addressLabel -> addressLabel.getType() == AddressType.INVOICE)
                .findFirst().orElseThrow(() -> new IllegalArgumentException("No AddressLabel found, broken custmoer: " + customer));

        invoiceAddressWebView.getEngine().loadContent(invoiceLabel.toHtml());
        if ( invoiceLabel.getCompany() != null )
            invoiceAddressCompanyListView.getSelectionModel().select(invoiceLabel.getCompany());
        if ( invoiceLabel.getContact() != null )
            invoiceAddressContactListView.getSelectionModel().select(invoiceLabel.getContact());

        invoiceAddressAddressListView.getSelectionModel().select(invoiceLabel.getAddress());

        customer.getAddressLabels()
                .stream()
                .filter(addressLabel -> addressLabel.getType() == AddressType.SHIPPING)
                .findFirst()
                .ifPresent(l -> {
                    shippingLabel = l;

                    shippingAddressWebView.getEngine().loadContent(shippingLabel.toHtml());

                    if ( shippingLabel.getCompany() != null )
                        shippingAddressCompanyListView.getSelectionModel().select(shippingLabel.getCompany());
                    if ( shippingLabel.getContact() != null )
                        shippingAddressContactListView.getSelectionModel().select(shippingLabel.getContact());

                    shippingAddressAddressListView.getSelectionModel().select(shippingLabel.getAddress());

                });
    }

    @Override
    public InvoiceAddressLabelWithNullableShippingAddressLabel getResult() {
        if ( isCanceled ) return null;
        return resultAdressLabel;
    }

    @FXML
    private void handleSaveButtonAction(ActionEvent event) {

        invoiceLabel.setCompany(invoiceAddressCompanyListView.getSelectionModel().getSelectedItem());
        invoiceLabel.setContact(invoiceAddressContactListView.getSelectionModel().getSelectedItem());
        invoiceLabel.setAddress(invoiceAddressAddressListView.getSelectionModel().getSelectedItem());

        Address sAddress = shippingAddressAddressListView.getSelectionModel().getSelectedItem();
        Company sCompany = shippingAddressCompanyListView.getSelectionModel().getSelectedItem();
        Contact sContact = shippingAddressContactListView.getSelectionModel().getSelectedItem();

        if ( sAddress == null || (sCompany == null && sContact == null) ) { // shipping label removed
            shippingLabel = null;
        } else if ( shippingLabel == null ) { // create new
            shippingLabel = new AddressLabel(sCompany, sContact, sAddress, SHIPPING);
        } else { // Update existing
            shippingLabel.setCompany(sCompany);
            shippingLabel.setContact(sContact);
            shippingLabel.setAddress(sAddress);
        }

        isCanceled = false;
        this.resultAdressLabel = new InvoiceAddressLabelWithNullableShippingAddressLabel(shippingLabel, invoiceLabel);
        Ui.closeWindowOf(saveButton);
    }

    @FXML
    private void handleCancelButtonAction(ActionEvent event) {
        isCanceled = true;
        Ui.closeWindowOf(saveButton);
    }

    @FXML
    private void handleInvoiceAddressClearButtonAction(ActionEvent event) {
        invoiceAddressCompanyListView.getSelectionModel().clearSelection();
        invoiceAddressContactListView.getSelectionModel().clearSelection();
        invoiceAddressAddressListView.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleShippingAddressClearButtonAction(ActionEvent event) {
        shippingAddressCompanyListView.getSelectionModel().clearSelection();
        shippingAddressContactListView.getSelectionModel().clearSelection();
        shippingAddressAddressListView.getSelectionModel().clearSelection();
    }

    public static class InvoiceAddressLabelWithNullableShippingAddressLabel {

        @Getter
        private Optional<AddressLabel> shippingLabel;

        @Getter
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

    public static class CompanyListCell extends ListCell<Company> {

        @Override
        public void updateItem(Company item, boolean empty) {
            super.updateItem(item, empty);
            if ( empty ) {
                setText(null);
            } else {
                setText(item.getName());
            }
        }
    }

    public static class ContactListCell extends ListCell<Contact> {

        @Override
        public void updateItem(Contact item, boolean empty) {
            super.updateItem(item, empty);
            if ( empty ) {
                setText(null);
            } else {
                setText(((item.getTitle() == null) ? "" : item.getTitle())
                        + " " + item.getFirstName() + " " + item.getLastName());
            }
        }
    }

    public static class AddressListCell extends ListCell<Address> {

        @Override
        public void updateItem(Address item, boolean empty) {
            super.updateItem(item, empty);
            if ( empty ) {
                setText(null);
            } else {
                setText(item.getStreet() + " " + item.getZipCode() + " " + item.getCity());
            }
        }
    }

}
