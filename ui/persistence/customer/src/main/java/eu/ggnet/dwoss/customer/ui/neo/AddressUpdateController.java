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
import eu.ggnet.dwoss.customer.ui.CustomerTask;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import org.apache.commons.lang3.StringUtils;

import eu.ggnet.dwoss.customer.entity.Address;
import eu.ggnet.dwoss.rules.AddressType;
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

    private final Pattern decimalPattern = Pattern.compile("-?\\d*(\\,\\d{0,2})?");

    @FXML
    private Button saveButton;

    @FXML
    private Button closeButton;

    @FXML
    private ChoiceBox preferedtxpbox;

    @FXML
    private ChoiceBox countrybox;

    @FXML
    private TextField zipcode;

    @FXML
    private TextField city;

    @FXML
    private TextField street;

    private Address adresse;

    @FXML
    /**
     * Close the Editor window and discard all changes.
     */
    private void handleCloseButtonAction(ActionEvent event) {
        this.adresse = null;
        Ui.closeWindowOf(zipcode);
    }

    @FXML
    /**
     * Close the Editor window and discard all changes.
     *
     * @todo
     * objekte passen mit saft
     */
    private void handleSaveButtonAction(ActionEvent event) {

        if ( StringUtils.isBlank(street.getText()) ) {
            UiAlert.message("Es muss ein Strasse gesetzt werden").show(UiAlertBuilder.Type.WARNING);
            return;
        }

        Ui.closeWindowOf(street);
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        preferedtxpbox.getItems().addAll(AddressType.values());
        preferedtxpbox.getSelectionModel().selectFirst();

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
        this.adresse = a;
        if ( adresse != null ) {
            preferedtxpbox.getSelectionModel().select(adresse.getPreferedType());
        }

        //the isoCountry is hardcoded to DE
        //IDEA Enum for more usefull List: https://en.wikipedia.org/wiki/ISO_3166-1
        countrybox.getItems().addAll(adresse.getIsoCountry());
        countrybox.getSelectionModel().selectFirst();

        zipcode.setText(adresse.getZipCode());
        city.setText(adresse.getCity());

        street.setText(adresse.getStreet());
    }

    @Override
    public Address getResult() {
        if ( adresse == null ) {
            return null;
        }
        return adresse;
    }

}
