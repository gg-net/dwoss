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
package eu.ggnet.dwoss.customer.ui;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import org.apache.commons.lang3.StringUtils;

import eu.ggnet.dwoss.customer.entity.Communication;
import eu.ggnet.dwoss.customer.entity.Customer;
import eu.ggnet.saft.Ui;
import eu.ggnet.saft.UiAlert;
import eu.ggnet.saft.api.ui.ClosedListener;
import eu.ggnet.saft.api.ui.FxController;
import eu.ggnet.saft.core.ui.FxSaft;
import eu.ggnet.saft.core.ui.UiAlertBuilder;

/**
 * FXML Controller class
 *
 * @author jens.papenhagen
 */
public class AdditionalCustomerIdController implements Initializable, FxController, ClosedListener {

    private final CustomerTask LOADING_TASK = new CustomerTask();

    @FXML
    Button saveButton;

    @FXML
    Button closeButton;

    @FXML
    ChoiceBox externalsystembox;

    @FXML
    TextField identifer;

    @Override
    public void closed() {
        FxSaft.dispatch(() -> {
            if ( LOADING_TASK.isRunning() ) LOADING_TASK.cancel();
            return null;
        });
    }

    @FXML
    /**
     * Close the Editor window and discard all changes.
     */
    private void save(ActionEvent event) {

        if ( StringUtils.isBlank(identifer.getText()) ) {
            UiAlert.message("Es muss das Feld gesetzt werden").show(UiAlertBuilder.Type.WARNING);
            return;
        }

        //TODO
        Ui.closeWindowOf(identifer);
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        externalsystembox.getItems().addAll(Customer.ExternalSystem.values());
        externalsystembox.getSelectionModel().selectFirst();
        

        Ui.progress().observe(LOADING_TASK);
        Ui.exec(LOADING_TASK);
    }

}
