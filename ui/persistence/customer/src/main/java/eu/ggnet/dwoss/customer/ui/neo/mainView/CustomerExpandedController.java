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

import javafx.collections.*;
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
import eu.ggnet.dwoss.customer.ui.neo.listView.*;
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
    private ObservableList<Company> companies = FXCollections.observableArrayList();

    @Getter
    @Setter
    private ObservableList<Contact> contacts;

    @Getter
    @Setter
    private ObservableList<MandatorMetadata> mandatorMetadata;

    @Getter
    @Setter
    private Set<CustomerFlag> flags = new HashSet<>();

    @Getter
    @Setter
    private Source source;

    @Getter
    @Setter
    private ObservableMap<ExternalSystem, String> additionalCustomerIds = FXCollections.observableMap(new EnumMap(ExternalSystem.class));

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
    private BorderPane rootPane;

    @FXML
    private GridPane midGridPane;

    @FXML
    private CustomerCompany customerCompany;

    @FXML
    private CustomerContactController customerContactController;

    @FXML
    private CustomerAdditionalCustomerId additionalCustomerId;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setUp();
        Ui.progress().observe(LOADING_TASK);
        Ui.exec(LOADING_TASK);

    }

    public void setUp() {
        setFxElementsUp();
        CustomerGenerator gen = new CustomerGenerator();
        companies.addAll(gen.makeCompanies(10));

        customerCompany = new CustomerCompany();
        additionalCustomerId = new CustomerAdditionalCustomerId(this.getAdditionalCustomerIds());

        midGridPane.add(customerCompany.getVbox(), 0, 3);
        midGridPane.add(additionalCustomerId.getVbox(), 3, 2);

    }

    @Override
    public void closed() {
        FxSaft.dispatch(() -> {
            if ( LOADING_TASK.isRunning() ) LOADING_TASK.cancel();
            return null;
        });
    }

    public void setControllerState(MainControllerDto dto) {
        this.setAdditionalCustomerIds(FXCollections.observableMap(dto.getAdditionalCustomerIds()));
        this.setComment(dto.getComment());
        this.setCompanies(FXCollections.observableList(dto.getCompanies()));
        this.setContacts(FXCollections.observableList(dto.getContacts()));
        this.setFlags(dto.getFlags());
        this.setId(dto.getId());
        this.setKeyAccounter(dto.getKeyAccounter());
        this.setMandatorMetadata(FXCollections.observableList(dto.getMandatorMetadata()));
        this.setOptLock(dto.getOptLock());
        this.setSource(dto.getSource());
        setFxElementsUp();

    }

    public MainControllerDto getCustomerDto() {
        MainControllerDto dto = new MainControllerDto();

        dto.setAdditionalCustomerIds(new EnumMap(this.getAdditionalCustomerIds()));
        dto.setComment(this.getComment());
        dto.setCompanies(new ArrayList(this.getCompanies()));
        dto.setContacts(new ArrayList(this.getContacts()));
        dto.setFlags(flags);
        dto.setId(this.getId());
        dto.setKeyAccounter(this.getKeyAccounter());
        dto.setMandatorMetadata(this.getMandatorMetadata());
        dto.setOptLock(this.getOptLock());
        dto.setSource(this.getSource());
        return null;
    }

    private void setFxElementsUp() {
        setSourceBoxUp();

        setKeyAccounterUp();
        setCommentUp();
        setFlagVboxUp();

    }

    private void setSourceBoxUp() {

        sourceComboBox.getItems().addAll(Source.values());
        if ( this.source != null )
            sourceComboBox.getSelectionModel().select(source);

    }

    private void setKeyAccounterUp() {
        if ( this.keyAccounter != null )
            keyAccounterTextField.setText(keyAccounter);
    }

    private void setCommentUp() {
        if ( this.comment != null )
            commentTextArea.setText(comment);
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
}
