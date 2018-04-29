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
import eu.ggnet.saft.core.ui.Title;
import eu.ggnet.saft.core.ui.ResultProducer;
import eu.ggnet.saft.core.ui.FxController;

import java.net.URL;
import java.util.*;
import java.util.function.Consumer;

import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.StringConverter;

import eu.ggnet.dwoss.customer.ee.entity.Address;
import eu.ggnet.saft.core.Ui;
import eu.ggnet.saft.core.ui.AlertType;

/**
 * Controller class for the editor view of a Address. Allows the user to
 * change all values of the Address.
 *
 * @author jens.papenhagen
 */
@Title("Kunden Adresse bearbeiten")
public class AddressUpdateController implements Initializable, FxController, Consumer<Address>, ResultProducer<Address> {

    private Address address;

    @FXML
    private ChoiceBox<Locale> countrybox;

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
    private void handleCloseButtonAction(ActionEvent event) {
        address = null;
        Ui.closeWindowOf(zipcode);
    }

    @FXML
    /**
     * Close the Editor window and save all changes.
     * <p>
     */
    private void handleSaveButtonAction(ActionEvent event) {
        address = getAddress();

        //only get valid object out
        if ( address.getViolationMessage() != null ) {
            Ui.exec(() -> {
                Ui.build().alert().message("Adresse ist inkompatibel: " + address.getViolationMessage()).show(AlertType.WARNING);
            });
            return;
        }

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
        List<Locale> countries = new ArrayList<>();
        countries.add(new Locale("de", "DE"));
        countries.add(new Locale("ch", "CH"));
        countries.add(new Locale("at", "AT"));
        countrybox.setConverter(new StringConverter<Locale>() {
            @Override
            public Locale fromString(String string) {
                throw new UnsupportedOperationException("Invalid operation for Convert a String into a Locale.");
            }

            @Override
            public String toString(Locale myClassinstance) {
                return myClassinstance.getDisplayCountry();
            }
        }
        );
        countrybox.getItems().addAll(countries);

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

    }

    @Override
    public void accept(Address a) {
        if ( a != null ) {
            setAddress(a);
        } else {
            Ui.exec(() -> {
                Ui.build().alert().message("Addresse ist inkompatibel").show(AlertType.WARNING);
            });
        }
    }

    @Override
    public Address getResult() {
        if ( address == null ) {
            return null;
        }
        return address;
    }

    /**
     * Set the Address for the Edit
     *
     * @param a is the Address
     */
    private void setAddress(Address a) {
        Locale tempLocale = new Locale(a.getIsoCountry().toLowerCase(), a.getIsoCountry().toUpperCase());
        countrybox.getSelectionModel().select(tempLocale);
        if ( a.getCity() != null ) {
            city.setText(a.getCity());
        }
        if ( a.getZipCode() != null ) {
            zipcode.setText(a.getZipCode());
        }
        if ( a.getStreet() != null ) {
            street.setText(a.getStreet());
        }

    }

    /**
     * Get the Address back
     */
    private Address getAddress() {
        Address a = new Address();
        a.setStreet(street.getText());
        a.setZipCode(zipcode.getText());
        a.setCity(city.getText());
        a.setIsoCountry(countrybox.getSelectionModel().getSelectedItem());

        return a;
    }

}
