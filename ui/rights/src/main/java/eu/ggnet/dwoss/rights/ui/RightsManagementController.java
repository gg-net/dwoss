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
import java.util.concurrent.CancellationException;
import java.util.stream.Collectors;

import jakarta.inject.Inject;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.stage.Modality;
import javafx.stage.Stage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.core.widget.dl.RemoteDl;
import eu.ggnet.dwoss.rights.api.*;
import eu.ggnet.dwoss.rights.ee.entity.Persona;
import eu.ggnet.saft.core.Saft;
import eu.ggnet.saft.core.ui.*;

import jakarta.enterprise.context.Dependent;

import static javafx.scene.control.ButtonType.OK;

/**
 * FXML Controller class for the RightsManagementView.fxml.
 *
 * @author mirko.schulze
 */
@Dependent
@Title("Rechte-Verwaltung")
@Frame
public class RightsManagementController implements Initializable, FxController {

    private static final Logger L = LoggerFactory.getLogger(RightsManagementController.class);

    @Inject
    private Saft saft;

    @Inject
    private RemoteDl remote;

    private UserApi userApi;

    private GroupApi groupApi;

    /**
     * List with a {@link User} representation for each {@link Operator} in the database.
     */
    private List<User> allUsers;

    /**
     * List with a {@link Group} representation for each {@link Persona} in the database.
     */
    private List<Group> allGroups;

    /**
     * The {@link User} currently selected in {@link #userListView}.
     */
    private User selectedUser;

    /**
     * The {@link Group} currently selected in either {@link #activeGroupsListView} or {@link #inactiveGroupsListView}.
     */
    private Group selectedGroup;

    //<editor-fold defaultstate="collapsed" desc="FX Components">
    /**
     * Shows all current {@link User}<code>s</code>.
     */
    @FXML
    private ListView<User> userListView;

    /**
     * Shows all {@link Group}<code>s</code> that are associated with the {@link #selectedUser}.
     */
    @FXML
    private ListView<Group> activeGroupsListView;

    /**
     * Shows all {@link Group}<code>s</code> that are not associated with the {@link #selectedUser} or all Groups if no {@link User} is selected.
     */
    @FXML
    private ListView<Group> inactiveGroupsListView;

    /**
     * Shows all {@link AtomicRight}<code>s</code> that are granted to the {@link #selectedUser}.
     */
    @FXML
    private ListView<AtomicRight> activeRightsListView;

    /**
     * Shows all {@link AtomicRight}<code>s</code> that are not granted to the {@link #selectedUser} or all AtomicRights if no {@link User} is selected.
     */
    @FXML
    private ListView<AtomicRight> inactiveRightsListView;

    /**
     * Shows all {@link AtomicRight}<code>s</code> that are granted to the {@link #selectedUser}, both directly or via an associated {@link Group}.
     */
    @FXML
    private ListView<AtomicRight> allActiveUserRightsListView;

    /**
     * Shows all {@link AtomicRight}<code>s</code> that are granted to the {@link #selectedGroup}.
     */
    @FXML
    private ListView<AtomicRight> allActiveGroupRightsListView;

    @FXML
    private Button addAllGroupsButton, addGroupButton, removeGroupButton, removeAllGroupsButton, addAllRightsButton, addRightButton, removeRightButton,
            removeAllRightsButton, createUserButton, createGroupButton, deleteUserButton, deleteGroupButton, closeButton, changePasswordButton;
    //</editor-fold>

    private User getSelectedUserFromUserListView() {
        return userListView.getSelectionModel().getSelectedItem();
    }

