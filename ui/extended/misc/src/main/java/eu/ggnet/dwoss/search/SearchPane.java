/*
 * Copyright (C) 2017 GG-Net GmbH
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
package eu.ggnet.dwoss.search;

import java.util.Collections;
import java.util.List;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;

import org.apache.commons.lang3.StringUtils;

import eu.ggnet.dwoss.search.api.SearchResult;
import eu.ggnet.dwoss.search.op.Searcher;
import eu.ggnet.saft.api.ui.ClosedListener;
import eu.ggnet.saft.api.ui.Title;
import eu.ggnet.saft.core.Client;

import static java.lang.Double.MAX_VALUE;
import static javafx.concurrent.Worker.State.READY;

/**
 * Search View, new Version.
 *
 * @author oliver.guenther
 */
@Title("Suche")
public class SearchPane extends BorderPane implements ClosedListener {

    private final TextField searchField;

    private final Button searchButton;

    private final ListView<SearchResult> resultListView;

    private final StringProperty search;

    private final ObservableList<SearchResult> result;

    private Service<List<SearchResult>> searchService;

    public SearchPane() {
        search = new SimpleStringProperty();
        result = FXCollections.observableArrayList();

        searchField = new TextField();
        searchButton = new Button("Suchen");
        resultListView = new ListView<>();

        ProgressBar progressBar = new ProgressBar();
        progressBar.setMaxWidth(MAX_VALUE);
        progressBar.setMaxHeight(MAX_VALUE);
        ProgressIndicator progressIndicator = new ProgressIndicator();

        progressBar.setProgress(0);
        progressIndicator.setProgress(0);

        BorderPane top = new BorderPane();
        top.setLeft(new Label("Frage:"));
        top.setRight(searchButton);
        top.setCenter(searchField);

        BorderPane bottom = new BorderPane();
        bottom.setCenter(progressBar);
        bottom.setRight(progressIndicator);

        setTop(top);
        setCenter(resultListView);
        setBottom(bottom);

        searchField.textProperty().bindBidirectional(search);
        resultListView.itemsProperty().bind(new SimpleListProperty<>(result));

        searchButton.setOnAction((ActionEvent event) -> {
            if ( searchService.getState() == READY ) searchService.start();
            else searchService.restart();
        });

        searchField.setOnKeyPressed((ke) -> {
            if ( ke.getCode() == KeyCode.ENTER ) {
                if ( searchService.getState() == READY ) searchService.start();
                else searchService.restart();
            }
        });

        searchService = new Service<List<SearchResult>>() {

            private Searcher searcher;

            @Override
            protected Task<List<SearchResult>> createTask() {
                if ( searcher == null ) searcher = Client.lookup(Searcher.class);

                return new Task<List<SearchResult>>() {
                    @Override
                    protected List<SearchResult> call() throws Exception {
                        updateProgress(-1, -1);
                        if ( StringUtils.isEmpty(search.get()) ) return Collections.EMPTY_LIST; // Empty check.
                        searcher.initSearch(search.get());
                        List<SearchResult> last = Collections.EMPTY_LIST;
                        int done = 0;
                        while (!isCancelled() && searcher.hasNext()) {
                            last = searcher.next();
                            done = done + last.size();
                            updateValue(last);
                            int estimate = searcher.estimateMaxResults();
                            updateMessage("Searchresult " + done + " of " + estimate);
                            updateProgress(done, estimate);
                        }
                        updateProgress(100, 100);
                        return last;
                    }
                };
            }
        };

        searchService.valueProperty().addListener((ob, o, n) -> {
            if ( n == null ) result.clear(); // happens if service has allready a value and is reinited.
            else result.addAll(n);
        });

        progressBar.progressProperty().bind(searchService.progressProperty());
        progressIndicator.progressProperty().bind(searchService.progressProperty());

        bottom.visibleProperty().bind(searchService.runningProperty());

    }

    @Override
    public void closed() {
        if ( searchService != null && searchService.isRunning() ) searchService.cancel();
    }

    // TODO: optional on hide: pause search
    // TODO: implement a stop/cancel search
}
