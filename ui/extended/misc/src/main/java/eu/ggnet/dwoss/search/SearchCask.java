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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.search.api.SearchRequest;
import eu.ggnet.dwoss.search.api.ShortSearchResult;
import eu.ggnet.dwoss.search.op.Searcher;
import eu.ggnet.saft.api.ui.ClosedListener;
import eu.ggnet.saft.api.ui.Title;
import eu.ggnet.saft.core.Client;
import eu.ggnet.saft.core.UiCore;

import static java.lang.Double.MAX_VALUE;
import static javafx.concurrent.Worker.State.READY;

/**
 * Search View, new Version.
 * Uses {@link Searcher}.
 *
 * @author oliver.guenther
 */
@Title("Suche")
public class SearchCask extends BorderPane implements ClosedListener {

    private final Service<List<ShortSearchResult>> searchService;

    private final Logger LOG = LoggerFactory.getLogger(SearchCask.class);

    public SearchCask() {
        // Creating and laying out the Ui
        StringProperty searchProperty = new SimpleStringProperty();
        ObservableList<ShortSearchResult> resultProperty = FXCollections.observableArrayList();

        TextField searchField = new TextField();
        Button searchButton = new Button("Suchen");
        ListView<ShortSearchResult> resultListView = new ListView<>();

        ProgressBar progressBar = new ProgressBar();
        progressBar.setMaxWidth(MAX_VALUE); // Needed, so the bar will fill the space, otherwise it keeps beeing small
        progressBar.setMaxHeight(MAX_VALUE);// Needed, so the bar will fill the space, otherwise it keeps beeing small
        ProgressIndicator progressIndicator = new ProgressIndicator();

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

        // Search Service. Creates for every search request a task, which picks up results in the background. Optional, cancels the allready running task.
        searchService = new Service<List<ShortSearchResult>>() {

            private Searcher searcher;

            @Override
            protected Task<List<ShortSearchResult>> createTask() {
                if ( searcher == null ) searcher = Client.lookup(Searcher.class);

                return new Task<List<ShortSearchResult>>() {
                    @Override
                    protected List<ShortSearchResult> call() throws Exception {
                        updateProgress(-1, -1);
                        if ( StringUtils.isEmpty(searchProperty.get()) ) return Collections.EMPTY_LIST; // Empty check.
                        LOG.info("Searching {}", searchProperty.get());
                        searcher.initSearch(new SearchRequest(searchProperty.get()));
                        List<ShortSearchResult> last = Collections.EMPTY_LIST;
                        int done = 0;
                        while (!isCancelled() && searcher.hasNext()) {
                            last = searcher.next();
                            LOG.debug("Search of {} found {}", searchProperty.get(), last);
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

        // Binding all Ui Properties
        UiCore.observeProgress(searchService);
        searchProperty.bind(searchField.textProperty());
        resultListView.itemsProperty().bind(new SimpleListProperty<>(resultProperty));
        progressBar.progressProperty().bind(searchService.progressProperty());
        progressIndicator.progressProperty().bind(searchService.progressProperty());
        bottom.visibleProperty().bind(searchService.runningProperty());

        // Adding Actions and Listeners
        searchService.valueProperty().addListener((ob, o, n) -> {
            if ( n == null ) resultProperty.clear(); // happens if service has allready a value and is reinited.
            else resultProperty.addAll(n);
        });

        searchButton.setOnAction((ActionEvent event) -> search());
        searchField.setOnKeyPressed((ke) -> {
            if ( ke.getCode() == KeyCode.ENTER ) search();
        });

    }

    private void search() {
        if ( searchService.getState() == READY ) searchService.start();
        else searchService.restart();
    }

    @Override
    public void closed() {
        if ( searchService != null && searchService.isRunning() ) searchService.cancel();
    }

    // TODO: optional on hide: pause search
    // TODO: implement a stop/cancel search
}
