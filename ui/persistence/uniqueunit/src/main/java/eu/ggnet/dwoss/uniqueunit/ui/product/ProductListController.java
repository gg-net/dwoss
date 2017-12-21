package eu.ggnet.dwoss.uniqueunit.ui.product;

import eu.ggnet.dwoss.rules.ProductGroup;
import eu.ggnet.dwoss.rules.TradeName;
import eu.ggnet.dwoss.uniqueunit.entity.Product;
import eu.ggnet.saft.api.ui.FxController;

import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.uniqueunit.api.PicoProduct;
import eu.ggnet.dwoss.uniqueunit.ui.ProductTask;
import eu.ggnet.saft.Ui;
import eu.ggnet.saft.api.ui.ClosedListener;
import eu.ggnet.saft.core.ui.FxSaft;

import static javafx.scene.control.SelectionMode.MULTIPLE;

/**
 * Defines the displayed products in the table. Handles the filtering of the
 * table.
 *
 * @author lucas.huelsen
 */
public class ProductListController implements Initializable, FxController, ClosedListener {

    public static final DataFormat dataFormatPicoProduct = new DataFormat("dw/product");

    private static final Logger L = LoggerFactory.getLogger(ProductListController.class);

    private ProductTask productsTask = new ProductTask();

    // is used to filter the list of products
    private FilteredList<Product> filteredProducts;

    @FXML
    private TableView<Product> tableView;

    @FXML
    private TableColumn<Product, Long> productId;

    @FXML
    private TableColumn<Product, String> productName;

    @FXML
    private TableColumn<Product, TradeName> productTradeName;

    @FXML
    private TableColumn<Product, ProductGroup> productGroup;

    @FXML
    private TableColumn<Product, String> productPartNo;

    @FXML
    private TableColumn<Product, Integer> productImageId;

    @FXML
    private TableColumn<Product, Long> productGtin;

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

    @FXML
    /**
     * Reset the TradeName and the ProductGroup filter.
     */
    private void openAssignment(ActionEvent event) {
        Ui.exec(() -> {
            Ui.fxml().show(AssignmentController.class);
        });
    }

    @Override
    /**
     * Adding the filters to the combo box. Setting the cell values and the
     * filtered list containing the data.
     */
    public void initialize(URL url, ResourceBundle rb) {

        tableView.getSelectionModel().setSelectionMode(MULTIPLE);

        menuTradeName.getItems().addAll(FXCollections.observableArrayList(TradeName.values()));
        menuProductGroup.getItems().addAll(ProductGroup.values());

        tableView.setOnDragDetected(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                ArrayList<Product> selectedProducts = new ArrayList<>();
                selectedProducts.addAll(tableView.getSelectionModel().getSelectedItems());
                ArrayList<PicoProduct> selectedPicoProducts = new ArrayList<>();
                if ( selectedProducts.isEmpty() ) return;
                Dragboard db = tableView.startDragAndDrop(TransferMode.ANY);
                ClipboardContent content = new ClipboardContent();
                selectedPicoProducts.addAll(selectedProducts.stream().map(p -> new PicoProduct(p.getId(), p.getName())).collect(Collectors.toList()));
                content.put(dataFormatPicoProduct, selectedPicoProducts);
                db.setContent(content);
                event.consume();
            }
        });

        setCellValues();

        progressBar.progressProperty()
                .bind(productsTask.progressProperty());
        progressBar.visibleProperty()
                .bind(productsTask.runningProperty());

        filteredProducts = new FilteredList<>(productsTask.getPartialResults(), p -> true);

        // filteredList does not allow sorting so it needs to be wrapped in a sortedList
        SortedList<Product> sortedProducts = new SortedList<>(filteredProducts);
        sortedProducts.comparatorProperty().bind(tableView.comparatorProperty());

        tableView.setItems(sortedProducts);

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
        productImageId.setCellValueFactory(new PropertyValueFactory<>("imageId"));
        productGtin.setCellValueFactory(new PropertyValueFactory<>("gtin"));
        productEol.setCellValueFactory(p -> {
            SimpleStringProperty property = new SimpleStringProperty();
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            property.setValue(dateFormat.format(p.getValue().getEol()));
            return property;
        });

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
                || (menuEol.isSelected() && (product.getEol() == null || getSelectedDate().before(product.getEol())));

        onlyEol = onlyEol.and(product -> getSelectedTradeName() == null || product.getTradeName() == getSelectedTradeName());

        onlyEol = onlyEol.and(product -> getSelectedProductGroup() == null || product.getGroup() == getSelectedProductGroup());
        return onlyEol;
    }

    @Override
    public void closed() {
        FxSaft.dispatch(() -> {
            if ( productsTask.isRunning() ) productsTask.cancel();
            return null;
        });
    }
}
