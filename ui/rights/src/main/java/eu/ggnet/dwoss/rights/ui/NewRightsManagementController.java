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
import eu.ggnet.dwoss.rights.ee.GroupAgent;
import eu.ggnet.dwoss.rights.ee.UserAgent;
import eu.ggnet.dwoss.rights.ee.entity.Operator;
import eu.ggnet.dwoss.rights.ee.entity.Persona;
import eu.ggnet.saft.core.Ui;
import eu.ggnet.saft.core.ui.FxController;
import eu.ggnet.saft.core.ui.Title;

/**
 * FXML Controller class for the RightsManagementView.fxml.
 *
 * @author mirko.schulze
 */
//TODO umlaute berücksichtigen beim sortieren
@Title("Rechte-Verwaltung")
public class NewRightsManagementController implements Initializable, FxController {

    private static final Logger L = LoggerFactory.getLogger(NewRightsManagementController.class);

//    private UiOperator selectedUser;
    private User selectedUser;

//    private UiPersona selectedGroup;
    private Group selectedGroup;

    //<editor-fold defaultstate="collapsed" desc="FX Components">
//    @FXML
//    private ListView<UiOperator> userListView;
    @FXML
    private ListView<User> userListView;

//    @FXML
//    private ListView<UiPersona> activeGroupsListView;
//
//    @FXML
//    private ListView<UiPersona> inactiveGroupsListView;
    @FXML
    private ListView<Group> activeGroupsListView;

    @FXML
    private ListView<Group> inactiveGroupsListView;

    @FXML
    private ListView<AtomicRight> activeRightsListView;

    @FXML
    private ListView<AtomicRight> inactiveRightsListView;

    @FXML
    private ListView<AtomicRight> allActiveUserRightsListView;

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

//    private UiOperator getSelectedUserFromUserListView() {
//        return userListView.getSelectionModel().getSelectedItem();
//    }
//
//    private Long getSelectedUserIdFromUserListView() {
//        return userListView.getSelectionModel().getSelectedItem().idProperty().get();
//    }
    private User getSelectedUserFromUserListView() {
        return userListView.getSelectionModel().getSelectedItem();
    }

