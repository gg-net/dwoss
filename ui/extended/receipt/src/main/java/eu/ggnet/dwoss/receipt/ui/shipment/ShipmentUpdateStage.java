/*
 * Copyright (C) 2014 GG-Net GmbH - Oliver Günther
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
package eu.ggnet.dwoss.receipt.ui.shipment;

import java.util.Date;
import java.util.Set;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;

import org.apache.commons.lang3.StringUtils;

import eu.ggnet.dwoss.common.api.values.TradeName;
import eu.ggnet.dwoss.mandator.upi.CachedMandators;
import eu.ggnet.dwoss.stock.ee.entity.Shipment;
import eu.ggnet.saft.core.Dl;

import static eu.ggnet.dwoss.stock.ee.entity.Shipment.Status.OPENED;
import static java.lang.Double.MAX_VALUE;

/**
 * Stage for creating/eediting Shipments.
 * <p>
 * @author pascal.perau
 */
public class ShipmentUpdateStage extends Stage {

    private Shipment shipment;

    private boolean ok = false;

    private ComboBox<TradeName> ownerBox;

    private ComboBox<TradeName> manufacturerBox;

    private ComboBox<Shipment.Status> statusBox;

    private TextField idField;

    private TextField shipIdField;

    private Label errorLabel = new Label("ShipmentID, Besitzer, Hersteller und Status setzen.");

    private Button okButton = new Button("OK");

    private Button cancelButton = new Button("Abbrechen");

    public ShipmentUpdateStage() {
        this.shipment = new Shipment();
        init(shipment);
    }

    public ShipmentUpdateStage(Shipment shipment) {
        this.shipment = shipment;
        init(shipment);
    }

    public boolean isOk() {
        return ok;
    }

    private boolean isValid() {
        ok = (shipment.getContractor() != null
              && !StringUtils.isBlank(shipment.getShipmentId())
              && shipment.getStatus() != null
              && shipment.getDefaultManufacturer() != null);
        if ( !ok ) {
            errorLabel.setTextFill(Color.web("#600000"));
        }
        return ok;
    }

    private void init(Shipment s) {

        okButton.setOnAction((ActionEvent event) -> {
            shipment = getShipment();
            if ( isValid() ) close();
        });

        cancelButton.setOnAction((ActionEvent event) -> {
            close();
        });

        idField = new TextField(Long.toString(s.getId()));
        idField.setDisable(true);
        shipIdField = new TextField(s.getShipmentId());

        Callback<ListView<TradeName>, ListCell<TradeName>> cb = new Callback<ListView<TradeName>, ListCell<TradeName>>() {
            @Override
            public ListCell<TradeName> call(ListView<TradeName> param) {
                return new ListCell<TradeName>() {
                    @Override
                    protected void updateItem(TradeName item, boolean empty) {
                        super.updateItem(item, empty);
                        if ( item == null || empty ) setText("Hersteller wählen...");
                        else setText(item.getName());
                    }
                };
            }
        };

        Set<TradeName> contractors = Dl.local().lookup(CachedMandators.class).loadContractors().all();
        ownerBox = new ComboBox<>(FXCollections.observableArrayList(contractors));
        ownerBox.setMaxWidth(MAX_VALUE);
        ownerBox.setCellFactory(cb);
        ownerBox.getSelectionModel().selectedItemProperty()
                .addListener((ObservableValue<? extends TradeName> observable, TradeName oldValue, TradeName newValue) -> {
                    if ( newValue == null ) return;
                    shipment.setContractor(newValue);
                    manufacturerBox.getSelectionModel().select(newValue.getManufacturer());
                });

        ObservableList<TradeName> manufacturers = FXCollections.observableArrayList(TradeName.getManufacturers());
        manufacturerBox = new ComboBox<>(manufacturers);
        manufacturerBox.setMaxWidth(MAX_VALUE);
        manufacturerBox.setCellFactory(cb);
        SingleSelectionModel<TradeName> sm = ownerBox.getSelectionModel();
        if ( s.getContractor() == null ) sm.selectFirst();
        else sm.select(s.getContractor());
        if ( shipment.getDefaultManufacturer() != null ) manufacturerBox.getSelectionModel().select(shipment.getDefaultManufacturer());

        statusBox = new ComboBox<>(FXCollections.observableArrayList(Shipment.Status.values()));
        statusBox.setMaxWidth(MAX_VALUE);
        statusBox.getSelectionModel().select(s.getStatus() == null ? OPENED : s.getStatus());

        GridPane grid = new GridPane();
        grid.addRow(1, new Label("ID:"), idField);
        grid.addRow(2, new Label("Shipment ID:"), shipIdField);
        grid.addRow(3, new Label("Besitzer:"), ownerBox);
        grid.addRow(4, new Label("Hersteller:"), manufacturerBox);
        grid.addRow(5, new Label("Status"), statusBox);
        grid.setMaxWidth(MAX_VALUE);
        grid.vgapProperty().set(2.);
        grid.getColumnConstraints().add(0, new ColumnConstraints(100, 100, Double.MAX_VALUE, Priority.SOMETIMES, HPos.LEFT, false));
        grid.getColumnConstraints().add(1, new ColumnConstraints(100, 150, Double.MAX_VALUE, Priority.ALWAYS, HPos.LEFT, true));

        HBox hButtonBox = new HBox(okButton, cancelButton);
        hButtonBox.alignmentProperty().set(Pos.TOP_RIGHT);

        errorLabel.setWrapText(true);
        BorderPane rootPane = new BorderPane(grid, errorLabel, null, hButtonBox, null);

        this.setTitle(s.getId() > 0 ? "Shipment bearbeiten" : "Shipment anlegen");
        this.setScene(new Scene(rootPane));
        this.setResizable(false);
    }

    public Shipment getShipment() {
        shipment.setDate(new Date());
        shipment.setContractor(ownerBox.getValue());
        shipment.setDefaultManufacturer(manufacturerBox.getValue());
        shipment.setShipmentId(shipIdField.getText());
        shipment.setStatus(statusBox.getValue());
        return shipment;
    }
}
