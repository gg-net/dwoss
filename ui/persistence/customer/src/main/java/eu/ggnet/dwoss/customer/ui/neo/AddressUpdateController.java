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
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import org.apache.commons.lang3.StringUtils;

import eu.ggnet.dwoss.customer.entity.Address;
import eu.ggnet.saft.Ui;
import eu.ggnet.saft.UiAlert;
import eu.ggnet.saft.api.ui.*;
import eu.ggnet.saft.core.ui.UiAlertBuilder;

/**
 * FXML Controller class
 *
 * @author jens.papenhagen
 */
@Title("Kunden Adresse bearbeiten")
public class AddressUpdateController implements Initializable, FxController, Consumer<Address>, ResultProducer<Address> {

    private final Pattern decimalPattern = Pattern.compile("\\d+");

    private Address address;

    @FXML
    private Button closeButton;

    @FXML
    private Button saveButton;

    @FXML
    private ChoiceBox<String> countrybox;

    @FXML
    private TextField zipcode;

    @FXML
    private TextField city;

    @FXML
    private TextField street;

    @FXML
    /**
     * Close the Editor window and discard all changes.
     */
    private void handleCloseButtonAction(ActionEvent event) {
        Ui.closeWindowOf(zipcode);
    }

    @FXML
    /**
     * Close the Editor window and save all changes.
     *
     * @todo
     * objekte passen mit saft
     */
    private void handleSaveButtonAction(ActionEvent event) {
        if ( StringUtils.isBlank(street.getText()) ) {
            UiAlert.message("Es muss ein Strasse gesetzt werden").show(UiAlertBuilder.Type.WARNING);
            return;
        }
        getAddress();
        Ui.closeWindowOf(zipcode);
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        //the isoCountry is hardcoded to DE
        //IDEA Enum for more usefull List: https://en.wikipedia.org/wiki/ISO_3166-1
        List<String> countries = new ArrayList<>();
        countries.add("DE");
        countries.add("CH");
        countries.add("AT");
        countrybox.getItems().addAll(countries);

        // force the field to be numeric only
        zipcode.textFormatterProperty().set(new TextFormatter<>(changeed -> {
            if ( decimalPattern.matcher(changeed.getControlNewText()).matches() ) {
                return changeed;
            } else {
                return null;
            }
        }));

    }

    @Override
    public void accept(Address a) {
        if ( a != null ) {
            address = a;
            setAddress(address);
        } else {
            UiAlert.message("Firma ist inkompatibel").show(UiAlertBuilder.Type.WARNING);
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
        countrybox.getSelectionModel().select(address.getIsoCountry());
        zipcode.setText(address.getZipCode());
        city.setText(address.getCity());
        street.setText(address.getStreet());
    }

    /**
     * Get the Address back
     */
    private void getAddress() {
        address.setStreet(street.getText());
        address.setZipCode(zipcode.getText());
        address.setCity(city.getText());
        
        if ( countrybox.getSelectionModel().getSelectedItem() != null ) {
            Locale tempLocale = new Locale(countrybox.getSelectionModel().getSelectedItem().toLowerCase(), countrybox.getSelectionModel().getSelectedItem().toUpperCase());
            address.setIsoCountry(tempLocale );
        } else {
            address.setIsoCountry(new Locale("de"));
        }
    }

}
