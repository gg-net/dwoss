package eu.ggnet.dwoss.uniqueunit.ui;

import eu.ggnet.dwoss.rules.ProductGroup;
import eu.ggnet.dwoss.rules.TradeName;
import eu.ggnet.dwoss.uniqueunit.entity.Product;
import eu.ggnet.saft.Ui;
import eu.ggnet.saft.api.ui.FxController;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Defines the displayed products in the table. Handles the filtering of the
 * table.
 *
 * @author lucas.huelsen
 */
public class ProductListController implements Initializable, FxController {

    private static final Logger L = LoggerFactory.getLogger(ProductListController.class);

    // is used to filter the list of products
    private FilteredList<Product> filteredProducts;

    @FXML
    private TableView tableView;

    @FXML
    private TableColumn<Product, String> productId;

    @FXML
    private TableColumn<Product, String> productName;

    @FXML
    private TableColumn<Product, String> productTradeName;

    @FXML
    private TableColumn<Product, String> productGroup;

    @FXML
    private TableColumn<Product, String> productPartNo;

    @FXML
    private TableColumn<Product, String> productPrice;

    @FXML
    private TableColumn<Product, String> productImageId;

    @FXML
    private TableColumn<Product, String> productGtin;

    @FXML
    private TableColumn<Product, String> productEol;

    @FXML
    private ComboBox<TradeName> menuTradeName;

    @FXML
    private ComboBox<ProductGroup> menuProductGroup;

    @FXML
    private CheckBox menuEol;

    @FXML
    private DatePicker eolDatePicker;

    @FXML
    private ProgressBar progressBar;

    @FXML
    /**
     * Filter the displayed products based on the selected TradeName &&
     * ProductGroup && Eol.
     */
    private void onActionFilter(ActionEvent event) {
        filteredProducts.setPredicate(getPredicate());
    }

    @FXML
    /**
     * Reset the TradeName and the ProductGroup filter.
     */
    private void onActionResetFilter(ActionEvent event) {
        menuTradeName.getSelectionModel().clearSelection();
        menuProductGroup.getSelectionModel().clearSelection();
        eolDatePicker.setValue(null);
    }

    @Override
    /**
     * Adding the filters to the combo box. Setting the cell values and the
     * filtered list containing the data.
     */
    public void initialize(URL url, ResourceBundle rb) {

        menuTradeName.getItems().addAll(FXCollections.observableArrayList(TradeName.values()));
        menuProductGroup.getItems().addAll(ProductGroup.values());

        setCellValues();

        ProductTask productsTask = new ProductTask();

        progressBar.progressProperty()
                .bind(productsTask.progressProperty());
        progressBar.visibleProperty()
                .bind(productsTask.runningProperty());

        filteredProducts = new FilteredList<>(productsTask.getPartialResults(), p -> true);
        tableView.setItems(filteredProducts);

        Ui.progress().observe(productsTask);
        Ui.exec(productsTask);
    }

    /**
     * Defining the cell values for each table column.
     */
    private void setCellValues() {

        productId.setCellValueFactory(new PropertyValueFactory<>("id"));
        productName.setCellValueFactory(new PropertyValueFactory<>("name"));
        productTradeName.setCellValueFactory(new PropertyValueFactory<>("tradeName"));
        productGroup.setCellValueFactory(new PropertyValueFactory<>("group"));
        productPartNo.setCellValueFactory(new PropertyValueFactory<>("partNo"));
        productPrice.setCellValueFactory(new PropertyValueFactory<>("prices"));
        productImageId.setCellValueFactory(new PropertyValueFactory<>("imageId"));
        productGtin.setCellValueFactory(new PropertyValueFactory<>("gtin"));
        productEol.setCellValueFactory(new PropertyValueFactory<>("eol"));
    }

    /**
     * Returns the selected TradeName from the filter menu.
     *
     * @return TradeName
     */
    private TradeName getSelectedTradeName() {

        return menuTradeName.getSelectionModel().getSelectedItem();
    }

    /**
     * Returns the selected ProductGroup from the filter menu.
     *
     * @return
     */
    private ProductGroup getSelectedProductGroup() {
        return menuProductGroup.getSelectionModel().getSelectedItem();
    }

    /**
     * Retuns the selected Date from the eolDatePicker. If no date ist selected
     * the current date is used.
     *
     * @return Date.
     */
    private Date getSelectedDate() {
        Date date = new Date();
        if ( eolDatePicker.getValue() != null ) {
            LocalDate localDate = eolDatePicker.getValue();
            Instant instant = Instant.from(localDate.atStartOfDay(ZoneId.systemDefault()));
            date = Date.from(instant);
        }
        return date;
    }

    /**
     * Creates the predicate based on the selected filters.
     *
     * @return Predicate.
     */
    private Predicate<Product> getPredicate() {

        Predicate<Product> onlyEol = product -> !menuEol.isSelected()
                || (menuEol.isSelected() && getSelectedDate().before(product.getEol()));

        onlyEol = onlyEol.and(product -> getSelectedTradeName() == null || product.getTradeName() == getSelectedTradeName());

        onlyEol = onlyEol.and(product -> getSelectedProductGroup() == null || product.getGroup() == getSelectedProductGroup());
        return onlyEol;
    }

}