    private Long getSelectedUserIdFromUserListView() {
        return userListView.getSelectionModel().getSelectedItem().getId().get();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        userApi = remote.lookup(UserApi.class);
        groupApi = remote.lookup(GroupApi.class);
        allUsers = userApi.findAll();
        allGroups = groupApi.findAll();
        //ListViews
        //userListView
        userListView.setCellFactory(new UserListCell.Factory());
        userListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        userListView.setOnMouseClicked(e -> {
            if ( e.getButton().equals(MouseButton.PRIMARY) ) {
                selectedUser = getSelectedUserFromUserListView();
                activeRightsListView.getSelectionModel().select(-1);
                inactiveRightsListView.getSelectionModel().select(-1);
                if ( e.getClickCount() == 1 ) {
                    refreshRightsListViews();
                    refreshGroupsListViews();
                    refreshSelectedUserRightsListViews();
                    refreshSelectedGroupRightsListView();
                } else {
                    saft.build(closeButton)
                            .title("Benutzer-Verwaltung: Benutzer bearbeiten")
                            .modality(Modality.WINDOW_MODAL)
                            .fxml()
                            .eval(() -> selectedUser, UserManagementController.class)
                            .cf()
                            .thenAcceptAsync(result -> {
                                selectedUser.getRights().forEach(r -> userApi.removeRight(selectedUser.getId().get(), r));
                                selectedUser.getGroups().forEach(g -> userApi.removeGroup(selectedUser.getId().get(), g.getId().get()));
                                userApi.updateUsername(selectedUser.getId().get(), result.getUser().getUsername());
                                result.getPassword().ifPresent(p -> userApi.updatePassword(selectedUser.getId().get(), p.toCharArray()));
                                result.getUser().getRights().forEach(r -> userApi.addRight(selectedUser.getId().get(), r));
                                result.getUser().getGroups().forEach(g -> userApi.addGroup(selectedUser.getId().get(), g.getId().get()));
                            })
                            .thenRunAsync(() -> {
                                loadUsersAndGroups();
                                refreshUi();
                            }, Platform::runLater)
                            .handle(saft.handler());
                }
            }
        });
        //activeRightsListView
        activeRightsListView.setCellFactory(new RightsListCell.Factory());
        activeRightsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        activeRightsListView.setOnMouseClicked(e -> {
            inactiveRightsListView.getSelectionModel().select(-1);
        });
        //inactiveRightsListView
        inactiveRightsListView.setCellFactory(new RightsListCell.Factory());
        inactiveRightsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        inactiveRightsListView.setOnMouseClicked(e -> {
            activeRightsListView.getSelectionModel().select(-1);
        });
        //activeGroupsListView
        activeGroupsListView.setCellFactory(new GroupListCell.Factory());
        activeGroupsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        activeGroupsListView.setOnMouseClicked(e -> {
            if ( e.getButton().equals(MouseButton.PRIMARY) ) {
                selectedGroup = activeGroupsListView.getSelectionModel().getSelectedItem();
                inactiveGroupsListView.getSelectionModel().select(-1);
                if ( e.getClickCount() == 1 ) {
                    refreshSelectedGroupRightsListView();
                } else {
                    editGroup(selectedGroup);
                    refreshSelectedGroupRightsListView();
                }
            }
        });
        //inactiveGroupsListView
        inactiveGroupsListView.setCellFactory(new GroupListCell.Factory());
        inactiveGroupsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        inactiveGroupsListView.setOnMouseClicked(e -> {
            if ( e.getButton().equals(MouseButton.PRIMARY) ) {
                selectedGroup = inactiveGroupsListView.getSelectionModel().getSelectedItem();
                activeGroupsListView.getSelectionModel().select(-1);
                if ( e.getClickCount() == 1 ) {
                    refreshSelectedGroupRightsListView();
                } else {
                    editGroup(selectedGroup);
                    refreshSelectedGroupRightsListView();
                }
            }
        });
        //allActiveRightsUserListView
        allActiveUserRightsListView.setCellFactory(new RightsListCell.Factory());
        //allActiveRightsGroupListView
        allActiveGroupRightsListView.setCellFactory(new RightsListCell.Factory());
        //Buttons
        //addAllRightsButton
        addAllRightsButton.disableProperty().bind(userListView.getSelectionModel().selectedIndexProperty().isEqualTo(-1));
        addAllRightsButton.setOnAction(e -> {
            inactiveRightsListView.getItems().forEach(r -> userApi.addRight(getSelectedUserIdFromUserListView(), r));
            loadUsersAndGroups();
            selectedUser = userApi.findById(selectedUser.getId().get());
            refreshUi();
        });
        //addRightButton
        addRightButton.disableProperty().bind(userListView.getSelectionModel().selectedIndexProperty().isEqualTo(-1));
        addRightButton.setOnAction(e -> {
            inactiveRightsListView.getSelectionModel().getSelectedItems().forEach(r -> userApi.addRight(getSelectedUserIdFromUserListView(), r));
            loadUsersAndGroups();
            selectedUser = userApi.findById(selectedUser.getId().get());
            refreshUi();
        });
        //removeRightButton
        removeRightButton.disableProperty().bind(userListView.getSelectionModel().selectedIndexProperty().isEqualTo(-1));
        removeRightButton.setOnAction(e -> {
            activeRightsListView.getSelectionModel().getSelectedItems().forEach(r -> userApi.removeRight(getSelectedUserIdFromUserListView(), r));
            loadUsersAndGroups();
            selectedUser = userApi.findById(selectedUser.getId().get());
            refreshUi();
        });
        //removeAllRightsButton
        removeAllRightsButton.disableProperty().bind(userListView.getSelectionModel().selectedIndexProperty().isEqualTo(-1));
        removeAllRightsButton.setOnAction(e -> {
            activeRightsListView.getItems().forEach(r -> userApi.removeRight(getSelectedUserIdFromUserListView(), r));
            loadUsersAndGroups();
            selectedUser = userApi.findById(selectedUser.getId().get());
            refreshUi();
        });
        //addAllGroupsButton
        addAllGroupsButton.disableProperty().bind(userListView.getSelectionModel().selectedIndexProperty().isEqualTo(-1));
        addAllGroupsButton.setOnAction(e -> {
            List<Long> inactiveGroupIds = inactiveGroupsListView.getItems()
                    .stream()
                    .mapToLong(g -> g.getId().get())
                    .boxed()
                    .collect(Collectors.toList());
            inactiveGroupIds.forEach(id -> userApi.addGroup(getSelectedUserIdFromUserListView(), id));
            loadUsersAndGroups();
            selectedUser = userApi.findById(selectedUser.getId().get());
            refreshUi();
        });
        //addGroupButton
        addGroupButton.disableProperty().bind(userListView.getSelectionModel().selectedIndexProperty().isEqualTo(-1));
        addGroupButton.setOnAction(e -> {
            List<Long> selectedInactiveGroupIds = inactiveGroupsListView.getSelectionModel().getSelectedItems()
                    .stream()
                    .mapToLong(g -> g.getId().get())
                    .boxed()
                    .collect(Collectors.toList());
            selectedInactiveGroupIds.forEach(id -> userApi.addGroup(getSelectedUserIdFromUserListView(), id));
            loadUsersAndGroups();
            selectedUser = userApi.findById(selectedUser.getId().get());
            refreshUi();
        });
        //removeGroupButton
        removeGroupButton.disableProperty().bind(userListView.getSelectionModel().selectedIndexProperty().isEqualTo(-1));
        removeGroupButton.setOnAction(e -> {
            List<Long> selectedActiveGroupIds = activeGroupsListView.getSelectionModel().getSelectedItems()
                    .stream()
                    .mapToLong(g -> g.getId().get())
                    .boxed()
                    .collect(Collectors.toList());
            selectedActiveGroupIds.forEach(id -> userApi.removeGroup(getSelectedUserIdFromUserListView(), id));
            loadUsersAndGroups();
            selectedUser = userApi.findById(selectedUser.getId().get());
            refreshUi();
        });
        //removeAllGroupsButton
        removeAllGroupsButton.disableProperty().bind(userListView.getSelectionModel().selectedIndexProperty().isEqualTo(-1));
        removeAllGroupsButton.setOnAction(e -> {
            List<Long> activeGroupIds = activeGroupsListView.getItems()
                    .stream()
                    .mapToLong(g -> g.getId().get())
                    .boxed()
                    .collect(Collectors.toList());
            activeGroupIds.forEach(id -> userApi.removeGroup(getSelectedUserIdFromUserListView(), id));
            loadUsersAndGroups();
            selectedUser = userApi.findById(selectedUser.getId().get());
            refreshUi();
        });
        //createUserButton
        createUserButton.setOnAction(e -> {
            L.debug("createUser");
            saft.build(createUserButton)
                    .title("Benutzer-Verwaltung: Neuen Benutzer anlegen")
                    .modality(Modality.WINDOW_MODAL)
                    .fxml()
                    .eval(UserManagementController.class)
                    .cf()
                    .thenAcceptAsync(result -> {
                        userApi.create(result.getUser().getUsername());
                        long id = userApi.findByName(result.getUser().getUsername()).getId().get();
                        result.getPassword().ifPresent(p -> userApi.updatePassword(id, p.toCharArray()));
                        result.getUser().getRights().forEach(r -> userApi.addRight(id, r));
                        result.getUser().getGroups().forEach(g -> userApi.addGroup(id, g.getId().get()));
                        selectedUser = userApi.findById(id);
                        userListView.getSelectionModel().select(selectedUser);
                    })
                    .thenRunAsync(() -> {
                        loadUsersAndGroups();
                        refreshUi();
                    }, Platform::runLater)
                    .handle(saft.handler());
        });
        //createGroupButton
        createGroupButton.setOnAction(e -> {
            saft.build(createGroupButton)
                    .title("Gruppen-Verwaltung: Neue Gruppe anlegen")
                    .modality(Modality.WINDOW_MODAL)
                    .fxml()
                    .eval(GroupManagementController.class)
                    .cf()
                    .thenAcceptAsync(group -> {
                        groupApi.create(group.getName());
                        long id = groupApi.findByName(group.getName()).getId().get();
                        group.getRights().forEach(r -> groupApi.addRight(id, r));
                    })
                    .thenRunAsync(() -> {
                        loadUsersAndGroups();
                        refreshUi();
                    }, Platform::runLater)
                    .handle(saft.handler());
        });
        //deleteUserButton
        deleteUserButton.setOnAction(e -> {
            if ( selectedUser == null ) return;
            saft.build(addAllGroupsButton).dialog()
                    .eval(() -> new Alert(Alert.AlertType.CONFIRMATION,
                    "Wollen Sie die ausgewählten Benutzer löschen?")).cf().thenAccept((bt) -> {
                if ( bt != OK ) throw new CancellationException();
                List<Long> selectedUsersIds = userListView.getSelectionModel().getSelectedItems()
                        .stream()
                        .mapToLong(u -> u.getId().get())
                        .boxed()
                        .collect(Collectors.toList());
                selectedUsersIds.forEach(id -> userApi.delete(id));
                selectedUser = null;
                loadUsersAndGroups();
                refreshUi();
            });
        });
        //deleteGroupButton
        deleteGroupButton.setOnAction(e -> {
            if ( selectedGroup != null ) {
                //Groups that are used by any User must not simply be deleted
                Set<Group> groupsInUsage = new HashSet<>();
                allUsers.forEach(u -> u.getGroups().forEach(g -> groupsInUsage.add(g)));

                List<String> groupUsers = new ArrayList<>();
                List<String> groupNames = new ArrayList<>();

                for (Group group : inactiveGroupsListView.getSelectionModel().getSelectedItems()) {
                    groupNames.add(group.getName());
                    for (User user : allUsers) {
                        if ( user.getGroups().contains(group) ) groupUsers.add(user.getUsername());
                    }
                }

                final String msg = "Wollen Sie die Gruppen " + groupNames + " löschen?"
                        + (groupUsers.isEmpty() ? "" : "\nFolgende Nutzer sind noch Mitglied:" + groupUsers);

                saft.build(addAllGroupsButton).dialog()
                        .eval(() -> new Alert(Alert.AlertType.CONFIRMATION, msg)).cf().thenAccept((bt) -> {
                    if ( bt != OK ) throw new CancellationException();
                    for (Group group : inactiveGroupsListView.getSelectionModel().getSelectedItems()) {
                        groupApi.delete(group.getId().get());
                    }
                    selectedGroup = null;
                    loadUsersAndGroups();
                    refreshUi();
                });
            }
        });
        
        changePasswordButton.disableProperty().bind(userListView.getSelectionModel().selectedIndexProperty().isEqualTo(-1));
        
        changePasswordButton.setOnAction(e -> {
            saft.build(changePasswordButton).fx()
                    .eval(() -> selectedUser,ChangePasswordPane.class).cf()
                    .thenAccept(p -> userApi.updatePassword(p.id(), p.password()));
        });

        //closeButton
        closeButton.setOnAction(e -> {
            saft.closeWindowOf(closeButton);
        }
        );
        //load data
        loadUsersAndGroups();
        refreshUi();
    }

