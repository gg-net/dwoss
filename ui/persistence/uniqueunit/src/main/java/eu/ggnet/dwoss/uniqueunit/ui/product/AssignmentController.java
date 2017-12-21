package eu.ggnet.dwoss.uniqueunit.ui.product;

import eu.ggnet.dwoss.uniqueunit.entity.Product;
import eu.ggnet.saft.api.ui.FxController;

import java.net.URL;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.rules.ProductGroup;
import eu.ggnet.dwoss.uniqueunit.api.PicoUnit;
import eu.ggnet.dwoss.uniqueunit.entity.*;
import eu.ggnet.dwoss.uniqueunit.entity.UniqueUnit.Identifier;
import eu.ggnet.dwoss.uniqueunit.ui.ProductTask;
import eu.ggnet.saft.Ui;
import eu.ggnet.saft.api.ui.*;
import eu.ggnet.saft.core.ui.FxSaft;

/**
 * Defines the displayed products in the table. Handles the filtering of the
 * table.
 *
 * @author lucas.huelsen
 */
@Title("Zuweisungsansicht")
public class AssignmentController implements Initializable, FxController, ClosedListener {

    public static final DataFormat df = new DataFormat("dw/uniqueUnit");

    private static final Logger L = LoggerFactory.getLogger(AssignmentController.class);

    private final ProductTask productsTask = new ProductTask();

    // is used to filter the list of products
    private FilteredList<Product> filteredProducts;

    @FXML
    private ListView<Product> productList;

    @FXML
    private ListView<UnitCollection> unitCollectionList;

    @FXML
    private ListView<PicoUnit> assignedUnitsList;

    @FXML
    private ListView<PicoUnit> unassignedUnitsList;

    @FXML
    private ListView<PicoUnit> differentAssignedUnitsList;

    @FXML
    private ComboBox<ProductGroup> productGroupFilter;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private void filterProductGroup(ActionEvent event) {

        Predicate<Product> predicate = product
                -> productGroupFilter.getSelectionModel().getSelectedItem() == null
                || product.getGroup() == productGroupFilter.getSelectionModel().getSelectedItem();

        filteredProducts.setPredicate(predicate);
    }

    @FXML
    private void removeFilter(ActionEvent event) {
        productGroupFilter.getSelectionModel().clearSelection();
        //        System.out.println(differentAssignedUnitsList.setItems(productList.getItems().stream().filter(p -> p != productList.getSelectionModel().getSelectedItem()).collect(Collectors.toList())));
        //        productList
        //                .getItems()
        //                .stream()
        //                .filter(p -> p != productList.getSelectionModel().getSelectedItem())
        //                .forEach(s -> System.out.println(s));
        ;
    }

    @Override
    /**
     * Adding the filters to the combo box. Setting the cell values and the
     * filtered list containing the data.
     */
    public void initialize(URL url, ResourceBundle rb) {

        dragAndDropHandling();

        productGroupFilter.getItems().addAll(ProductGroup.values());

        setCellValues();

        progressBar.progressProperty()
                .bind(productsTask.progressProperty());
        progressBar.visibleProperty()
                .bind(productsTask.runningProperty());

        filteredProducts = new FilteredList<>(productsTask.getPartialResults(), p -> true);

        productList.setItems(filteredProducts);

        productList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Product>() {
            @Override
            public void changed(ObservableValue<? extends Product> observable, Product oldValue, Product newValue) {
                if ( newValue != null ) {
                    unitCollectionList.setItems(FXCollections.observableArrayList(newValue.getUnitCollections()));
                } else {
                    unitCollectionList.setItems(FXCollections.emptyObservableList());
                }
            }
        });

        unitCollectionList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<UnitCollection>() {
            @Override
            public void changed(ObservableValue<? extends UnitCollection> observable, UnitCollection oldValue, UnitCollection newValue) {
                if ( newValue != null ) {

                    unassignedUnitsList.getItems().clear();

                    List<UniqueUnit> unassigned = newValue.getUnits().stream()
                            .filter(u -> u.getUnitCollection() != null)
                            .collect(Collectors.toList());

                    unassigned.stream().forEach(u -> unassignedUnitsList.getItems().add(new PicoUnit(u.getId(), (String)u.getIdentifier(Identifier.SERIAL))));

                } else {
                    unitCollectionList.setItems(FXCollections.emptyObservableList());
                }
            }
        });

        Ui.progress().observe(productsTask);
        Ui.exec(productsTask);
    }

    private void setCellValues() {

        productList.setCellFactory(lv -> new ListCell<Product>() {
            @Override
            public void updateItem(Product p, boolean empty) {
                super.updateItem(p, empty);
                if ( empty ) {
                    setText(null);
                } else {
                    String text = p.getName();
                    setText(text);
                }
            }
        });
    }

    @Override
    public void closed() {
        FxSaft.dispatch(() -> {
            if ( productsTask.isRunning() ) productsTask.cancel();
            return null;
        });
    }

    private void dragAndDropHandling() {

//        unassignedUnitsList.setOnDragDetected(new EventHandler<MouseEvent>() {
//            @Override
//            public void handle(MouseEvent event) {
//                UniqueUnit selectedUnit = unassignedUnitsList.getSelectionModel().getSelectedItem();
//                if ( selectedUnit == null ) return;
//                Dragboard db = unassignedUnitsList.startDragAndDrop(TransferMode.ANY);
//                ClipboardContent content = new ClipboardContent();
//                content.put(df, selectedUnit);
//                db.setContent(content);
//                L.info("DnD of {} started", selectedUnit.getId());
//                event.consume();
//            }
//        });
//
//        // accept drag Over for the products list
//        assignedUnitsList.setOnDragOver(new EventHandler<DragEvent>() {
//            @Override
//            public void handle(DragEvent event) {
//                if ( event.getGestureSource() != assignedUnitsList && event.getDragboard().hasContent(df) ) {
//                    event.acceptTransferModes(TransferMode.ANY);
//                }
//                event.consume();
//            }
//        });
//
//        // handle dropped objets onto the products list
//        assignedUnitsList.setOnDragDropped(new EventHandler<DragEvent>() {
//            @Override
//            public void handle(DragEvent event) {
//                Dragboard db = event.getDragboard();
//                boolean success = false;
//                if ( db.hasContent(df) ) {
//                    //add Unit
//                    success = true;
//                }
//                event.setDropCompleted(success);
//                event.consume();
//            }
//        });
    }
}
