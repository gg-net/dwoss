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
import java.text.Collator;
import java.util.*;
import java.util.concurrent.CancellationException;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.collections.*;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.stage.Modality;
import javafx.stage.Stage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.core.widget.Dl;
import eu.ggnet.dwoss.rights.api.*;
import eu.ggnet.dwoss.rights.ee.entity.Persona;
import eu.ggnet.saft.core.Ui;
import eu.ggnet.saft.core.ui.*;

import static javafx.scene.control.ButtonType.OK;

/**
 * FXML Controller class for the RightsManagementView.fxml.
 *
 * @author mirko.schulze
 */
//TODO 
//reselect Group
//refresh überdenken
@Title("Rechte-Verwaltung")
@Frame
public class NewRightsManagementController implements Initializable, FxController {

    private static final Logger L = LoggerFactory.getLogger(NewRightsManagementController.class);

    private final UserApi userApi = Dl.remote().lookup(UserApi.class);

    private final GroupApi groupApi = Dl.remote().lookup(GroupApi.class);

    private final Comparator<User> userComparator = Comparator.comparing(User::getUsername, Collator.getInstance(Locale.GERMAN));

    private final Comparator<Group> groupComparator = Comparator.comparing(Group::getName, Collator.getInstance(Locale.GERMAN));

    private final Comparator<AtomicRight> rightComparator = Comparator.comparing(AtomicRight::toName, Collator.getInstance(Locale.GERMAN));

    /**
     * List with a {@link User} representation for each {@link Operator} in the database.
     */
    private List<User> allUsers = userApi.findAll();

