package eu.ggnet.dwoss.uniqueunit.ui.product;

import eu.ggnet.saft.api.ui.FxController;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.uniqueunit.UniqueUnitAgent;
import eu.ggnet.dwoss.uniqueunit.api.PicoProduct;
import eu.ggnet.dwoss.uniqueunit.api.PicoUnit;
import eu.ggnet.dwoss.uniqueunit.entity.*;
import eu.ggnet.saft.Client;
import eu.ggnet.saft.Ui;
import eu.ggnet.saft.api.ui.*;

/**
 * A ui that can show all UnitCollections of different Products. The UniqueUnits of a Product are also shown in different categorys(assigned to the selected
 * UnitCollection, assigned to a different UnitCollection than the selected, not assigned to any UnitCollection). These Units can be added or removed via Drag
 * and Drop from the selected UnitCollection.
 *
 * @author lucas.huelsen
 */
@Title("Zuweisungsansicht")
public class AssignmentController implements Initializable, FxController {

    // Dataformat used for UniqueUnits
    public static final DataFormat df = new DataFormat("dw/uniqueUnit");

    private static final Logger L = LoggerFactory.getLogger(AssignmentController.class);

    @FXML
    private ListView<PicoProduct> productList;

    @FXML
    private ListView<UnitCollection> unitCollectionList;

    @FXML
    private ListView<UniqueUnit> assignedUnitsList;

    @FXML
    private ListView<UniqueUnit> unassignedUnitsList;

    @FXML
    private ListView<UniqueUnit> differentAssignedUnitsList;

    @Override
    /**
     * Drag and Drop handling.
     * CellValueFactories
     * ActionListener
     */
    public void initialize(URL url, ResourceBundle rb) {

        dragAndDropHandling();

        setCellFactories();

        actionListenerHandling();
    }

    /**
     * Setting the Cell Factories for each Listview
     */
    private void setCellFactories() {

        // List containing the Products
        productList.setCellFactory(lv -> new ListCell<PicoProduct>() {
            @Override
            public void updateItem(PicoProduct p, boolean empty) {
                super.updateItem(p, empty);
                if ( empty ) {
                    setText(null);
                } else {
                    String text = p.getShortDescription();
                    setText(text);
                }
            }
        });

        // List containing the UnitCollections of the current selected Product
        unitCollectionList.setCellFactory(lv -> new ListCell<UnitCollection>() {
            @Override
            public void updateItem(UnitCollection uc, boolean empty) {
                super.updateItem(uc, empty);
                if ( empty ) {
                    setText(null);
                } else {
                    String text = uc.getNameExtension();
                    setText(text);
                }
            }
        });

        // List containing the unassigned Units.
        unassignedUnitsList.setCellFactory(SimpleUnitListCell.factory());

        // List containing the assigned Units of the Selected UnitCollection
        assignedUnitsList.setCellFactory(SimpleUnitListCell.factory());

        // list containg all Units that are assigned to different UnitCollections than the selected
        differentAssignedUnitsList.setCellFactory(SimpleUnitListCell.factory());
    }

