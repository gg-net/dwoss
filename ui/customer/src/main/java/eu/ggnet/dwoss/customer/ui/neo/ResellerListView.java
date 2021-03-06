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
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;

import eu.ggnet.dwoss.customer.ee.CustomerAgent;
import eu.ggnet.dwoss.customer.ee.entity.Customer;
import eu.ggnet.dwoss.core.widget.Dl;

/**
 *
 * @author oliver.guenther
 */
public class ResellerListView extends BorderPane implements Consumer<List<Customer>> {

    private final TableView<Customer> table;

    public ResellerListView() {
        table = new TableView<>();
        table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        TableColumn<Customer, Number> tcKid = new TableColumn<>("Kid");
        TableColumn<Customer, String> tcName = new TableColumn<>("Name");
        TableColumn<Customer, String> tcMail = new TableColumn<>("Email für Händlerliste");

        tcKid.setCellValueFactory((cdf) -> new ReadOnlyLongWrapper(cdf.getValue().getId()).getReadOnlyProperty());
        tcName.setCellValueFactory((cdf) -> new ReadOnlyStringWrapper(cdf.getValue().toName()).getReadOnlyProperty());
        tcMail.setCellValueFactory((cdf) -> new ReadOnlyStringWrapper(cdf.getValue().getResellerListEmailCommunication().get().getIdentifier()).getReadOnlyProperty());

        table.getColumns().addAll(tcKid, tcName, tcMail);
        MenuItem select = new MenuItem("Abonnement beenden");
        select.setOnAction(e -> {
            Customer c = table.getSelectionModel().getSelectedItem();
            Dl.remote().lookup(CustomerAgent.class).clearResellerListEmailCommunication(c.getId());
            table.getItems().remove(c);
            table.getSelectionModel().clearSelection();
        });

        ContextMenu cm = new ContextMenu();
        cm.getItems().add(select);

        table.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent t1) -> {
            if ( t1.getButton() == MouseButton.SECONDARY ) {
                cm.show(table, t1.getScreenX(), t1.getScreenY());
            }
        });

        setCenter(table);
        setPrefWidth(500);
    }

    @Override
    public void accept(List<Customer> in) {
        table.setItems(FXCollections.observableArrayList(in));
    }

}
