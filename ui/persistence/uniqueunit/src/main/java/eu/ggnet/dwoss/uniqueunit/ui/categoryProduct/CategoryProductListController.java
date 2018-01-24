package eu.ggnet.dwoss.uniqueunit.ui.categoryProduct;

import java.net.URL;
import java.util.*;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.*;
import javafx.scene.layout.BorderPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.uniqueunit.UniqueUnitAgent;
import eu.ggnet.dwoss.uniqueunit.entity.CategoryProduct;
import eu.ggnet.dwoss.uniqueunit.entity.Product;
import eu.ggnet.dwoss.uniqueunit.ui.ProductTask;
import eu.ggnet.saft.Client;
import eu.ggnet.saft.Ui;
import eu.ggnet.saft.api.ui.*;
import eu.ggnet.saft.core.auth.Guardian;
import eu.ggnet.saft.core.ui.FxSaft;

/**
 * Controller for the list containing all CategoryProducts. If a categoryProduct
 * ist selected the edit button opens the editor view for it.
 *
 * @author lucas.huelsen
 */
@Title("KategoryProdukte verwalten")
public class CategoryProductListController implements Initializable, FxController, ClosedListener {

    private static final DataFormat df = new DataFormat("dw/product");

    private static final Logger L = LoggerFactory.getLogger(CategoryProductListController.class);

    private FilteredList<CategoryProduct> filteredCategoryProducts;

    private FilteredList<Product> filteredProducts;

    private ObservableList<Product> observableProducts = FXCollections.observableArrayList();

    private final CategoryProductTask LOADING_CP_TASK = new CategoryProductTask();

    private final ProductTask LOADING_PRODUCTS_TASK = new ProductTask();

    @FXML
    private TableColumn<CategoryProduct, String> categoryProductName;

    @FXML
    private TableColumn<CategoryProduct, String> categoryProductSalesChannel;

    @FXML
    private TableView<CategoryProduct> categoryProductsTableView;

    @FXML
    private ListView<Product> productsList;

    @FXML
    private ListView<Product> productsWithoutCpList;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private TextArea categoryProductDescription;

    @FXML
    private BorderPane root;

    @FXML
    /**
     * When selecting a CategoryProduct via mouse click the description int the
     * textarea gets updated. .
     */
    private void onMouseClickedTableView(MouseEvent event) {
        if ( categoryProductsTableView.getSelectionModel().getSelectedItem() != null ) {
            categoryProductDescription.setText(categoryProductsTableView.getSelectionModel().getSelectedItem().getDescription());
        }
    }

    @FXML
    /**
     * When selecting a CategoryProduct via key pressed the description in the
     * textarea gets updated.
     */
    private void onKeyPressedTableView(KeyEvent event) {
        if ( categoryProductsTableView.getSelectionModel().getSelectedItem() != null ) {
            categoryProductDescription.setText(categoryProductsTableView.getSelectionModel().getSelectedItem().getDescription());
        }
    }

    @FXML
    /**
     * Opens the editor view and allows to create a new CategoryProduct.
     */
    private void addCategoryProduct() {
        openEdit(new CategoryProduct());
    }

    @FXML
    /**
     * Removes the selected CategoryProduct.Before deleting the CategoryProduct
     * a confirmation dialog is shown. If no CategoryProduct is selected the
     * view won't open.
     */
    private void removeCategoryProduct() {
        CategoryProduct cp = categoryProductsTableView.getSelectionModel().getSelectedItem();
        if ( cp != null ) {
            Ui.exec(() -> {
                Ui.dialog().parent(root).eval(() -> new Alert(Alert.AlertType.WARNING, "Soll das CategoryProduct " + cp.getName() + " wirklich gelöscht werden?!", ButtonType.YES, ButtonType.NO))
                        .filter((type) -> type.equals(ButtonType.YES))
                        .map(i -> Client.lookup(UniqueUnitAgent.class).deleteCategoryProduct(cp.getId()))
                        .filter(Ui.failure()::handle)
                        .ifPresent((t) -> filteredCategoryProducts.getSource().remove(categoryProductsTableView.getSelectionModel().getSelectedItem()));
            });
        }

    }

    @FXML
    /**
     * Opens the editor view for the selected CategoryProduct. All values from
     * the selected CategoryProduct will be set in the editor view. If no
     * CategoryProduct is selected the view won't open.
     */
    private void editCategoryProduct(ActionEvent event) {
        if ( categoryProductsTableView.getSelectionModel().getSelectedItem() != null ) {
            CategoryProduct cp = categoryProductsTableView.getSelectionModel().getSelectedItem();
            openEdit(cp);
        }
    }

    @Override
    /**
     * Setting the cell values. Binding the Progress bar. Starting the task to
     * fill the filteredList with the categoryProducts.
     */
    public void initialize(URL url, ResourceBundle rb) {

        productsList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        productsWithoutCpList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        setCellValues();
        dragAndDropHandling();

        categoryProductsTableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<CategoryProduct>() {
            @Override
            public void changed(ObservableValue<? extends CategoryProduct> observable, CategoryProduct oldValue, CategoryProduct newValue) {
                if ( newValue != null ) {
                    productsList.getItems().clear();
                    productsList.getItems().addAll(newValue.getProducts());
                } else {
                    productsList.getItems().clear();
                }
            }
        });

        observableProducts = LOADING_PRODUCTS_TASK.getPartialResults();