    private void dragAndDropHandling() {

        /**
         * Start of the Drag an Drop from the unassigned units List.
         */
        unassignedUnitsList.setOnDragDetected(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                UniqueUnit selectedUnit = unassignedUnitsList.getSelectionModel().getSelectedItem();
                if ( selectedUnit == null ) return;
                Dragboard db = unassignedUnitsList.startDragAndDrop(TransferMode.ANY);
                ClipboardContent content = new ClipboardContent();
                content.put(df, selectedUnit);
                db.setContent(content);
                L.info("DnD of {} started", selectedUnit.getId());
                event.consume();
            }
        });

        /**
         * Handling the DragOver for only UniqueUnits with the right dataformat.
         */
        unassignedUnitsList.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                if ( event.getGestureSource() != unassignedUnitsList && event.getDragboard().hasContent(df) ) {
                    event.acceptTransferModes(TransferMode.ANY);
                }
                event.consume();
            }
        });

        /**
         * Handling the Dropped UniqueUnit and saving it.
         */
        unassignedUnitsList.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(final DragEvent event) {
                Dragboard db = event.getDragboard();
                boolean success = false;
                if ( db.hasContent(df) ) {
                    UniqueUnit draggedUnit = (UniqueUnit)db.getContent(df);
                    Optional.of(Client.lookup(UniqueUnitAgent.class).unsetUnitCollection(new PicoUnit(draggedUnit.getId(), "RefurbishedId=" + draggedUnit.getRefurbishId())))
                            .filter(r -> {
                                if ( !r.hasSucceded() ) event.setDropCompleted(false);
                                return Ui.failure().handle(r);
                            })
                            .ifPresent(c -> {
                                unassignedUnitsList.getItems().add(draggedUnit);
                                assignedUnitsList.getItems().remove(draggedUnit);
                                event.setDropCompleted(true);
                            });
                } else {
                    event.setDropCompleted(false);
                }
                event.consume();
            }
        });

        /**
         * Start of the Drag an Drop from the assigned units List.
         */
        assignedUnitsList.setOnDragDetected(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                UniqueUnit selectedUnit = assignedUnitsList.getSelectionModel().getSelectedItem();
                if ( selectedUnit == null ) return;
                Dragboard db = assignedUnitsList.startDragAndDrop(TransferMode.ANY);
                ClipboardContent content = new ClipboardContent();
                content.put(df, selectedUnit);
                db.setContent(content);
                L.info("DnD of {} started", selectedUnit.getId());
                event.consume();
            }
        });

        /**
         * Handling the DragOver for only UniqueUnits with the right dataformat.
         */
        assignedUnitsList.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                if ( event.getGestureSource() != assignedUnitsList && event.getDragboard().hasContent(df) ) {
                    event.acceptTransferModes(TransferMode.ANY);
                }
                event.consume();
            }
        });

        /**
         * Handling the Dropped UniqueUnit and saving it.
         */
        assignedUnitsList.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                boolean success = false;
                if ( db.hasContent(df) ) {
                    UnitCollection selectedCollection = unitCollectionList.getSelectionModel().getSelectedItem();
                    UniqueUnit draggedUnit = (UniqueUnit)db.getContent(df);
                    Optional.of(Client.lookup(UniqueUnitAgent.class).addToUnitCollection(new PicoUnit(draggedUnit.getId(), "RefurbishedId=" + draggedUnit.getRefurbishId()), selectedCollection.getId()))
                            .filter(r -> {
                                if ( !r.hasSucceded() ) event.setDropCompleted(false);
                                return Ui.failure().handle(r);
                            })
                            .ifPresent(c -> {
                                assignedUnitsList.getItems().add(draggedUnit);
                                unassignedUnitsList.getItems().remove(draggedUnit);
                                event.setDropCompleted(true);
                            });
                } else {
                    event.setDropCompleted(false);
                }
                event.consume();
            }
        });

        /**
         * Handling the DragOver for only PicoProducts with the right dataformat.
         */
        productList.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                if ( event.getGestureSource() != productList && event.getDragboard().hasContent(ProductListController.dataFormatPicoProduct) ) {
                    event.acceptTransferModes(TransferMode.ANY);
                }
                event.consume();
            }
        });

        /**
         * Handling the Dropped PicoProduct
         */
        productList.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                boolean success = false;
                if ( db.hasContent(ProductListController.dataFormatPicoProduct) ) {
                    ArrayList<PicoProduct> products = (ArrayList<PicoProduct>)db.getContent(ProductListController.dataFormatPicoProduct);
                    productList.getItems().addAll(products.stream().filter(p -> !productList.getItems().contains(p)).collect(Collectors.toList()));
                    success = true;
                }
                event.setDropCompleted(success);
                event.consume();
            }
        });
    }

    private void actionListenerHandling() {

        /**
         * Loading the Product from the selected PicoProduct. Updating the Selected UnitCollection
         */
        productList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<PicoProduct>() {
            @Override
            public void changed(ObservableValue<? extends PicoProduct> observable, PicoProduct oldValue, PicoProduct newValue) {
                if ( newValue != null ) {

                    // get the Product based on the id of the PicoProduct
                    Product product = Client.lookup(UniqueUnitAgent.class).findByIdEager(Product.class, newValue.getId());

                    //Fill the list with UnitCollections of the Product
                    unitCollectionList.getItems().clear();
                    unitCollectionList.setItems(FXCollections.observableArrayList(product.getUnitCollections()));

                    // show unassignedUnits
                    unassignedUnitsList.getItems().clear();
                    unassignedUnitsList.getItems().addAll(product
                            .getUniqueUnits()
                            .stream()
                            .filter(u -> u.getProduct() != null)
                            .collect(Collectors.toList()));
                } else {
                    // clear the lists if no Product is selected
                    unitCollectionList.getItems().clear();
                    unassignedUnitsList.getItems().clear();
                    assignedUnitsList.getItems().clear();
                }
            }
        });

        /**
         * Change the UniqueUnit Lists based on the selected Collection
         */
        unitCollectionList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<UnitCollection>() {
            @Override
            public void changed(ObservableValue<? extends UnitCollection> observable, UnitCollection oldValue, UnitCollection newValue) {
                if ( newValue != null ) {

                    // show assigned Units
                    assignedUnitsList.getItems().clear();
                    assignedUnitsList.getItems().addAll(newValue.getUnits());

                    // show different assigned units
                    differentAssignedUnitsList.getItems().clear();

                    differentAssignedUnitsList.getItems().addAll(newValue.getProduct()
                            .getUnitCollections()
                            .stream()
                            .filter(uc -> uc != newValue)
                            .flatMap(t -> t.getUnits().stream())
                            .collect(Collectors.toList()));
                } else {
                    // clear the lists if no cellection is selected
                    assignedUnitsList.getItems().clear();
                    unitCollectionList.getItems().clear();
                    differentAssignedUnitsList.getItems().clear();
                }
            }
        });

    }

}
