/*
 * Copyright (C) 2021 GG-Net GmbH
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
package eu.ggnet.dwoss.receipt.ui.unit;

import eu.ggnet.dwoss.receipt.ui.PicoStockListCell;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;

import eu.ggnet.dwoss.stock.api.PicoStock;
import eu.ggnet.saft.core.ui.*;

import jakarta.enterprise.context.Dependent;

import static eu.ggnet.saft.core.ui.Bind.Type.SHOWING;

/**
 *
 * @author mirko.schulze
 */
@Dependent
public class StockController implements FxController, Consumer<StockController.In>, ResultProducer<PicoStock>, Initializable {

    public static class In {

        private List<PicoStock> stocks;

        private PicoStock selectedStock;

        public In(List<PicoStock> stocks, PicoStock selectedStock) {
            this.stocks = stocks;
            this.selectedStock = selectedStock;
        }

    }

    private boolean ok;

    @Bind(SHOWING)
    private BooleanProperty showingProperty = new SimpleBooleanProperty();

    @FXML
    private ComboBox<PicoStock> stockComboBox;

    @FXML
    private Button okButton;

    @FXML
    private Button cancelButton;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        stockComboBox.setCellFactory(new PicoStockListCell.Factory());
        stockComboBox.setButtonCell(new PicoStockListCell());

        okButton.setOnAction(e -> {
            ok = true;
            showingProperty.set(false);
        });

        cancelButton.setOnAction(e -> showingProperty.set(false));
    }

    @Override
    public void accept(StockController.In stockDto) {
        stockComboBox.setItems(FXCollections.observableArrayList(stockDto.stocks));
        stockComboBox.getSelectionModel().select(stockDto.selectedStock);
    }

    @Override
    public PicoStock getResult() {
        return ok == true ? stockComboBox.getValue() : null;
    }

}
