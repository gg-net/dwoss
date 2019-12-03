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

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.core.common.Css;
import eu.ggnet.dwoss.core.widget.HtmlPane;
import eu.ggnet.dwoss.customer.ee.CustomerAgent;
import eu.ggnet.dwoss.customer.ee.entity.Customer;
import eu.ggnet.dwoss.customer.ee.entity.Customer.SearchField;
import eu.ggnet.dwoss.customer.ee.entity.projection.PicoCustomer;
import eu.ggnet.dwoss.customer.ui.CustomerTaskService;
import eu.ggnet.saft.core.Dl;
import eu.ggnet.saft.core.Ui;
import eu.ggnet.saft.core.ui.*;

import static java.lang.Double.MAX_VALUE;
import static javafx.concurrent.Worker.State.READY;

/**
 * FXML Controller class
 *
 * @author jens.papenhagen
 */
@Title("Kunden Suche")
public class CustomerSearchController implements Initializable, FxController, ClosedListener {

    private final static Logger L = LoggerFactory.getLogger(CustomerSearchController.class);

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
    private CheckBox communication;

    @FXML
    private ListView<PicoCustomer> resultListView;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private ProgressIndicator progressIndicator;

    @FXML
    private HBox statusHbox;

    private final CustomerAgent AGENT = Dl.remote().lookup(CustomerAgent.class);

    private final Set<SearchField> SEARCH_FIELDS = new HashSet<>();

    private ObservableList<PicoCustomer> observableCustomers = FXCollections.observableArrayList();

    private final StringProperty SEARCH_PROPERTY = new SimpleStringProperty();

    private CustomerTaskService CUSTOMER_TASK_SERVICE;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        resultListView.setCellFactory(listView -> {
            return new ListCell<PicoCustomer>() {
                @Override
                protected void updateItem(PicoCustomer item, boolean empty) {
                    super.updateItem(item, empty);
                    if ( item == null || empty ) {
                        setText("");
                        setGraphic(null);
                    } else {
                        Text id = new Text(item.id + " ");
                        id.setStyle("-fx-font-weight: bold;");

                        Text description = new Text(item.shortDescription);
                        description.setStyle("-fx-font-weight: regular");
                        TextFlow flow = new TextFlow(id, description);

                        setGraphic(flow);
                    }
                }
            };
        });

        //bind the checkboxes to the Set of SearchField
        bindCheckBoxes();

        //add contextmenu to listview
        resultListView.setContextMenu(buildContextMenu());

        CUSTOMER_TASK_SERVICE = new CustomerTaskService();
        observableCustomers = CUSTOMER_TASK_SERVICE.getPartialResults();
        resultListView.setItems(observableCustomers);

        searchButton.setOnAction((ActionEvent event) -> search());
        searchField.setOnKeyPressed((ke) -> {
            if ( ke.getCode() == KeyCode.ENTER
                    && !searchField.getText().trim().isEmpty()
                    && (searchField.getText().trim().length() >= 3) ) {
                search();
            }
        });
        SEARCH_PROPERTY.bind(searchField.textProperty());

        // Needed, so the bar will fill the space, otherwise it keeps beeing small
        progressBar.setMaxWidth(MAX_VALUE);
        progressBar.setMaxHeight(MAX_VALUE);

        //hidde the HBox
        progressBar.visibleProperty().bind(CUSTOMER_TASK_SERVICE.runningProperty());
        progressIndicator.visibleProperty().bind(CUSTOMER_TASK_SERVICE.runningProperty());
        statusHbox.visibleProperty().bind(CUSTOMER_TASK_SERVICE.runningProperty());

        progressIndicator.setProgress(0);
        progressBar.setProgress(0);

        progressBar.progressProperty().bind(CUSTOMER_TASK_SERVICE.progressProperty());
        progressIndicator.progressProperty().bind(CUSTOMER_TASK_SERVICE.progressProperty());

