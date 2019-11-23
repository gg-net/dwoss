/*
 * Copyright (C) 2014 GG-Net GmbH - Oliver Günther
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

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import eu.ggnet.dwoss.rights.api.AtomicRight;
import eu.ggnet.dwoss.rights.ee.RightsAgent;
import eu.ggnet.saft.core.Dl;

/**
 *
 * @author Bastian Venz
 */
public class PersonaManagmentController implements Initializable {

    @FXML
    TextField nameField;

    @FXML
    ListView<AtomicRight> activatedRights;

    @FXML
    ListView<AtomicRight> deactivatedRights;

    @FXML
    Button addButton;

    @FXML
    Button removeButton;

    private UiPersona uiPersona;

    private ObservableList<AtomicRight> deactivatedRightsList;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        activatedRights.setCellFactory(new RightsListCell.Factory());
        activatedRights.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        deactivatedRights.setCellFactory(new RightsListCell.Factory());
        deactivatedRights.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        activatedRights.setOnMouseClicked(t -> deactivatedRights.getSelectionModel().clearSelection());
        deactivatedRights.setOnMouseClicked(t -> activatedRights.getSelectionModel().clearSelection());
        addButton.visibleProperty().bind(deactivatedRights.getSelectionModel().selectedIndexProperty().greaterThanOrEqualTo(0));
        removeButton.visibleProperty().bind(activatedRights.getSelectionModel().selectedIndexProperty().greaterThanOrEqualTo(0));

    }

    public void setPersona(UiPersona p) {
        if ( p == null ) p = new UiPersona();
        Bindings.bindBidirectional(nameField.textProperty(), p.nameProperty());
        Bindings.bindBidirectional(activatedRights.itemsProperty(), p.personaRightsProperty());
        Set<AtomicRight> allOf = EnumSet.allOf(AtomicRight.class);
        allOf.removeAll(p.personaRightsProperty().get());
        deactivatedRightsList = FXCollections.observableList(new ArrayList<>(allOf));
        Bindings.bindBidirectional(deactivatedRights.itemsProperty(), new SimpleObjectProperty<>(deactivatedRightsList));
        this.uiPersona = p;
    }

    @FXML
    private void handleAddButton() {
        ObservableList<AtomicRight> selectedItems = deactivatedRights.getSelectionModel().getSelectedItems();
        activatedRights.itemsProperty().unbindBidirectional(uiPersona.personaRightsProperty());
        activatedRights.setItems(FXCollections.<AtomicRight>observableArrayList());
        activatedRights.getItems().clear();
        uiPersona.addAll(selectedItems);
        deactivatedRightsList.removeAll(selectedItems);
        setPersona(uiPersona);
    }

    @FXML
    private void handleRemoveButton() {
        ObservableList<AtomicRight> selectedItems = activatedRights.getSelectionModel().getSelectedItems();
        uiPersona.personaRightsProperty().get().removeAll(selectedItems);
        activatedRights.itemsProperty().unbindBidirectional(uiPersona.personaRightsProperty());
        activatedRights.setItems(FXCollections.<AtomicRight>observableArrayList());
        activatedRights.getItems().clear();
        deactivatedRightsList.addAll(selectedItems);
        setPersona(uiPersona);
    }

    @FXML
    public void onConfirm() {
        RightsAgent agent = Dl.remote().lookup(RightsAgent.class);
        agent.store(uiPersona.toPersona());
        onCancel();
    }

    // Mach wech.
    @FXML
    public void onCancel() {
        Stage stage = (Stage)nameField.getScene().getWindow();
        stage.close();
    }

    public static URL loadFxml() {
        return PersonaManagmentController.class.getResource("PersonaManagmentView.fxml");
    }

}
