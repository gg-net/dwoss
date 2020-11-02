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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;

import eu.ggnet.dwoss.core.widget.Dl;
import eu.ggnet.dwoss.rights.api.*;

/**
 *
 * @author mirko.schulze
 */
public class EditUserDialog extends Dialog<User>{
    
    public EditUserDialog(User user) {
        this.setTitle("Benutzer-Verwaltung");
        this.setHeaderText("Passen Sie den Benutzer an.");
        this.initModality(Modality.WINDOW_MODAL);

        Label nameLabel = new Label("Name: ");
        TextField nameTextField = new TextField(user.getUsername());
        nameTextField.setPromptText("Max Musterfrau");

        HBox nameBox = new HBox(5, nameLabel, nameTextField);

        ListView<AtomicRight> selectedRightsListView = new ListView<>(FXCollections.observableArrayList(user.getRights()));
        selectedRightsListView.setCellFactory(new RightsListCell.Factory());
        selectedRightsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        
        TitledPane selectedRightsTitle = new TitledPane("Gewährte Rechte", selectedRightsListView);
        selectedRightsTitle.setCollapsible(false);
        selectedRightsTitle.setAlignment(Pos.CENTER);

        List<AtomicRight> filteredRights = Arrays.asList(AtomicRight.values())
                .stream()
                .filter(r -> !user.getRights().contains(r))
                .collect(Collectors.toList());
        ListView<AtomicRight> availableRightsListView = new ListView<>(FXCollections.observableArrayList(filteredRights));
        availableRightsListView.setCellFactory(new RightsListCell.Factory());
        availableRightsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        
        TitledPane availableRightsTitle = new TitledPane("Verfügbare Rechte", availableRightsListView);
        availableRightsTitle.setCollapsible(false);
        availableRightsTitle.setAlignment(Pos.CENTER);

        Button addAllRightsButton = new Button("˄ ˄");
        addAllRightsButton.setOnAction(e -> {
            selectedRightsListView.setItems(FXCollections.observableArrayList(AtomicRight.values()));
            availableRightsListView.getItems().clear();
        });

        Button addRightButton = new Button("˄");
        addRightButton.setOnAction(e -> {
            List<AtomicRight> rights = availableRightsListView.getSelectionModel().getSelectedItems();
            rights.forEach(r -> {
                selectedRightsListView.getItems().add(r);
                availableRightsListView.getItems().remove(r);
            });
        });

        Button removeRightButton = new Button("˅");
        removeRightButton.setOnAction(e -> {
            List<AtomicRight> rights = selectedRightsListView.getSelectionModel().getSelectedItems();
            rights.forEach(r -> {
                availableRightsListView.getItems().add(r);
                selectedRightsListView.getItems().remove(r);
            });
        });

        Button removeAllRightsButton = new Button("˅ ˅");
        removeAllRightsButton.setOnAction(e -> {
            availableRightsListView.setItems(FXCollections.observableArrayList(AtomicRight.values()));
            selectedRightsListView.getItems().clear();
        });

        HBox rightsButtonBox = new HBox(5, addAllRightsButton, addRightButton, removeRightButton, removeAllRightsButton);
        rightsButtonBox.setAlignment(Pos.CENTER);
        VBox rightsBox = new VBox(5, selectedRightsTitle, rightsButtonBox, availableRightsTitle);
        
        ListView<Group> selectedGroupsListView = new ListView<>(FXCollections.observableArrayList(user.getGroups()));
        selectedGroupsListView.setCellFactory(new GroupListCell.Factory());
        selectedGroupsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        
        TitledPane selectedGroupsTitle = new TitledPane("Zugewiesene Gruppen", selectedGroupsListView);
        selectedGroupsTitle.setCollapsible(false);
        selectedGroupsTitle.setAlignment(Pos.CENTER);

        GroupApi bean = Dl.remote().lookup(GroupApi.class);
        List<Group> groups = bean.findAll();
        List<Group> filteredGroups = groups
                .stream()
                .filter(g -> !user.getGroups().contains(g))
                .collect(Collectors.toList());
        ListView<Group> availableGroupsListView = new ListView<>(FXCollections.observableArrayList(filteredGroups));
        availableGroupsListView.setCellFactory(new GroupListCell.Factory());
        availableGroupsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        
        TitledPane availableGroupsTitle = new TitledPane("Verfügbare Gruppen", availableGroupsListView);
        availableGroupsTitle.setCollapsible(false);
        availableGroupsTitle.setAlignment(Pos.CENTER);

        Button addAllGroupsButton = new Button("˄ ˄");
        addAllGroupsButton.setOnAction(e -> {
            selectedGroupsListView.setItems(FXCollections.observableArrayList(groups));
            availableGroupsListView.getItems().clear();
        });

        Button addGroupButton = new Button("˄");
        addGroupButton.setOnAction(e -> {
            List<Group> selectedGroups = availableGroupsListView.getSelectionModel().getSelectedItems();
            selectedGroups.forEach(g -> {
                selectedGroupsListView.getItems().add(g);
                availableGroupsListView.getItems().remove(g);
            });
        });

        Button removeGroupButton = new Button("˅");
        removeGroupButton.setOnAction(e -> {
            List<Group> selectedGroups = selectedGroupsListView.getSelectionModel().getSelectedItems();
            selectedGroups.forEach(g -> {
                availableGroupsListView.getItems().add(g);
                selectedGroupsListView.getItems().remove(g);
            });
        });

        Button removeAllGroupsButton = new Button("˅ ˅");
        removeAllGroupsButton.setOnAction(e -> {
            availableGroupsListView.setItems(FXCollections.observableArrayList(groups));
            selectedGroupsListView.getItems().clear();
        });

        HBox groupsButtonBox = new HBox(5, addAllGroupsButton, addGroupButton, removeGroupButton, removeAllGroupsButton);
        groupsButtonBox.setAlignment(Pos.CENTER);
        VBox groupsBox = new VBox(5, selectedGroupsTitle, groupsButtonBox, availableGroupsTitle);

        HBox hbox = new HBox(5, rightsBox, groupsBox);
        hbox.setAlignment(Pos.CENTER);
        VBox vbox = new VBox(5, nameBox, hbox);

        this.getDialogPane().setPrefSize(700, 800);
        this.getDialogPane().setContent(vbox);
        this.getDialogPane().getButtonTypes().addAll(ButtonType.FINISH, ButtonType.CANCEL);

        Button finishButton = (Button)this.getDialogPane().lookupButton(ButtonType.FINISH);
        finishButton.addEventFilter(ActionEvent.ACTION, ef -> {
            if ( nameTextField.getText().isEmpty() ) {
                ef.consume();
                new Alert(Alert.AlertType.ERROR, "Gib einen Namen ein").show();
            }
        });

        this.setResultConverter(type -> {
            if ( type == ButtonType.FINISH ) {
                return new User.Builder()
                        .setUsername(nameTextField.getText())
                        .addAllRights(selectedRightsListView.getItems())
                        .addAllGroups(selectedGroupsListView.getItems())
                        .build();
            } else {
                return null;
            }
        });
    }
    
}
