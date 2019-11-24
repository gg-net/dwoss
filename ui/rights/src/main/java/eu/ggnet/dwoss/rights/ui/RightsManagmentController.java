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

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;

import eu.ggnet.dwoss.rights.api.AtomicRight;
import eu.ggnet.dwoss.rights.ee.RightsAgent;
import eu.ggnet.dwoss.rights.ee.entity.Operator;
import eu.ggnet.dwoss.rights.ee.entity.Persona;
import eu.ggnet.saft.core.*;
import eu.ggnet.saft.core.ui.FxController;
import eu.ggnet.saft.core.ui.Title;

/**
 * This is the FXML Controller Class for the RightsManagmentView in which {@link Operator}'s get {@link AtomicRight}'s and {@link Persona}.
 *
 * @author Bastian Venz
 */
@Title("Rechte Verwaltung")
public class RightsManagmentController implements Initializable, FxController {

    @FXML
    private ListView<UiOperator> userlist;

    @FXML
    private ListView<UiPersona> activePersonas;

    @FXML
    private ListView<UiPersona> deactivePersonas;

    @FXML
    private ListView<AtomicRight> activeRights;

    @FXML
    private ListView<AtomicRight> deactiveRights;

    @FXML
    private ListView<AtomicRight> allRights;

    @FXML
    private Button addRightButton;

    @FXML
    private Button removeRightButton;

    @FXML
    private Button addPersonaButton;

    @FXML
    private Button removePersonaButton;

    private final Set<UiPersona> allPersonas = new HashSet<>();

    private ObservableList<AtomicRight> deactivatedRightsList;

    private ObservableList<UiPersona> deactivatedPersonasList;

    private UiOperator selectedOperator;

