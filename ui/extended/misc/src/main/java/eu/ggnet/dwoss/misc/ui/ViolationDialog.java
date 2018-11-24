/*
 * Copyright (C) 2018 GG-Net GmbH
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
package eu.ggnet.dwoss.misc.ui;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import eu.ggnet.dwoss.customer.ee.entity.Customer;

/**
 * Dialog to display a collection of String in a Listview.
 *
 *
 *
 *
 * @author pascal.perau
 */
public class ViolationDialog extends BorderPane implements Consumer<Map<String, List<Customer>>> {

    private Label topLabel;

    private TabPane tabPane;

    public ViolationDialog() {
        topLabel = new Label();
        tabPane = new TabPane();
        this.setTop(topLabel);
        this.setCenter(tabPane);
    }

    @Override
    public void accept(Map<String, List<Customer>> t) {

        tabPane.getTabs().clear();

        tabPane.getTabs().addAll(t.entrySet().stream().map(entry -> {

            ObservableList<Customer> items = FXCollections.observableArrayList(entry.getValue());
            ListView lv = new ListView<>(items);

            Button copyToClipboard = new Button("In Zwischenablage Kopieren");
            copyToClipboard.setOnAction((event) -> {
                ClipboardContent content = new ClipboardContent();
                content.putString(items.stream().map(c -> c.getId() + " - " + c.toName()).collect(Collectors.joining("\n")));
                Clipboard.getSystemClipboard().clear();
                Clipboard.getSystemClipboard().setContent(content);
            });

            VBox vb = new VBox(new Label("Violation: " + entry.getKey()), lv, copyToClipboard);
            return new Tab(entry.getKey(), vb);
        }).collect(Collectors.toList()));

        topLabel.setText(t.size() + " ViolationMessages gefunden");

    }

}