    /**
     * Opens a new {@link Stage} to modify the submitted {@link Group}.
     *
     * @param group Group to be modified.
     */
    private void editGroup(Group group) {
        L.debug("editGroup({}) called", group);
        saft.build(closeButton)
                .title("Gruppen-Verwaltung: Gruppe bearbeiten")
                .modality(Modality.WINDOW_MODAL)
                .fxml()
                .eval(() -> group, GroupManagementController.class)
                .cf()
                .thenAccept(g -> {
                    selectedGroup.getRights().forEach(r -> groupApi.removeRight(selectedGroup.getId().get(), r));
                    groupApi.updateName(g.getId().get(), g.getName());
                    g.getRights().forEach(r -> groupApi.addRight(selectedGroup.getId().get(), r));
                    loadUsersAndGroups();
                    refreshUi();
                })
                .handle(saft.handler());
    }

    /**
     * Searches for all {@link Operator}<code>s</code> and {@link Persona}<code>s</code> and fills {@link #allUsers} and {@link #allGroups}.
     */
    private void loadUsersAndGroups() {
        L.debug("loadUsersAndGroups() called");
        allUsers = userApi.findAll();
        L.debug("allUsers = {}", allUsers);
        allGroups = groupApi.findAll();
        L.debug("allGroups = {}", allGroups);
    }