        Ui.progress().observe(CUSTOMER_TASK_SERVICE);
    }

    /**
     * bind the checkboxes to the Set of SearchField
     * <p>
     */
    private void bindCheckBoxes() {
        kid.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if ( newValue ) {
                SEARCH_FIELDS.add(SearchField.ID);
            } else {
                SEARCH_FIELDS.remove(SearchField.ID);
            }
        });
        lastname.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if ( newValue ) {
                SEARCH_FIELDS.add(SearchField.LASTNAME);
            } else {
                SEARCH_FIELDS.remove(SearchField.LASTNAME);
            }
        });
        lastname.setSelected(true);//for a nice default case
        firstname.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if ( newValue ) {
                SEARCH_FIELDS.add(SearchField.FIRSTNAME);
            } else {
                SEARCH_FIELDS.remove(SearchField.FIRSTNAME);
            }
        });
        company.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if ( newValue ) {
                SEARCH_FIELDS.add(SearchField.COMPANY);
            } else {
                SEARCH_FIELDS.remove(SearchField.COMPANY);
            }
        });
        address.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if ( newValue ) {
                SEARCH_FIELDS.add(SearchField.ADDRESS);
            } else {
                SEARCH_FIELDS.remove(SearchField.ADDRESS);
            }
        });
        communication.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if ( newValue ) {
                SEARCH_FIELDS.add(SearchField.COMMUNICATION);
            } else {
                SEARCH_FIELDS.remove(SearchField.COMMUNICATION);
            }
        });
    }

    /**
     * Build a ContextMenu for ListView of the search results for a better navigation
     *
     * @return ContextMenu the filled ContextMenu
     */
    private ContextMenu buildContextMenu() {
        //Create a ContextMenu
        ContextMenu contextMenu = new ContextMenu();
        MenuItem viewCustomer = new MenuItem("Detailansicht");
        MenuItem viewCompleteCustomer = new MenuItem("Detailansicht inc. aller Mandatendetails");
        MenuItem editCustomer = new MenuItem("Bearbeiten");

        //adding actions to the context menu
        viewCustomer.setOnAction((ActionEvent event) -> {
            //open toHtml(String matchcode, DefaultCustomerSalesdata defaults)
            if ( resultListView.getSelectionModel().getSelectedItem() == null ) return;
            PicoCustomer selectedCustomer = resultListView.getSelectionModel().getSelectedItem();
            Ui.exec(() -> {
                Ui.build(statusHbox).title("Kunde mit Mandant").fx().show(() -> Css.toHtml5WithStyle(AGENT.findCustomerAsMandatorHtml(selectedCustomer.id)), () -> new HtmlPane());
            });
        });

        viewCompleteCustomer.setOnAction((ActionEvent event) -> {
            //open toHtml(String salesRow, String comment)
            if ( resultListView.getSelectionModel().getSelectedItem() == null ) return;
            PicoCustomer selectedCustomer = resultListView.getSelectionModel().getSelectedItem();
            Ui.exec(() -> {
                Ui.build(statusHbox).title("Kunden Ansicht").fx().show(() -> Css.toHtml5WithStyle(AGENT.findCustomerAsHtml(selectedCustomer.id)), () -> new HtmlPane());
            });
        });

        editCustomer.setOnAction((ActionEvent event) -> {
            if ( resultListView.getSelectionModel().getSelectedItem() == null ) return;
            PicoCustomer picoCustomer = resultListView.getSelectionModel().getSelectedItem();
            Ui.exec(() -> {
                CustomerConnectorFascade.edit(Ui.progress().call(() -> AGENT.findByIdEager(Customer.class, picoCustomer.id)), UiParent.of(resultListView), () -> {
                    // TODO: We could reload the picocustomer, which was changed and updaten the search list.
                });
            });
        });

        //add MenuItemes to ContextMenu
        contextMenu.getItems().addAll(viewCustomer, viewCompleteCustomer, editCustomer);

        return contextMenu;
    }

    private void search() {
        CUSTOMER_TASK_SERVICE.setCustomerFields(SEARCH_FIELDS);
        CUSTOMER_TASK_SERVICE.setSearchsting(SEARCH_PROPERTY.get());
        observableCustomers.clear();
        resultListView.getContextMenu().getItems().forEach((item) -> {
            item.setDisable(false);
        });

        if ( CUSTOMER_TASK_SERVICE.getState() == READY ) {
            CUSTOMER_TASK_SERVICE.start();
        } else {
            CUSTOMER_TASK_SERVICE.restart();
        }
    }

    @Override
    public void closed() {
        FxSaft.dispatch(() -> {
            if ( CUSTOMER_TASK_SERVICE.isRunning() ) {
                CUSTOMER_TASK_SERVICE.cancel();
            }
            return null;
        });
    }

}
