package eu.ggnet.dwoss.customer.ui.neo;

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
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import eu.ggnet.dwoss.customer.ee.entity.Address;
import eu.ggnet.dwoss.customer.ee.entity.Country;
import eu.ggnet.saft.core.Ui;
import eu.ggnet.saft.core.ui.*;

import jakarta.enterprise.context.Dependent;

/**
 * Controller class for the editor view of a Address. Allows the user to
 * change all values of the Address.
 *
 * @author jens.papenhagen
 */
@Dependent
@Title("Kunden Adresse bearbeiten")
public class AddressUpdateController implements Initializable, FxController, Consumer<Address>, ResultProducer<Address> {

    private Address address;

    private boolean isCanceled = true;

    @FXML
    private ComboBox<Country> countryComboBox;

    @FXML
    private TextField zipcode;

    @FXML
    private TextField city;

    @FXML
    private TextField street;

    @FXML
    private Button saveButton;

    @FXML
    /**
     * Close the Editor window and discard all changes.
     */
    private void clickCancelButton(ActionEvent event) {
        isCanceled = true;
        Ui.closeWindowOf(zipcode);
    }

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        countryComboBox.getItems().addAll(Country.values());
        countryComboBox.setButtonCell(new CountryListCell());
        countryComboBox.setCellFactory((p) -> new CountryListCell());

        //get overwriten in accept()
        zipcode.setText("");
        city.setText("");
        street.setText("");

        //button behavior
        //enable the save button only on filled TextFields
        saveButton.disableProperty().bind(
                Bindings.createBooleanBinding(()
                        -> zipcode.getText().trim().isEmpty(), zipcode.textProperty()
                ).or(
                        Bindings.createBooleanBinding(()
                                -> city.getText().trim().isEmpty(), city.textProperty()
                        )
                ).or(
                        Bindings.createBooleanBinding(()
                                -> street.getText().trim().isEmpty(), street.textProperty()
                        )
                )
        );

        saveButton.setOnAction((e) -> {
            updateAddress();
            //only get valid object out
            if ( address.getViolationMessage() != null ) {
                Ui.build().alert().message("Adresse ist invalid: " + address.getViolationMessage()).show(AlertType.WARNING);
                return;
            }
            isCanceled = false;
            Ui.closeWindowOf(zipcode);
        });

    }

    @Override
    public void accept(Address address) {
        setAddress(Objects.requireNonNull(address, "address must not be null"));
    }

    @Override
    public Address getResult() {
        if ( isCanceled ) return null;
        return address;
    }

    /**
     * Set the Address for the Edit
     *
     * @param address is the Address
     */
    private void setAddress(Address address) {
        this.address = address;
        countryComboBox.getSelectionModel().select(address.getCountry());
        if ( address.getCity() != null ) city.setText(address.getCity());
        if ( address.getZipCode() != null ) zipcode.setText(address.getZipCode());
        if ( address.getStreet() != null ) street.setText(address.getStreet());
    }

    /**
     * Get the Address back. consider changes on event.
     */
    private void updateAddress() {
        if ( address == null ) address = new Address(); // If we are on a new instance.        
        address.setStreet(street.getText());
        address.setZipCode(zipcode.getText());
        address.setCity(city.getText());
        address.setCountry(countryComboBox.getValue());
    }

}
