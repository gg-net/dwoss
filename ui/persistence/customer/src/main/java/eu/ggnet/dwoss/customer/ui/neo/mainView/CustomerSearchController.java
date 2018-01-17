package eu.ggnet.dwoss.customer.ui.neo.mainView;

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
import java.net.URL;
import java.util.*;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;

import org.apache.commons.lang3.StringUtils;

import eu.ggnet.dwoss.customer.op.CustomerSearchProvider;
import eu.ggnet.dwoss.customer.ui.CustomerTask;
import eu.ggnet.dwoss.search.api.SearchRequest;
import eu.ggnet.dwoss.search.api.ShortSearchResult;
import eu.ggnet.saft.Ui;
import eu.ggnet.saft.api.ui.*;
import eu.ggnet.saft.core.ui.FxSaft;

import static java.lang.Double.MAX_VALUE;
import static javafx.concurrent.Worker.State.READY;

/**
 * FXML Controller class
 *
 * @author jens.papenhagen
 */
@Title("Kunden Suche")
public class CustomerSearchController implements Initializable, FxController, ClosedListener {

    private final CustomerTask LOADING_TASK = new CustomerTask();

    @FXML
    private Button searchButton;

    @FXML
    private TextField searchField;

    @FXML
    private CheckBox kid;

    @FXML
    private CheckBox lastname;

    @FXML
    private CheckBox firstname;

    @FXML
    private CheckBox company;

    @FXML
    private ListView<ShortSearchResult> resultListView;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private ProgressIndicator progressIndicator;

    @FXML
    private HBox bottom;

    private Service<List<ShortSearchResult>> searchService;

    private CustomerSearchProvider searcher;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Creating and laying out the Ui
        StringProperty searchProperty = new SimpleStringProperty();
        ObservableList<ShortSearchResult> resultProperty = FXCollections.observableArrayList();

        resultListView = new ListView<>();
//        resultListView.setCellFactory(new SearchListCell.Factory());

        progressBar.setMaxWidth(MAX_VALUE); // Needed, so the bar will fill the space, otherwise it keeps beeing small
        progressBar.setMaxHeight(MAX_VALUE);// Needed, so the bar will fill the space, otherwise it keeps beeing small

        // Search Service. Creates for every search request a task, which picks up results in the background. Optional, cancels the allready running task.
        searchService = new Service<List<ShortSearchResult>>() {

            @Override
            protected Task<List<ShortSearchResult>> createTask() {

                return new Task<List<ShortSearchResult>>() {
                    @Override
                    protected List<ShortSearchResult> call() throws Exception {
                        updateProgress(-1, -1);
                        if ( StringUtils.isEmpty(searchProperty.get()) ) return Collections.EMPTY_LIST; // Empty check.
                        List<ShortSearchResult> searchlist = searcher.search(new SearchRequest(searchProperty.get()), 0, searcher.estimateMaxResults(new SearchRequest(searchProperty.get())));
                        List<ShortSearchResult> last = Collections.EMPTY_LIST;

                        int done = 0;
                        int i = 0;

                        while (!isCancelled() && i < searchlist.size()) {
                            last.add(searchlist.get(i));
                            done = done + last.size();
                            updateValue(last);
                            int estimate = searcher.estimateMaxResults(new SearchRequest(searchProperty.get()));
                            updateMessage("Searchresult " + done + " of " + estimate);
                            updateProgress(done, estimate);
                            i++;
                        }
                        updateProgress(100, 100);

                        return last;
                    }
                };
            }
        };

        // Adding Actions and Listeners
        searchService.valueProperty().addListener((ob, o, n) -> {
            if ( n == null ) resultProperty.clear(); // happens if service has allready a value and is reinited.
            else resultProperty.addAll(n);
        });

        searchButton.setOnAction((ActionEvent event) -> search());
        searchField.setOnKeyPressed((ke) -> {
            if ( ke.getCode() == KeyCode.ENTER ) search();
        });

        // Binding all Ui Properties
        Ui.progress().observe(searchService);
        searchProperty.bind(searchField.textProperty());
        resultListView.itemsProperty().bind(new SimpleListProperty<>(resultProperty));

        progressBar.progressProperty().bind(searchService.progressProperty());
        progressIndicator.progressProperty().bind(searchService.progressProperty());

        bottom.visibleProperty().bind(searchService.runningProperty());

        Ui.progress().observe(LOADING_TASK);
        Ui.exec(LOADING_TASK);

    }

    private void search() {
      // if(kid.isSelected()) { String kundennummer;}
        
        
        if ( searchService.getState() == READY ) searchService.start();
        else searchService.restart();
    }

    @Override
    public void closed() {
        FxSaft.dispatch(() -> {
            if ( LOADING_TASK.isRunning() ) LOADING_TASK.cancel();
            return null;
        });
    }

}
