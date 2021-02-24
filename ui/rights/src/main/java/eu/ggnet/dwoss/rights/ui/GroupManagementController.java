/*
 * Copyright (C) 2020 GG-Net GmbH
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
package eu.ggnet.dwoss.rights.ui;

import java.net.URL;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.rights.api.AtomicRight;
import eu.ggnet.dwoss.rights.api.Group;
import eu.ggnet.saft.core.Ui;
import eu.ggnet.saft.core.ui.FxController;
import eu.ggnet.saft.core.ui.ResultProducer;

/**
 *
 * @author mirko.schulze
 */
public class GroupManagementController implements Initializable, FxController, Consumer<Group>, ResultProducer<Group> {

    private static final Logger L = LoggerFactory.getLogger(GroupManagementController.class);

    private Group group;

    private boolean accept;

    @FXML
    private TextField nameTextField;

    @FXML
    private ListView<AtomicRight> selectedRightsListView, availableRightsListView;

    @FXML
    private Button addAllRightsButton, addSelectedRightsButton, removeSelectedRightsButton, removeAllRightsButton, acceptButton, cancelButton;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        selectedRightsListView.setCellFactory(new RightsListCell.Factory());
        selectedRightsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        availableRightsListView.setCellFactory(new RightsListCell.Factory());
        availableRightsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        availableRightsListView.setItems(FXCollections.observableArrayList(AtomicRight.values()));

        addAllRightsButton.setOnAction(e -> {
            selectedRightsListView.setItems(FXCollections.observableArrayList(AtomicRight.values()));
            availableRightsListView.getItems().clear();
        });

        addSelectedRightsButton.setOnAction(e -> {
            List<AtomicRight> selRights = new ArrayList<>(selectedRightsListView.getItems());
            List<AtomicRight> avaRights = new ArrayList<>(availableRightsListView.getItems());

            List<AtomicRight> rights = new ArrayList<>(availableRightsListView.getSelectionModel().getSelectedItems());
            rights.forEach(r -> {
                selRights.add(r);
                avaRights.remove(r);
            });
            
            selectedRightsListView.setItems(FXCollections.observableArrayList(selRights));
            availableRightsListView.setItems(FXCollections.observableArrayList(avaRights));
        });

        removeSelectedRightsButton.setOnAction(e -> {
            List<AtomicRight> selRights = new ArrayList<>(selectedRightsListView.getItems());
            List<AtomicRight> avaRights = new ArrayList<>(availableRightsListView.getItems());

            List<AtomicRight> rights = new ArrayList<>(availableRightsListView.getSelectionModel().getSelectedItems());
            rights.forEach(r -> {
                selRights.remove(r);
                avaRights.add(r);
            });
            
            selectedRightsListView.setItems(FXCollections.observableArrayList(selRights));
            availableRightsListView.setItems(FXCollections.observableArrayList(avaRights));
        });

        removeAllRightsButton.setOnAction(e -> {
            availableRightsListView.setItems(FXCollections.observableArrayList(AtomicRight.values()));
            selectedRightsListView.getItems().clear();
        });

        acceptButton.setOnAction(e -> {
            if ( nameTextField.getText().isBlank() ) {
                new Alert(AlertType.ERROR, "Bitte geben Sie einen Namen ein.", ButtonType.CLOSE).showAndWait();
            } else {
                accept = true;
                Ui.closeWindowOf(acceptButton);
            }
        });

        cancelButton.setOnAction(e -> {
            Ui.closeWindowOf(cancelButton);
        });
    }

    @Override
    public void accept(Group t) {
        group = t;
        nameTextField.setText(t.getName());
        selectedRightsListView.setItems(FXCollections.observableArrayList(t.getRights()));
        List<AtomicRight> filteredRights = Arrays.asList(AtomicRight.values())
                .stream()
                .filter(r -> !t.getRights().contains(r))
                .collect(Collectors.toList());
        availableRightsListView.setItems(FXCollections.observableArrayList(filteredRights));
    }

    @Override
    public Group getResult() {
        if ( accept ) {
            Optional<Long> id;
            Optional<Integer> optLock;
            if ( group != null ) {
                id = group.getId();
                optLock = group.getOptLock();
            } else {
                id = Optional.empty();
                optLock = Optional.empty();
            }
            Group result = new Group.Builder()
                    .setId(id)
                    .setName(nameTextField.getText())
                    .setOptLock(optLock)
                    .addAllRights(new ArrayList<>(selectedRightsListView.getItems()))
                    .build();
            L.debug("Returning Group {}", result);
            return result;
        } else {
            L.debug("Returning null");
            return null;
        }
    }

    public static URL loadFxml() {
        return GroupManagementController.class.getResource("GroupManagementView.fxml");
    }

}
