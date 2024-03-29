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
package eu.ggnet.dwoss.search.ui;

import java.util.Collections;
import java.util.List;

import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.core.widget.*;
import eu.ggnet.dwoss.core.widget.ops.Selector;
import eu.ggnet.dwoss.search.api.SearchRequest;
import eu.ggnet.dwoss.search.api.ShortSearchResult;
import eu.ggnet.dwoss.search.ee.Searcher;
import eu.ggnet.dwoss.uniqueunit.api.PicoUnit;
import eu.ggnet.saft.core.Ui;
import eu.ggnet.saft.core.ui.*;

import jakarta.enterprise.context.Dependent;

import static eu.ggnet.dwoss.search.api.GlobalKey.Component.UNIQUE_UNIT;
import static java.lang.Double.MAX_VALUE;
import static javafx.concurrent.Worker.State.READY;
import static javafx.scene.input.MouseButton.PRIMARY;

/**
 * Search View, new Version.
 * Uses {@link Searcher}.
 *
 * @author oliver.guenther
 */
@Dependent
@Title("Suche")
@Frame
public class SearchCask extends BorderPane implements ClosedListener {

    public final static String ONCE_KEY = "Search";

    private final Service<List<ShortSearchResult>> searchService;

    private final Logger LOG = LoggerFactory.getLogger(SearchCask.class);

    private final Searcher searcher;

    private final BorderPane bottom;

    public SearchCask() {
        searcher = Dl.remote().lookup(Searcher.class);
        // Creating and laying out the Ui
        StringProperty searchProperty = new SimpleStringProperty();
        ObservableList<ShortSearchResult> resultProperty = FXCollections.observableArrayList();

        TextField searchField = new TextField();
        Button searchButton = new Button("Suchen");
        ListView<ShortSearchResult> resultListView = new ListView<>();
        resultListView.setCellFactory(new SearchListCell.Factory());

        ProgressBar progressBar = new ProgressBar();
        progressBar.setMaxWidth(MAX_VALUE); // Needed, so the bar will fill the space, otherwise it keeps beeing small
        progressBar.setMaxHeight(MAX_VALUE);// Needed, so the bar will fill the space, otherwise it keeps beeing small
        ProgressIndicator progressIndicator = new ProgressIndicator();

        BorderPane top = new BorderPane();
        top.setLeft(new Label("Frage:"));
        top.setRight(searchButton);
        top.setCenter(searchField);

        bottom = new BorderPane();
        bottom.setCenter(progressBar);
        bottom.setRight(progressIndicator);

        setTop(top);
        setCenter(resultListView);
        setBottom(bottom);

        // Search Service. Creates for every search request a task, which picks up results in the background. Optional, cancels the allready running task.
        searchService = new Service<List<ShortSearchResult>>() {

            @Override
            protected Task<List<ShortSearchResult>> createTask() {

                return new Task<List<ShortSearchResult>>() {
                    @Override
                    protected List<ShortSearchResult> call() throws Exception {
                        updateProgress(-1, -1);
                        if ( StringUtils.isBlank(searchProperty.get()) ) return Collections.emptyList(); // Empty check.
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
        Progressor.global().observe(searchService);
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

        searchButton.setOnAction(e -> search());
        searchField.setOnKeyPressed((ke) -> {
            if ( ke.getCode() == KeyCode.ENTER ) search();
        });

        resultListView.setOnMouseClicked((MouseEvent click) -> {
            MultipleSelectionModel<ShortSearchResult> selection = resultListView.getSelectionModel();
            if ( !selection.isEmpty() && click.getClickCount() == 2 && click.getButton() == PRIMARY ) {
                Ui.build(SearchCask.this).title(selection.getSelectedItem().key.toString()).fx()
                        .show(() -> searcher.details(selection.getSelectedItem().key), () -> new HtmlPane());
            }
        });

        final Selector<PicoUnit> seletor = Ops.seletor(PicoUnit.class);

        resultListView.getSelectionModel().getSelectedItems().addListener((Change<? extends ShortSearchResult> c) -> {
            while (c.next()) {
                // Call the selector of PicoUnit if the slected element is of picounit.
                if ( c.getAddedSubList().isEmpty() ) continue;
                ShortSearchResult selectedResult = c.getAddedSubList().get(0);
                if ( selectedResult.key.component != UNIQUE_UNIT ) continue;
                seletor.selected(new PicoUnit((int)selectedResult.key.id, selectedResult.shortDescription));
            }
        });
    }

    public void disableProgressBar() {
        this.getChildren().remove(bottom);
    }

    private void search() {
        if ( searchService.getState() == READY ) searchService.start();
        else searchService.restart();
    }

    @Override
    public void closed() {
        if ( searchService == null ) return;
        Platform.runLater(() -> { // The isRunning() must be called in the Fx Thread.
            if ( searchService.isRunning() ) searchService.cancel();
        });
    }

    // TODO: optional on hide: pause search
    // TODO: implement a stop/cancel search
}
