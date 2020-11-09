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

import javafx.application.Platform;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.stage.Modality;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.core.widget.Dl;
import eu.ggnet.dwoss.rights.api.*;
import eu.ggnet.saft.core.Ui;
import eu.ggnet.saft.core.UiCore;
import eu.ggnet.saft.core.ui.*;

/**
 * FXML Controller class for the RightsManagementView.fxml.
 *
 * @author mirko.schulze
 */
//TODO umlaute berücksichtigen beim sortieren
//sort unmodifiable collection
//rchte ersetzen verbessern
//completable future
//"data information" an groups/users
@Title("Rechte-Verwaltung")
@Frame
public class NewRightsManagementController implements Initializable, FxController {

    private static final Logger L = LoggerFactory.getLogger(NewRightsManagementController.class);

    private final UserApi userApi = Dl.remote().lookup(UserApi.class);

    private final GroupApi groupApi = Dl.remote().lookup(GroupApi.class);

    private List<User> allUsers = userApi.findAll();

    private List<Group> allGroups = groupApi.findAll();

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
            removeAllRightsButton, createUserButton, createGroupButton, deleteUserButton, deleteGroupButton, closeButton;
    //</editor-fold>

    private User getSelectedUserFromUserListView() {
        return userListView.getSelectionModel().getSelectedItem();
    }

