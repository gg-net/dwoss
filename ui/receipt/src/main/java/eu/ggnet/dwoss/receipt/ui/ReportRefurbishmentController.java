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
package eu.ggnet.dwoss.receipt.ui;

import java.net.URL;
import java.time.LocalDate;
import java.util.Date;
import java.util.ResourceBundle;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import eu.ggnet.dwoss.core.common.values.tradename.TradeName;
import eu.ggnet.dwoss.core.system.util.Utils;
import eu.ggnet.saft.core.ui.*;

import static eu.ggnet.saft.core.ui.Bind.Type.SHOWING;

/**
 *
 * @author mirko.schulze
 */
public class ReportRefurbishmentController implements FxController, ResultProducer<ReportRefurbishmentController.Result>, Initializable {

    public class Result {

        private Date start, end;

        private TradeName tradeName;

        public Result(Date start, Date end, TradeName tradeName) {
            this.start = start;
            this.end = end;
            this.tradeName = tradeName;
        }

        public Date getStart() {
            return start;
        }

        public Date getEnd() {
            return end;
        }

        public TradeName getTradeName() {
            return tradeName;
        }

    }

    @Bind(SHOWING)
    private BooleanProperty showingProperty = new SimpleBooleanProperty();

    private boolean ok;

    @FXML
    private ComboBox<TradeName> tradeNameComboBox;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private Button okButton;

    @FXML
    private Button cancelButton;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tradeNameComboBox.setItems(FXCollections.observableArrayList(TradeName.values()));
        tradeNameComboBox.getSelectionModel().select(0);
        tradeNameComboBox.setCellFactory(new TradeNameListCell.Factory());
        tradeNameComboBox.setButtonCell(new TradeNameListCell());

        startDatePicker.setValue(LocalDate.now());
        endDatePicker.setValue(LocalDate.now());

        okButton.setOnAction(e -> {
            ok = true;
            showingProperty.set(false);
        });
        cancelButton.setOnAction(e -> showingProperty.set(false));
    }

    @Override
    public ReportRefurbishmentController.Result getResult() {
        if ( !ok ) return null;
        return new Result(Utils.toDate(startDatePicker.getValue()), Utils.toDate(endDatePicker.getValue()), tradeNameComboBox.getValue());
    }

}
