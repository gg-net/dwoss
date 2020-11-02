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

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
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
public class CreateGroupDialog extends Dialog<Group> {

    public CreateGroupDialog() {
        this.setTitle("Gruppen-Verwaltung");
        this.setHeaderText("Legen Sie eine neue Gruppe an.");
        this.initModality(Modality.WINDOW_MODAL);

        Label nameLabel = new Label("Name: ");
        TextField nameTextField = new TextField();
        nameTextField.setPromptText("Masters of Desaster");

        HBox nameBox = new HBox(5, nameLabel, nameTextField);

        ListView<AtomicRight> selectedRightsListView = new ListView<>(FXCollections.observableArrayList());
        selectedRightsListView.setCellFactory(new RightsListCell.Factory());
        selectedRightsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        
        TitledPane selectedRightsTitle = new TitledPane("Gewährte Rechte", selectedRightsListView);
        selectedRightsTitle.setCollapsible(false);
        selectedRightsTitle.setAlignment(Pos.CENTER);

        ListView<AtomicRight> availableRightsListView = new ListView<>(FXCollections.observableArrayList(AtomicRight.values()));
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

        HBox buttonBox = new HBox(5, addAllRightsButton, addRightButton, removeRightButton, removeAllRightsButton);
        buttonBox.setAlignment(Pos.CENTER);

        VBox vbox = new VBox(5, nameBox, selectedRightsTitle, buttonBox, availableRightsTitle);

        this.getDialogPane().setPrefSize(400, 600);
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
                        .addAllRights(selectedRightsListView.getItems())
                        .build();
            } else {
                return null;
            }
        });
    }
}
