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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.stage.WindowEvent;

import eu.ggnet.saft.api.ui.DependendAction;
import eu.ggnet.saft.api.ui.Title;
import eu.ggnet.saft.core.Ops;
import eu.ggnet.saft.core.all.UiUtil;
import eu.ggnet.saft.core.fx.FxSaft;

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
            if ( !selectionModel.isEmpty()
                    && mouseEvent.getButton().equals(MouseButton.PRIMARY)
                    && mouseEvent.getClickCount() == 2 ) {
                SearchResult item = selectionModel.getSelectedItem();
                Ops.defaultOf(item).ifPresent(Runnable::run);
            }
        });
        ContextMenu toFxContextMenu = toFxContextMenu(selectionModel);
        System.out.println(toFxContextMenu);
//        toFxContextMenu.getItems().add(new MenuItem("Useless, manually added"));
        searchResults.setContextMenu(toFxContextMenu);

        setCenter(searchResults);

        Button searchButton = new Button("Simulierte Suche");
        searchButton.setOnAction(e -> {
            searchModel.addAll(VirtualDataSource.search());
        });

        setTop(searchButton);

    }

    private <T> ContextMenu toFxContextMenu(SelectionModel<T> selectionModel) {
        try {
            ContextMenu menu = FxSaft.dispatch(() -> new ContextMenu());
            final MenuItem noAction = new MenuItem("No Context Action");
            noAction.setDisable(true);
            menu.getItems().add(noAction);
            final List<MenuItem> lastItems = new ArrayList<>();
            menu.setOnHidden(e -> {
                menu.getItems().removeAll(lastItems);
                if ( menu.getItems().isEmpty() ) menu.getItems().add(noAction);
            });
            menu.setOnShowing(new EventHandler<WindowEvent>() {

                @Override
                public void handle(WindowEvent event) {
                    System.out.println("Showing");
                    if ( selectionModel.isEmpty() ) return;
                    T t = selectionModel.getSelectedItem();
                    List<DependendAction> actions = Ops.REGISTERED_ACTIONS.get(t.getClass());
                    if ( actions == null) return ;
                    lastItems.clear();
                    menu.getItems().remove(noAction);
                    for (DependendAction action : actions) {
                        MenuItem item = new MenuItem(UiUtil.title(action.getClass()));
                        item.setOnAction(e -> action.run(t));
                        lastItems.add(item);
                        menu.getItems().add(item);
                    }
                }
            });

            return menu;
        } catch (InterruptedException ex) {
            Logger.getLogger(SearchView.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ExecutionException ex) {
            Logger.getLogger(SearchView.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

}
