/*
 * Copyright (C) 2018 GG-Net GmbH
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
package eu.ggnet.dwoss.uniqueunit.ui.categoryProduct;

import java.net.URL;
import java.util.*;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.uniqueunit.entity.*;
import eu.ggnet.dwoss.uniqueunit.ui.ProductTask;
import eu.ggnet.saft.Ui;
import eu.ggnet.saft.api.ui.ClosedListener;
import eu.ggnet.saft.api.ui.FxController;
import eu.ggnet.saft.core.ui.FxSaft;

/**
 *
 * @author lucas.huelsen
 */
public class ProductAssignmentController implements Initializable, FxController, ClosedListener {

    private static final DataFormat df = new DataFormat("dw/product");

    private static final Logger L = LoggerFactory.getLogger(CategoryProductListController.class);

    private FilteredList<CategoryProduct> filteredCategoryProducts;

    private FilteredList<Product> filteredProducts;

    private ObservableList<Product> observableProducts = FXCollections.observableArrayList();

    private final CategoryProductTask LOADING_CP_TASK = new CategoryProductTask();

    private final ProductTask LOADING_PRODUCTS_TASK = new ProductTask();

    @FXML
    private ListView<CategoryProduct> listCategoryProduct;

    @FXML
    private ListView<Product> listProduct;

    @FXML
    private ListView<Product> listProductWithoutCp;

    @FXML
    private ListView<UnitCollection> listUnitCollection;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        setCellValues();
        dragAndDropHandling();

        listProductWithoutCp.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        filteredCategoryProducts = new FilteredList<>(LOADING_CP_TASK.getPartialResults(), p -> true);

        observableProducts = LOADING_PRODUCTS_TASK.getPartialResults();
        filteredProducts = new FilteredList<>(observableProducts);
        filteredProducts.setPredicate(p -> p.getCategoryProduct() == null);

        listCategoryProduct.setItems(filteredCategoryProducts);
        listProductWithoutCp.setItems(filteredProducts);

        Ui.progress().observe(LOADING_CP_TASK);
        Ui.progress().observe(LOADING_PRODUCTS_TASK);
        Ui.exec(LOADING_CP_TASK);
        Ui.exec(LOADING_PRODUCTS_TASK);

        listCategoryProduct.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<CategoryProduct>() {
            @Override
            public void changed(ObservableValue<? extends CategoryProduct> observable, CategoryProduct oldValue, CategoryProduct newValue) {
                if ( newValue != null ) {
                    listProduct.getItems().clear();
                    listUnitCollection.getItems().clear();
                    listProduct.getItems().addAll(newValue.getProducts());
                }
            }
        });

        listProduct.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Product>() {
            @Override
            public void changed(ObservableValue<? extends Product> observable, Product oldValue, Product newValue) {
                if ( newValue != null ) {
                    listUnitCollection.getItems().clear();
                    listUnitCollection.getItems().addAll(newValue.getUnitCollections());

                }
            }
        });

    }

    private void setCellValues() {
        listProduct.setCellFactory(lv -> new ListCell<Product>() {
            @Override
            public void updateItem(Product p, boolean empty) {
                super.updateItem(p, empty);
                if ( empty ) {
                    setText(null);
                } else {
                    String text = p.getName() + " (" + p.getUnitCollections().size() + ")";
                    setText(text);
                }
            }
        });

        listProductWithoutCp.setCellFactory(lv -> new ListCell<Product>() {
            @Override
            public void updateItem(Product p, boolean empty) {
                super.updateItem(p, empty);
                if ( empty ) {
                    setText(null);
                } else {
                    String text = p.getName() + " (" + p.getUnitCollections().size() + ")";
                    setText(text);
                }
            }
        });

        listUnitCollection.setCellFactory(lv -> new ListCell<UnitCollection>() {
            @Override
            public void updateItem(UnitCollection uc, boolean empty) {
                super.updateItem(uc, empty);
                if ( empty ) {
                    setText(null);
                } else {
                    String text = uc.getNameExtension() + " (" + uc.getUnits().size() + ")";
                    setText(text);
                }
            }
        });

        listCategoryProduct.setCellFactory(lv -> new ListCell<CategoryProduct>() {
            @Override
            public void updateItem(CategoryProduct cp, boolean empty) {
                super.updateItem(cp, empty);
                if ( empty ) {
                    setText(null);
                } else {
                    String text = cp.getName() + " (" + cp.getProducts().size() + ")";
                    setText(text);
                }
            }
        });

    }

    private void dragAndDropHandling() {

        listProductWithoutCp.setOnDragDetected(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                ArrayList<Product> selectedProducts = new ArrayList<>();
                selectedProducts.addAll(listProductWithoutCp.getSelectionModel().getSelectedItems());
                if ( selectedProducts.isEmpty() ) return;
                Dragboard db = listProductWithoutCp.startDragAndDrop(TransferMode.ANY);
                ClipboardContent content = new ClipboardContent();
                content.put(df, selectedProducts);
                db.setContent(content);
                L.info("DnD of {} Products started", selectedProducts.size());
                event.consume();
            }
        });

        listProduct.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                if ( event.getGestureSource() != listProduct && event.getDragboard().hasContent(df) ) {
                    event.acceptTransferModes(TransferMode.ANY);
                }
                event.consume();
            }
        });

        listProduct.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(final DragEvent event) {
                Dragboard db = event.getDragboard();
                boolean success = false;
                if ( db.hasContent(df) ) {
                    final List<Product> draggedProducts = (List<Product>)db.getContent(df);
                    CategoryProduct selected = listCategoryProduct.getSelectionModel().getSelectedItem();
                    for (Product p : draggedProducts) {
                        selected.getProducts().add(p);
                        listProduct.getItems().add(p);
                        observableProducts.remove(p);
                    }
                    success = true;
                }
                event.setDropCompleted(success);
                event.consume();
            }
        });
    }

    @Override
    public void closed() {
        FxSaft.dispatch(() -> {
            if ( LOADING_CP_TASK.isRunning() ) LOADING_CP_TASK.cancel();
            if ( LOADING_PRODUCTS_TASK.isRunning() ) LOADING_PRODUCTS_TASK.cancel();
            return null;
        });
    }
}
