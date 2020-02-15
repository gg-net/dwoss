/*
 * Copyright (C) 2020 GG-Net GmbH
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
package eu.ggnet.dwoss.redtapext.ui.cap;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.web.WebView;
import javafx.util.Callback;

import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.redtapext.ee.UnitOverseer;
import eu.ggnet.dwoss.uniqueunit.api.UnitShard;
import eu.ggnet.saft.core.Dl;

/**
 * A Ui Element to verify the status of of any refurbished unit quickly.
 *
 * @author oliver.guenther
 */
public class UnitAvailabilityPane extends BorderPane {

    private final TextField searchField;

    private final Button clearButton;

    private final ObservableList<UnitShard> results;

    public UnitAvailabilityPane() {
        searchField = new TextField();
        clearButton = new Button("Liste leeren");

        HBox top = new HBox(5, new Label("SopoNr:"), searchField, clearButton);
        top.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(searchField, Priority.ALWAYS);

        results = FXCollections.observableArrayList();

        ListView<UnitShard> resultListView = new ListView<>(results);
        

        resultListView.setCellFactory((ListView<UnitShard> view) -> new ListCell<>() {
            @Override
            protected void updateItem(UnitShard us, boolean empty) {
                super.updateItem(us, empty);
                setText(null);
                if ( empty ) {
                    setGraphic(null);
                } else {
                    WebView wv = new WebView();
                    wv.getEngine().loadContent(us.getHtmlDescription(), "text/html");
                    wv.setPrefWidth(USE_COMPUTED_SIZE);
                    wv.setPrefHeight(80); // TODO: no good solution here, because the webengine can't estimate the size of the result.
                    setGraphic(wv);
                }
            }

        });

        searchField.setOnAction((e) -> {
            String refurbishedId = searchField.getText().trim();
            UnitShard us = Dl.remote().lookup(UnitOverseer.class).find(refurbishedId);
            LoggerFactory.getLogger(UnitAvailabilityPane.class).debug("search({}) found {}", refurbishedId, us);
            results.add(0, us);
            searchField.setText("");
        });

        setTop(top);
        setCenter(resultListView);

    }

}
