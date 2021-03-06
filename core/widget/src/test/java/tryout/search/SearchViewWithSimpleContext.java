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
package tryout.search;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;

import eu.ggnet.dwoss.core.widget.Dl;
import eu.ggnet.saft.core.ui.Title;
import eu.ggnet.dwoss.core.widget.FxOps;
import eu.ggnet.dwoss.core.widget.auth.Guardian;

/**
 *
 * @author oliver.guenther
 */
@Title("Search Simulator")
public class SearchViewWithSimpleContext extends BorderPane {

    public SearchViewWithSimpleContext() {
        final ListView<SearchResult> searchResults = new ListView<>();
        final ObservableList<SearchResult> searchModel = FXCollections.observableArrayList();
        searchResults.setItems(searchModel);
        searchResults.setContextMenu(FxOps.contextMenuOf(searchResults.getSelectionModel(), null, Dl.local().lookup(Guardian.class)));

        setCenter(searchResults);

        Button searchButton = new Button("Simulierte Suche");
        searchButton.setOnAction(e -> {
            searchModel.addAll(VirtualDataSource.search());
        });

        setTop(searchButton);
    }

}
