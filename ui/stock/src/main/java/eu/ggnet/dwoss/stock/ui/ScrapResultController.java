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
package eu.ggnet.dwoss.stock.ui;

import java.net.URL;
import java.util.*;
import java.util.function.Consumer;

import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;

import eu.ggnet.dwoss.stock.api.StockApi;
import eu.ggnet.saft.core.ui.Bind;
import eu.ggnet.saft.core.ui.FxController;

import static eu.ggnet.saft.core.ui.Bind.Type.SHOWING;

public class ScrapResultController implements FxController, Consumer<List<StockApi.Scraped>> {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TableView<StockApi.Scraped> resultTableView;

    @FXML
    private Button closeButton;

    @Bind(SHOWING)
    private final BooleanProperty showingProperty = new SimpleBooleanProperty();

    @FXML
    void initialize() {
        TableColumn<StockApi.Scraped, String> descriptionColumn = new TableColumn<>("Beschreibung");
        descriptionColumn.setCellValueFactory(item -> new ReadOnlyStringWrapper(item.getValue().description()).getReadOnlyProperty());
        TableColumn<StockApi.Scraped, Boolean> successColumn = new TableColumn<>("Verschrottung erfolgreich");
        successColumn.setCellValueFactory(item -> new SimpleBooleanProperty(item.getValue().successful())); // Only a R/W Property works fine with the default CheckBoxTableCell.
        successColumn.setCellFactory(item -> new CheckBoxTableCell<>());

        TableColumn<StockApi.Scraped, String> commentColumn = new TableColumn<>("Kommentar");
        commentColumn.setCellValueFactory(item -> new ReadOnlyStringWrapper(item.getValue().comment()).getReadOnlyProperty());

        resultTableView.getColumns().addAll(descriptionColumn, successColumn, commentColumn);
        closeButton.setOnAction(e -> showingProperty.set(false));
    }

    @Override
    public void accept(List<StockApi.Scraped> in) {
        resultTableView.getItems().addAll(Objects.requireNonNull(in, "in must not be null"));
    }
}
