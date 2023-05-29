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
import java.util.function.Consumer;

import javax.inject.Inject;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import org.apache.commons.lang3.StringUtils;

import eu.ggnet.dwoss.core.common.values.tradename.TradeName;
import eu.ggnet.dwoss.core.widget.Dl;
import eu.ggnet.dwoss.mandator.spi.CachedMandators;
import eu.ggnet.dwoss.stock.ee.entity.Shipment;
import eu.ggnet.dwoss.core.common.values.ShipmentStatus;
import eu.ggnet.saft.core.Saft;
import eu.ggnet.saft.core.ui.Bind;
import eu.ggnet.saft.core.ui.ResultProducer;

import static eu.ggnet.dwoss.core.common.values.ShipmentStatus.OPENED;
import static eu.ggnet.saft.core.ui.Bind.Type.SHOWING;
import static java.lang.Double.MAX_VALUE;

/**
 * Stage for creating/eediting Shipments.
 * <p>
 * @author pascal.perau
 */
public class ShipmentEditView extends BorderPane implements Consumer<Shipment>, ResultProducer<Shipment> {

    private class TradeNameListCell extends ListCell<TradeName> {

        @Override
        protected void updateItem(TradeName item, boolean empty) {
            super.updateItem(item, empty);
            if ( !empty ) setText(item.getDescription());
        }
    };

    @Bind(SHOWING)
    private BooleanProperty showingProperty = new SimpleBooleanProperty();

    @Inject
    private Saft saft;

    private Shipment shipment;

    private boolean ok = false;

    private ComboBox<TradeName> contractorBox;

    private ComboBox<TradeName> manufacturerBox;

    private ComboBox<ShipmentStatus> statusBox;

    private TextField idField;

    private TextField shipIdField;
    
    private Spinner<Integer> amountSpinnter;

    private Button okButton = new Button("OK");

    private Button cancelButton = new Button("Abbrechen");

    public ShipmentEditView() {

        okButton.setOnAction((ActionEvent event) -> {
            saft.exec(() -> {
                if ( contractorBox.getSelectionModel().getSelectedItem() == null ) {
                    saft.build(this).alert("Besitzer nicht ausgewählt");
                    return;
                }
                if ( manufacturerBox.getSelectionModel().getSelectedItem() == null ) {
                    saft.build(this).alert("Hersteller nicht ausgewählt");
                    return;
                }
                if ( statusBox.getSelectionModel().getSelectedItem() == null ) {
                    saft.build(this).alert("Status nicht ausgewählt");
                    return;
                }
                if ( StringUtils.isBlank(shipIdField.getText()) ) {
                    saft.build(this).alert("Keine ShipmentId angegeben");
                    return;
                }
                ok = true;
                showingProperty.set(false);
            });
        });

        cancelButton.setOnAction(e -> showingProperty.set(false));

        idField = new TextField();
        idField.setDisable(true);
        shipIdField = new TextField();

        Set<TradeName> contractors = Dl.local().lookup(CachedMandators.class).loadContractors().all();

        ObservableList<TradeName> manufacturers = FXCollections.observableArrayList(TradeName.getManufacturers());
        manufacturerBox = new ComboBox<>(manufacturers);
        manufacturerBox.setMaxWidth(MAX_VALUE);
        manufacturerBox.setCellFactory(v -> new TradeNameListCell());
        manufacturerBox.setButtonCell(new TradeNameListCell());
        manufacturerBox.getSelectionModel().selectFirst();

        contractorBox = new ComboBox<>(FXCollections.observableArrayList(contractors));
        contractorBox.setMaxWidth(MAX_VALUE);
        contractorBox.setCellFactory(v -> new TradeNameListCell());
        contractorBox.setButtonCell(new TradeNameListCell());
        contractorBox.getSelectionModel().selectedItemProperty()
                .addListener((ObservableValue<? extends TradeName> observable, TradeName oldValue, TradeName newValue) -> {
                    if ( newValue == null ) return;
                    manufacturerBox.getSelectionModel().select(newValue.getManufacturer());
                });
        contractorBox.getSelectionModel().selectFirst();

        statusBox = new ComboBox<>(FXCollections.observableArrayList(ShipmentStatus.values()));
        statusBox.setMaxWidth(MAX_VALUE);
        statusBox.getSelectionModel().selectFirst();
        amountSpinnter = new Spinner<>(0, 100000, 0);
        amountSpinnter.setEditable(true);

        GridPane grid = new GridPane();
        grid.addRow(1, new Label("ID:"), idField);
        grid.addRow(2, new Label("Shipment ID:"), shipIdField);
        grid.addRow(3, new Label("Besitzer:"), contractorBox);
        grid.addRow(4, new Label("Hersteller:"), manufacturerBox);
        grid.addRow(5, new Label("Menge laut Lieferschein"), amountSpinnter);
        grid.addRow(6, new Label("Status"), statusBox);
        grid.setMaxWidth(MAX_VALUE);
        grid.vgapProperty().set(2.);
        grid.getColumnConstraints().add(0, new ColumnConstraints(100, 100, Double.MAX_VALUE, Priority.SOMETIMES, HPos.LEFT, false));
        grid.getColumnConstraints().add(1, new ColumnConstraints(100, 150, Double.MAX_VALUE, Priority.ALWAYS, HPos.LEFT, true));

        HBox hButtonBox = new HBox(okButton, cancelButton);
        hButtonBox.alignmentProperty().set(Pos.TOP_RIGHT);

        setCenter(grid);
        setBottom(hButtonBox);
        setPrefSize(400, 200);
    }

    @Override
    public void accept(Shipment shipment) {
        idField.setText(Long.toString(shipment.getId()));
        shipIdField.setText(shipment.getShipmentId());
        SingleSelectionModel<TradeName> sm = contractorBox.getSelectionModel();
        if ( shipment.getContractor() == null ) sm.selectFirst();
        else sm.select(shipment.getContractor());
        statusBox.getSelectionModel().select(shipment.getStatus() == null ? OPENED : shipment.getStatus());
        if ( shipment.getDefaultManufacturer() != null ) manufacturerBox.getSelectionModel().select(shipment.getDefaultManufacturer());
        amountSpinnter.getValueFactory().setValue(shipment.getAmountOfUnits());
        this.shipment = shipment;
    }

    @Override
    public Shipment getResult() {
        if ( !ok ) return null;
        Shipment result = this.shipment == null ? new Shipment() : this.shipment;
        result.setContractor(contractorBox.getValue());
        result.setDefaultManufacturer(manufacturerBox.getValue());
        result.setShipmentId(shipIdField.getText());
        result.setAmountOfUnits(amountSpinnter.getValue());
        result.setStatus(statusBox.getValue());
        return result;
    }

}
