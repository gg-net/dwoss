/*
 * Copyright (C) 2019 GG-Net GmbH
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
package eu.ggnet.dwoss.customer.ui.neo;

import java.util.List;
import java.util.function.Consumer;

import javafx.beans.property.ReadOnlyLongWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;

import eu.ggnet.dwoss.customer.api.ResellerListCustomer;

/**
 *
 * @author oliver.guenther
 */
public class ResellerListView extends BorderPane implements Consumer<List<ResellerListCustomer>> {

    private final TableView<ResellerListCustomer> table;

    public ResellerListView() {
        table = new TableView<>();
        TableColumn<ResellerListCustomer, Number> tcKid = new TableColumn<>("Kid");
        TableColumn<ResellerListCustomer, String> tcName = new TableColumn<>("Name");
        TableColumn<ResellerListCustomer, String> tcMail = new TableColumn<>("Email für Händlerliste");

        tcKid.setCellValueFactory((cdf) -> new ReadOnlyLongWrapper(cdf.getValue().id()).getReadOnlyProperty());
        tcName.setCellValueFactory((cdf) -> new ReadOnlyStringWrapper(cdf.getValue().name()).getReadOnlyProperty());
        tcMail.setCellValueFactory((cdf) -> new ReadOnlyStringWrapper(cdf.getValue().email()).getReadOnlyProperty());

        table.getColumns().addAll(tcKid, tcName, tcMail);

        setCenter(table);
        setPrefWidth(500);

    }

    @Override
    public void accept(List<ResellerListCustomer> in) {
        table.setItems(FXCollections.observableArrayList(in));
    }

}
