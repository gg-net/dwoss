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
package eu.ggnet.dwoss.customer.ui;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.BorderPane;

/**
 * Dialog to display a collection of String in a Listview.
 *
 *
 *
 *
 * @author pascal.perau
 */
public class ViolationDialog extends BorderPane implements Consumer<List<String>> {

    private ObservableList<String> items;

    private Label topLabel;

    private ListView<String> lv;

    public ViolationDialog() {
        topLabel = new Label();
        items = FXCollections.observableArrayList();
        lv = new ListView<>(items);
        this.setCenter(lv);

        Button copyToClipboard = new Button("In Zwischenablage Kopieren");
        copyToClipboard.setOnAction((event) -> {
            ClipboardContent content = new ClipboardContent();
            content.putString(items.stream().collect(Collectors.joining("\n")));
            Clipboard.getSystemClipboard().clear();
            Clipboard.getSystemClipboard().setContent(content);
        });
        this.setTop(topLabel);
        this.setBottom(copyToClipboard);
    }

    @Override
    public void accept(List<String> t) {
        items.clear();
        items.addAll(t);

        String nullAdress = t.stream().filter(s -> s.contains("Address is null")).count() + " mit NULL Addresse";
        String blankPartAdress = t.stream().filter(s -> s.contains("Address:")).count() + " mit leerem Addresseteil";
        String invalidEmailAdress = t.stream().filter(s -> s.contains("EMAIL")).count() + " mit invalider Email";

        topLabel.setText(t.size() + " ViolationMessages gefunden, " + nullAdress + ", " + blankPartAdress + ", " + invalidEmailAdress);

    }

}