    /**
     * List with a {@link Group} representation for each {@link Persona} in the database.
     */
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

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initListViews();
        initRightButtons();
        initGroupButtons();
        initCreateButtons();
        initEditButtons();
        initDeleteButtons();
        //closeButton
        closeButton.setOnAction(e -> {
            Ui.closeWindowOf(closeButton);
        }
        );
        //load data
        loadUsersAndGroups();
        refreshUi();
    }

    private void initListViews() {
        //userListView
        userListView.setCellFactory(new UserListCell.Factory());
        userListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        userListView.setOnMouseClicked(e -> {
            if ( e.getButton().equals(MouseButton.PRIMARY) ) {
                selectedUser = userListView.getSelectionModel().getSelectedItem();
                activeRightsListView.getSelectionModel().select(-1);
                inactiveRightsListView.getSelectionModel().select(-1);
                activeGroupsListView.getSelectionModel().select(-1);
                inactiveGroupsListView.getSelectionModel().select(-1);
                if ( e.getClickCount() == 1 ) {
                    refreshRightsListViews();
                    refreshGroupsListViews();
                    refreshSelectedUserRightsListViews();
                    refreshSelectedGroupRightsListView();
                } else {
                    Ui.build(closeButton)
                            .title("Benutzer-Verwaltung: Benutzer bearbeiten")
                            .modality(Modality.WINDOW_MODAL)
                            .fxml()
                            .eval(() -> selectedUser, UserManagementController.class)
                            .cf()
                            .thenAcceptAsync(u -> {
                                selectedUser.getRights().forEach(r -> userApi.removeRight(selectedUser.getId().get(), r));
                                selectedUser.getGroups().forEach(g -> userApi.removeGroup(selectedUser.getId().get(), g.getId().get()));
                                userApi.updateUsername(selectedUser.getId().get(), u.getUsername());
                                u.getPassword().ifPresent(p -> userApi.updatePassword(selectedUser.getId().get(), p.toCharArray()));
                                u.getRights().forEach(r -> userApi.addRight(selectedUser.getId().get(), r));
                                u.getGroups().forEach(g -> userApi.addGroup(selectedUser.getId().get(), g.getId().get()));
                                loadUsersAndGroups();
                                refreshUi();
                            })
                            .handle(Ui.handler());
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
                if ( e.getClickCount() == 1 ) {
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
                if ( e.getClickCount() == 1 ) {
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
    }

    private void initRightButtons() {
        //addAllRightsButton
        addAllRightsButton.disableProperty().bind(userListView.getSelectionModel().selectedIndexProperty().isEqualTo(-1));
        addAllRightsButton.setOnAction(e -> {
            inactiveRightsListView.getItems().forEach(r -> userApi.addRight(selectedUser.getId().get(), r));
            loadUsersAndGroups();
            selectedUser = userApi.findById(selectedUser.getId().get());
            refreshUi();
        });
        //addRightButton
        addRightButton.disableProperty().bind(userListView.getSelectionModel().selectedIndexProperty().isEqualTo(-1));
        addRightButton.setOnAction(e -> {
            inactiveRightsListView.getSelectionModel().getSelectedItems().forEach(r -> userApi.addRight(selectedUser.getId().get(), r));
            loadUsersAndGroups();
            selectedUser = userApi.findById(selectedUser.getId().get());
            refreshUi();
        });
        //removeRightButton
        removeRightButton.disableProperty().bind(userListView.getSelectionModel().selectedIndexProperty().isEqualTo(-1));
        removeRightButton.setOnAction(e -> {
            activeRightsListView.getSelectionModel().getSelectedItems().forEach(r -> userApi.removeRight(selectedUser.getId().get(), r));
            loadUsersAndGroups();
            selectedUser = userApi.findById(selectedUser.getId().get());
            refreshUi();
        });
        //removeAllRightsButton
        removeAllRightsButton.disableProperty().bind(userListView.getSelectionModel().selectedIndexProperty().isEqualTo(-1));
        removeAllRightsButton.setOnAction(e -> {
            activeRightsListView.getItems().forEach(r -> userApi.removeRight(selectedUser.getId().get(), r));
            loadUsersAndGroups();
            selectedUser = userApi.findById(selectedUser.getId().get());
            refreshUi();
        });
    }

    private void initGroupButtons() {
        //addAllGroupsButton
        addAllGroupsButton.disableProperty().bind(userListView.getSelectionModel().selectedIndexProperty().isEqualTo(-1));
        addAllGroupsButton.setOnAction(e -> {
            List<Long> inactiveGroupIds = inactiveGroupsListView.getItems()
                    .stream()
                    .mapToLong(g -> g.getId().get())
                    .boxed()
                    .collect(Collectors.toList());
            inactiveGroupIds.forEach(id -> userApi.addGroup(selectedUser.getId().get(), id));
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
            selectedInactiveGroupIds.forEach(id -> userApi.addGroup(selectedUser.getId().get(), id));
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
            selectedActiveGroupIds.forEach(id -> userApi.removeGroup(selectedUser.getId().get(), id));
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
            activeGroupIds.forEach(id -> userApi.removeGroup(selectedUser.getId().get(), id));
            loadUsersAndGroups();
            selectedUser = userApi.findById(selectedUser.getId().get());
            refreshUi();
        });
    }

    private void initCreateButtons() {
        //createUserButton
        createUserButton.setOnAction(e -> {
            L.info("createUser");
            Ui.build(createUserButton)
                    .title("Benutzer-Verwaltung: Neuen Benutzer anlegen")
                    .modality(Modality.WINDOW_MODAL)
                    .fxml()
                    .eval(() -> null, UserManagementController.class)
                    .cf()
                    .thenAccept(user -> {
                        userApi.create(user.getUsername());
                        long id = userApi.findByName(user.getUsername()).getId().get();
                        user.getPassword().ifPresent(p -> userApi.updatePassword(id, p.toCharArray()));
                        user.getRights().forEach(r -> userApi.addRight(id, r));
                        user.getGroups().forEach(g -> userApi.addGroup(id, g.getId().get()));
                        selectedUser = userApi.findById(id);
                        userListView.getSelectionModel().select(selectedUser);
                        loadUsersAndGroups();
                        refreshUi();
                    })
                    .handle(Ui.handler());
        });
        //createGroupButton
        createGroupButton.setOnAction(e -> {
            Ui.build(createGroupButton)
                    .title("Gruppen-Verwaltung: Neue Gruppe anlegen")
                    .modality(Modality.WINDOW_MODAL)
                    .fxml()
                    .eval(() -> null, GroupManagementController.class)
                    .cf()
                    .thenAccept(group -> {
                        groupApi.create(group.getName());
                        long id = groupApi.findByName(group.getName()).getId().get();
                        group.getRights().forEach(r -> groupApi.addRight(id, r));
                        loadUsersAndGroups();
                        refreshUi();
                    })
                    .handle(Ui.handler());
        });
    }

    private void initEditButtons() {

    }

    private void initDeleteButtons() {
        //deleteUserButton
        deleteUserButton.setOnAction(e -> {
            if ( selectedUser == null ) return;
            Ui.build(addAllGroupsButton).dialog()
                    .eval(() -> new Alert(Alert.AlertType.CONFIRMATION,
                    "Wollen Sie den ausgewählten Benutzer löschen?")).cf().thenAccept((bt) -> {
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

                Ui.build(addAllGroupsButton).dialog()
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
    }

    /**
     * Opens a new {@link Stage} to modify the submitted {@link Group}.
     *
     * @param group Group to be modified.
     */
    private void editGroup(Group group) {
        L.info("editGroup({}) called", group);
        Ui.build(closeButton)
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
                .handle(Ui.handler());
    }

    /**
     * Searches for all {@link Operator}<code>s</code> and {@link Persona}<code>s</code> and fills {@link #allUsers} and {@link #allGroups}.
     */
    private void loadUsersAndGroups() {
        L.info("loadUsersAndGroups() called");
        allUsers = userApi.findAll();
        L.info("allUsers = {}", allUsers);
        allGroups = groupApi.findAll();
        L.info("allGroups = {}", allGroups);
    }

    /**
     * Sets the items at {@link #userListView}, {@link #activeRightsListView}, {@link #inactiveRightsListView}, {@link #activeGroupsListView},
     * {@link #inactiveGroupsListView}, {@link #allActiveUserRightsListView}, {@link #allActiveGroupRightsListView}.
     */
    private void refreshUi() {
        L.info("refreshUi() called");
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
//        userListView.setItems(FXCollections.observableArrayList(allUsers));
        userListView.setItems(sortListForView(allUsers, userComparator));
        if ( index >= 0 ) {
            userListView.getSelectionModel().select(index);
            selectedUser = userListView.getSelectionModel().getSelectedItem();
        }
    }

    /**
     * Sets the items at {@link #activeRightsListView} and {@link #inactiveRightsListView} depending on the {@link #selectedUser}.
     */
    private void refreshRightsListViews() {
        L.info("refreshRightsListViews() called");
//        List<AtomicRight> sortedRights;
        if ( selectedUser == null ) {
            activeRightsListView.getItems().clear();
//            sortedRights = new SortedList<>(FXCollections.observableArrayList(AtomicRight.values()), rightComparator);
//            inactiveRightsListView.setItems(FXCollections.observableArrayList(sortedRights));
            inactiveRightsListView.setItems(sortListForView(List.of(AtomicRight.values()), rightComparator));
        } else {
//            List<AtomicRight> activeRights = selectedUser.getRights();
//            sortedRights = new SortedList<>(FXCollections.observableArrayList(activeRights), rightComparator);
//            activeRightsListView.setItems(FXCollections.observableArrayList(sortedRights));
            activeRightsListView.setItems(sortListForView(selectedUser.getRights(), rightComparator));
            List<AtomicRight> inactiveRights = Arrays.asList(AtomicRight.values())
                    .stream()
                    .filter(r -> !selectedUser.getRights().contains(r))
                    .collect(Collectors.toList());
//            sortedRights = new SortedList<>(FXCollections.observableArrayList(inactiveRights), rightComparator);
//            inactiveRightsListView.setItems(FXCollections.observableArrayList(sortedRights));
            inactiveRightsListView.setItems(sortListForView(inactiveRights, rightComparator));
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
//            List<Group> sortedGroups = new SortedList<>(FXCollections.observableArrayList(allGroups), groupComparator);
//            inactiveGroupsListView.setItems(FXCollections.observableArrayList(sortedGroups));
            inactiveGroupsListView.setItems(sortListForView(allGroups, groupComparator));
        } else {
//            List<Group> activeGroups = selectedUser.getGroups();
//            List<Group> sortedGroups = new SortedList<>(FXCollections.observableArrayList(activeGroups), groupComparator);
//            activeGroupsListView.setItems(FXCollections.observableArrayList(sortedGroups));
            activeGroupsListView.setItems(sortListForView(selectedUser.getGroups(), groupComparator));
            List<Group> inactiveGroups = allGroups
                    .stream()
                    .filter(g -> !selectedUser.getGroups().contains(g))
                    .collect(Collectors.toList());
//            sortedGroups = new SortedList<>(FXCollections.observableArrayList(inactiveGroups), groupComparator);
//            inactiveGroupsListView.setItems(FXCollections.observableArrayList(sortedGroups));
            inactiveGroupsListView.setItems(sortListForView(inactiveGroups, groupComparator));
        }
    }

    /**
     * Sets the items at {@link #allActiveUserRightsListView} depending on {@link #selectedUser}.
     */
    private void refreshSelectedUserRightsListViews() {
        L.info("refreshSelectedUserRightsListViews() called");
        if ( selectedUser == null ) {
            allActiveUserRightsListView.getItems().clear();
        } else {
//            ObservableList<AtomicRight> allActiveUserRights = FXCollections.observableArrayList(selectedUser.getAllRights());
//            List<AtomicRight> sortedRights = new SortedList<>(FXCollections.observableArrayList(allActiveUserRights), rightComparator);
//            allActiveUserRightsListView.setItems(FXCollections.observableArrayList(sortedRights));
            allActiveUserRightsListView.setItems(sortListForView(selectedUser.getAllRights(), rightComparator));
        }
    }

    /**
     * Sets the items at {@link #allActiveGroupRightsListView} depending on {@link #selectedGroup}.
     */
    private void refreshSelectedGroupRightsListView() {
        L.info("refreshSelectedGroupRightsListView() called");
        if ( selectedGroup == null ) {
            allActiveGroupRightsListView.getItems().clear();
        } else {
//            List<AtomicRight> allActiveGroupRights = selectedGroup.getRights();
//            List<AtomicRight> sortedRights = new SortedList<>(FXCollections.observableArrayList(allActiveGroupRights), rightComparator);
//            allActiveGroupRightsListView.setItems(FXCollections.observableArrayList(sortedRights));
            allActiveGroupRightsListView.setItems(sortListForView(selectedGroup.getRights(), rightComparator));
        }
    }
    
    private <T> ObservableList<T> sortListForView(List<T> list, Comparator<T> comparator){
//        List<T> sortedList = new SortedList<>(FXCollections.observableArrayList(immutableList), comparator);
//        return FXCollections.observableArrayList(sortedList);
        return new SortedList<>(FXCollections.observableArrayList(list), comparator);
    }

}
