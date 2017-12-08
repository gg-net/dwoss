package eu.ggnet.dwoss.uniqueunit.ui.categoryProduct;

import eu.ggnet.dwoss.uniqueunit.UniqueUnitAgent;
import eu.ggnet.dwoss.uniqueunit.entity.CategoryProduct;
import eu.ggnet.saft.Ui;
import eu.ggnet.saft.api.ui.FxController;
import eu.ggnet.saft.api.ui.Title;
import eu.ggnet.saft.core.Client;
import eu.ggnet.saft.core.authorisation.Guardian;

import javafx.event.ActionEvent;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller for the list containing all CategoryProducts. If a categoryProduct
 * ist selected the edit button opens the editor view for it.
 *
 * @author lucas.huelsen
 */
@Title("KategoryProdukte verwalten")
public class CategoryProductListController implements Initializable, FxController {

    private static final Logger L = LoggerFactory.getLogger(CategoryProductListController.class);

    private FilteredList<CategoryProduct> filteredCategoryProducts;

    @FXML
    private TableColumn<CategoryProduct, String> categoryProductName;

    @FXML
    private TableColumn<CategoryProduct, String> categoryProductSalesChannel;

    @FXML
    private TableColumn<CategoryProduct, String> categoryProductProductList;

    @FXML
    private TableView<CategoryProduct> categoryProductsTableView;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private TextArea categoryProductDescription;

    @FXML
    private BorderPane root;

    @FXML
    /**
     * When selecting a CategoryProduct via mouse click the description int the
     * textarea gets updated.
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
            openEdit(categoryProductsTableView.getSelectionModel().getSelectedItem());
        }
    }

    @Override
    /**
     * Setting the cell values. Binding the Progress bar. Starting the task to
     * fill the filteredList with the categoryProducts.
     */
    public void initialize(URL url, ResourceBundle rb) {
        setCellValues();

        CategoryProductTask categoryProductsTask = new CategoryProductTask();

        progressBar.progressProperty().bind(categoryProductsTask.progressProperty());
        progressBar.visibleProperty().bind(categoryProductsTask.runningProperty());

        filteredCategoryProducts = new FilteredList<>(categoryProductsTask.getPartialResults(), p -> true);
        categoryProductsTableView.setItems(filteredCategoryProducts);

        Ui.exec(categoryProductsTask);
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

    /**
     * Defining the cell values for each table column.
     */
    private void setCellValues() {

        categoryProductName.setCellValueFactory(new PropertyValueFactory<>("name"));
        categoryProductSalesChannel.setCellValueFactory(new PropertyValueFactory<>("salesChannel"));
//        categoryProductProductList.setCellValueFactory((param) -> {
//            return new SimpleStringProperty(param.getValue().getProducts().stream().map(p -> p.getName()).collect(Collectors.joining(", ")));
//        });
    }
}
