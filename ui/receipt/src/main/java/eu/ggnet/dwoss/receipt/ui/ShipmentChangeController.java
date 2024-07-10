/*
 * Copyright (C) 2024 GG-Net GmbH
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
package eu.ggnet.dwoss.receipt.ui;

import java.util.List;
import java.util.function.Consumer;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;

import eu.ggnet.dwoss.stock.api.StockApi;
import eu.ggnet.dwoss.stock.api.StockApi.SimpleShipment;
import eu.ggnet.saft.core.Saft;
import eu.ggnet.saft.core.ui.*;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;

import static eu.ggnet.saft.core.ui.Bind.Type.SHOWING;

@Dependent
public class ShipmentChangeController implements FxController, Consumer<List<StockApi.SimpleShipment>>, ResultProducer<ShipmentChangeController.Out> {

    public static record Out(String refurbishedId, long shipmentId, String shipmentLabel) {

    }

    @Bind(SHOWING)
    private BooleanProperty showingProperty = new SimpleBooleanProperty();

    @Inject
    private Saft saft;

    private boolean ok = false;

    @FXML
    private Button cancelButton;

    @FXML
    private Button okButton;

    @FXML
    private TextField refurbishIdField;

    @FXML
    private ComboBox<SimpleShipment> shipmentComboBox;

    @FXML
    public void initialize() {
        cancelButton.setOnAction(e -> showingProperty.set(false));
        okButton.setOnAction(e -> {
            ok = true;
            showingProperty.set(false);
        });
        shipmentComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(SimpleShipment item, boolean empty) {
                super.updateItem(item, empty);
                if ( !empty && item != null ) {
                    setText(item.description());
                } else {
                    setText(null);
                }
            }
        });
        shipmentComboBox.setCellFactory((ListView<SimpleShipment> param) -> new ListCell<>() {
            @Override
            protected void updateItem(SimpleShipment item, boolean empty) {
                super.updateItem(item, empty);
                if ( !empty && item != null ) {
                    setText(item.description());
                } else {
                    setText(null);
                }
            }
        });
    }

    @Override
    public void accept(List<StockApi.SimpleShipment> s) {
        shipmentComboBox.getItems().addAll(s);

        shipmentComboBox.getSelectionModel().selectFirst();
    }

    @Override
    public Out getResult() {
        if ( !ok || refurbishIdField.getText() == null || refurbishIdField.getText().isBlank() ) return null;
        return new Out(refurbishIdField.getText(),
                shipmentComboBox.getSelectionModel().getSelectedItem().id(),
                shipmentComboBox.getSelectionModel().getSelectedItem().description());
    }

}
