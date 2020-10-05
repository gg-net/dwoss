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
import java.util.stream.Collectors;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.rights.api.AtomicRight;
import eu.ggnet.dwoss.rights.ee.*;
import eu.ggnet.dwoss.rights.ee.entity.Operator;
import eu.ggnet.dwoss.rights.ee.entity.Persona;
import eu.ggnet.saft.core.*;
import eu.ggnet.saft.core.ui.FxController;
import eu.ggnet.saft.core.ui.Title;

import static javafx.scene.control.SelectionMode.MULTIPLE;

/**
 * FXML Controller class for the RightsManagementView.fxml.
 *
 * @author mirko.schulze
 */
@Title("Rechte-Verwaltung")
public class NewRightsManagementController implements Initializable, FxController {

    private static final Logger L = LoggerFactory.getLogger(NewRightsManagementController.class);

    private UiOperator selectedUser;

    private final Set<UiPersona> allGroups = new HashSet<>();

    private ObservableList<UiPersona> inactiveGroupsList;

    private ObservableList<AtomicRight> inactiveRightsList;

    @FXML
    private ListView<UiOperator> userListView;

    @FXML
    private ListView<UiPersona> activeGroupsListView;

    @FXML
    private ListView<UiPersona> inactiveGroupsListView;

    @FXML
    private ListView<AtomicRight> activeRightsListView;

    @FXML
    private ListView<AtomicRight> inactiveRightsListView;

    @FXML
    private ListView<AtomicRight> allActiveRightsListView;

    @FXML
    private Button addGroupButton;

    @FXML
    private Button removeGroupButton;

    @FXML
    private Button addRightButton;

    @FXML
    private Button removeRightButton;

    @FXML
    private Button createUserButton;

    @FXML
    private Button createGroupButton;

    @FXML
    private Button deleteUserButton;

    @FXML
    private Button deleteGroupButton;

    @FXML
    private Button closeButton;

    private UiOperator getSelectedUserFromUserListView() {
        return userListView.getSelectionModel().getSelectedItem();
    }

    private Long getSelectedUserIdFromUserListView() {
        return userListView.getSelectionModel().getSelectedItem().idProperty().get();
    }

    private void setSelectedUserAndRefresh(UiOperator user) {
        if ( user == null ) return;
        //reset ListViews
        if ( selectedUser != null ) {
            activeGroupsListView.itemsProperty().unbindBidirectional(selectedUser.personasProperty());
            activeGroupsListView.setItems(FXCollections.<UiPersona>observableArrayList());
            activeRightsListView.itemsProperty().unbindBidirectional(selectedUser.rightsProperty());
            activeRightsListView.setItems(FXCollections.<AtomicRight>observableArrayList());
        }
        //set ListViews
        selectedUser = user;
        activeGroupsListView.itemsProperty().bindBidirectional(selectedUser.personasProperty());
        activeRightsListView.itemsProperty().bindBidirectional(selectedUser.rightsProperty());
        //refresh UI
        refreshInactiveGroupsList();
        refreshInactiveRightsList();
        refreshAllActiveRightsListView();
    }

    private UiPersona getSelectedGroupFromInactiveGroupListView() {
        return inactiveGroupsListView.getSelectionModel().getSelectedItem();
    }

