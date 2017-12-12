/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.ggnet.dwoss.uniqueunit.ui.categoryProduct;

import eu.ggnet.dwoss.uniqueunit.assist.CategoryProductDto;
import eu.ggnet.dwoss.rules.SalesChannel;
import eu.ggnet.dwoss.uniqueunit.api.PicoProduct;
import eu.ggnet.dwoss.uniqueunit.entity.CategoryProduct;
import eu.ggnet.dwoss.uniqueunit.entity.PriceType;
import eu.ggnet.dwoss.uniqueunit.ui.product.ProductListController;
import eu.ggnet.saft.Ui;
import eu.ggnet.saft.api.ui.FxController;
import eu.ggnet.saft.api.ui.ResultProducer;
import eu.ggnet.saft.api.ui.Title;
import eu.ggnet.saft.core.Alert;
import eu.ggnet.saft.core.UiAlert;

import java.net.URL;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.util.Callback;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller class for the editor view of a categoryProduct. Allows the user to
 * change all values of the categoryProduct.
 *
 * @author lucas.huelsen
 */
@Title("KategoieProdukt bearbeiten")
public class CategoryProductEditorController implements Initializable, FxController, Consumer<CategoryProduct>, ResultProducer<CategoryProductDto> {

    private static final Logger L = LoggerFactory.getLogger(CategoryProductEditorController.class);

    private final Pattern decimalPattern = Pattern.compile("-?\\d*(\\,\\d{0,2})?");

    private CategoryProductFx categoryProductFx;

    private CategoryProductDto categoryProductDto;

    @FXML
    private TextField name;

    @FXML
    private TextArea description;

    @FXML
    private ChoiceBox<SalesChannel> salesChannel;

    @FXML
    private ListView<Entry<PriceType, Double>> listViewPrices;

    @FXML
    private ListView<PicoProduct> listViewProducts;

    @FXML
    private ChoiceBox<PriceType> priceType;

    @FXML
    private TextField priceInput;

    @FXML
    private Button deleteProduct;

    @FXML
    private Button deletePrice;

    @FXML
    /**
     * Open a Listview containing all Products. From this view products can be
     * added to the listViewProducts via dnd.
     */
    private void addProductsDnD(ActionEvent event) {

        Ui.exec(() -> {
            Ui.fxml().show(ProductListController.class);
        });
    }

    @FXML
    /**
     * Removes the selected Product from the CategoryProduct.
     */
    private void removeProduct(ActionEvent event) {
        if ( listViewProducts.getSelectionModel().getSelectedItem() != null ) {
            L.info("Removed Product{}", listViewProducts.getSelectionModel().getSelectedItem());
            listViewProducts.getItems().remove(listViewProducts.getSelectionModel().getSelectedItem());
            L.info("Products {}", categoryProductFx.getProductsProperty());
        }
    }

    @FXML
    /**
     * Add a price to the CategoryProduct based on the selected PriceType and
     * the value in priceInput. Both values must be set to be able to add a
     * price.
     */
    private void addPrice(ActionEvent event) {

        if ( priceType.getSelectionModel().getSelectedItem() != null && !priceInput.getText().isEmpty() ) {

            categoryProductFx.getPricesProperty().put(priceType.getSelectionModel().getSelectedItem(), Double.parseDouble(priceInput.getText().replace(",", ".")));
            L.info("added Price {}={}", priceType.getSelectionModel().getSelectedItem(), Double.parseDouble(priceInput.getText().replace(",", ".")));
        }
    }

    @FXML
    /**
     * Removes a Price from the CategoryProduct. A remove simply means setting
     * the value to 0.
     */
    private void removePrice(ActionEvent event) {
        if ( listViewPrices.getSelectionModel().getSelectedItem() != null ) {
            L.info("Removed Price {}", listViewPrices.getSelectionModel().getSelectedItem());
            categoryProductFx.getPricesProperty().put(listViewPrices.getSelectionModel().getSelectedItem().getKey(), 0d);
            L.info("All Prices {}", categoryProductFx.getPricesProperty().entrySet());
        }
    }

    @FXML
    /**
     * Write all the changes to the categoryProductDto and return it as result.
     * At least the name field must be set to save the categoryProduct.
     */
    private void save(ActionEvent event) {
        categoryProductDto = new CategoryProductDto();

        if ( StringUtils.isBlank(name.getText()) ) {
            Alert.message("Es muss ein Name gesetzt werden").show(UiAlert.Type.WARNING);
            return;
        }

        categoryProductDto.setId(categoryProductFx.getId());
        categoryProductDto.setName(categoryProductFx.getNameProperty().get());
        categoryProductDto.setDescription(categoryProductFx.getDescriptionProperty().get());
        categoryProductDto.setSalesChannel(categoryProductFx.getSalesChannelProperty().get());
        categoryProductDto.setPrices(new HashMap<>(categoryProductFx.getPricesProperty()));
        categoryProductDto.setProducts(new ArrayList<>(categoryProductFx.getProductsProperty()));

        Ui.closeWindowOf(name);
    }

