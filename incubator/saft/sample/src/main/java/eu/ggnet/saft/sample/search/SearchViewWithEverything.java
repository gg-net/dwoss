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

import eu.ggnet.saft.experimental.ops.Selector;
import eu.ggnet.saft.experimental.FxOps;
import eu.ggnet.saft.experimental.Ops;

import java.util.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;

import eu.ggnet.saft.core.ui.ClosedListener;
import eu.ggnet.saft.core.ui.Title;
import eu.ggnet.saft.experimental.ops.SelectionEnhancer;

/**
 *
 * @author oliver.guenther
 */
@Title("Search Simulator")
public class SearchViewWithEverything extends BorderPane implements ClosedListener {

    private final Selector<SearchResult> selector;

    public SearchViewWithEverything() {
        SelectionEnhancer<SearchResult> enhancer = (SearchResult selected) -> {
            if ( selected instanceof MicroUnitDossier ) {
                MicroUnitDossier mud = (MicroUnitDossier)selected;
                return Arrays.asList(new MicroUnit(mud.uniqueUnitId, mud.shortDescription), new MicroDossier(mud.dossierId, mud.shortDescription));
            }
            return new ArrayList<>();
        };

        selector = Ops.seletor(SearchResult.class, enhancer);
        final ListView<SearchResult> searchResults = new ListView<>();
        final ObservableList<SearchResult> searchModel = FXCollections.observableArrayList();
        searchResults.setItems(searchModel);
        final MultipleSelectionModel<SearchResult> selectionModel = searchResults.getSelectionModel();
        selectionModel.selectedItemProperty().addListener((ob, o, n) -> {
            selector.selected(n);
        });

        searchResults.setOnMouseClicked(FxOps.defaultMouseEventOf(selectionModel));

        ContextMenu toFxContextMenu = FxOps.contextMenuOf(selectionModel, enhancer);

        searchResults.setContextMenu(toFxContextMenu);

        setCenter(searchResults);

        Button searchButton = new Button("Simulierte Suche");
        searchButton.setOnAction(e -> {
            searchModel.addAll(VirtualDataSource.search());
        });

        setTop(searchButton);
    }

    @Override
    public void closed() {
        selector.clear();
    }

}
