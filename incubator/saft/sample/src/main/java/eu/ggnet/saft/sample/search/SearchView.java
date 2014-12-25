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
package eu.ggnet.saft.sample.search;

import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;

import eu.ggnet.saft.api.ui.Title;


/**
 *
 * @author oliver.guenther
 */
@Title("Search Simulator")
public class SearchView extends BorderPane {

    public SearchView() {
        final ListView<SearchResult> searchResults = new ListView<>();
        final ObservableList<SearchResult> searchModel = FXCollections.observableArrayList();
        searchResults.setItems(searchModel);
        final MultipleSelectionModel<SearchResult> selectionModel = searchResults.getSelectionModel();
        searchResults.setOnMouseClicked((mouseEvent) -> {
            if ( mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2 ) {
                if (selectionModel.isEmpty()) return;
                SearchResult item = selectionModel.getSelectedItem();
                Optional<Object> o = null;
                o.ifPresent(x -> x.notify());
                // Ops.defaultOf(item.getClass()).ifPresent(x -> x.exec(item));
            }
        });

        setCenter(searchResults);

        Button searchButton = new Button("Simulierte Suche");
        searchButton.setOnAction(e -> {
            searchModel.addAll(VirtualDataSource.search());
        });

        setTop(searchButton);

    }

}
