package eu.ggnet.dwoss.rights;

import java.net.URL;
import java.util.*;

import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import eu.ggnet.dwoss.rights.api.AtomicRight;
import eu.ggnet.dwoss.rights.entity.Persona;

import static eu.ggnet.saft.core.Client.lookup;

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

    private Persona p;

    private ObservableList<AtomicRight> deactivatedRightsList;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        activatedRights.setCellFactory(new RightsListCell.Factory());
        activatedRights.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        deactivatedRights.setCellFactory(new RightsListCell.Factory());
        deactivatedRights.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        activatedRights.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                deactivatedRights.getSelectionModel().clearSelection();
            }
        });
        deactivatedRights.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                activatedRights.getSelectionModel().clearSelection();
            }
        });
        addButton.visibleProperty().bind(deactivatedRights.getSelectionModel().selectedIndexProperty().greaterThanOrEqualTo(0));
        removeButton.visibleProperty().bind(activatedRights.getSelectionModel().selectedIndexProperty().greaterThanOrEqualTo(0));

    }

    public void setPersona(Persona p) {
        if ( p == null ) p = new Persona();
        Bindings.bindBidirectional(nameField.textProperty(), p.nameProperty());
        Bindings.bindBidirectional(activatedRights.itemsProperty(), p.personaRightsProperty());
        Set<AtomicRight> allOf = EnumSet.allOf(AtomicRight.class);
        allOf.removeAll(p.getPersonaRights());
        deactivatedRightsList = FXCollections.observableList(new ArrayList<>(allOf));
        Bindings.bindBidirectional(deactivatedRights.itemsProperty(), new SimpleObjectProperty<>(deactivatedRightsList));
        this.p = p;
    }

    @FXML
    private void handleAddButton() {
        ObservableList<AtomicRight> selectedItems = deactivatedRights.getSelectionModel().getSelectedItems();
        activatedRights.itemsProperty().unbindBidirectional(p.personaRightsProperty());
        activatedRights.setItems(FXCollections.<AtomicRight>observableArrayList());
        activatedRights.getItems().clear();
        p.addAll(selectedItems);
        deactivatedRightsList.removeAll(selectedItems);
        setPersona(p);
    }

    @FXML
    private void handleRemoveButton() {
        ObservableList<AtomicRight> selectedItems = activatedRights.getSelectionModel().getSelectedItems();
        p.removeAll(selectedItems);
        activatedRights.itemsProperty().unbindBidirectional(p.personaRightsProperty());
        activatedRights.setItems(FXCollections.<AtomicRight>observableArrayList());
        activatedRights.getItems().clear();
        deactivatedRightsList.addAll(selectedItems);
        setPersona(p);
    }

    @FXML
    public void onConfirm() {
        RightsAgent agent = lookup(RightsAgent.class);
        agent.store(p);
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
