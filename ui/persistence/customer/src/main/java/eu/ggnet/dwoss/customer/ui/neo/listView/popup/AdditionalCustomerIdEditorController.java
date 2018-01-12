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
package eu.ggnet.dwoss.customer.ui.neo.listView.popup;

import java.net.URL;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import org.apache.commons.lang3.StringUtils;

import eu.ggnet.dwoss.customer.entity.Customer;
import eu.ggnet.dwoss.customer.entity.Customer.ExternalSystem;
import eu.ggnet.dwoss.customer.ui.CustomerTask;
import eu.ggnet.saft.Ui;
import eu.ggnet.saft.UiAlert;
import eu.ggnet.saft.api.ui.*;
import eu.ggnet.saft.core.ui.UiAlertBuilder;

/**
 * FXML Controller class
 *
 * @author jens.papenhagen
 */
@Title("Externe Kunden Nummer bearbeiten")
public class AdditionalCustomerIdEditorController implements Initializable, FxController, Consumer<ObservableMap.Entry<ExternalSystem, String>>, ResultProducer<ObservableMap.Entry<ExternalSystem, String>> {

    private final CustomerTask LOADING_TASK = new CustomerTask();

    @FXML
    Button saveButton;

    @FXML
    Button closeButton;

    @FXML
    ChoiceBox externalsystembox;

    @FXML
    TextField identifer;

    ObservableMap.Entry<ExternalSystem, String> entry;

    @FXML
    private void handleCloseButtonAction(ActionEvent event) {

        this.entry = null;
        Ui.closeWindowOf(identifer);
    }

    @FXML
    private void handleSaveButtonAction(ActionEvent event) {
        if ( StringUtils.isBlank(identifer.getText()) ) {
            UiAlert.message("Es muss das Feld gesetzt werden").show(UiAlertBuilder.Type.WARNING);
            return;
        }

        //TODO
        Ui.closeWindowOf(identifer);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        externalsystembox.getItems().addAll(Customer.ExternalSystem.values());
        externalsystembox.getSelectionModel().selectFirst();

        Ui.progress().observe(LOADING_TASK);
        Ui.exec(LOADING_TASK);
    }

    @Override
    public void accept(Entry<ExternalSystem, String> a) {
        this.entry = a;
        identifer.setText(entry.getValue());
        if ( entry.getKey() != null ) {
            externalsystembox.getSelectionModel().select(entry.getKey());
        }
    }

    @Override
    public Entry<ExternalSystem, String> getResult() {
        return this.entry;
    }

}
