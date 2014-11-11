/* 
 * Copyright (C) 2014 pascal.perau
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
package eu.ggnet.dwoss.rights;

import java.io.IOException;
import java.net.URL;
import java.util.*;

import eu.ggnet.saft.core.Workspace;

import eu.ggnet.dwoss.rights.RightsAgent;

import eu.ggnet.dwoss.rights.api.AtomicRight;

import eu.ggnet.dwoss.rights.entity.Operator;
import eu.ggnet.dwoss.rights.entity.Persona;
import eu.ggnet.dwoss.common.ExceptionUtil;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import static eu.ggnet.saft.core.Client.lookup;

/**
 * This is the FXML Controller Class for the RightsManagmentView in which {@link Operator}'s get {@link AtomicRight}'s and {@link Persona}.
 *
 * @author Bastian Venz
 */
public class RightsManagmentController implements Initializable {

    @FXML
    ListView<Operator> userlist;

    @FXML
    ListView<Persona> activePersonas;

    @FXML
    ListView<Persona> deactivePersonas;

    @FXML
    ListView<AtomicRight> activeRights;

    @FXML
    ListView<AtomicRight> deactiveRights;

    @FXML
    ListView<AtomicRight> allRights;

    @FXML
    Button addRightButton;

    @FXML
    Button removeRightButton;

    @FXML
    Button addPersonaButton;

    @FXML
    Button removePersonaButton;

    private final Set<Persona> allPersonas = new HashSet<>();

    private ObservableList<AtomicRight> deactivatedRightsList;

    private ObservableList<Persona> deactivatedPersonasList;

    private Operator selectedOperator;