    /**
     * Sets the items at {@link #userListView}, {@link #activeRightsListView}, {@link #inactiveRightsListView}, {@link #activeGroupsListView},
     * {@link #inactiveGroupsListView}, {@link #allActiveUserRightsListView}, {@link #allActiveGroupRightsListView}.
     */
    private void refreshUi() {
        L.debug("refreshUi() called");
        Platform.runLater(() -> {
            refreshUserListView();
            refreshRightsListViews();
            refreshGroupsListViews();
            refreshSelectedUserRightsListViews();
            refreshSelectedGroupRightsListView();
        });
    }

    /**
     * Sets the items at {@link #userListView} and handles reselection of the last selected {@link User}.
     */
    private void refreshUserListView() {
        L.debug("refreshUserListView() called");
        int index = userListView.getSelectionModel().selectedIndexProperty().get();
        userListView.setItems(FXCollections.observableArrayList(allUsers));
        if ( index >= 0 ) {
            userListView.getSelectionModel().select(index);
            selectedUser = userListView.getSelectionModel().getSelectedItem();
        }
    }

    /**
     * Sets the items at {@link #activeRightsListView} and {@link #inactiveRightsListView} depending on the {@link #selectedUser}.
     */
    private void refreshRightsListViews() {
        L.debug("refreshRightsListViews() called");
        if ( selectedUser == null ) {
            activeRightsListView.getItems().clear();
            ObservableList<AtomicRight> inactiveRights = FXCollections.observableArrayList(AtomicRight.values());
            Collections.sort(inactiveRights, Comparator.comparing(AtomicRight::description));
            inactiveRightsListView.setItems(FXCollections.observableArrayList(inactiveRights));
        } else {
            List<AtomicRight> activeRights = new ArrayList<>(selectedUser.getRights());
            Collections.sort(activeRights, Comparator.comparing(AtomicRight::description));
            activeRightsListView.setItems(FXCollections.observableArrayList(activeRights));
            List<AtomicRight> inactiveRights = Arrays.asList(AtomicRight.values())
                    .stream()
                    .filter(r -> !selectedUser.getRights().contains(r))
                    .collect(Collectors.toList());
            Collections.sort(inactiveRights, Comparator.comparing(AtomicRight::description));
            inactiveRightsListView.setItems(FXCollections.observableArrayList(inactiveRights));
        }
    }

