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
import eu.ggnet.dwoss.uniqueunit.entity.*;
import eu.ggnet.dwoss.uniqueunit.entity.UniqueUnit.Identifier;
import eu.ggnet.saft.Client;
import eu.ggnet.saft.api.ui.*;

import static eu.ggnet.dwoss.uniqueunit.entity.UniqueUnit.Identifier.REFURBISHED_ID;

/**
 * Defines the displayed products in the table. Handles the filtering of the
 * table.
 *
 * @author lucas.huelsen
 */
@Title("Zuweisungsansicht")
public class AssignmentController implements Initializable, FxController {

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
     * Adding the filters to the combo box. Setting the cell values and the
     * filtered list containing the data.
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

        unassignedUnitsList.setCellFactory(lv -> new ListCell<UniqueUnit>() {
            @Override
            public void updateItem(UniqueUnit p, boolean empty) {
                super.updateItem(p, empty);
                if ( empty ) {
                    setText(null);
                } else {
                    String text = p.getIdentifier(Identifier.SERIAL) + " || " + p.getIdentifier(REFURBISHED_ID) + " || " + p.getCondition().getNote();
                    setText(text);
                }
            }
        });

        assignedUnitsList.setCellFactory(lv -> new ListCell<UniqueUnit>() {
            @Override
            public void updateItem(UniqueUnit p, boolean empty) {
                super.updateItem(p, empty);
                if ( empty ) {
                    setText(null);
                } else {
                    String text = p.getIdentifier(Identifier.SERIAL) + " || " + p.getIdentifier(REFURBISHED_ID) + " || " + p.getCondition().getNote();
                    setText(text);
                }
            }
        });

        differentAssignedUnitsList.setCellFactory(lv -> new ListCell<UniqueUnit>() {
            @Override
            public void updateItem(UniqueUnit p, boolean empty) {
                super.updateItem(p, empty);
                if ( empty ) {
                    setText(null);
                } else {
                    String text = p.getIdentifier(Identifier.SERIAL) + " || " + p.getIdentifier(REFURBISHED_ID) + " || " + p.getCondition().getNote();
                    setText(text);
                }
            }
        });
    }

    private void dragAndDropHandling() {

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

        unassignedUnitsList.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                if ( event.getGestureSource() != unassignedUnitsList && event.getDragboard().hasContent(df) ) {
                    event.acceptTransferModes(TransferMode.ANY);
                }
                event.consume();
            }
        });

        unassignedUnitsList.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                boolean success = false;
                if ( db.hasContent(df) ) {
                    unassignedUnitsList.getItems().add((UniqueUnit)db.getContent(df));
                    assignedUnitsList.getItems().remove((UniqueUnit)db.getContent(df));

                    success = true;

                }
                event.setDropCompleted(success);
                event.consume();
            }
        });

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

        assignedUnitsList.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                if ( event.getGestureSource() != assignedUnitsList && event.getDragboard().hasContent(df) ) {
                    event.acceptTransferModes(TransferMode.ANY);
                }
                event.consume();
            }
        });

        assignedUnitsList.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                boolean success = false;
                if ( db.hasContent(df) ) {
                    assignedUnitsList.getItems().add((UniqueUnit)db.getContent(df));
                    unassignedUnitsList.getItems().remove((UniqueUnit)db.getContent(df));

                    success = true;

                }
                event.setDropCompleted(success);
                event.consume();
            }
        });

        /**
         * Receive Products from ListView
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

        // update the UI if the Product changes
        productList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<PicoProduct>() {
            @Override
            public void changed(ObservableValue<? extends PicoProduct> observable, PicoProduct oldValue, PicoProduct newValue) {
                if ( newValue != null ) {

                    Product product = Client.lookup(UniqueUnitAgent.class).findProductByPartNo("s");

                    unitCollectionList.getItems().clear();
                    unitCollectionList.setItems(FXCollections.observableArrayList(product.getUnitCollections()));

                    // show unassignedUnits
                    unassignedUnitsList.getItems().clear();
                    unassignedUnitsList.getItems().addAll((List<UniqueUnit>)product
                            .getUniqueUnits()
                            .stream()
                            .filter(u -> u.getProduct() != null).collect(Collectors.toList()));
                } else {
                    unitCollectionList.getItems().clear();
                    unassignedUnitsList.getItems().clear();
                    assignedUnitsList.getItems().clear();
                }
            }
        });
        unitCollectionList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<UnitCollection>() {
            @Override
            public void changed(ObservableValue<? extends UnitCollection> observable, UnitCollection oldValue, UnitCollection newValue) {
                if ( newValue != null ) {

                    // show assigned Units
                    assignedUnitsList.getItems().clear();
                    assignedUnitsList.getItems().addAll(newValue.getUnits());

                    // show different assigned units
                    differentAssignedUnitsList.getItems().clear();

                    List<UnitCollection> notSelected = newValue.getProduct()
                            .getUnitCollections()
                            .stream()
                            .filter(uc -> uc != newValue)
                            .collect(Collectors.toList());
                    for (UnitCollection unitCollection : notSelected) {
                        differentAssignedUnitsList.getItems().addAll(unitCollection.getUnits());
                    }

                } else {
                    assignedUnitsList.getItems().clear();
                    unitCollectionList.getItems().clear();
                    differentAssignedUnitsList.getItems().clear();
                }
            }
        });

    }

}
