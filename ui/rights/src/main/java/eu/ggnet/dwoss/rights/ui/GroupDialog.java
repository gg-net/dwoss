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

import java.util.*;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;

import eu.ggnet.dwoss.rights.api.AtomicRight;
import eu.ggnet.dwoss.rights.api.Group;

/**
 *
 * @author mirko.schulze
 */
public class GroupDialog extends Dialog<Group> {

    public GroupDialog() {
        this.setTitle("Gruppen-Verwaltung");
        this.setHeaderText("Legen Sie eine neue Gruppe an.");
        this.initModality(Modality.WINDOW_MODAL);

        Label nameLabel = new Label("Name: ");
        TextField nameTextField = new TextField();
        nameTextField.setPromptText("Masters of Desaster");

        HBox nameBox = new HBox(5, nameLabel, nameTextField);

        ListView<AtomicRight> selectedRights = new ListView<>(FXCollections.observableArrayList());
        selectedRights.setCellFactory(new RightsListCell.Factory());
        selectedRights.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        ListView<AtomicRight> availableRights = new ListView<>(FXCollections.observableArrayList(AtomicRight.values()));
        availableRights.setCellFactory(new RightsListCell.Factory());
        availableRights.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        Button addAllRightsButton = new Button("˄ ˄");
        addAllRightsButton.setOnAction(e -> {
            selectedRights.setItems(FXCollections.observableArrayList(AtomicRight.values()));
            availableRights.getItems().clear();
        });

        Button addRightButton = new Button("˄");
        addRightButton.setOnAction(e -> {
            List<AtomicRight> rights = availableRights.getSelectionModel().getSelectedItems();
            rights.forEach(r -> {
                selectedRights.getItems().add(r);
                availableRights.getItems().remove(r);
            });
        });

        Button removeRightButton = new Button("˅");
        removeRightButton.setOnAction(e -> {
            List<AtomicRight> rights = selectedRights.getSelectionModel().getSelectedItems();
            rights.forEach(r -> {
                availableRights.getItems().add(r);
                selectedRights.getItems().remove(r);
            });
        });

        Button removeAllRightsButton = new Button("˅ ˅");
        removeAllRightsButton.setOnAction(e -> {
            availableRights.setItems(FXCollections.observableArrayList(AtomicRight.values()));
            selectedRights.getItems().clear();
        });

        HBox buttonBox = new HBox(5, addAllRightsButton, addRightButton, removeRightButton, removeAllRightsButton);

        VBox vbox = new VBox(5, nameBox, selectedRights, buttonBox, availableRights);

        this.getDialogPane().setPrefSize(600, 400);
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
                return new Group.Builder()
                        .setName(nameTextField.getText())
                        .addAllRights(selectedRights.getItems())
                        .setOptLock(0)
                        .build();
            } else {
                return null;
            }
        });
    }

    public GroupDialog(Group group) {
        this.setTitle("Gruppen-Verwaltung");
        this.setHeaderText("Legen Sie eine neue Gruppe an.");
        this.initModality(Modality.WINDOW_MODAL);

        Label nameLabel = new Label("Name: ");
        TextField nameTextField = new TextField();
        nameTextField.setPromptText("Masters of Desaster");

        HBox nameBox = new HBox(5, nameLabel, nameTextField);

        ListView<AtomicRight> selectedRights = new ListView<>(FXCollections.observableArrayList(group.getRights()));
        selectedRights.setCellFactory(new RightsListCell.Factory());
        selectedRights.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        List<AtomicRight> filteredRights = Arrays.asList(AtomicRight.values())
                .stream()
                .filter(r -> !group.getRights().contains(r))
                .collect(Collectors.toList());
        ListView<AtomicRight> availableRights = new ListView<>(FXCollections.observableArrayList(filteredRights));
        availableRights.setCellFactory(new RightsListCell.Factory());
        availableRights.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        Button addAllRightsButton = new Button("˄ ˄");
        addAllRightsButton.setOnAction(e -> {
            selectedRights.setItems(FXCollections.observableArrayList(AtomicRight.values()));
            availableRights.getItems().clear();
        });

        Button addRightButton = new Button("˄");
        addRightButton.setOnAction(e -> {
            List<AtomicRight> rights = availableRights.getSelectionModel().getSelectedItems();
            rights.forEach(r -> {
                selectedRights.getItems().add(r);
                availableRights.getItems().remove(r);
            });
        });

        Button removeRightButton = new Button("˅");
        removeRightButton.setOnAction(e -> {
            List<AtomicRight> rights = selectedRights.getSelectionModel().getSelectedItems();
            rights.forEach(r -> {
                availableRights.getItems().add(r);
                selectedRights.getItems().remove(r);
            });
        });

        Button removeAllRightsButton = new Button("˅ ˅");
        removeAllRightsButton.setOnAction(e -> {
            availableRights.setItems(FXCollections.observableArrayList(AtomicRight.values()));
            selectedRights.getItems().clear();
        });

        HBox buttonBox = new HBox(5, addAllRightsButton, addRightButton, removeRightButton, removeAllRightsButton);

        VBox vbox = new VBox(5, nameBox, selectedRights, buttonBox, availableRights);

        this.getDialogPane().setPrefSize(600, 400);
        this.getDialogPane().setContent(vbox);
        this.getDialogPane().getButtonTypes().addAll(ButtonType.FINISH, ButtonType.CANCEL);

        this.setResultConverter(type -> {
            if ( type == ButtonType.FINISH ) {
                return new Group.Builder()
                        .setName(nameTextField.getText())
                        .addAllRights(selectedRights.getItems())
                        .build();
            } else {
                return null;
            }
        });
    }

}
