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
package eu.ggnet.dwoss.customer.ui.neo;


import java.net.URL;
import java.util.*;
import java.util.function.Consumer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import eu.ggnet.dwoss.customer.assist.gen.CustomerGenerator;
import eu.ggnet.dwoss.customer.entity.Customer.Source;
import eu.ggnet.dwoss.customer.entity.*;
import eu.ggnet.dwoss.rules.CustomerFlag;
import eu.ggnet.saft.Ui;
import eu.ggnet.saft.UiAlert;
import eu.ggnet.saft.api.ui.*;
import eu.ggnet.saft.core.ui.UiAlertBuilder;

/**
 * FXML Controller class
 *
 * @author jens.papenhagen
 */
public class CustomerEnhanceController implements Initializable, FxController, Consumer<Customer>, ResultProducer<Customer> {

    @FXML
    private Button okButton;

    @FXML
    private Button cancelButton;

    @FXML
    private Label kid;

    @FXML
    private Label kundenname;

    @FXML
    private ListView<?> contactList;

    @FXML
    private Button addButton;

    @FXML
    private Button delButton;

    @FXML
    private Button editButton;

    @FXML
    private Button mandatorInfoButton;

    @FXML
    private ChoiceBox<?> soruce;

    @FXML
    private TextField keyAccount;

    private List<Company> companies;

    private VBox flagVbox;

    private Set<CustomerFlag> flags = new HashSet<>();

    private boolean bussines = false;

    private Customer customer;

    
    
    private TextField keyAccounterTextField;

    private TextArea commentTextArea;

    private ComboBox<Source> sourceComboBox;

    private GridPane midGridPane;

  
    

    @Override
    public void initialize(URL url, ResourceBundle rb) {        
        sourceComboBox.getItems().addAll(Source.values());
        keyAccounterTextField.setText("");

        commentTextArea.setText("");

        setFlagVboxUp();

    }

    public void setCustomer(Customer c) {
        setFxElementsUp();

        CustomerGenerator gen = new CustomerGenerator();
        companies.addAll(gen.makeCompanies(10));

//        companyList = new CompanyList(FXCollections.observableArrayList(companies));
//
////        ObservableMap<ExternalSystem, String> additionalCustomerIdMap = FXCollections.observableHashMap();
////        additionalCustomerIdMap.put(LEXWARE, "hund");
////        additionalCustomerIdListViewController.setObservableMap(additionalCustomerIdMap);
//        List<Address> addressesList = gen.makeAddresses(5);
//
//        addressListedViewController = new AddressList(FXCollections.observableArrayList(addressesList));
//
//        midGridPane.add(addressListedViewController.getList(), 2, 4);
//        midGridPane.add(companyList.getList(), 0, 3);

    }

    public void closed() {
        this.companies = null;
        Ui.closeWindowOf(keyAccounterTextField);
    }

    private void setFxElementsUp() {

    }

    private void setFlagVboxUp() {
        EventHandler customerFlagEventHandler = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if ( event.getSource() instanceof CheckBox ) {
                    CheckBox source = (CheckBox)event.getSource();
                    if ( source.isSelected() )
                        flags.add(CustomerFlag.valueOf(source.getText()));

                    else
                        flags.remove(CustomerFlag.valueOf(source.getText()));

                }
            }
        };

        List<CheckBox> list = new ArrayList<>(CustomerFlag.values().length);

        CustomerFlag[] customerFlags = CustomerFlag.values();

        for (int i = 0; i < CustomerFlag.values().length; i++) {
            list.add(new CheckBox(customerFlags[i].name()));
            list.get(i).setOnAction(customerFlagEventHandler);
            list.get(i).allowIndeterminateProperty().setValue(Boolean.FALSE);
            if ( flags.contains(customerFlags[i]) )
                list.get(i).setSelected(true);

        }
        flagVbox.getChildren().addAll(list);
    }

    @Override
    public void accept(Customer cust) {
        if ( cust != null ) {
            if ( cust.isBussines() ) {
                bussines = true;
            }
            customer = cust;
            setCustomer(customer);
        } else {
            UiAlert.message("Kunde ist inkompatibel").show(UiAlertBuilder.Type.WARNING);
        }
    }

    @Override
    public Customer getResult() {
        if ( customer == null ) {
            return null;
        }
        return customer;
    }
}
