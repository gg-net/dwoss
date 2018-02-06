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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;

import eu.ggnet.dwoss.customer.ee.entity.Customer;
import eu.ggnet.dwoss.customer.ee.entity.Customer.SearchField;
import eu.ggnet.dwoss.customer.ui.CustomerTask;
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
    private HBox statusHbox;

    private Set<SearchField> customerFields;

    private ObservableList<Customer> observableCustomers = FXCollections.observableArrayList();

    private StringProperty searchProperty = new SimpleStringProperty();

    private CustomerTask LOADING_CUSTOMER_TASK;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        resultListView = new ListView<>();
        resultListView.setCellFactory(listView -> {
            return new ListCell<Customer>() {
                @Override
                protected void updateItem(Customer item, boolean empty) {
                    super.updateItem(item, empty);
                    if ( item == null || empty ) {
                        this.setGraphic(null);
                    } else {
                        this.setText(item.toName());
                    }
                }
            };
        });

        LOADING_CUSTOMER_TASK = new CustomerTask(searchProperty.get(), customerFields);

        customerFields = fillSet();
        observableCustomers = LOADING_CUSTOMER_TASK.getPartialResults();

        resultListView.setItems(observableCustomers);

        progressBar.setMaxWidth(MAX_VALUE); // Needed, so the bar will fill the space, otherwise it keeps beeing small
        progressBar.setMaxHeight(MAX_VALUE);// Needed, so the bar will fill the space, otherwise it keeps beeing small

        searchButton.setOnAction((ActionEvent event) -> search());
        searchField.setOnKeyPressed((ke) -> {
            if ( ke.getCode() == KeyCode.ENTER ) {
                search();
            }
        });

        // Binding all Ui Properties
        searchProperty.bind(searchField.textProperty());

        progressBar.progressProperty().bind(LOADING_CUSTOMER_TASK.progressProperty());
        progressIndicator.progressProperty().bind(LOADING_CUSTOMER_TASK.progressProperty());

        //hidde the HBox
        progressBar.visibleProperty().bind(LOADING_CUSTOMER_TASK.runningProperty());
        progressIndicator.visibleProperty().bind(LOADING_CUSTOMER_TASK.runningProperty());
        statusHbox.visibleProperty().bind(LOADING_CUSTOMER_TASK.runningProperty());

        Ui.progress().observe(LOADING_CUSTOMER_TASK);
        Ui.exec(LOADING_CUSTOMER_TASK);
    }

    /**
     * fill the Set for filter the Search
     *
     * @return a Set of Enums form Customer.SearchField
     */
    private Set<SearchField> fillSet() {
        Set<SearchField> set = new HashSet<>();
        if ( kid.isSelected() ) {
            set.add(Customer.SearchField.ID);
        }
        if ( lastname.isSelected() ) {
            set.add(Customer.SearchField.LASTNAME);
        }
        if ( firstname.isSelected() ) {
            set.add(Customer.SearchField.FIRSTNAME);
        }
        if ( company.isSelected() ) {
            set.add(Customer.SearchField.COMPANY);
        }
        if ( address.isSelected() ) {
            set.add(Customer.SearchField.ADDRESS);
        }

        return set;
    }

    private void search() {
        if ( LOADING_CUSTOMER_TASK.getState() == READY ) {
            LOADING_CUSTOMER_TASK.setCustomerFields(customerFields);
            LOADING_CUSTOMER_TASK.setSearchsting(searchProperty.get());
        } else {
            LOADING_CUSTOMER_TASK.setCustomerFields(customerFields);
            LOADING_CUSTOMER_TASK.setSearchsting(searchProperty.get());
        }
    }

    @Override
    public void closed() {
        FxSaft.dispatch(() -> {
            if ( LOADING_CUSTOMER_TASK.isRunning() ) {
                LOADING_CUSTOMER_TASK.cancel();
            }
            return null;
        });

        Ui.closeWindowOf(kid);
    }

}
