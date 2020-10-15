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


import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;

import eu.ggnet.dwoss.core.widget.Dl;
import eu.ggnet.dwoss.rights.api.AtomicRight;
import eu.ggnet.dwoss.rights.ee.GroupAgent;
import eu.ggnet.dwoss.rights.ee.entity.Persona;


/**
 *
 * @author mirko.schu
import static javafx.stage.Modality.WINDOW_MODAL;
lze
 */
public class NewGroupDialog extends Dialog<UiPersona> {
    
    private Persona group;

    public NewGroupDialog() {
        this.setTitle("Gruppen-Verwaltung");
        this.setHeaderText("Legen Sie eine neue Gruppe an.");
        this.initModality(Modality.WINDOW_MODAL);

        Label nameLabel = new Label("Name: ");

        TextField nameInput = new TextField();
        nameInput.setPromptText("Masters of Desaster");

        HBox nameHbox = new HBox(5, nameLabel, nameInput);

        ListView<AtomicRight> selectedRights = new ListView<>(FXCollections.observableArrayList());
        selectedRights.setCellFactory(new RightsListCell.Factory());
        selectedRights.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        ListView<AtomicRight> availableRights = new ListView<>(FXCollections.observableArrayList(AtomicRight.values()));
        availableRights.setCellFactory(new RightsListCell.Factory());
        availableRights.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        
        Button addAllRightsButton = new Button("Alle Rechte hinzufügen");
        addAllRightsButton.setOnAction(e -> {
            selectedRights.setItems(FXCollections.observableArrayList(AtomicRight.values()));
            availableRights.getItems().clear();
        });

        Button addRightButton = new Button("Recht hinzufügen");
        addRightButton.setOnAction(e -> {
            AtomicRight right = availableRights.getSelectionModel().getSelectedItem();
            selectedRights.getItems().add(right);
            availableRights.getItems().remove(right);
        });

        Button removeRightButton = new Button("Recht entfernen");
        removeRightButton.setOnAction(e -> {
            AtomicRight right = selectedRights.getSelectionModel().getSelectedItem();
            availableRights.getItems().add(right);
            selectedRights.getItems().remove(right);
        });
        
        Button removeAllRightsButton = new Button("Alle Rechte entfernen");
        removeAllRightsButton.setOnAction(e -> {
            availableRights.setItems(FXCollections.observableArrayList(AtomicRight.values()));
            selectedRights.getItems().clear();
        });
        
        HBox buttonBox = new HBox(5,addAllRightsButton, addRightButton, removeRightButton, removeAllRightsButton);
        
        VBox vbox = new VBox(5, nameHbox, selectedRights, buttonBox, availableRights);

        this.getDialogPane().setPrefSize(600, 400);
        this.getDialogPane().setContent(vbox);

        this.getDialogPane().getButtonTypes().addAll(ButtonType.FINISH, ButtonType.CANCEL);
        Button finishButton = (Button)this.getDialogPane().lookupButton(ButtonType.FINISH);

        finishButton.addEventFilter(ActionEvent.ACTION, eh -> {
            String input = nameInput.getText();
            if ( input.isEmpty() ) {
                eh.consume();
                new Alert(Alert.AlertType.ERROR, "Gib einen Namen ein").show();
            } else {
                GroupAgent agent = Dl.remote().lookup(GroupAgent.class);
                agent.create(nameInput.getText());
                group = agent.findByName(nameInput.getText());
                selectedRights.getItems().forEach(r -> agent.addRight(group.getId(), r));
            }
        });

        this.setResultConverter(type -> {
            if ( type == ButtonType.FINISH ) {
                return new UiPersona(group);
            } else {
                return null;
            }
        });
    }


}
