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
package eu.ggnet.dwoss.customer.ui.neo.mainView;

import java.net.URL;
import java.util.*;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import eu.ggnet.dwoss.customer.assist.gen.CustomerGenerator;
import eu.ggnet.dwoss.customer.entity.Customer.ExternalSystem;
import eu.ggnet.dwoss.customer.entity.Customer.Source;
import eu.ggnet.dwoss.customer.entity.*;
import eu.ggnet.dwoss.customer.ui.CustomerTask;
import eu.ggnet.dwoss.customer.ui.neo.listView.CustomerCompanyListController;
import eu.ggnet.dwoss.rules.CustomerFlag;
import eu.ggnet.saft.Ui;
import eu.ggnet.saft.api.ui.ClosedListener;
import eu.ggnet.saft.api.ui.FxController;
import eu.ggnet.saft.core.ui.FxSaft;

import lombok.Getter;
import lombok.Setter;

/**
 * FXML Controller class
 *
 * @author jens.papenhagen
 */
public class CustomerExpandedController implements Initializable, FxController, ClosedListener {

    private final CustomerTask LOADING_TASK = new CustomerTask();

    @Getter
    @Setter
    private long id;

    @Getter
    @Setter
    private short optLock;

    @Getter
    @Setter
    private List<Company> companies;

    @Getter
    @Setter
    private List<Contact> contacts;

    @Getter
    @Setter
    private List<MandatorMetadata> mandatorMetadata;

    @Getter
    @Setter
    private Set<CustomerFlag> flags = new HashSet<>();

    @Getter
    @Setter
    private Source source;

    @Getter
    @Setter
    private Map<ExternalSystem, String> additionalCustomerIds = new EnumMap<>(ExternalSystem.class);

    @Getter
    @Setter
    private String keyAccounter;  // Null is ok.

    @Getter
    @Setter
    private String comment;

    @FXML
    private TextField keyAccounterTextField;

    @FXML
    private TextArea commentTextArea;

    @FXML
    private ComboBox<Source> sourceComboBox;

    @FXML
    private VBox flagVbox;

    @FXML
    private FlowPane listViewVbox;

    @FXML
    private CustomerCompanyListController customerCompanyListController;

    @FXML
    private BorderPane rootPane;

    @FXML
    private GridPane midGridPane;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        CustomerGenerator gen = new CustomerGenerator();
        customerCompanyListController = new CustomerCompanyListController(FXCollections.observableArrayList(gen.makeCompanies(10)));

        midGridPane.add(customerCompanyListController, 0, 2);

        customerCompanyListController.setVisible(true);
        setFlagVboxUp();

        Ui.progress().observe(LOADING_TASK);
        Ui.exec(LOADING_TASK);

    }

    @Override
    public void closed() {
        FxSaft.dispatch(() -> {
            if ( LOADING_TASK.isRunning() ) LOADING_TASK.cancel();
            return null;
        });
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

        }
        flagVbox.getChildren().addAll(list);
    }

}
