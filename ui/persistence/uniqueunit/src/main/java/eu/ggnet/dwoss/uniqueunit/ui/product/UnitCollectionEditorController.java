/*
 * Copyright (C) 2017 GG-Net GmbH
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
package eu.ggnet.dwoss.uniqueunit.ui.product;

import eu.ggnet.saft.api.ui.FxController;

import java.net.URL;
import java.util.*;

import javafx.event.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.*;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.rules.*;
import eu.ggnet.dwoss.uniqueunit.api.PicoUnit;
import eu.ggnet.dwoss.uniqueunit.ui.ProductTask;
import eu.ggnet.saft.Ui;
import eu.ggnet.saft.UiAlert;
import eu.ggnet.saft.api.ui.*;
import eu.ggnet.saft.core.ui.FxSaft;
import eu.ggnet.saft.core.ui.UiAlertBuilder;

/**
 *
 * @author jens.papenhagen
 */
@Title("uniqueUnitCollection Editor")
public class UnitCollectionEditorController implements Initializable, FxController, ClosedListener {

    public static final DataFormat df = new DataFormat("dw/uniqueUnitCollection");

    private static final Logger L = LoggerFactory.getLogger(UnitCollectionEditorController.class);

    private final ProductTask productsTask = new ProductTask();

    @FXML
    private TextField name;

    @FXML
    private Label producatnamefix;

    @FXML
    private TextArea description;

    @FXML
    private ChoiceBox<SalesChannel> salesChannel;

    @FXML
    private TextField priceInput;

    @FXML
    private TextField partNo;

    @FXML
    private ListView<PicoUnit> listViewUnits;

    @FXML
    private ListView<PicoUnit> listViewRemainingUnits;

    @FXML
    /**
     * Close the Editor window and discard all changes.
     */
    private void cancel(ActionEvent event) {
        Ui.closeWindowOf(name);
    }

    @FXML
    /**
     * Close the Editor window and discard all changes.
     */
    private void save(ActionEvent event) {

        if ( StringUtils.isBlank(name.getText()) ) {
            UiAlert.message("Es muss ein Name gesetzt werden").show(UiAlertBuilder.Type.WARNING);
            return;
        }
    }

    @FXML
    private void removeUnit(ActionEvent event) {

    }

    @FXML
    private void removePrice(ActionEvent event) {

    }

    @Override
    /**
     * Adding the filters to the combo box. Setting the cell values and the
     * filtered list containing the data.
     */
    public void initialize(URL url, ResourceBundle rb) {
        salesChannel.getItems().addAll(SalesChannel.values());
        salesChannel.getSelectionModel().selectFirst();
        
        producatnamefix.setText("");
        
        
        
        Ui.progress().observe(productsTask);
        Ui.exec(productsTask);

    }

    @Override
    public void closed() {
        FxSaft.dispatch(() -> {
            if ( productsTask.isRunning() ) productsTask.cancel();
            return null;
        });
    }

}
