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
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.rights.api.AtomicRight;
import eu.ggnet.dwoss.rights.api.Group;

/**
 * Invokes a specified {@link Dialog} pane with a {@link Label}, a {@link TextField} and {@link ListView} components to edit a {@link Group}.
 * <p>
 * The modified Group is the return value of the constructor, if a valid name is entered and the finish button is clicked, else the return value is null.
 *
 * @author mirko.schulze
 */
public class EditGroupDialog extends Dialog<Group> {

    private static final Logger L = LoggerFactory.getLogger(EditGroupDialog.class);

    public EditGroupDialog(Group group) {
        L.debug("Constructor called");
        this.setTitle("Gruppen-Verwaltung");
        this.setHeaderText("Passen Sie die Gruppe an.");
        this.initModality(Modality.WINDOW_MODAL);

        Label nameLabel = new Label("Name: ");
        TextField nameTextField = new TextField(group.getName());

        HBox nameBox = new HBox(5, nameLabel, nameTextField);

        ListView<AtomicRight> selectedRightsListView = new ListView<>(FXCollections.observableArrayList(group.getRights()));
        selectedRightsListView.setCellFactory(new RightsListCell.Factory());
        selectedRightsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        TitledPane selectedRightsTitle = new TitledPane("Gewährte Rechte", selectedRightsListView);
        selectedRightsTitle.setCollapsible(false);
        selectedRightsTitle.setAlignment(Pos.CENTER);

        List<AtomicRight> filteredRights = Arrays.asList(AtomicRight.values())
                .stream()
                .filter(r -> !group.getRights().contains(r))
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

        HBox buttonBox = new HBox(5, addAllRightsButton, addRightButton, removeRightButton, removeAllRightsButton);

        VBox vbox = new VBox(5, nameBox, selectedRightsTitle, buttonBox, availableRightsTitle);

        this.getDialogPane().setPrefSize(400, 600);
        this.getDialogPane().setContent(vbox);
        this.getDialogPane().getButtonTypes().addAll(ButtonType.FINISH, ButtonType.CANCEL);

        Button finishButton = (Button)this.getDialogPane().lookupButton(ButtonType.FINISH);
        finishButton.addEventFilter(ActionEvent.ACTION, ef -> {
            if ( nameTextField.getText().isEmpty() ) {
                L.debug("Consuming {}: no name entered", ef.getEventType());
                ef.consume();
                new Alert(Alert.AlertType.ERROR, "Geben Sie einen Namen ein.").show();
            }
        });

        this.setResultConverter(type -> {
            if ( type == ButtonType.FINISH ) {
                Group g = new Group.Builder()
                        .setId(group.getId())
                        .setName(nameTextField.getText())
                        .setOptLock(group.getOptLock())
                        .addAllRights(selectedRightsListView.getItems())
                        .build();
                L.debug("Returning Group {}", g.toString());
                return g;
            } else {
                L.debug("Returning null");
                return null;
            }
        });
    }

}