    private Long getSelectedUserIdFromUserListView() {
        return userListView.getSelectionModel().getSelectedItem().getId();
    }

//    private UiPersona getSelectedGroupFromActiveGroupsListViewOrInactiveGroupsListView() {
//        return activeGroupsListView.getSelectionModel().getSelectedItem() != null
//                ? activeGroupsListView.getSelectionModel().getSelectedItem()
//                : inactiveGroupsListView.getSelectionModel().getSelectedItem();
//    }
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        UserAgent userAgent = Dl.remote().lookup(UserAgent.class);
        GroupAgent groupAgent = Dl.remote().lookup(GroupAgent.class);
        //ListViews
        //userListView
//        userListView.setCellFactory(new OperatorListCell.Factory());
        userListView.setCellFactory(new UserListCell.Factory());
        userListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        userListView.setOnMouseClicked(e -> {
            if ( e.getButton().equals(MouseButton.PRIMARY) ) {
                selectedUser = getSelectedUserFromUserListView();
                if ( e.getClickCount() == 1 ) {
                    refreshUi(false, false);
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
//        activeGroupsListView.setCellFactory(new PersonaListCell.Factory());
        activeGroupsListView.setCellFactory(new GroupListCell.Factory());
        activeGroupsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        activeGroupsListView.setOnMouseClicked(e -> {
            if ( e.getButton().equals(MouseButton.PRIMARY) ) {
                selectedGroup = activeGroupsListView.getSelectionModel().getSelectedItem();
                inactiveGroupsListView.getSelectionModel().select(-1);
                if ( e.getClickCount() == 1 ) {
                    refreshSelectedGroupRightsListView();
                } else {
                    new GroupDialog(selectedGroup).showAndWait().ifPresent(g -> {
                        groupAgent.updateName(selectedGroup.getId(), g.getName());
                        g.getRights().forEach(r -> groupAgent.addRight(selectedGroup.getId(), r));
                        refreshUi(false, false);
                    });
                }
            }
        });
        //inactiveGroupsListView
//        inactiveGroupsListView.setCellFactory(new PersonaListCell.Factory());
        inactiveGroupsListView.setCellFactory(new GroupListCell.Factory());
        inactiveGroupsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        inactiveGroupsListView.setOnMouseClicked(e -> {
            if ( e.getButton().equals(MouseButton.PRIMARY) ) {
                selectedGroup = inactiveGroupsListView.getSelectionModel().getSelectedItem();
                activeGroupsListView.getSelectionModel().select(-1);
                if ( e.getClickCount() == 1 ) {
                    refreshSelectedGroupRightsListView();
                } else {
                    new GroupDialog(selectedGroup).showAndWait().ifPresent(g -> {
                        groupAgent.updateName(selectedGroup.getId(), g.getName());
                        g.getRights().forEach(r -> groupAgent.addRight(selectedGroup.getId(), r));
                        refreshUi(false, false);
                    });
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
            inactiveRightsListView.getItems().forEach(r -> userAgent.addRight(getSelectedUserIdFromUserListView(), r));
            refreshUi(false, false);
        });
        //addRightButton
        addRightButton.disableProperty().bind(inactiveRightsListView.getSelectionModel().selectedIndexProperty().isEqualTo(-1));
        addRightButton.setOnAction(e -> {
            inactiveRightsListView.getSelectionModel().getSelectedItems()
                    .forEach(r -> userAgent.addRight(getSelectedUserIdFromUserListView(), r));
            refreshUi(false, false);
        });
        //removeRightButton
        removeRightButton.disableProperty().bind(activeRightsListView.getSelectionModel().selectedIndexProperty().isEqualTo(-1));
        removeRightButton.setOnAction(e -> {
            activeRightsListView.getSelectionModel().getSelectedItems()
                    .forEach(r -> userAgent.removeRight(getSelectedUserIdFromUserListView(), r));
            refreshUi(false, false);
        });
        //removeAllRightsButton
        removeAllRightsButton.disableProperty().bind(userListView.getSelectionModel().selectedIndexProperty().isEqualTo(-1));
        removeAllRightsButton.setOnAction(e -> {
            activeRightsListView.getItems().forEach(r -> userAgent.removeRight(getSelectedUserIdFromUserListView(), r));
            refreshUi(false, false);
        });
        //addAllGroupsButton
        addAllGroupsButton.disableProperty().bind(userListView.getSelectionModel().selectedIndexProperty().isEqualTo(-1));
        addAllGroupsButton.setOnAction(e -> {
            List<Long> inactiveGroupIds = inactiveGroupsListView.getItems()
                    .stream()
                    //                    .mapToLong(g -> g.idProperty().get())
                    .mapToLong(g -> g.getId())
                    .boxed()
                    .collect(Collectors.toList());
            inactiveGroupIds.forEach(id -> userAgent.addGroup(getSelectedUserIdFromUserListView(), id));
            refreshUi(false, false);
        });
        //addGroupButton
        addGroupButton.disableProperty().bind(inactiveGroupsListView.getSelectionModel().selectedIndexProperty().isEqualTo(-1));
        addGroupButton.setOnAction(e -> {
            List<Long> selectedInactiveGroupIds = inactiveGroupsListView.getSelectionModel().getSelectedItems()
                    .stream()
                    //                    .mapToLong(g -> g.idProperty().get())
                    .mapToLong(g -> g.getId())
                    .boxed()
                    .collect(Collectors.toList());
            selectedInactiveGroupIds.forEach(id -> userAgent.addGroup(getSelectedUserIdFromUserListView(), id));
            refreshUi(false, false);
        });
        //removeGroupButton
        removeGroupButton.disableProperty().bind(activeGroupsListView.getSelectionModel().selectedIndexProperty().isEqualTo(-1));
        removeGroupButton.setOnAction(e -> {
            List<Long> selectedActiveGroupIds = activeGroupsListView.getSelectionModel().getSelectedItems()
                    .stream()
                    //                    .mapToLong(g -> g.idProperty().get())
                    .mapToLong(g -> g.getId())
                    .boxed()
                    .collect(Collectors.toList());
            selectedActiveGroupIds.forEach(id -> userAgent.removeGroup(getSelectedUserIdFromUserListView(), id));
            refreshUi(false, false);
        });
        //removeAllGroupsButton
        removeAllGroupsButton.disableProperty().bind(userListView.getSelectionModel().selectedIndexProperty().isEqualTo(-1));
        removeAllGroupsButton.setOnAction(e -> {
            List<Long> activeGroupIds = activeGroupsListView.getItems()
                    .stream()
                    //                    .mapToLong(g -> g.idProperty().get())
                    .mapToLong(g -> g.getId())
                    .boxed()
                    .collect(Collectors.toList());
            activeGroupIds.forEach(id -> userAgent.removeGroup(getSelectedUserIdFromUserListView(), id));
            refreshUi(false, false);
        });
        //TODO createUser
        //createUserButton
        createUserButton.setOnAction(e -> {

        });
        //TODO createGroup
        //createGroupButton
        createGroupButton.setOnAction(e -> {
            new GroupDialog().showAndWait().ifPresent(g -> {
                groupAgent.create(g.getName());
                g.getRights().forEach(r -> groupAgent.addRight(selectedGroup.getId(), r));
                refreshUi(false, false);
            });
        });
        //deleteUserButton
        deleteUserButton.setOnAction(e -> {
            if ( selectedUser != null ) {
                List<Long> selectedUsersIds = userListView.getSelectionModel().getSelectedItems()
                        .stream()
                        //                        .mapToLong(u -> u.idProperty().get())
                        .mapToLong(u -> u.getId())
                        .boxed()
                        .collect(Collectors.toList());
                selectedUsersIds.forEach(id -> userAgent.delete(id));
                selectedUser = null;
                refreshUi(true, false);
            }
        });
        //deleteGroupButton
        deleteGroupButton.setOnAction(e -> {
            if ( selectedGroup != null ) {
                //Groups that are used by any User must not simply be deleted
                List<Operator> allUsers = userAgent.findAllEager(Operator.class);
                Set<Persona> groupsInUsage = new HashSet<>();
                allUsers.forEach(u -> u.getPersonas().forEach(g -> groupsInUsage.add(g)));
                List<Long> selectedGroupsIds = activeGroupsListView.getSelectionModel().getSelectedItems()
                        .stream()
                        //                        .mapToLong(g -> g.idProperty().get())
                        .mapToLong(g -> g.getId())
                        .boxed()
                        .collect(Collectors.toList());
                if ( selectedGroupsIds.isEmpty() ) {
                    selectedGroupsIds = inactiveGroupsListView.getSelectionModel().getSelectedItems()
                            .stream()
                            //                            .mapToLong(g -> g.idProperty().get())
                            .mapToLong(g -> g.getId())
                            .boxed()
                            .collect(Collectors.toList());
                }
                selectedGroupsIds.forEach(id -> {
                    Persona group = groupAgent.findById(Persona.class, id);
                    if ( groupsInUsage.contains(group) ) {
                        if ( new Alert(Alert.AlertType.CONFIRMATION,
                                "Gruppe " + group.getName() + " wird noch verwendet.\nWollen Sie dir Gruppe dennoch löschen?")
                                .showAndWait().get() == ButtonType.OK ) {
                            allUsers.forEach(u -> {
                                if ( u.getPersonas().contains(groupAgent.findById(Persona.class, group.getId())) ) {
                                    userAgent.removeGroup(u.getId(), group.getId());
                                }
                            });
                            groupAgent.delete(group.getId());
                            selectedGroup = null;
                            refreshUi(false, true);
                        }
                    } else {
                        groupAgent.delete(group.getId());
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

    private void refreshUi(boolean isCalledAfterUserDeletion, boolean isCalledAfterGroupDeletion) {
        L.info("refreshUi() called");
        Platform.runLater(() -> {
            refreshUserListView(isCalledAfterUserDeletion);
            refreshRightsListViews(isCalledAfterUserDeletion);
            refreshGroupsListViews(isCalledAfterUserDeletion, isCalledAfterGroupDeletion);
            refreshSelectedUserRightsListViews();
            refreshSelectedGroupRightsListView();
        });
    }

    private void refreshUserListView(boolean isCalledAfterUserDeletion) {
        L.debug("refreshUserListView() called");
        int index = userListView.getSelectionModel().selectedIndexProperty().get();
        UserAgent userAgent = Dl.remote().lookup(UserAgent.class);
        List<Operator> foundUsers = userAgent.findAll(Operator.class);
//        List<UiOperator> allUsers = new ArrayList<>();
//        foundUsers.forEach(u -> allUsers.add(new UiOperator(u)));
        List<User> allUsers = new ArrayList<>();
        foundUsers.forEach(u -> {
            List<Group> groups = new ArrayList<>();
            u.getPersonas().forEach(g -> groups.add(new Group.Builder()
                    .setId(g.getId())
                    .setName(g.getName())
                    .setOptLock(g.getOptLock())
                    .addAllRights(g.getPersonaRights())
                    .build()));
            allUsers.add(new User.Builder()
                    .setId(u.getId())
                    .setUsername(u.getUsername())
                    .setOptLock(u.getOptLock())
                    .addAllRights(u.getRights())
                    .addAllGroups(groups)
                    .build());
        });
        userListView.setItems(FXCollections.observableArrayList(allUsers));
        if ( !isCalledAfterUserDeletion ) {
            if ( selectedUser != null ) {
                userListView.getSelectionModel().select(index);
            } else {
                selectedUser = null;
            }
        }
    }

    private void refreshRightsListViews(boolean isCalledAfterUserDeletion) {
        L.debug("refreshRightListViews() called");
//        UiOperator user = getSelectedUserFromUserListView();
        User user = getSelectedUserFromUserListView();
        if ( selectedUser == null || isCalledAfterUserDeletion ) {
            activeRightsListView.getItems().clear();
            inactiveRightsListView.setItems(FXCollections.observableArrayList(AtomicRight.values()));
        } else {
            List<AtomicRight> activeRights = user.getRights();
//            Collections.sort(activeRights, (AtomicRight o1, AtomicRight o2) -> o1.toName().compareTo(o2.toName()));
            activeRightsListView.setItems(FXCollections.observableArrayList(activeRights));
            List<AtomicRight> inactiveRights = Arrays.asList(AtomicRight.values())
                    .stream()
                    .filter(r -> !user.getRights().contains(r))
                    .collect(Collectors.toList());
            Collections.sort(inactiveRights, (AtomicRight o1, AtomicRight o2) -> o1.toName().compareTo(o2.toName()));
            inactiveRightsListView.setItems(FXCollections.observableArrayList(inactiveRights));
        }
    }

    private void refreshGroupsListViews(boolean isCalledAfterUserDeletion, boolean isCalledAfterGroupDeletion) {
        L.debug("refreshGroupListViews() called");
        GroupAgent agent = Dl.remote().lookup(GroupAgent.class);
        List<Persona> foundGroups = agent.findAll(Persona.class);
//        List<UiPersona> allGroups = new ArrayList<>();
//        foundGroups.forEach(g -> allGroups.add(new UiPersona(g)));
        List<Group> allGroups = new ArrayList<>();
        foundGroups.forEach(g -> allGroups.add(new Group.Builder()
                .setId(g.getId())
                .setName(g.getName())
                .setOptLock(g.getOptLock())
                .addAllRights(g.getPersonaRights())
                .build()));
        if ( selectedUser == null || isCalledAfterUserDeletion ) {
            activeGroupsListView.getItems().clear();
//            Collections.sort(allGroups, (UiPersona o1, UiPersona o2) -> o1.nameProperty().get().compareTo(o2.nameProperty().get()));
            Collections.sort(allGroups, (Group o1, Group o2) -> o1.getName().compareTo(o2.getName()));
            inactiveGroupsListView.setItems(FXCollections.observableArrayList(allGroups));
        } else {
//            UiOperator user = getSelectedUserFromUserListView();
            User user = getSelectedUserFromUserListView();
//            List<UiPersona> activeGroups = user.getPersonas();
//            Collections.sort(activeGroups, (UiPersona o1, UiPersona o2) -> o1.nameProperty().get().compareTo(o2.nameProperty().get()));
//            List<Group> activeGroups = user.getPersonas();
            List<Group> activeGroups = user.getGroups();
//            Collections.sort(activeGroups, (Group o1, Group o2) -> o1.getName().compareTo(o2.getName()));
            activeGroupsListView.setItems(FXCollections.observableArrayList(activeGroups));
//            List<UiPersona> inactiveGroups = allGroups
            List<Group> inactiveGroups = allGroups
                    .stream()
                    //                    .filter(g -> !user.getPersonas().contains(g))
                    .filter(g -> !user.getGroups().contains(g))
                    .collect(Collectors.toList());
//            Collections.sort(inactiveGroups, (UiPersona o1, UiPersona o2) -> o1.nameProperty().get().compareTo(o2.nameProperty().get()));
            Collections.sort(inactiveGroups, (Group o1, Group o2) -> o1.getName().compareTo(o2.getName()));
            inactiveGroupsListView.setItems(FXCollections.observableArrayList(inactiveGroups));
        }
        if ( !isCalledAfterGroupDeletion && selectedGroup != null ) {
//            long groupId = selectedGroup.idProperty().get();
//            ObservableList<UiPersona> groups = activeGroupsListView.getItems();
            long groupId = selectedGroup.getId();
            ObservableList<Group> groups = activeGroupsListView.getItems();
//            if ( groups.stream().mapToLong(g -> g.idProperty().get())
            if ( groups.stream().mapToLong(g -> g.getId())
                    .anyMatch(id -> id == groupId) ) {
                activeGroupsListView.getSelectionModel().select(selectedGroup);
            } else {
                inactiveGroupsListView.getSelectionModel().select(selectedGroup);
            }
        }
    }

    private void refreshSelectedUserRightsListViews() {
        L.debug("refreshSelectedUserRightsListViews() called");
        if ( selectedUser == null ) {
            allActiveUserRightsListView.getItems().clear();
        } else {
//            ObservableList<AtomicRight> allActiveUserRights = FXCollections.observableArrayList(getSelectedUserFromUserListView().getAllActiveRights());
            ObservableList<AtomicRight> allActiveUserRights = FXCollections.observableArrayList(getSelectedUserFromUserListView().getAllRights());
            Collections.sort(allActiveUserRights, (AtomicRight r1, AtomicRight r2) -> r1.toName().compareTo(r2.toName()));
            allActiveUserRightsListView.setItems(allActiveUserRights);
        }

    }

    private void refreshSelectedGroupRightsListView() {
        L.debug("refreshSelectedGroupRightsListView() called");
        if ( selectedGroup == null ) {
            allActiveGroupRightsListView.getItems().clear();
        } else {
//            List<AtomicRight> allActiveGroupRights = selectedGroup.personaRightsProperty().get();
            List<AtomicRight> allActiveGroupRights = selectedGroup.getRights();
//            Collections.sort(allActiveGroupRights, (AtomicRight o1, AtomicRight o2) -> o1.toName().compareTo(o2.toName()));
            allActiveGroupRightsListView.setItems(FXCollections.observableArrayList(allActiveGroupRights));
        }
    }

    public static URL loadFxml() {
        return NewRightsManagementController.class.getResource("NewRightsManagementView.fxml");
    }

}
