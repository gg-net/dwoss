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
import eu.ggnet.dwoss.customer.ee.entity.AddressLabel;
import eu.ggnet.dwoss.core.common.values.AddressType;
import eu.ggnet.dwoss.customer.ee.entity.dto.AddressLabelDto;
import eu.ggnet.saft.core.Ui;
import eu.ggnet.saft.core.ui.FxController;
import eu.ggnet.saft.core.ui.ResultProducer;

import static eu.ggnet.dwoss.core.common.values.AddressType.INVOICE;
import static eu.ggnet.dwoss.core.common.values.AddressType.SHIPPING;

/**
 *
 * @author jacob.weinhold
 *
 */
public class PreferedAddressLabelsController implements Initializable, FxController, Consumer<Customer>, ResultProducer<Collection<AddressLabelDto>> {

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

    private AddressLabel invoiceLabel;

    private AddressLabel shippingLabel;

    private Customer customer;
    
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

        this.customer = customer;
        
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

        customer.getAddressLabels()
                .stream()
                .filter(addressLabel -> addressLabel.getType() == AddressType.INVOICE)
                .findFirst()
                .ifPresent(il -> {
                    invoiceAddressWebView.getEngine().loadContent(il.toHtml());
                    if ( il.getCompany() != null )
                        invoiceAddressCompanyListView.getSelectionModel().select(il.getCompany());
                    if ( il.getContact() != null )
                        invoiceAddressContactListView.getSelectionModel().select(il.getContact());
                    invoiceAddressAddressListView.getSelectionModel().select(il.getAddress());
                    invoiceLabel = il;
                });

        customer.getAddressLabels()
                .stream()
                .filter(addressLabel -> addressLabel.getType() == AddressType.SHIPPING)
                .findFirst()
                .ifPresent(sl -> {
                    shippingAddressWebView.getEngine().loadContent(sl.toHtml());
                    if ( sl.getCompany() != null )
                        shippingAddressCompanyListView.getSelectionModel().select(sl.getCompany());
                    if ( sl.getContact() != null )
                        shippingAddressContactListView.getSelectionModel().select(sl.getContact());
                    shippingAddressAddressListView.getSelectionModel().select(sl.getAddress());
                    shippingLabel = sl;
                });
    }

    @Override
    public Collection<AddressLabelDto> getResult() {
        if ( isCanceled ) return null;
        Collection<AddressLabelDto> result = new ArrayList<>();
        result.add(new AddressLabelDto(invoiceLabel));
        if (shippingLabel != null) result.add(new AddressLabelDto(shippingLabel));
        return result;
    }

    @FXML
    private void handleSaveButtonAction(ActionEvent event) {

        if (invoiceLabel == null) {
            invoiceLabel = new AddressLabel();
            invoiceLabel.setType(INVOICE);
            invoiceLabel.setCustomer(customer);            
        }
        
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
            shippingLabel.setCustomer(customer);
        } else { // Update existing
            shippingLabel.setCompany(sCompany);
            shippingLabel.setContact(sContact);
            shippingLabel.setAddress(sAddress);
        }

        isCanceled = false;
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
