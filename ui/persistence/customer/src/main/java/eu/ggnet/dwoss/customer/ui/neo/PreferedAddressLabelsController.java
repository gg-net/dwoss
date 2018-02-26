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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.web.WebView;

import eu.ggnet.dwoss.customer.ee.entity.*;
import eu.ggnet.dwoss.customer.ee.entity.projection.AddressLabel;
import eu.ggnet.dwoss.customer.ui.neo.PreferedAddressLabelsController.InvoiceAddressLabelWithNullableShippingAddressLabel;
import eu.ggnet.dwoss.rules.AddressType;
import eu.ggnet.saft.Ui;
import eu.ggnet.saft.api.ui.FxController;
import eu.ggnet.saft.api.ui.ResultProducer;

import lombok.Getter;

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
    private WebView invoiceAddressWebView;

    @FXML
    private WebView shippingAddressWebView;

    private InvoiceAddressLabelWithNullableShippingAddressLabel resultAdressLabel;

    private Customer customer;

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

    public void accept(Customer inputCustomer) {

        this.customer = inputCustomer;

        invoiceAddressCompanyListView.getItems().addAll(customer.getCompanies());
        invoiceAddressCompanyListView.getItems().forEach(company -> invoiceAddressContactListView.getItems().addAll(company.getContacts()));
        invoiceAddressCompanyListView.getItems().forEach(company -> invoiceAddressAddressListView.getItems().addAll(company.getAddresses()));
        invoiceAddressContactListView.getItems().addAll(customer.getContacts());
        invoiceAddressContactListView.getItems().forEach(contact -> invoiceAddressAddressListView.getItems().addAll(contact.getAddresses()));

        shippingAddressCompanyListView.getItems().addAll(customer.getCompanies());
        shippingAddressCompanyListView.getItems().forEach(company -> shippingAddressContactListView.getItems().addAll(company.getContacts()));
        shippingAddressCompanyListView.getItems().forEach(company -> shippingAddressAddressListView.getItems().addAll(company.getAddresses()));
        shippingAddressContactListView.getItems().addAll(customer.getContacts());
        shippingAddressContactListView.getItems().forEach(contact -> shippingAddressAddressListView.getItems().addAll(contact.getAddresses()));

        if ( customer.getAddressLabels()
                .stream()
                .filter(addressLabel -> addressLabel.getType() == AddressType.INVOICE)
                .findFirst()
                .isPresent() ) {

            AddressLabel invoiceLabel = customer.getAddressLabels()
                    .stream()
                    .filter(addressLabel -> addressLabel.getType() == AddressType.INVOICE)
                    .findFirst()
                    .get();
            invoiceAddressWebView.getEngine().loadContent(invoiceLabel.toHtml());
            if ( invoiceLabel.getCompany() != null )
                invoiceAddressCompanyListView.getSelectionModel().select(invoiceLabel.getCompany());
            if ( invoiceLabel.getContact() != null )
                invoiceAddressContactListView.getSelectionModel().select(invoiceLabel.getContact());

            invoiceAddressAddressListView.getSelectionModel().select(invoiceLabel.getAddress());
        }

        if ( customer.getAddressLabels()
                .stream()
                .filter(addressLabel -> addressLabel.getType() == AddressType.SHIPPING)
                .findFirst()
                .isPresent() ) {

            AddressLabel shippingLabel
                    = customer.getAddressLabels()
                            .stream()
                            .filter(addressLabel -> addressLabel.getType() == AddressType.SHIPPING)
                            .findFirst()
                            .get();

            shippingAddressWebView.getEngine().loadContent(shippingLabel.toHtml());

            if ( shippingLabel.getCompany() != null )
                shippingAddressCompanyListView.getSelectionModel().select(shippingLabel.getCompany());
            if ( shippingLabel.getContact() != null )
                shippingAddressContactListView.getSelectionModel().select(shippingLabel.getContact());

            shippingAddressAddressListView.getSelectionModel().select(shippingLabel.getAddress());
        }
    }

    @Override
    public InvoiceAddressLabelWithNullableShippingAddressLabel getResult() {

        return this.resultAdressLabel;

    }

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

        this.resultAdressLabel = new InvoiceAddressLabelWithNullableShippingAddressLabel(shippingLabel, invoiceLabel);
        Ui.closeWindowOf(saveButton);
    }

    @FXML
    private void handleCancelButtonAction(ActionEvent event) {
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
