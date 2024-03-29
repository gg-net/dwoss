package eu.ggnet.dwoss.uniqueunit.ui.product;

import java.net.URL;
import java.time.*;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.function.Predicate;

import jakarta.inject.Inject;

import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.core.common.values.ProductGroup;
import eu.ggnet.dwoss.core.common.values.tradename.TradeName;
import eu.ggnet.dwoss.core.system.util.Utils;
import eu.ggnet.dwoss.core.widget.Progressor;
import eu.ggnet.dwoss.uniqueunit.ee.entity.Product;
import eu.ggnet.dwoss.uniqueunit.ui.ProductTask;
import eu.ggnet.saft.core.Saft;
import eu.ggnet.saft.core.ui.Bind;
import eu.ggnet.saft.core.ui.FxController;

import jakarta.enterprise.context.Dependent;

import static eu.ggnet.saft.core.ui.Bind.Type.SHOWING;
import static javafx.scene.control.SelectionMode.MULTIPLE;

/**
 * Defines the displayed products in the table. Handles the filtering of the
 * table.
 *
 * @author lucas.huelsen
 */
@Dependent
public class ProductListController implements Initializable, FxController {

    private static final Logger L = LoggerFactory.getLogger(ProductListController.class);

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
    private Button editButton;

    @Inject
    private Saft saft;

    @Inject
    private ProductTask productTask;

    @Bind(SHOWING)
    private final BooleanProperty showingProperty = new SimpleBooleanProperty();

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
    private void openAssignment(ActionEvent event) {
        // TODO : Remove me
    }

    /**
     * Adding the filters to the combo box. Setting the cell values and the
     * filtered list containing the data.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        tableView.getSelectionModel().setSelectionMode(MULTIPLE);

        menuTradeName.getItems().addAll(FXCollections.observableArrayList(TradeName.values()));
        menuProductGroup.getItems().addAll(ProductGroup.values());
//      TODO: Was for DragNDrop. Removed PicoProduct from API, so ....
//        tableView.setOnDragDetected((MouseEvent event) -> {
//            ArrayList<Product> selectedProducts = new ArrayList<>();
//            selectedProducts.addAll(tableView.getSelectionModel().getSelectedItems());
//            ArrayList<PicoProduct> selectedPicoProducts = new ArrayList<>();
//            if ( selectedProducts.isEmpty() ) return;
//            Dragboard db = tableView.startDragAndDrop(TransferMode.ANY);
//            ClipboardContent content = new ClipboardContent();
//            selectedPicoProducts.addAll(selectedProducts.stream().map(p -> new PicoProduct(p.getId(), p.getName())).collect(Collectors.toList()));
//            content.put(PICO_PRODUCT_DATA_FORMAT, selectedPicoProducts);
//            db.setContent(content);
//            event.consume();
//        });

        setCellValues();

        progressBar.progressProperty()
                .bind(productTask.progressProperty());
        progressBar.visibleProperty()
                .bind(productTask.runningProperty());

        filteredProducts = new FilteredList<>(productTask.getPartialResults(), p -> true);

        // filteredList does not allow sorting so it needs to be wrapped in a sortedList
        SortedList<Product> sortedProducts = new SortedList<>(filteredProducts);
        sortedProducts.comparatorProperty().bind(tableView.comparatorProperty());

        tableView.setItems(sortedProducts);
        editButton.disableProperty().bind(tableView.getSelectionModel().selectedItemProperty().isNull());

        Progressor.global().submit(productTask);

        showingProperty.addListener((ov, o, n) -> {
            Platform.runLater(() -> {
                if ( !n && productTask.isRunning() ) productTask.cancel();
            });
        });
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
            if ( p.getValue().getEol() != null ) property.setValue(Utils.ISO_DATE.format(p.getValue().getEol()));
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

    @FXML
    private void create() {
        saft.build(tableView).alert("Not yet implemented");
    }

    @FXML
    private void edit() {
        saft.build(tableView).alert("Not yet implemented");
    }
}