    @FXML
    /**
     * Close the Editor window and discard all changes.
     */
    private void cancel(ActionEvent event) {

        Ui.closeWindowOf(name);
    }

    @Override
    /**
     * Adding values for the choice boxes. Set Cell Factory for picoProducts.
     * Handle dnd for the productsListView. Add listeners to enable the delete
     * buttons only if a selection is made. Set a formatter for the priceInput
     * field.
     */
    public void initialize(URL location, ResourceBundle resources) {

        salesChannel.getItems().addAll(SalesChannel.values());
        priceType.getItems().addAll(PriceType.values());

        listViewProducts.setCellFactory(new Callback<ListView<PicoProduct>, ListCell<PicoProduct>>() {
            @Override
            public ListCell<PicoProduct> call(ListView<PicoProduct> p) {
                ListCell<PicoProduct> cell = new ListCell<PicoProduct>() {
                    @Override
                    protected void updateItem(PicoProduct t, boolean bln) {
                        super.updateItem(t, bln);
                        if ( t != null ) {
                            setText(t.getShortDescription());
                        } else {
                            setText("");
                        }
                    }
                };
                return cell;
            }
        });

        // accept drag Over for the products list
        listViewProducts.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                if ( event.getGestureSource() != listViewProducts && event.getDragboard().hasContent(ProductListController.df) ) {
                    event.acceptTransferModes(TransferMode.ANY);
                }
                event.consume();
            }
        });

        // handle dropped objets onto the products list
        listViewProducts.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                boolean success = false;
                if ( db.hasContent(ProductListController.df) ) {
                    if ( !categoryProductFx.getProductsProperty().contains((PicoProduct)db.getContent(ProductListController.df)) ) {
                        categoryProductFx.getProductsProperty().add((PicoProduct)db.getContent(ProductListController.df));
                    }
                    success = true;
                }
                event.setDropCompleted(success);
                event.consume();
            }
        });

        // force the field to be numeric only
        priceInput.textFormatterProperty().set(new TextFormatter<>(changeed -> {
            if ( decimalPattern.matcher(changeed.getControlNewText()).matches() ) {
                return changeed;
            } else {
                return null;
            }
        }));

        // enable the deleteProductButton only if a product is selected
        deleteProduct.disableProperty().bind(listViewProducts.getSelectionModel().selectedIndexProperty().lessThan(0));

        // enable the deletePriceButton only if a price is selected
        listViewPrices.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if ( newValue.intValue() < 0 ) {
                    deletePrice.disableProperty().setValue(Boolean.TRUE);
                } else {
                    deletePrice.disableProperty().setValue(Boolean.FALSE);
                }
            }
        });
    }

    /**
     * Create a CategoryProductFx based on the values from cp. Bind the
     * CategoryProductFx with the ui components.
     *
     * @param cp received categoryProduct.
     */
    public void setCategoryProduct(CategoryProduct cp) {

        categoryProductFx = new CategoryProductFx(cp.getId(), cp.getName(),
                cp.getDescription(),
                cp.getSalesChannel(),
                cp.getProducts()
                        .stream()
                        .map(p -> new PicoProduct(p.getId(), p.getName()))
                        .collect(Collectors.toList()),
                cp.getPrices());

        name.textProperty().bindBidirectional(categoryProductFx.getNameProperty());
        description.textProperty().bindBidirectional(categoryProductFx.getDescriptionProperty());
        salesChannel.valueProperty().bindBidirectional(categoryProductFx.getSalesChannelProperty());

        listViewProducts.setItems(categoryProductFx.getProductsProperty());

        listViewPrices.setItems(FXCollections.observableArrayList(categoryProductFx.getPricesProperty().entrySet()));

        categoryProductFx.getPricesProperty().addListener((MapChangeListener<PriceType, Double>)change -> {
            listViewPrices.getItems().clear();
            listViewPrices.getItems().addAll(categoryProductFx.getPricesProperty().entrySet());
        });
    }

    @Override
    public void accept(CategoryProduct t) {
        setCategoryProduct(t);
    }

    @Override
    public CategoryProductDto getResult() {
        if ( categoryProductDto == null ) {
            return null;
        }
        return categoryProductDto;
    }

}