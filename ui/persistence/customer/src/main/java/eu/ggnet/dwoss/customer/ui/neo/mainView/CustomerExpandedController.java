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
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import eu.ggnet.dwoss.customer.entity.Customer.ExternalSystem;
import eu.ggnet.dwoss.customer.entity.Customer.Source;
import eu.ggnet.dwoss.customer.entity.*;
import eu.ggnet.dwoss.customer.ui.CustomerTask;
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

    @Override
    public void initialize(URL url, ResourceBundle rb) {

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

        flagVbox.getChildren().add(new ChoiceBox<CustomerFlag>(FXCollections.observableArrayList(Arrays.asList(CustomerFlag.values()))));

    }

}
