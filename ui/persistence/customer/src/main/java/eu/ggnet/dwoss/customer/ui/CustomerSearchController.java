package eu.ggnet.dwoss.customer.ui;

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
import java.util.function.Predicate;

import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;

import org.apache.commons.lang3.StringUtils;

import eu.ggnet.dwoss.customer.entity.Customer;
import eu.ggnet.dwoss.search.api.SearchRequest;
import eu.ggnet.dwoss.search.api.ShortSearchResult;
import eu.ggnet.dwoss.search.op.Searcher;
import eu.ggnet.dwoss.search.ui.SearchListCell;
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
    Button searchButton;

    @FXML
    TextField searchField;

    @FXML
    CheckBox kid;

    @FXML
    CheckBox lastname;

    @FXML
    CheckBox firstname;

    @FXML
    ListView<ShortSearchResult> resultListView;

    @FXML
    ProgressBar progressBar;

    @FXML
    ProgressIndicator progressIndicator;

    @FXML
    BorderPane bottom;

    private Service<List<ShortSearchResult>> searchService;

    private Searcher searcher;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Creating and laying out the Ui
        StringProperty searchProperty = new SimpleStringProperty();
        ObservableList<ShortSearchResult> resultProperty = FXCollections.observableArrayList();

        resultListView = new ListView<>();
        resultListView.setCellFactory(new SearchListCell.Factory());

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
                        searcher.initSearch(new SearchRequest(searchProperty.get()));
                        List<ShortSearchResult> last = Collections.EMPTY_LIST;
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

//        //convert listview to a filter list, for the checkbox
//        FilteredList resultListFiltered = new FilteredList<>(resultProperty);
//        SortedList resultListSorted = new SortedList<>(resultListFiltered);
//        resultListSorted.comparatorProperty().bind(resultListView.itemsProperty());
//        
//        //clear than set new soreted list
//        resultListFiltered.setPredicate(getPredicate());
//        resultListView.getItems().clear();
//        resultListView.setItems(resultListSorted);

    }

    private void search() {
        if ( searchService.getState() == READY ) searchService.start();
        else searchService.restart();
    }

    /**
     * Creates the predicate based on the selected filters.
     *
     * @return Predicate.
     */
    private Predicate<Customer> getPredicate() {
        //problems with the predicate

        Predicate<Customer> searchfilter = c -> !kid.isSelected() || !lastname.isSelected() || !firstname.isSelected();
//            searchfilter = searchfilter.and(c -> kid.isSelected() && searchField.getText().equals(c.getId()));
//            searchfilter = searchfilter.and(c -> lastname.isSelected() && searchField.getText()
//                    .equals(c.getContacts().stream()
//                            .filter((customerContact) -> (customerContact.isPrefered()))
//                            .forEachOrdered((customerContact) -> {
//                                customerContact.getLastName();
//                            })));
//    
//            searchfilter = searchfilter.and(c -> firstname.isSelected() && searchField.getText().equals(c.getContacts().stream()
//                    .filter((customerContact) -> (customerContact.isPrefered()))
//                    .forEachOrdered((customerContact) -> {
//                        customerContact.getFirstName();
//                    })));

        return searchfilter;
    }

    @Override
    public void closed() {
        FxSaft.dispatch(() -> {
            if ( LOADING_TASK.isRunning() ) LOADING_TASK.cancel();
            return null;
        });
    }

}
