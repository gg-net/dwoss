/*
 * Copyright (C) 2014 GG-Net GmbH
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
package eu.ggnet.dwoss.stock.ui.transactions;

import jakarta.inject.Inject;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import eu.ggnet.dwoss.core.widget.dl.RemoteDl;
import eu.ggnet.dwoss.stock.ee.StockAgent;
import eu.ggnet.dwoss.stock.ee.entity.Stock;
import eu.ggnet.saft.core.Saft;
import eu.ggnet.saft.core.ui.*;

import jakarta.enterprise.context.Dependent;

/**
 * FXML Controller class
 *
 * @author oliver.guenther
 */
@Dependent
@Title("Umfuhr für (ein) einzelne(s) Gerät(e)")
public class CreateSelectionController implements FxController, ResultProducer<CreateSelectionController> {

    private boolean ok = false;

    @Inject
    private Saft saft;

    @Inject
    private RemoteDl remote;

    @FXML
    private GridPane root;

    @FXML
    private TextField refurbishIds;

    @FXML
    private TextField comment;

    @FXML
    private ComboBox<Stock> target;

    @FXML
    void initialize() {
        target.setCellFactory(new StockListCell.Factory());
        target.setButtonCell(new StockListCell());
        target.getItems().addAll(remote.lookup(StockAgent.class).findAll(Stock.class));
    }

    @FXML
    void okPressed() {
        ok = true;
        saft.closeWindowOf(root);
    }

    @FXML
    void cancelPressed() {
        saft.closeWindowOf(root);
    }

    public Stock target() {
        return target.getSelectionModel().getSelectedItem();
    }

    public String refurbishIds() {
        return refurbishIds.getText();
    }

    public String comment() {
        return comment.getText();
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "(target=" + target() + ", refurbishIds=" + refurbishIds() + ", comment" + comment() + ")";
    }

    @Override
    public CreateSelectionController getResult() {
        if ( ok ) return this;
        return null;
    }

}
