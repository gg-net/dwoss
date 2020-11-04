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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.core.widget.Dl;
import eu.ggnet.dwoss.rights.api.*;
import eu.ggnet.saft.core.Ui;
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
//"data debugrmation" an groups/users
@Title("Rechte-Verwaltung")
@Frame
public class NewRightsManagementController implements Initializable, FxController {

    private static final Logger L = LoggerFactory.getLogger(NewRightsManagementController.class);

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
    private Button addAllGroupsButton;

    @FXML
    private Button addGroupButton;

    @FXML
    private Button removeGroupButton;

    @FXML
    private Button removeAllGroupsButton;

    @FXML
    private Button addAllRightsButton;

    @FXML
    private Button addRightButton;

    @FXML
    private Button removeRightButton;

    @FXML
    private Button removeAllRightsButton;

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
    //</editor-fold>

    private User getSelectedUserFromUserListView() {
        return userListView.getSelectionModel().getSelectedItem();
    }

    private Long getSelectedUserIdFromUserListView() {
        return userListView.getSelectionModel().getSelectedItem().getId().get();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        UserApi userBean = Dl.remote().lookup(UserApi.class);
        GroupApi groupBean = Dl.remote().lookup(GroupApi.class);
        //ListViews
        //userListView
        userListView.setCellFactory(new UserListCell.Factory());
        userListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        userListView.setOnMouseClicked(e -> {
            if ( e.getButton().equals(MouseButton.PRIMARY) ) {
                selectedUser = getSelectedUserFromUserListView();
                if ( e.getClickCount() == 1 ) {
                    refreshUi(false, false);
                } else {
                    editUser();
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
                    editGroup();
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
                    editGroup();
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
            inactiveRightsListView.getItems().forEach(r -> userBean.addRight(getSelectedUserIdFromUserListView(), r));
            refreshUi(false, false);
        });
        //addRightButton
        addRightButton.disableProperty().bind(userListView.getSelectionModel().selectedIndexProperty().isEqualTo(-1));
        addRightButton.setOnAction(e -> {
            inactiveRightsListView.getSelectionModel().getSelectedItems()
                    .forEach(r -> userBean.addRight(getSelectedUserIdFromUserListView(), r));
            refreshUi(false, false);
        });
        //removeRightButton
        removeRightButton.disableProperty().bind(userListView.getSelectionModel().selectedIndexProperty().isEqualTo(-1));
        removeRightButton.setOnAction(e -> {
            activeRightsListView.getSelectionModel().getSelectedItems()
                    .forEach(r -> userBean.removeRight(getSelectedUserIdFromUserListView(), r));
            refreshUi(false, false);
        });
        //removeAllRightsButton
        removeAllRightsButton.disableProperty().bind(userListView.getSelectionModel().selectedIndexProperty().isEqualTo(-1));
        removeAllRightsButton.setOnAction(e -> {
            activeRightsListView.getItems().forEach(r -> userBean.removeRight(getSelectedUserIdFromUserListView(), r));
            refreshUi(false, false);
        });
        //addAllGroupsButton
        addAllGroupsButton.disableProperty().bind(userListView.getSelectionModel().selectedIndexProperty().isEqualTo(-1));
        addAllGroupsButton.setOnAction(e -> {
            List<Long> inactiveGroupIds = inactiveGroupsListView.getItems()
                    .stream()
                    .mapToLong(g -> g.getId().get())
                    .boxed()
                    .collect(Collectors.toList());
            inactiveGroupIds.forEach(id -> userBean.addGroup(getSelectedUserIdFromUserListView(), id));
            refreshUi(false, false);
        });
        //addGroupButton
        addGroupButton.disableProperty().bind(userListView.getSelectionModel().selectedIndexProperty().isEqualTo(-1));
        addGroupButton.setOnAction(e -> {
            List<Long> selectedInactiveGroupIds = inactiveGroupsListView.getSelectionModel().getSelectedItems()
                    .stream()
                    .mapToLong(g -> g.getId().get())
                    .boxed()
                    .collect(Collectors.toList());
            selectedInactiveGroupIds.forEach(id -> userBean.addGroup(getSelectedUserIdFromUserListView(), id));
            refreshUi(false, false);
        });
        //removeGroupButton
        removeGroupButton.disableProperty().bind(userListView.getSelectionModel().selectedIndexProperty().isEqualTo(-1));
        removeGroupButton.setOnAction(e -> {
            List<Long> selectedActiveGroupIds = activeGroupsListView.getSelectionModel().getSelectedItems()
                    .stream()
                    .mapToLong(g -> g.getId().get())
                    .boxed()
                    .collect(Collectors.toList());
            selectedActiveGroupIds.forEach(id -> userBean.removeGroup(getSelectedUserIdFromUserListView(), id));
            refreshUi(false, false);
        });
        //removeAllGroupsButton
        removeAllGroupsButton.disableProperty().bind(userListView.getSelectionModel().selectedIndexProperty().isEqualTo(-1));
        removeAllGroupsButton.setOnAction(e -> {
            List<Long> activeGroupIds = activeGroupsListView.getItems()
                    .stream()
                    .mapToLong(g -> g.getId().get())
                    .boxed()
                    .collect(Collectors.toList());
            activeGroupIds.forEach(id -> userBean.removeGroup(getSelectedUserIdFromUserListView(), id));
            refreshUi(false, false);
        });
        //createUserButton
        createUserButton.setOnAction(e -> {
            createUser();
        });
        //createGroupButton
        createGroupButton.setOnAction(e -> {
            createGroup();
        });
        //deleteUserButton
        deleteUserButton.setOnAction(e -> {
            if ( selectedUser != null ) {
                List<Long> selectedUsersIds = userListView.getSelectionModel().getSelectedItems()
                        .stream()
                        .mapToLong(u -> u.getId().get())
                        .boxed()
                        .collect(Collectors.toList());
                selectedUsersIds.forEach(id -> userBean.delete(id));
                selectedUser = null;
                refreshUi(true, false);
            }
        });
        //deleteGroupButton
        deleteGroupButton.setOnAction(e -> {
            if ( selectedGroup != null ) {
                //Groups that are used by any User must not simply be deleted
                List<User> allUsers = userBean.findAll();
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
                selectedGroupsIds.forEach(id -> {
                    Group group = groupBean.findById(id);
                    if ( groupsInUsage.contains(group) ) {
                        if ( new Alert(Alert.AlertType.CONFIRMATION,
                                "Gruppe " + group.getName() + " wird noch verwendet.\nWollen Sie die Gruppe dennoch löschen?")
                                .showAndWait().get() == ButtonType.OK ) {
                            allUsers.forEach(u -> {
                                if ( u.getGroups().contains(groupBean.findById(group.getId().get())) ) {
                                    userBean.removeGroup(u.getId().get(), group.getId().get());
                                }
                            });
                            groupBean.delete(group.getId().get());
                            selectedGroup = null;
                            refreshUi(false, true);
                        }
                    } else {
                        groupBean.delete(group.getId().get());
                        selectedGroup = null;
                        refreshUi(false, true);
                    }
                });
            }
        });
        //closeButton
        closeButton.setOnAction(e -> {
            Ui.closeWindowOf(closeButton);
        });
        //load data
        refreshUi(false, false);
    }

    /**
     * Opens a new {@link CreateUserDialog} to create a new {@link User}.
     */
    private void createUser() {
        Ui.build(closeButton)
                .dialog()
                .eval(() -> new CreateUserDialog())
                .cf()
                .thenAccept(u -> {
                    UserApi bean = Dl.remote().lookup(UserApi.class);
                    bean.create(u.getUsername());
                    User user = bean.findByName(u.getUsername());
                    System.out.println(u.toString());
                    System.out.println("\n" + u.getId().toString() + "\n" + u.getGroups().toString() + "\n\n");
                    u.getGroups().forEach(g -> bean.addGroup(user.getId().get(), g.getId().get()));
                    u.getRights().forEach(r -> bean.addRight(u.getId().get(), r));
                })
                .thenRunAsync(() -> refreshUi(false, false), Platform::runLater)
                .handle(Ui.handler());
    }

    /**
     * Opens a new {@link CreateGroupDialog} to create a new {@link Group}.
     */
    private void createGroup() {
        Ui.build(closeButton)
                .dialog()
                .eval(() -> new CreateGroupDialog())
                .cf()
                .thenAccept(g -> {
                    GroupApi bean = Dl.remote().lookup(GroupApi.class);
                    bean.create(g.getName());
                    Group group = bean.findByName(g.getName());
                    g.getRights().forEach(r -> bean.addRight(group.getId().get(), r));
                })
                .thenRunAsync(() -> refreshUi(false, false), Platform::runLater)
                .handle(Ui.handler());
    }

    /**
     * Opens a new {@link EditUserDialog} to edit the {@link #selectedUser}.
     */
    private void editUser() {
        Ui.build(closeButton)
                .dialog()
                // .eval (() -> selectedUser, () -> new CreateUpdateUserDialog())
                .eval(() -> new EditUserDialog(selectedUser))
                .cf()
                .thenAccept(u -> {
                    UserApi bean = Dl.remote().lookup(UserApi.class);
                    bean.updateUsername(selectedUser.getId().get(), u.getUsername());
                    selectedUser.getGroups().forEach(g -> bean.removeGroup(selectedUser.getId().get(), g.getId().get()));
                    u.getGroups().forEach(g -> bean.addGroup(selectedUser.getId().get(), g.getId().get()));
                    selectedUser.getRights().forEach(r -> bean.removeRight(selectedUser.getId().get(), r));
                    u.getRights().forEach(r -> bean.addRight(selectedUser.getId().get(), r));
                })
                .thenRunAsync(() -> refreshUi(false, false))
                .handle(Ui.handler());
    }

    /**
     * Opens a new {@link EditUserDialog} to edit the {@link #selectedGroup}.
     */
    private void editGroup() {
        Ui.build(closeButton)
                .dialog()
                .eval(() -> new EditGroupDialog(selectedGroup))
                .cf()
                .thenAccept(g -> {
                    GroupApi bean = Dl.remote().lookup(GroupApi.class);
                    bean.updateName(selectedGroup.getId().get(), g.getName());
                    selectedGroup.getRights().forEach(r -> bean.removeRight(selectedGroup.getId().get(), r));
                    g.getRights().forEach(r -> bean.addRight(selectedGroup.getId().get(), r));
                })
                .thenRunAsync(() -> refreshUi(false, false), Platform::runLater)
                .handle(Ui.handler());
    }

    /**
     *
     * @param isCalledAfterUserDeletion
     * @param isCalledAfterGroupDeletion
     */
    private void refreshUi(boolean isCalledAfterUserDeletion, boolean isCalledAfterGroupDeletion) {
        L.debug("refreshUi() called");
        
        Platform.runLater(() -> {
            refreshUserListView(isCalledAfterUserDeletion);
            refreshRightsListViews(isCalledAfterUserDeletion);
            refreshGroupsListViews(isCalledAfterUserDeletion, isCalledAfterGroupDeletion);
            refreshSelectedUserRightsListViews();
            refreshSelectedGroupRightsListView();
        });
    }

    /**
     *
     * @param isCalledAfterUserDeletion
     */
    private void refreshUserListView(boolean isCalledAfterUserDeletion) {
        L.debug("refreshUserListView() called");
        int index = userListView.getSelectionModel().selectedIndexProperty().get();

        UserApi userBean = Dl.remote().lookup(UserApi.class);
        List<User> users = userBean.findAll();

        userListView.setItems(FXCollections.observableArrayList(users));
        if ( !isCalledAfterUserDeletion ) {
            if ( selectedUser != null ) {
                userListView.getSelectionModel().select(index);
            } else {
                selectedUser = null;
            }
        }
    }

    /**
     * Sets the items at {@link #activeRightsListView} and {@link #inactiveRightsListView}
     * @param isCalledAfterUserDeletion
     */
    private void refreshRightsListViews(boolean isCalledAfterUserDeletion) {
        L.debug("refreshRightListViews() called");
        User user = getSelectedUserFromUserListView();
        if ( selectedUser == null || isCalledAfterUserDeletion ) {
            activeRightsListView.getItems().clear();
            inactiveRightsListView.setItems(FXCollections.observableArrayList(AtomicRight.values()));
        } else {
            List<AtomicRight> activeRights = user.getRights();
            activeRightsListView.setItems(FXCollections.observableArrayList(activeRights));
            List<AtomicRight> inactiveRights = Arrays.asList(AtomicRight.values())
                    .stream()
                    .filter(r -> !user.getRights().contains(r))
                    .collect(Collectors.toList());
            Collections.sort(inactiveRights, (AtomicRight o1, AtomicRight o2) -> o1.toName().compareTo(o2.toName()));
            inactiveRightsListView.setItems(FXCollections.observableArrayList(inactiveRights));
        }
    }

    /**
     * Sets the items at {@link #activeGroupsListView} and {@link #inactiveGroupsListView} depending on the {@link #selectedUser} and handles reselection of the
     * last selected {@link Group}.
     * <p/>
     * If this method is called after a {@link User} has been deleted, {@link #activeGroupsListView} is cleared, all {@link Group}<code>s</code> are added to
     * {@link #inactiveGroupsListView} and {@link #selectedGroup} is set to null.
     * <p/>
     * If this method is called after a Group has been deleted, no Group will be selected and {@link #selectedGroup} is set to null.
     *
     * @param isCalledAfterUserDeletion  if true, {@link #activeGroupsListView} and {@link #inactiveGroupsListView} are reset and {@link #selectedGroup} is set
     *                                   to null.
     * @param isCalledAfterGroupDeletion if true, {@link #selectedGroup} is set to null.
     */
    private void refreshGroupsListViews(boolean isCalledAfterUserDeletion, boolean isCalledAfterGroupDeletion) {
        L.debug("refreshGroupListViews() called");
        GroupApi groupBean = Dl.remote().lookup(GroupApi.class);
        List<Group> groups = groupBean.findAll();

        if ( selectedUser == null || isCalledAfterUserDeletion ) {
            activeGroupsListView.getItems().clear();
            Collections.sort(groups, (Group o1, Group o2) -> o1.getName().compareTo(o2.getName()));
            inactiveGroupsListView.setItems(FXCollections.observableArrayList(groups));
            selectedGroup = null;
        } else {
            User user = getSelectedUserFromUserListView();
            List<Group> activeGroups = user.getGroups();
//            Collections.sort(activeGroups, (Group o1, Group o2) -> o1.getName().compareTo(o2.getName()));
            activeGroupsListView.setItems(FXCollections.observableArrayList(activeGroups));
            List<Group> inactiveGroups = groups
                    .stream()
                    .filter(g -> !user.getGroups().contains(g))
                    .collect(Collectors.toList());
//            Collections.sort(inactiveGroups, (Group o1, Group o2) -> o1.getName().compareTo(o2.getName()));
            inactiveGroupsListView.setItems(FXCollections.observableArrayList(inactiveGroups));
        }
        if ( !isCalledAfterGroupDeletion && selectedGroup != null ) {
            long groupId = selectedGroup.getId().get();
            ObservableList<Group> obsGroups = activeGroupsListView.getItems();
            if ( obsGroups.stream().mapToLong(g -> g.getId().get())
                    .anyMatch(id -> id == groupId) ) {
                activeGroupsListView.getSelectionModel().select(selectedGroup);
            } else {
                inactiveGroupsListView.getSelectionModel().select(selectedGroup);
            }
        } else {
            selectedGroup = null;
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
            ObservableList<AtomicRight> allActiveUserRights = FXCollections.observableArrayList(getSelectedUserFromUserListView().getAllRights());
            Collections.sort(allActiveUserRights, Comparator.comparing(AtomicRight::toName));
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
            List<AtomicRight> allActiveGroupRights = selectedGroup.getRights();
            allActiveGroupRightsListView.setItems(FXCollections.observableArrayList(allActiveGroupRights));
        }
    }
}