    /**
     * Initializes the controller class.
     * <p>
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        userlist.setCellFactory(new OperatorListCell.Factory());
        ReadOnlyObjectProperty<UiOperator> opProp = userlist.getSelectionModel().selectedItemProperty();
        addRightButton.visibleProperty().bind(deactiveRights.getSelectionModel().selectedIndexProperty().greaterThanOrEqualTo(0).and(opProp.isNotNull()));
        removeRightButton.visibleProperty().bind(activeRights.getSelectionModel().selectedIndexProperty().greaterThanOrEqualTo(0).and(opProp.isNotNull()));
        addPersonaButton.visibleProperty().bind(deactivePersonas.getSelectionModel().selectedIndexProperty().greaterThanOrEqualTo(0).and(opProp.isNotNull()));
        removePersonaButton.visibleProperty().bind(activePersonas.getSelectionModel().selectedIndexProperty().greaterThanOrEqualTo(0).and(opProp.isNotNull()));

        userlist.setOnMouseClicked((event) -> {
            if ( event.getButton().equals(MouseButton.PRIMARY) ) {
                UiOperator op = userlist.getSelectionModel().getSelectedItem();
                if ( event.getClickCount() == 1 ) {
                    setSelectedOperator(op);
                } else if ( op != null ) {
                    openOperatorManagment(op);
                }
            }
        });

        activePersonas.setCellFactory(new PersonaListCell.Factory());
        activePersonas.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        deactivePersonas.setCellFactory(new PersonaListCell.Factory());
        deactivePersonas.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        activeRights.setCellFactory(new RightsListCell.Factory());
        activeRights.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        deactiveRights.setCellFactory(new RightsListCell.Factory());
        deactiveRights.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        allRights.setCellFactory(new RightsListCell.Factory());

        deactivatedRightsList = FXCollections.observableArrayList(AtomicRight.values());
        deactivatedPersonasList = FXCollections.observableArrayList();
        Bindings.bindBidirectional(deactiveRights.itemsProperty(), new SimpleListProperty<>(deactivatedRightsList));
        Bindings.bindBidirectional(deactivePersonas.itemsProperty(), new SimpleListProperty<>(deactivatedPersonasList));

        deactiveRights.getItems().addAll();

        activePersonas.setOnMouseClicked((MouseEvent t) -> {
            if ( t.getClickCount() > 1 ) {
                if ( activePersonas.getSelectionModel().getSelectedItem() != null )
                    openPersonaManagment(activePersonas.getSelectionModel().getSelectedItem());
                return;
            }
            deactivePersonas.getSelectionModel().clearSelection();
            activeRights.getSelectionModel().clearSelection();
            deactiveRights.getSelectionModel().clearSelection();
        });
        deactivePersonas.setOnMouseClicked((MouseEvent t) -> {
            if ( t.getClickCount() > 1 ) {
                if ( deactivePersonas.getSelectionModel().getSelectedItem() != null )
                    openPersonaManagment(deactivePersonas.getSelectionModel().getSelectedItem());
                return;
            }
            activePersonas.getSelectionModel().clearSelection();
            activeRights.getSelectionModel().clearSelection();
            deactiveRights.getSelectionModel().clearSelection();
        });
        activeRights.setOnMouseClicked((MouseEvent t) -> {
            deactivePersonas.getSelectionModel().clearSelection();
            activePersonas.getSelectionModel().clearSelection();
            deactiveRights.getSelectionModel().clearSelection();
        });
        deactiveRights.setOnMouseClicked((MouseEvent t) -> {
            deactivePersonas.getSelectionModel().clearSelection();
            activePersonas.getSelectionModel().clearSelection();
            activeRights.getSelectionModel().clearSelection();
        });
        refreshAll();
    }

    @FXML
    private void handleAddRightButton() {
        UiOperator op = userlist.getSelectionModel().getSelectedItem();
        List<AtomicRight> selectedItems = new ArrayList<>(deactiveRights.getSelectionModel().getSelectedItems());
        System.out.println("SelectedIt: " + selectedItems);
        op.addAllRight(selectedItems);
        resetDeactiveRights();
        resetAllRights();
//        setSelectedOperator(op);
        Dl.remote().lookup(RightsAgent.class).store(op.toOperator());
    }

    @FXML
    private void handleRemoveRightButton() {
        UiOperator op = userlist.getSelectionModel().getSelectedItem();
        List<AtomicRight> selectedItems = new ArrayList<>(activeRights.getSelectionModel().getSelectedItems());
        selectedOperator().removeAllRight(selectedItems);
        resetDeactiveRights();
        resetAllRights();
//        setSelectedOperator(op);
        Dl.remote().lookup(RightsAgent.class).store(op.toOperator());
    }

    private void resetDeactiveRights() {
        UiOperator op = userlist.getSelectionModel().getSelectedItem();
        deactivatedRightsList.clear();
        deactivatedRightsList.addAll(EnumSet.complementOf(op.getAllActiveRights()));
    }

    @FXML
    private void handleAddPersonaButton() {
        UiOperator op = userlist.getSelectionModel().getSelectedItem();
        List<UiPersona> selectedItems = new ArrayList<>(deactivePersonas.getSelectionModel().getSelectedItems());
        op.addAllPersona(selectedItems);
        resetDeactivePersonas();
        resetDeactiveRights();
        resetAllRights();

//        setSelectedOperator(op);
        Dl.remote().lookup(RightsAgent.class).store(op.toOperator());
    }

    private UiOperator selectedOperator() {
        return userlist.getSelectionModel().getSelectedItem();
    }

    private void resetDeactivePersonas() {
        List<UiPersona> removed = new ArrayList<>(allPersonas);
        removed.removeAll(selectedOperator().getPersonas());
        deactivatedPersonasList.clear();
        deactivatedPersonasList.addAll(removed);
    }

    @FXML
    private void handleRemovePersonaButton() {
        UiOperator op = userlist.getSelectionModel().getSelectedItem();
        List<UiPersona> selectedItems = new ArrayList<>(activePersonas.getSelectionModel().getSelectedItems());
        op.removeAllPersona(selectedItems);
        List<UiPersona> removed = new ArrayList<>(allPersonas);
        removed.removeAll(op.getPersonas());
        resetDeactivePersonas();
        resetDeactiveRights();
        resetAllRights();

//        setSelectedOperator(op);
        Dl.remote().lookup(RightsAgent.class).store(op.toOperator());
    }

    private void resetAllRights() {
        allRights.getItems().clear();
        allRights.getItems().addAll(selectedOperator().getAllActiveRights());
    }

    @FXML
    private void handleAddNewPersonaButton() {
        openPersonaManagment(new UiPersona());
    }

    @FXML
    private void handleAddNewOperatorButton() {
        openOperatorManagment(new UiOperator());
    }

    /**
     * This Method is to set the selected operator and fill/refresh the lists.
     * <p>
     * @param op is the {@link Operator} wich is setted.
     */
    private void setSelectedOperator(UiOperator op) {
        if ( op == null ) return;
        if ( selectedOperator != null ) {//to correclty refresh the both active Lists
            activePersonas.itemsProperty().unbindBidirectional(selectedOperator.personasProperty());
            activePersonas.setItems(FXCollections.<UiPersona>observableArrayList());
            activeRights.itemsProperty().unbindBidirectional(selectedOperator.rightsProperty());
            activeRights.setItems(FXCollections.<AtomicRight>observableArrayList());
        }
        selectedOperator = op;

        activePersonas.itemsProperty().bindBidirectional(selectedOperator.personasProperty());
//        Set<AtomicRight> allOf = EnumSet.allOf(AtomicRight.class);
//        allOf.removeAll(op.getRights());
//        deactivatedRightsList.clear();
//        deactivatedRightsList.addAll(allOf);

        activeRights.itemsProperty().bindBidirectional(selectedOperator.rightsProperty());
//        ArrayList<Persona> deactivePersonas = new ArrayList<>(allPersonas);
//        deactivePersonas.removeAll(selectedOperator.getPersonas());
//        deactivatedPersonasList.clear();
//        deactivatedPersonasList.addAll(deactivePersonas);

        resetDeactivePersonas();
        resetDeactiveRights();
        resetAllRights();
    }

