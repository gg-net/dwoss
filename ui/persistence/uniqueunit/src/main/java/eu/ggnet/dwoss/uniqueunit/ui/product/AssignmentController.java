package eu.ggnet.dwoss.uniqueunit.ui.product;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.*;
import javafx.scene.layout.BorderPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.stock.api.StockApi;
import eu.ggnet.dwoss.uniqueunit.UniqueUnitAgent;
import eu.ggnet.dwoss.uniqueunit.api.PicoProduct;
import eu.ggnet.dwoss.uniqueunit.api.PicoUnit;
import eu.ggnet.dwoss.uniqueunit.entity.*;
import eu.ggnet.saft.Dl;
import eu.ggnet.saft.Ui;
import eu.ggnet.saft.api.Reply;
import eu.ggnet.saft.api.ui.FxController;
import eu.ggnet.saft.api.ui.Title;
import eu.ggnet.saft.core.auth.Guardian;

import static javafx.scene.control.SelectionMode.MULTIPLE;

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

    @FXML
    private BorderPane root;

    @FXML
    private void assignUnit(ActionEvent event) {
        List<UniqueUnit> selected = unassignedUnitsList.getSelectionModel().getSelectedItems();
        UnitCollection selectedCollection = unitCollectionList.getSelectionModel().getSelectedItem();
        if ( !selected.isEmpty() ) {

            for (UniqueUnit uu : selected) {
                Dl.remote().lookup(UniqueUnitAgent.class).addToUnitCollection(new PicoUnit(uu.getId(), "RefurbishedId=" + uu.getRefurbishId()), selectedCollection.getId());
                assignedUnitsList.getItems().add(uu);
                unassignedUnitsList.getItems().remove(uu);
            }
            unassignedUnitsList.getSelectionModel().clearSelection();
        }
    }

    @FXML
    private void unassignUnit(ActionEvent event) {
        List<UniqueUnit> selected = assignedUnitsList.getSelectionModel().getSelectedItems();
        if ( !selected.isEmpty() ) {

            for (UniqueUnit uu : selected) {

                Optional.of(Dl.remote().lookup(UniqueUnitAgent.class).unsetUnitCollection(new PicoUnit(uu.getId(), "RefurbishedId=" + uu.getRefurbishId())))
                        .filter(r -> {
                            return Ui.failure().handle(r);
                        })
                        .ifPresent(c -> {
                            unassignedUnitsList.getItems().add(uu);
                            assignedUnitsList.getItems().remove(uu);
                        });
            }
            unassignedUnitsList.getSelectionModel().clearSelection();
        }
    }

    @FXML
    private void addUnitCollection() {
        final PicoProduct selectedProduct = productList.getSelectionModel().getSelectedItem();
        if ( selectedProduct == null ) return;
        Ui.exec(() -> {
            Ui.build(root).fxml().eval(() -> new UnitCollection(), UnitCollectionEditorController.class)
                    .opt()
                    .map(dto -> Dl.remote().lookup(UniqueUnitAgent.class).createOnProduct(selectedProduct.getId(), dto, Dl.local().lookup(Guardian.class).getUsername()))
                    .filter(Ui.failure()::handle)
                    .map(Reply::getPayload)
                    .ifPresent(uc -> {
                        unitCollectionList.getItems().add(uc);
                    });
        });
    }

    @FXML
    private void editUnitCollection() {
        UnitCollection selectedUnitCollection = unitCollectionList.getSelectionModel().getSelectedItem();
        int indexOf = unitCollectionList.getItems().indexOf(selectedUnitCollection);
        if ( selectedUnitCollection == null ) return;
        Ui.exec(() -> {
            Ui.build(root).fxml().eval(() -> new UnitCollection(), UnitCollectionEditorController.class)
                    .opt()
                    .map(dto -> Dl.remote().lookup(UniqueUnitAgent.class).update(dto, Dl.local().lookup(Guardian.class).getUsername()))
                    .filter(Ui.failure()::handle)
                    .map(Reply::getPayload)
                    .ifPresent(uc -> {
                        unitCollectionList.getItems().set(indexOf, uc);
                    });
        });
    }

    @Override
    /**
     * Drag and Drop handling.
     * CellValueFactories
     * ActionListener
     */
    public void initialize(URL url, ResourceBundle rb) {

        unassignedUnitsList.getSelectionModel().setSelectionMode(MULTIPLE);
        assignedUnitsList.getSelectionModel().setSelectionMode(MULTIPLE);

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
                    Product dbProduct = Dl.remote().lookup(UniqueUnitAgent.class).findByIdEager(Product.class, p.getId());
                    String text = p.getShortDescription() + " (" + dbProduct.getUnitCollections().size() + ")";
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
                    UnitCollection dbCollection = Dl.remote().lookup(UniqueUnitAgent.class).findByIdEager(UnitCollection.class, uc.getId());
                    String text = uc.getNameExtension() + " (" + dbCollection.getUnits().size() + ")";
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
                ArrayList<UniqueUnit> selected = new ArrayList<>();
                selected.addAll(unassignedUnitsList.getSelectionModel().getSelectedItems());
                if ( selected.isEmpty() || unitCollectionList.getSelectionModel().getSelectedItem() == null ) return;
                Dragboard db = unassignedUnitsList.startDragAndDrop(TransferMode.ANY);
                ClipboardContent content = new ClipboardContent();
                content.put(df, selected);
                db.setContent(content);
                L.info("DnD of {} Units started", selected.size());
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
                if ( db.hasContent(df) ) {
                    List<UniqueUnit> dragged = (List<UniqueUnit>)db.getContent(df);
                    for (UniqueUnit draggedUnit : dragged) {
                        Optional.of(Dl.remote().lookup(UniqueUnitAgent.class).unsetUnitCollection(new PicoUnit(draggedUnit.getId(), "RefurbishedId=" + draggedUnit.getRefurbishId())))
                                .filter(r -> {
                                    if ( !r.hasSucceded() ) event.setDropCompleted(false);
                                    return Ui.failure().handle(r);
                                })
                                .ifPresent(c -> {
                                    unassignedUnitsList.getItems().add(draggedUnit);
                                    assignedUnitsList.getItems().remove(draggedUnit);
                                });
                    }
                    event.setDropCompleted(true);
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

                List<UniqueUnit> selected = new ArrayList<>();
                selected.addAll(assignedUnitsList.getSelectionModel().getSelectedItems());
                if ( selected.isEmpty() ) return;
                Dragboard db = assignedUnitsList.startDragAndDrop(TransferMode.ANY);
                ClipboardContent content = new ClipboardContent();
                content.put(df, selected);
                db.setContent(content);
                L.info("DnD of {} Units started", selected.size());
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
                if ( db.hasContent(df) ) {
                    List<UniqueUnit> dragged = (List<UniqueUnit>)db.getContent(df);
                    for (UniqueUnit draggedUnit : dragged) {
                        UnitCollection selectedCollection = unitCollectionList.getSelectionModel().getSelectedItem();
                        Optional.of(Dl.remote().lookup(UniqueUnitAgent.class).addToUnitCollection(new PicoUnit(draggedUnit.getId(), "RefurbishedId=" + draggedUnit.getRefurbishId()), selectedCollection.getId()))
                                .filter(r -> {
                                    if ( !r.hasSucceded() ) event.setDropCompleted(false);
                                    return Ui.failure().handle(r);
                                })
                                .ifPresent(c -> {
                                    assignedUnitsList.getItems().add(draggedUnit);
                                    unassignedUnitsList.getItems().remove(draggedUnit);
                                    event.setDropCompleted(true);
                                });
                    }
                    event.setDropCompleted(true);
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
                if ( event.getGestureSource() != productList && event.getDragboard().hasContent(ProductListController.PICO_PRODUCT_DATA_FORMAT) ) {
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
                if ( db.hasContent(ProductListController.PICO_PRODUCT_DATA_FORMAT) ) {
                    ArrayList<PicoProduct> products = (ArrayList<PicoProduct>)db.getContent(ProductListController.PICO_PRODUCT_DATA_FORMAT);
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
                    Product product = Dl.remote().lookup(UniqueUnitAgent.class).findByIdEager(Product.class, newValue.getId());
                    // Remove all non available units. This is by spaggetiemonster not good, but lets see it work.
                    Dl.remote().optional(StockApi.class).ifPresent(sapi -> {
                        List<PicoUnit> available = sapi.filterAvailable(product.getUniqueUnits().stream().map(UniqueUnit::toPicoUnit).collect(Collectors.toList()));
                        for (UniqueUnit uu : new ArrayList<>(product.getUniqueUnits())) {
                            if ( !available.contains(uu.toPicoUnit()) ) {
                                uu.setProduct(null);
                                uu.setUnitCollection(null);
                            }
                        }
                    });

                    //Fill the list with UnitCollections of the Product
                    unitCollectionList.getItems().clear();
                    unitCollectionList.setItems(FXCollections.observableArrayList(product.getUnitCollections()));

                    // show unassignedUnits
                    unassignedUnitsList.getItems().clear();
                    unassignedUnitsList.getItems().addAll(product
                            .getUniqueUnits()
                            .stream()
                            .filter(u -> u.getUnitCollection() != null)
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