        filteredProducts = new FilteredList<>(observableProducts);
        filteredProducts.setPredicate(p -> p.getCategoryProduct() == null);
        productsWithoutCpList.setItems(filteredProducts);

        filteredCategoryProducts = new FilteredList<>(LOADING_CP_TASK.getPartialResults(), p -> true);
        categoryProductsTableView.setItems(filteredCategoryProducts);

        progressBar.progressProperty().bind(LOADING_CP_TASK.progressProperty());
        progressBar.visibleProperty().bind(LOADING_CP_TASK.runningProperty());
        Ui.progress().observe(LOADING_CP_TASK);
        Ui.progress().observe(LOADING_PRODUCTS_TASK);
        Ui.exec(LOADING_CP_TASK);
        Ui.exec(LOADING_PRODUCTS_TASK);
    }

    /**
     * Opens the editor window for the definded CategoryProduct.
     *
     * @param cp the category product to be updated
     */
    private void openEdit(CategoryProduct cp) {
        Ui.exec(() -> {
            Ui.fxml().parent(root).eval(() -> cp, CategoryProductEditorController.class)
                    .map(dto -> Client.lookup(UniqueUnitAgent.class).createOrUpdate(dto, Client.lookup(Guardian.class).getUsername()))
                    .ifPresent(this::updateList);
        });
    }

    /**
     * Updates the tableView with the new CategoryProduct.
     * <p>
     */
    private void updateList(CategoryProduct newCp) {
        ObservableList<CategoryProduct> source = (ObservableList<CategoryProduct>)filteredCategoryProducts.getSource();
        if ( source.contains(newCp) ) {
            source.set(source.indexOf(newCp), newCp);
        } else {
            source.add(newCp);
        }
        categoryProductsTableView.getSelectionModel().select(newCp);
    }

    private void dragAndDropHandling() {

        productsWithoutCpList.setOnDragDetected(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                ArrayList<Product> selectedProducts = new ArrayList<>();
                selectedProducts.addAll(productsWithoutCpList.getSelectionModel().getSelectedItems());
                if ( selectedProducts.isEmpty() || categoryProductsTableView.getSelectionModel().getSelectedItem() == null ) return;
                Dragboard db = productsWithoutCpList.startDragAndDrop(TransferMode.ANY);
                ClipboardContent content = new ClipboardContent();
                content.put(df, selectedProducts);
                db.setContent(content);
                L.info("DnD of {} Products started", selectedProducts.size());
                event.consume();
            }
        });

        productsList.setOnDragDetected(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                ArrayList<Product> selectedProducts = new ArrayList<>();
                selectedProducts.addAll(productsList.getSelectionModel().getSelectedItems());
                if ( selectedProducts.isEmpty() ) return;
                Dragboard db = productsList.startDragAndDrop(TransferMode.ANY);
                ClipboardContent content = new ClipboardContent();
                content.put(df, selectedProducts);
                db.setContent(content);
                L.info("DnD of {} Products started", selectedProducts.size());
                event.consume();
            }
        });

        productsList.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                if ( event.getGestureSource() != productsList && event.getDragboard().hasContent(df) ) {
                    event.acceptTransferModes(TransferMode.ANY);
                }
                event.consume();
            }
        });

        productsWithoutCpList.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                if ( event.getGestureSource() != productsWithoutCpList && event.getDragboard().hasContent(df) ) {
                    event.acceptTransferModes(TransferMode.ANY);
                }
                event.consume();
            }
        });

        productsList.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(final DragEvent event) {
                Dragboard db = event.getDragboard();
                boolean success = false;
                if ( db.hasContent(df) ) {
                    final List<Product> draggedProducts = (List<Product>)db.getContent(df);
                    CategoryProduct selected = categoryProductsTableView.getSelectionModel().getSelectedItem();
                    for (Product p : draggedProducts) {
                        selected.getProducts().add(p);
                        productsList.getItems().add(p);
                        observableProducts.remove(p);
                    }
                    success = true;
                }
                event.setDropCompleted(success);
                event.consume();
            }
        });

        productsWithoutCpList.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(final DragEvent event) {
                Dragboard db = event.getDragboard();
                boolean success = false;
                if ( db.hasContent(df) ) {
                    final List<Product> draggedProducts = (List<Product>)db.getContent(df);
                    CategoryProduct selected = categoryProductsTableView.getSelectionModel().getSelectedItem();
                    for (Product p : draggedProducts) {
                        selected.getProducts().remove(p);
                        productsList.getItems().remove(p);
                        observableProducts.add(p);
                    }
                    success = true;
                }
                event.setDropCompleted(success);
                event.consume();
            }
        });

    }

    /**
     * Defining the cell values for each table column.
     */
    private void setCellValues() {

        categoryProductName.setCellValueFactory(new PropertyValueFactory<>("name"));
        categoryProductSalesChannel.setCellValueFactory(new PropertyValueFactory<>("salesChannel"));

        productsList.setCellFactory(lv -> new ListCell<Product>() {
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

        productsWithoutCpList.setCellFactory(lv -> new ListCell<Product>() {
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
            if ( LOADING_CP_TASK.isRunning() ) LOADING_CP_TASK.cancel();
            if ( LOADING_PRODUCTS_TASK.isRunning() ) LOADING_PRODUCTS_TASK.cancel();
            return null;
        });
    }
}