    /**
     * Clears all Lists and get all {@link Operator}'s directly from the Database.
     */
    protected void refreshAll() {
        Platform.runLater(() -> {
            userlist.getItems().clear();
            activePersonas.getItems().clear();
            allPersonas.clear();
            deactivePersonas.getItems().clear();
            activeRights.getItems().clear();
            deactiveRights.getItems().clear();
            RightsAgent agent = Dl.remote().lookup(RightsAgent.class);
            agent.findAllEager(Operator.class).forEach(o -> userlist.getItems().add(new UiOperator(o)));
            agent.findAllEager(Persona.class).forEach(p -> {
                UiPersona uip = new UiPersona(p);
                allPersonas.add(uip);
                deactivePersonas.getItems().add(uip);
            });
        });
    }

    /**
     * Open a Stage in which the given {@link Persona} is edit if the {@link Persona} is not null, if is null it will be a creation of a {@link Persona}.
     * <p>
     * @param p is the {@link Persona} which is edited, can be null to create a new.
     */
    private void openPersonaManagment(UiPersona p) {
        Ui.build(removePersonaButton).title("Rollen Management").modality(Modality.WINDOW_MODAL).fxml().eval(() -> p, PersonaManagmentController.class).cf()
                .thenAcceptAsync(uc -> Dl.remote().lookup(RightsAgent.class).store(uc.toPersona()), UiCore.getExecutor())
                .thenRunAsync(() -> refreshAll(), Platform::runLater)
                .handle(Ui.handler());
    }

    /**
     * This open a Stage to edit the {@link Operator} if the {@link Operator} is not null, if is null it will be a creation of a {@link Operator}.
     * <p>
     * @param op is the {@link Operator} which is edited, can be null to create a new.
     */
    private void openOperatorManagment(UiOperator op) {
        System.out.println("Muh");
        Ui.build(removePersonaButton).title("Nutzer Management").modality(Modality.WINDOW_MODAL).fxml().eval(() -> op, OperatorManagmentController.class).cf()
                .thenAcceptAsync(uc -> Dl.remote().lookup(RightsAgent.class).store(uc.toOperator()), UiCore.getExecutor())
                .thenRunAsync(() -> refreshAll(), Platform::runLater)
                .handle(Ui.handler());
    }

    /**
     * This method close the Stage.
     */
    @FXML
    private void handleCloseButton() {
        Ui.closeWindowOf(userlist);
    }

    public static URL loadFxml() {
        return RightsManagmentController.class.getResource("RightsManagmentView.fxml");
    }

}
