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
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.core.widget.Dl;
import eu.ggnet.dwoss.rights.api.*;
import eu.ggnet.saft.core.Ui;
import eu.ggnet.saft.core.ui.FxController;
import eu.ggnet.saft.core.ui.ResultProducer;

/**
 *
 * @author mirko.schulze
 */
public class UserManagementController implements Initializable, FxController, Consumer<User>, ResultProducer<User> {

    private static final Logger L = LoggerFactory.getLogger(UserManagementController.class);

    private User user;

    private boolean accept;

    private List<Group> allGroups;

    @FXML
    private TextField nameTextField;

    @FXML
    private PasswordField passwordField, confirmPasswordField;

    @FXML
    private ListView<AtomicRight> selectedRightsListView, availableRightsListView;

    @FXML
    private ListView<Group> selectedGroupsListView, availableGroupsListView;

    @FXML
    private Button addAllRightsButton, addSelectedRightsButton, removeSelectedRightsButton, removeAllRightsButton, addAllGroupsButton, addSelectedGroupsButton,
            removeSelectedGroupsButton, removeAllGroupsButton, acceptButton, cancelButton;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        allGroups = Dl.remote().lookup(GroupApi.class).findAll();

        selectedRightsListView.setCellFactory(new RightsListCell.Factory());
        selectedRightsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        availableRightsListView.setCellFactory(new RightsListCell.Factory());
        availableRightsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        availableRightsListView.setItems(FXCollections.observableArrayList(AtomicRight.values()));

        selectedGroupsListView.setCellFactory(new GroupListCell.Factory());
        selectedGroupsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        availableGroupsListView.setCellFactory(new GroupListCell.Factory());
        availableGroupsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        availableGroupsListView.setItems(FXCollections.observableArrayList(allGroups));

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

        addAllGroupsButton.setOnAction(e -> {
            selectedGroupsListView.setItems(FXCollections.observableArrayList(allGroups));
            availableGroupsListView.getItems().clear();
        });

        addSelectedGroupsButton.setOnAction(e -> {
            List<Group> selGroups = new ArrayList<>(selectedGroupsListView.getItems());
            List<Group> avaGroups = new ArrayList<>(availableGroupsListView.getItems());

            List<Group> groups = new ArrayList<>(availableGroupsListView.getSelectionModel().getSelectedItems());
            groups.forEach(r -> {
                selGroups.add(r);
                avaGroups.remove(r);
            });

            selectedGroupsListView.setItems(FXCollections.observableArrayList(selGroups));
            availableGroupsListView.setItems(FXCollections.observableArrayList(avaGroups));
        });

        removeSelectedGroupsButton.setOnAction(e -> {
            List<Group> selGroups = new ArrayList<>(selectedGroupsListView.getItems());
            List<Group> avaGroups = new ArrayList<>(availableGroupsListView.getItems());

            List<Group> groups = new ArrayList<>(availableGroupsListView.getSelectionModel().getSelectedItems());
            groups.forEach(r -> {
                selGroups.remove(r);
                avaGroups.add(r);
            });

            selectedGroupsListView.setItems(FXCollections.observableArrayList(selGroups));
            availableGroupsListView.setItems(FXCollections.observableArrayList(avaGroups));
        });

        removeAllGroupsButton.setOnAction(e -> {
            availableGroupsListView.setItems(FXCollections.observableArrayList(allGroups));
            selectedGroupsListView.getItems().clear();
        });

        acceptButton.setOnAction(e -> {
            if ( nameTextField.getText().isBlank() ) {
                new Alert(AlertType.ERROR, "Bitte geben Sie einen Namen ein.", ButtonType.CLOSE).showAndWait();
            } else if ( !passwordField.getText().equals(confirmPasswordField.getText()) ) {
                new Alert(AlertType.ERROR, "Die eingegebenen Passwörter stimmen nicht überein.", ButtonType.CLOSE).showAndWait();
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
    public void accept(User t) {
        user = t;
        nameTextField.setText(t.getUsername());
        selectedRightsListView.setItems(FXCollections.observableArrayList(t.getRights()));
        List<AtomicRight> filteredRights = Arrays.asList(AtomicRight.values())
                .stream()
                .filter(r -> !t.getRights().contains(r))
                .collect(Collectors.toList());
        availableRightsListView.setItems(FXCollections.observableArrayList(filteredRights));
        selectedGroupsListView.setItems(FXCollections.observableArrayList(t.getGroups()));
        List<Group> filteredGroups = allGroups.stream()
                .filter(g -> !t.getGroups().contains(g))
                .collect(Collectors.toList());
        availableGroupsListView.setItems(FXCollections.observableArrayList(filteredGroups));
    }

    @Override
    public User getResult() {
        if ( accept ) {
            Optional<Long> id;
            Optional<Integer> optLock;
            if ( user != null ) {
                id = user.getId();
                optLock = user.getOptLock();
            } else {
                id = Optional.empty();
                optLock = Optional.empty();
            }
            User result = new User.Builder()
                    .setId(id)
                    .setUsername(nameTextField.getText())
                    .setOptLock(optLock)
                    .addAllRights(selectedRightsListView.getItems())
                    .addAllGroups(selectedGroupsListView.getItems())
                    .build();
            L.info("Returning User {}", result);
            return result;
        } else {
            L.info("Returning null");
            return null;
        }
    }

    public static URL loadFxml() {
        return PersonaManagmentController.class.getResource("UserManagementView.fxml");
    }

}