    //TODO refresh
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        UserAgent userAgent = Dl.remote().lookup(UserAgent.class);
        GroupAgent groupAgent = Dl.remote().lookup(GroupAgent.class);
        //ObservableLists
        //inactiveGroupsList
        inactiveGroupsList = FXCollections.observableArrayList();
        Bindings.bindBidirectional(new SimpleListProperty<>(inactiveGroupsList), inactiveGroupsListView.itemsProperty());
        //inactiveRightsList
        inactiveRightsList = FXCollections.observableArrayList(AtomicRight.values());
        Bindings.bindBidirectional(new SimpleListProperty<>(inactiveRightsList), inactiveRightsListView.itemsProperty());
        //ListViews
        //userListView
        userListView.setCellFactory(new OperatorListCell.Factory());
        userListView.setOnMouseClicked(e -> {
            if ( e.getButton().equals(MouseButton.PRIMARY) ) {
                UiOperator user = userListView.getSelectionModel().getSelectedItem();
                if ( e.getClickCount() == 1 ) {
                    setSelectedUserAndRefresh(user);
                }
            }
        });
        userAgent.findAll(Operator.class).forEach(u -> userListView.getItems().add(new UiOperator(u)));
        //activeGroupsListView
        activeGroupsListView.setCellFactory(new PersonaListCell.Factory());
        activeGroupsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        //inactiveGroupsListView
        inactiveGroupsListView.setCellFactory(new PersonaListCell.Factory());
        inactiveGroupsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        groupAgent.findAll(Persona.class).forEach(g -> inactiveGroupsListView.getItems().add(new UiPersona(g)));
        //activeRightsListView
        activeRightsListView.setCellFactory(new RightsListCell.Factory());
        activeRightsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        //inactiveRightsListView
        inactiveRightsListView.setCellFactory(new RightsListCell.Factory());
        inactiveRightsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        Arrays.asList(AtomicRight.values()).forEach(r -> inactiveRightsListView.getItems().add(r));
        //allRightsListView
        allActiveRightsListView.setCellFactory(new RightsListCell.Factory());
        //Buttons
        //addGroupButton
        addGroupButton.disableProperty().bind(inactiveGroupsListView.getSelectionModel().selectedIndexProperty().greaterThanOrEqualTo(0));
        addGroupButton.setOnAction(e -> {
            List<Long> selectedInactiveGroupIds = inactiveGroupsListView.getSelectionModel().getSelectedItems()
                    .stream().mapToLong(g -> g.idProperty().get())
                    .boxed().collect(Collectors.toList());
            selectedInactiveGroupIds.forEach(id -> userAgent.addGroup(getSelectedUserIdFromUserListView(), id));
        });
        //removeGroupButton
        removeGroupButton.disableProperty().bind(activeGroupsListView.getSelectionModel().selectedIndexProperty().greaterThanOrEqualTo(0));
        removeGroupButton.setOnAction(e -> {
            List<Long> selectedActiveGroupIds = activeGroupsListView.getSelectionModel().getSelectedItems()
                    .stream().mapToLong(g -> g.idProperty().get())
                    .boxed().collect(Collectors.toList());
            selectedActiveGroupIds.forEach(id -> userAgent.removeGroup(getSelectedUserIdFromUserListView(), id));
        });
        //addRightButton
        addRightButton.disableProperty().bind(inactiveRightsListView.getSelectionModel().selectedIndexProperty().greaterThanOrEqualTo(0));
        addRightButton.setOnAction(e -> inactiveRightsListView.getSelectionModel().getSelectedItems()
                .forEach(r -> userAgent.addRight(getSelectedUserIdFromUserListView(), r)));
        //removeRightButton
        removeRightButton.disableProperty().bind(activeRightsListView.getSelectionModel().selectedIndexProperty().greaterThanOrEqualTo(0));
        removeRightButton.setOnAction(e -> activeRightsListView.getSelectionModel().getSelectedItems()
                .forEach(r -> userAgent.removeRight(getSelectedUserIdFromUserListView(), r)));
        //TODO createUser
        //createUserButton
        createUserButton.setOnAction(e -> {

        });
        //TODO createGroup
        //createGroupButton
        createGroupButton.setOnAction(e -> {

        });
        //TODO multi select?
        //deleteUserButton
        deleteUserButton.visibleProperty().bind(userListView.getSelectionModel().selectedIndexProperty().greaterThanOrEqualTo(0));
        deleteUserButton.setOnAction(e -> userAgent.delete(getSelectedUserIdFromUserListView()));
        //TODO multi select?
        //deleteGroupButton
        deleteGroupButton.visibleProperty().bind(inactiveGroupsListView.getSelectionModel().selectedIndexProperty().greaterThanOrEqualTo(0));
        deleteGroupButton.setOnAction(e -> {
            //Groups that are used by any User must not just be deleted
            UiPersona selectedGroup = getSelectedGroupFromInactiveGroupListView();
            List<Operator> allUsers = userAgent.findAllEager(Operator.class);
            Set<Persona> groupsInUsage = new HashSet<>();
            allUsers.forEach(user -> user.getPersonas().forEach(persona -> groupsInUsage.add(persona)));
            if ( !groupsInUsage.isEmpty() ) {
                if ( new Alert(Alert.AlertType.CONFIRMATION,
                        "Gruppe " + selectedGroup.nameProperty().get() + " wird noch verwendet.\nWollen Sie dir Gruppe dennoch löschen?")
                        .showAndWait().get() == ButtonType.OK ) {
                    groupAgent.delete(selectedGroup.idProperty().get());
                }
            } else {
                groupAgent.delete(selectedGroup.idProperty().get());
            }
        });
        //closeButton
        closeButton.setOnAction(e -> {
            if ( new Alert(Alert.AlertType.CONFIRMATION, "Wollen Sie die Rechte-Verwaltung schließen?").showAndWait().get() == ButtonType.OK ) {
                Stage stage = (Stage)closeButton.getScene().getWindow();
                stage.close();
            }
        });
    }

    private void refreshInactiveGroupsList() {
        L.info("refreshInActiveGroupsList() called");
        inactiveGroupsList.clear();
        List<UiPersona> groups = new ArrayList<>(allGroups);
        groups.removeAll(getSelectedUserFromUserListView().getPersonas());
        inactiveGroupsList.addAll(groups);
    }

    private void refreshInactiveRightsList() {
        L.info("refreshInactiveRightsList() called");
        inactiveRightsList.clear();
        inactiveRightsList.addAll(EnumSet.complementOf(getSelectedUserFromUserListView().getAllActiveRights()));
    }

    private void refreshAllActiveRightsListView() {
        L.info("refreshAllActiveRightsListView() called");
        allActiveRightsListView.getItems().clear();
        allActiveRightsListView.getItems().addAll(getSelectedUserFromUserListView().getAllActiveRights());
    }

    public static URL loadFxml() {
        return NewRightsManagementController.class.getResource("NewRightsManagementView.fxml");
    }

}