    /**
     * Sets the items at {@link #activeGroupsListView} and {@link #inactiveGroupsListView} depending on the {@link #selectedUser} and handles reselection of the
     * last selected {@link Group}.
     */
    private void refreshGroupsListViews() {
        L.debug("refreshGroupListViews() called");
        boolean active = true;
        int index = activeGroupsListView.getSelectionModel().selectedIndexProperty().get();
        if ( index == -1 ) {
            index = inactiveGroupsListView.getSelectionModel().selectedIndexProperty().get();
            active = false;
        }
        if ( selectedUser == null ) {
            activeGroupsListView.getItems().clear();
            inactiveGroupsListView.setItems(FXCollections.observableArrayList(allGroups));
        } else {
            List<Group> activeGroups = selectedUser.getGroups();
            activeGroupsListView.setItems(FXCollections.observableArrayList(activeGroups));
            List<Group> inactiveGroups = allGroups
                    .stream()
                    .filter(g -> !selectedUser.getGroups().contains(g))
                    .collect(Collectors.toList());
            inactiveGroupsListView.setItems(FXCollections.observableArrayList(inactiveGroups));
        }
        if ( active ) {
            activeGroupsListView.getSelectionModel().select(index);
            selectedGroup = activeGroupsListView.getSelectionModel().getSelectedItem();
        } else {
            inactiveGroupsListView.getSelectionModel().select(index);
            selectedGroup = inactiveGroupsListView.getSelectionModel().getSelectedItem();
        }
    }