    private Long getSelectedUserIdFromUserListView() {
        return userListView.getSelectionModel().getSelectedItem().getId().get();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //ListViews
        //userListView
        userListView.setCellFactory(new UserListCell.Factory());
        userListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        userListView.setOnMouseClicked(e -> {
            if ( e.getButton().equals(MouseButton.PRIMARY) ) {
                selectedUser = getSelectedUserFromUserListView();
                if ( e.getClickCount() == 1 ) {
                    refreshRightsListViews();
                    refreshGroupsListViews();
                    refreshSelectedUserRightsListViews();
                    refreshSelectedGroupRightsListView();
                } else {
                    editUser(selectedUser);
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
//                    refreshUi();
                    refreshSelectedGroupRightsListView();
                } else {
                    editGroup(selectedGroup);
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
//                    refreshUi();
                    refreshSelectedGroupRightsListView();
                } else {
                    editGroup(selectedGroup);
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
            refreshUi();
        });
        //addRightButton
        addRightButton.disableProperty().bind(userListView.getSelectionModel().selectedIndexProperty().isEqualTo(-1));
        addRightButton.setOnAction(e -> {
            inactiveRightsListView.getSelectionModel().getSelectedItems().forEach(r -> userApi.addRight(getSelectedUserIdFromUserListView(), r));
            refreshUi();
        });
        //removeRightButton
        removeRightButton.disableProperty().bind(userListView.getSelectionModel().selectedIndexProperty().isEqualTo(-1));
        removeRightButton.setOnAction(e -> {
            activeRightsListView.getSelectionModel().getSelectedItems().forEach(r -> userApi.removeRight(getSelectedUserIdFromUserListView(), r));
            refreshUi();
        });
        //removeAllRightsButton
        removeAllRightsButton.disableProperty().bind(userListView.getSelectionModel().selectedIndexProperty().isEqualTo(-1));
        removeAllRightsButton.setOnAction(e -> {
            activeRightsListView.getItems().forEach(r -> userApi.removeRight(getSelectedUserIdFromUserListView(), r));
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
            refreshUi();
        });
        //createUserButton
        createUserButton.setOnAction(e -> {
            L.info("createUser");
            Ui.build(createUserButton)
                    .frame(true)
                    .title("Benutzer-Verwaltung: Neuen Benutzer anlegen")
                    .modality(Modality.WINDOW_MODAL)
                    .fxml()
                    .eval(() -> null, UserManagementController.class)
                    .cf()
                    .thenAcceptAsync(user -> {
                        userApi.create(user.getUsername());
                        long id = userApi.findByName(user.getUsername()).getId().get();
                        user.getRights().forEach(r -> userApi.addRight(id, r));
                        user.getGroups().forEach(g -> userApi.addGroup(id, g.getId().get()));
                    }, UiCore.getExecutor())
                    .thenRunAsync(() -> refreshUi(), Platform::runLater)
                    .handle(Ui.handler());
        });
        //createGroupButton
        createGroupButton.setOnAction(e -> {
            Ui.build(createGroupButton)
                    .frame(true)
                    .title("Gruppen-Verwaltung: Neue Gruppe anlegen")
                    .modality(Modality.WINDOW_MODAL)
                    .fxml()
                    .eval(() -> null, GroupManagementController.class)
                    .cf()
                    .thenAcceptAsync(group -> {
                        groupApi.create(group.getName());
                        long id = groupApi.findByName(group.getName()).getId().get();
                        group.getRights().forEach(r -> groupApi.addRight(id, r));
                    }, UiCore.getExecutor())
                    .thenRunAsync(() -> refreshUi(), Platform::runLater)
                    .handle(Ui.handler());
        });
        //deleteUserButton
        deleteUserButton.setOnAction(e -> {
            if ( selectedUser != null ) {
                if ( new Alert(Alert.AlertType.CONFIRMATION,
                        "Wollen Sie den ausgewählten Benutzer löschen?")
                        .showAndWait().get() == ButtonType.OK ) {
                    List<Long> selectedUsersIds = userListView.getSelectionModel().getSelectedItems()
                            .stream()
                            .mapToLong(u -> u.getId().get())
                            .boxed()
                            .collect(Collectors.toList());
                    selectedUsersIds.forEach(id -> userApi.delete(id));
                    selectedUser = null;
                    refreshUi();
                }
            }
        });
        //deleteGroupButton
        deleteGroupButton.setOnAction(e -> {
            if ( selectedGroup != null ) {
                //Groups that are used by any User must not simply be deleted
                Set<Group> groupsInUsage = new HashSet<>();
                allUsers.forEach(u -> u.getGroups().forEach(g -> groupsInUsage.add(g)));
                List<Long> selectedGroupsIds = activeGroupsListView.getSelectionModel().getSelectedItems()
                        .stream()
                        .mapToLong(g -> g.getId().get())
                        .boxed()
                        .collect(Collectors.toList());
                if ( selectedGroupsIds.isEmpty() ) {
                    selectedGroupsIds = inactiveGroupsListView.getSelectionModel().getSelectedItems()
                            .stream()
                            .mapToLong(g -> g.getId().get())
                            .boxed()
                            .collect(Collectors.toList());
                }
                if ( new Alert(Alert.AlertType.CONFIRMATION,
                        "Wollen Sie die ausgewählten Gruppen löschen?")
                        .showAndWait().get() == ButtonType.OK ) {
                    selectedGroupsIds.forEach(id -> {
                        Group group = groupApi.findById(id);
                        if ( groupsInUsage.contains(group) ) {
                            if ( new Alert(Alert.AlertType.CONFIRMATION,
                                    "Gruppe " + group.getName() + " wird noch verwendet.\nWollen Sie die Gruppe dennoch löschen?")
                                    .showAndWait().get() == ButtonType.OK ) {
                                allUsers.forEach(u -> {
                                    if ( u.getGroups().contains(groupApi.findById(group.getId().get())) ) {
                                        userApi.removeGroup(u.getId().get(), group.getId().get());
                                    }
                                });
                                groupApi.delete(group.getId().get());
                                selectedGroup = null;
                                refreshUi();
                            }
                        } else {
                            groupApi.delete(group.getId().get());
                            selectedGroup = null;
                            refreshUi();
                        }
                    });
                }
            }
        });
        //closeButton
        closeButton.setOnAction(e -> {
            Ui.closeWindowOf(closeButton);
        }
        );
        //load data
        refreshUi();
    }

    private void editUser(User user) {
        L.info("editUser({}) called", user);
        Ui.build(closeButton)
                .frame(true)
                .title("Benutzer-Verwaltung: Benutzer bearbeiten")
                .modality(Modality.WINDOW_MODAL)
                .fxml()
                .eval(() -> user, UserManagementController.class)
                .cf()
                .thenAcceptAsync(u -> {
                    selectedUser.getRights().forEach(r -> userApi.removeRight(selectedUser.getId().get(), r));
                    selectedUser.getGroups().forEach(g -> userApi.removeGroup(selectedUser.getId().get(), g.getId().get()));
                    userApi.updateUsername(selectedUser.getId().get(), u.getUsername());
                    u.getRights().forEach(r -> userApi.addRight(selectedUser.getId().get(), r));
                    u.getGroups().forEach(g -> userApi.addGroup(selectedUser.getId().get(), g.getId().get()));
                }, UiCore.getExecutor())
                .thenRunAsync(() -> refreshUi(), Platform::runLater)
                .handle(Ui.handler());
    }

    private void editGroup(Group group) {
        L.info("editGroup({}) called", group);
        Ui.build(closeButton)
                .frame(true)
                .title("Gruppen-Verwaltung: Gruppe bearbeiten")
                .modality(Modality.WINDOW_MODAL)
                .fxml()
                .eval(() -> group, GroupManagementController.class)
                .cf()
                .thenAcceptAsync(g -> {
                    selectedGroup.getRights().forEach(r -> groupApi.removeRight(selectedGroup.getId().get(), r));
                    groupApi.updateName(g.getId().get(), g.getName());
                    g.getRights().forEach(r -> groupApi.addRight(selectedGroup.getId().get(), r));
                }, UiCore.getExecutor())
                .thenRunAsync(() -> refreshUi(), Platform::runLater)
                .handle(Ui.handler());
    }

    /**
     * Sets the items at {@link #userListView}, {@link #activeRightsListView}, {@link #inactiveRightsListView}, {@link #activeGroupsListView},
     * {@link #inactiveGroupsListView}, {@link #allActiveUserRightsListView}, {@link #allActiveGroupRightsListView}.
     */
    private void refreshUi() {
        L.info("refreshUi() called");
        allUsers = userApi.findAll();
        allGroups = groupApi.findAll();

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
        L.info("refreshUserListView() called");
        int index = userListView.getSelectionModel().selectedIndexProperty().get();

        userListView.setItems(FXCollections.observableArrayList(allUsers));
        if ( selectedUser != null ) {
            userListView.getSelectionModel().select(index);
        } else {
            selectedUser = null;
        }
    }

    /**
     * Sets the items at {@link #activeRightsListView} and {@link #inactiveRightsListView} depending on the {@link #selectedUser}.
     */
    private void refreshRightsListViews() {
        L.info("refreshRightsListViews() called");
        User user = getSelectedUserFromUserListView();
        if ( selectedUser == null ) {
            activeRightsListView.getItems().clear();
            L.info("refreshRightsListViews() : activeRightsListView cleared");
            ObservableList<AtomicRight> inactiveRights = FXCollections.observableArrayList(AtomicRight.values());
            Collections.sort(inactiveRights, Comparator.comparing(AtomicRight::toName));
            inactiveRightsListView.setItems(FXCollections.observableArrayList(inactiveRights));
            L.info("refreshRightsListViews() : inactiveRightsListView set to {}", inactiveRights);
        } else {
            List<AtomicRight> activeRights = user.getRights();
            activeRightsListView.setItems(FXCollections.observableArrayList(activeRights));
            L.info("refreshRightsListViews() : inactiveRightsListView set to {}", activeRights);
            List<AtomicRight> inactiveRights = Arrays.asList(AtomicRight.values())
                    .stream()
                    .filter(r -> !user.getRights().contains(r))
                    .collect(Collectors.toList());
            Collections.sort(inactiveRights, (AtomicRight o1, AtomicRight o2) -> o1.toName().compareTo(o2.toName()));
            inactiveRightsListView.setItems(FXCollections.observableArrayList(inactiveRights));
            L.info("refreshRightsListViews() : inactiveRightsListView set to {}", inactiveRights);
        }
    }

    /**
     * Sets the items at {@link #activeGroupsListView} and {@link #inactiveGroupsListView} depending on the {@link #selectedUser} and handles reselection of the
     * last selected {@link Group}.
     */
    private void refreshGroupsListViews() {
        L.info("refreshGroupListViews() called");
        if ( selectedUser == null ) {
            activeGroupsListView.getItems().clear();
            L.info("refreshGroupsListViews() : activeGroupsListView cleared");
            inactiveGroupsListView.setItems(FXCollections.observableArrayList(allGroups));
            L.info("refreshGroupsListViews() : inactiveGroupsListView set to {}", allGroups);
            selectedGroup = null;
        } else {
            User user = getSelectedUserFromUserListView();
            List<Group> activeGroups = user.getGroups();
            activeGroupsListView.setItems(FXCollections.observableArrayList(activeGroups));
            L.info("refreshGroupsListViews() : activeGroupsListView set to {}", activeGroups);
            List<Group> inactiveGroups = allGroups
                    .stream()
                    .filter(g -> !user.getGroups().contains(g))
                    .collect(Collectors.toList());
            inactiveGroupsListView.setItems(FXCollections.observableArrayList(inactiveGroups));
            L.info("refreshGroupsListViews() : inactiveGroupsListView set to {}", inactiveGroups);
        }
        if ( selectedGroup != null ) {
            long groupId = selectedGroup.getId().get();
            ObservableList<Group> obsGroups = activeGroupsListView.getItems();
            if ( obsGroups.stream().mapToLong(g -> g.getId().get())
                    .anyMatch(id -> id == groupId) ) {
                activeGroupsListView.getSelectionModel().select(selectedGroup);
            } else {
                inactiveGroupsListView.getSelectionModel().select(selectedGroup);
            }
            L.info("refreshGroupsListViews() : Group {} selected", selectedGroup);
        }
    }

    /**
     * Sets the items at {@link #allActiveUserRightsListView} depending on {@link #selectedUser}.
     */
    private void refreshSelectedUserRightsListViews() {
        L.info("refreshSelectedUserRightsListViews() called");
        if ( selectedUser == null ) {
            allActiveUserRightsListView.getItems().clear();
            L.info("refreshSelectedUserRightsListViews() : UserRightsListView cleared");
        } else {
            ObservableList<AtomicRight> allActiveUserRights = FXCollections.observableArrayList(getSelectedUserFromUserListView().getAllRights());
            Collections.sort(allActiveUserRights, Comparator.comparing(AtomicRight::toName));
            allActiveUserRightsListView.setItems(allActiveUserRights);
            L.info("refreshSelectedUserRightsListViews() : selectedUserRightsListView set to {}", allActiveUserRights);
        }
    }

    /**
     * Sets the items at {@link #allActiveGroupRightsListView} depending on {@link #selectedGroup}.
     */
    private void refreshSelectedGroupRightsListView() {
        L.info("refreshSelectedGroupRightsListView() called");
        if ( selectedGroup == null ) {
            allActiveGroupRightsListView.getItems().clear();
            L.info("refreshSelectedGroupRightsListView() : selectedGroupRightsListView cleared");
        } else {
            List<AtomicRight> allActiveGroupRights = selectedGroup.getRights();
            allActiveGroupRightsListView.setItems(FXCollections.observableArrayList(allActiveGroupRights));
            L.info("refreshSelectedGroupRightsListView() : selectedGroupRightsListView set to {}", allActiveGroupRights);
        }
    }

}
