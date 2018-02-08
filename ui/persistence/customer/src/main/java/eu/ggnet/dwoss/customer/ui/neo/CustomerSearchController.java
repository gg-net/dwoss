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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.customer.ee.entity.Customer;
import eu.ggnet.dwoss.customer.ee.entity.Customer.SearchField;
import eu.ggnet.dwoss.customer.ee.entity.MandatorMetadata;
import eu.ggnet.dwoss.customer.ui.CustomerTaskService;
import eu.ggnet.dwoss.mandator.api.value.DefaultCustomerSalesdata;
import eu.ggnet.dwoss.rules.*;
import eu.ggnet.dwoss.util.HtmlPane;
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

    private CustomerTaskService CUSTOMER_TASK_SERVICE;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
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

        //Create a ContextMenu
        ContextMenu contextMenu = new ContextMenu();
        MenuItem viewCustomer = new MenuItem("Mandatenstandard des Kunden Anzeigen");
        MenuItem viewCompleteCustomer = new MenuItem("Kunden Anzeigen");
        MenuItem editCustomer = new MenuItem("Kunden editieren");

        //actions for the context menu
        viewCustomer.setOnAction((ActionEvent event) -> {
            //open toHtml(String matchcode, DefaultCustomerSalesdata defaults)
            if ( resultListView.getSelectionModel().getSelectedItem() != null ) {
                Customer selectedItem = resultListView.getSelectionModel().getSelectedItem();

                DefaultCustomerSalesdata defaults = DefaultCustomerSalesdata.builder()
                        .allowedSalesChannels(EnumSet.of(SalesChannel.CUSTOMER))
                        .paymentCondition(PaymentCondition.CUSTOMER)
                        .shippingCondition(ShippingCondition.DEALER_ONE)
                        .paymentMethod(PaymentMethod.DIRECT_DEBIT).build();

                String matchcode = selectedItem.getMandatorMetadata().stream().map(MandatorMetadata::getMandatorMatchcode).findFirst().orElse("NONE");

                Ui.exec(() -> {
                    Ui.build().title("Mandatenstandard des Kunden Anzeigen").fx().show(() -> selectedItem.toHtml(matchcode, defaults), () -> new HtmlPane());
                });

            }
        });
        viewCompleteCustomer.setOnAction((ActionEvent event) -> {
            //open toHtml(String salesRow, String comment)
            if ( resultListView.getSelectionModel().getSelectedItem() != null ) {
                Customer selectedItem = resultListView.getSelectionModel().getSelectedItem();
                Ui.exec(() -> {
                    Ui.build().title("Kunden Ansicht").fx().show(() -> selectedItem.toHtml(), () -> new HtmlPane());
                });

            }

        });
        editCustomer.setOnAction((ActionEvent event) -> {
            if ( resultListView.getSelectionModel().getSelectedItem() != null ) {
                Customer selectedItem = resultListView.getSelectionModel().getSelectedItem();
                if ( selectedItem.isSimple() ) {
                    Ui.exec(() -> {
                        Ui.build().fxml().show(() -> selectedItem, CustomerSimpleController.class);
                    });
                } else {
                    Ui.exec(() -> {
                        Ui.build().fxml().eval(() -> selectedItem, CustomerEnhanceController.class);
                    });
                }
            }

        });

        // Add MenuItemes to ContextMenu
        contextMenu.getItems().addAll(viewCustomer, viewCompleteCustomer, editCustomer);

        //add contextmenu to listview
        resultListView.setContextMenu(contextMenu);

        CUSTOMER_TASK_SERVICE = new CustomerTaskService();

        customerFields = fillSet();
        observableCustomers = CUSTOMER_TASK_SERVICE.getPartialResults();

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

        progressBar.progressProperty().bind(CUSTOMER_TASK_SERVICE.progressProperty());
        progressIndicator.progressProperty().bind(CUSTOMER_TASK_SERVICE.progressProperty());

        //hidde the HBox
        progressBar.visibleProperty().bind(CUSTOMER_TASK_SERVICE.runningProperty());
        progressIndicator.visibleProperty().bind(CUSTOMER_TASK_SERVICE.runningProperty());
        statusHbox.visibleProperty().bind(CUSTOMER_TASK_SERVICE.runningProperty());

        Ui.progress().observe(CUSTOMER_TASK_SERVICE);
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
        CUSTOMER_TASK_SERVICE.setCustomerFields(customerFields);
        CUSTOMER_TASK_SERVICE.setSearchsting(searchProperty.get());
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