    /**
     * Sets the items at {@link #allActiveUserRightsListView} depending on {@link #selectedUser}.
     */
    private void refreshSelectedUserRightsListViews() {
        L.debug("refreshSelectedUserRightsListViews() called");
        if ( selectedUser == null ) {
            allActiveUserRightsListView.getItems().clear();
        } else {
            ObservableList<AtomicRight> allActiveUserRights = FXCollections.observableArrayList(selectedUser.getAllRights());
            Collections.sort(allActiveUserRights, Comparator.comparing(AtomicRight::description));
            allActiveUserRightsListView.setItems(allActiveUserRights);
        }
    }

    /**
     * Sets the items at {@link #allActiveGroupRightsListView} depending on {@link #selectedGroup}.
     */
    private void refreshSelectedGroupRightsListView() {
        L.debug("refreshSelectedGroupRightsListView() called");
        if ( selectedGroup == null ) {
            allActiveGroupRightsListView.getItems().clear();
        } else {
            List<AtomicRight> allActiveGroupRights = new ArrayList<>(selectedGroup.getRights());
            Collections.sort(allActiveGroupRights, Comparator.comparing(AtomicRight::description));
            allActiveGroupRightsListView.setItems(FXCollections.observableArrayList(allActiveGroupRights));
        }
    }

}