    /**
     * Initializes the controller class.
     * <p>
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        userlist.setCellFactory(new OperatorListCell.Factory());
        ReadOnlyObjectProperty<Operator> opProp = userlist.getSelectionModel().selectedItemProperty();
        addRightButton.visibleProperty().bind(deactiveRights.getSelectionModel().selectedIndexProperty().greaterThanOrEqualTo(0).and(opProp.isNotNull()));
        removeRightButton.visibleProperty().bind(activeRights.getSelectionModel().selectedIndexProperty().greaterThanOrEqualTo(0).and(opProp.isNotNull()));
        addPersonaButton.visibleProperty().bind(deactivePersonas.getSelectionModel().selectedIndexProperty().greaterThanOrEqualTo(0).and(opProp.isNotNull()));
        removePersonaButton.visibleProperty().bind(activePersonas.getSelectionModel().selectedIndexProperty().greaterThanOrEqualTo(0).and(opProp.isNotNull()));

        userlist.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if ( event.getButton().equals(MouseButton.PRIMARY) ) {
                    Operator op = userlist.getSelectionModel().getSelectedItem();
                    if ( event.getClickCount() == 1 ) {
                        setSelectedOperator(op);
                    } else if ( op != null ) {
                        openOperatorManagment(op);
                    }
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

        activePersonas.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                System.out.println("handleActivePersonas:c=" + t.getClickCount() + ",selection=" + activePersonas.getSelectionModel().getSelectedItem());
                if ( t.getClickCount() > 1 ) {
                    if ( activePersonas.getSelectionModel().getSelectedItem() != null )
                        openPersonaManagment(activePersonas.getSelectionModel().getSelectedItem());
                    return;
                }
                deactivePersonas.getSelectionModel().clearSelection();
                activeRights.getSelectionModel().clearSelection();
                deactiveRights.getSelectionModel().clearSelection();
            }
        });
        deactivePersonas.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                if ( t.getClickCount() > 1 ) {
                    if ( deactivePersonas.getSelectionModel().getSelectedItem() != null )
                        openPersonaManagment(deactivePersonas.getSelectionModel().getSelectedItem());
                    return;
                }
                activePersonas.getSelectionModel().clearSelection();
                activeRights.getSelectionModel().clearSelection();
                deactiveRights.getSelectionModel().clearSelection();
            }
        });
        activeRights.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                deactivePersonas.getSelectionModel().clearSelection();
                activePersonas.getSelectionModel().clearSelection();
                deactiveRights.getSelectionModel().clearSelection();
            }
        });
        deactiveRights.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                deactivePersonas.getSelectionModel().clearSelection();
                activePersonas.getSelectionModel().clearSelection();
                activeRights.getSelectionModel().clearSelection();
            }
        });
        refreshAll();
    }

    @FXML
    private void handleAddRightButton() {
        Operator op = userlist.getSelectionModel().getSelectedItem();
        List<AtomicRight> selectedItems = new ArrayList<>(deactiveRights.getSelectionModel().getSelectedItems());
        System.out.println("SelectedIt: " + selectedItems);
        op.addAllRight(selectedItems);
        resetDeactiveRights();
        resetAllRights();
//        setSelectedOperator(op);
        lookup(RightsAgent.class).store(op);
    }

    @FXML
    private void handleRemoveRightButton() {
        Operator op = userlist.getSelectionModel().getSelectedItem();
        List<AtomicRight> selectedItems = new ArrayList<>(activeRights.getSelectionModel().getSelectedItems());
        selectedOperator().removeAllRight(selectedItems);
        resetDeactiveRights();
        resetAllRights();
//        setSelectedOperator(op);
        lookup(RightsAgent.class).store(op);
    }

    private void resetDeactiveRights() {
        Operator op = userlist.getSelectionModel().getSelectedItem();
        deactivatedRightsList.clear();
        deactivatedRightsList.addAll(EnumSet.complementOf(op.getAllActiveRights()));
    }

    @FXML
    private void handleAddPersonaButton() {
        Operator op = userlist.getSelectionModel().getSelectedItem();
        List<Persona> selectedItems = new ArrayList<>(deactivePersonas.getSelectionModel().getSelectedItems());
        op.addAllPersona(selectedItems);
        resetDeactivePersonas();
        resetDeactiveRights();
        resetAllRights();

//        setSelectedOperator(op);
        lookup(RightsAgent.class).store(op);
    }

    private Operator selectedOperator() {
        return userlist.getSelectionModel().getSelectedItem();
    }

    private void resetDeactivePersonas() {
        List<Persona> removed = new ArrayList<>(allPersonas);
        removed.removeAll(selectedOperator().getPersonas());
        deactivatedPersonasList.clear();
        deactivatedPersonasList.addAll(removed);
    }

    @FXML
    private void handleRemovePersonaButton() {
        Operator op = userlist.getSelectionModel().getSelectedItem();
        List<Persona> selectedItems = new ArrayList<>(activePersonas.getSelectionModel().getSelectedItems());
        op.removeAllPersona(selectedItems);
        List<Persona> removed = new ArrayList<>(allPersonas);
        removed.removeAll(op.getPersonas());
        resetDeactivePersonas();
        resetDeactiveRights();
        resetAllRights();

//        setSelectedOperator(op);
        lookup(RightsAgent.class).store(op);
    }

    private void resetAllRights() {
        allRights.getItems().clear();
        allRights.getItems().addAll(selectedOperator().getAllActiveRights());
    }

    @FXML
    private void handleAddNewPersonaButton() {
        openPersonaManagment(null);
    }

    @FXML
    private void handleAddNewOperatorButton() {
        openOperatorManagment(null);
    }

    /**
     * This Method is to set the selected operator and fill/refresh the lists.
     * <p>
     * @param op is the {@link Operator} wich is setted.
     */
    private void setSelectedOperator(Operator op) {
        if ( op == null ) return;
        if ( selectedOperator != null ) {//to correclty refresh the both active Lists
            activePersonas.itemsProperty().unbindBidirectional(selectedOperator.personasProperty());
            activePersonas.setItems(FXCollections.<Persona>observableArrayList());
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
        userlist.getItems().clear();
        activePersonas.getItems().clear();
        deactivePersonas.getItems().clear();
        activeRights.getItems().clear();
        deactiveRights.getItems().clear();
        Platform.runLater(
                new Runnable() {
                    @Override
                    public void run() {
                        RightsAgent agent = lookup(RightsAgent.class);
                        userlist.getItems().addAll(agent.findAllEager(Operator.class));
                        List<Persona> findAllEager = agent.findAllEager(Persona.class);
                        allPersonas.addAll(findAllEager);
                        deactivePersonas.getItems().addAll(findAllEager);
                    }
                }
        );
    }

    /**
     * Open a Stage in which the given {@link Persona} is edit if the {@link Persona} is not null, if is null it will be a creation of a {@link Persona}.
     * <p>
     * @param p is the {@link Persona} which is edited, can be null to create a new.
     */
    private void openPersonaManagment(Persona p) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            AnchorPane page = (AnchorPane)fxmlLoader.load(getClass().getResource("PersonaManagmentView.fxml").openStream());
            PersonaManagmentController controller = (PersonaManagmentController)fxmlLoader.getController();
            controller.setPersona(p);
            Stage stage = new Stage();
            stage.setTitle("Rollen Managment");
            Scene scene = new Scene(page, Color.ALICEBLUE);
            stage.setScene(scene);
            stage.showAndWait();
            resetDeactivePersonas();
            resetDeactiveRights();
            resetAllRights();
        } catch (IOException exception) {
            ExceptionUtil.show(lookup(Workspace.class).getMainFrame(), exception);
        }
    }

    /**
     * This open a Stage to edit the {@link Operator} if the {@link Operator} is not null, if is null it will be a creation of a {@link Operator}.
     * <p>
     * @param op is the {@link Operator} which is edited, can be null to create a new.
     */
    private void openOperatorManagment(Operator op) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            AnchorPane page = (AnchorPane)fxmlLoader.load(getClass().getResource("OperatorManagmentView.fxml").openStream());
            OperatorManagmentController controller = (OperatorManagmentController)fxmlLoader.getController();
            controller.setOperator(op);
            Stage stage = new Stage();
            stage.setTitle("Nutzer Managment");
            Scene scene = new Scene(page, Color.ALICEBLUE);
            stage.setScene(scene);
            stage.showAndWait();
        } catch (IOException exception) {
            ExceptionUtil.show(lookup(Workspace.class).getMainFrame(), exception);
        }
    }

    /**
     * Merge all {@link Operator}'s in the database and close the Stage.
     */
    @FXML
    private void handleSaveButton() {
        handleCancleButton();
    }

    // mach wech.
    /**
     * This method close the Stage.
     */
    @FXML
    private void handleCancleButton() {
        Stage stage = (Stage)userlist.getScene().getWindow();
        stage.close();
    }
    
    public static URL loadFxml() {
        return RightsManagmentController.class.getResource("RightsManagmentView.fxml");
    }

}
