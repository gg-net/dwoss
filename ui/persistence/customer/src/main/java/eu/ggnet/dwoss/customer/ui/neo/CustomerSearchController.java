package eu.ggnet.dwoss.customer.ui.neo;

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

import eu.ggnet.dwoss.customer.CustomerAgent;
import eu.ggnet.dwoss.customer.entity.Customer;
import eu.ggnet.dwoss.customer.entity.Customer.SearchField;
import eu.ggnet.dwoss.customer.ui.CustomerTask;
import eu.ggnet.saft.Client;
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
    private CheckBox address;

    @FXML
    private ListView<Customer> resultListView;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private ProgressIndicator progressIndicator;

    @FXML
    private HBox bottom;

    private Service<List<Customer>> searchService;
    
    private Set<SearchField> customerFields;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Creating and laying out the Ui
        StringProperty searchProperty = new SimpleStringProperty();
        ObservableList<Customer> resultProperty = FXCollections.observableArrayList();

        resultListView = new ListView<>();
//        resultListView.setCellFactory(new SearchListCell.Factory());

        progressBar.setMaxWidth(MAX_VALUE); // Needed, so the bar will fill the space, otherwise it keeps beeing small
        progressBar.setMaxHeight(MAX_VALUE);// Needed, so the bar will fill the space, otherwise it keeps beeing small

        customerFields = new HashSet<>();
        
        
        //fill the Set
        if ( kid.isSelected() ) {
            customerFields.add(Customer.SearchField.ID);
        }
        if ( lastname.isSelected() ) {
            customerFields.add(Customer.SearchField.LASTNAME);
        }
        if ( firstname.isSelected() ) {
            customerFields.add(Customer.SearchField.FIRSTNAME);
        }
        if ( company.isSelected() ) {
            customerFields.add(Customer.SearchField.COMPANY);
        }
        if ( address.isSelected() ) {
            customerFields.add(Customer.SearchField.ADDRESS);
        }

        // Search Service. Creates for every search request a task, which picks up results in the background. Optional, cancels the allready running task.
        searchService = new Service<List<Customer>>() {
            @Override
            protected Task<List<Customer>> createTask() {

                return new Task<List<Customer>>() {
                    @Override
                    protected List<Customer> call() throws Exception {
                        CustomerAgent searcher = Client.lookup(CustomerAgent.class);
                        searcher.search(searchProperty.get(), customerFields);

                        updateProgress(-1, -1);
                        if ( StringUtils.isEmpty(searchProperty.get()) ){
                            return Collections.EMPTY_LIST;
                        } // Empty check.
                        List<Customer> searchlist = searcher.search(searchProperty.get(), customerFields, 0, searcher.countSearch(searchProperty.get(), customerFields));
                        List<Customer> last = Collections.EMPTY_LIST;

                        int done = 0;
                        int i = 0;

                        while (!isCancelled() && i < searchlist.size()) {
                            last.add(searchlist.get(i));
                            done = done + last.size();
                            updateValue(last);
                            int estimate = searcher.countSearch(searchProperty.get(), customerFields);
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
            if ( n == null ) {
                resultProperty.clear();
            } // happens if service has allready a value and is reinited.
            else {
                resultProperty.addAll(n);
            }
        });

        searchButton.setOnAction((ActionEvent event) -> search());
        searchField.setOnKeyPressed((ke) -> {
            if ( ke.getCode() == KeyCode.ENTER ) {
                search();
            }
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
        if ( searchService.getState() == READY ) {
            searchService.start();
        } else {
            searchService.restart();
        }
    }

    @Override
    public void closed() {
        FxSaft.dispatch(() -> {
            if ( LOADING_TASK.isRunning() ) {
                LOADING_TASK.cancel();
            }
            return null;
        });
    }

}
