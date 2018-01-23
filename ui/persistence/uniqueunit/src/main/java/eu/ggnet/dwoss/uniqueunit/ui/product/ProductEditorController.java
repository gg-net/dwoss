/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.ggnet.dwoss.uniqueunit.ui.product;

import java.net.URL;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.NumberStringConverter;

import org.apache.commons.lang3.StringUtils;

import eu.ggnet.dwoss.rules.*;
import eu.ggnet.dwoss.uniqueunit.entity.PriceType;
import eu.ggnet.dwoss.uniqueunit.entity.Product;
import eu.ggnet.dwoss.uniqueunit.entity.dto.ProductDto;
import eu.ggnet.dwoss.uniqueunit.ui.product.ProductFx.AdditionalPartNo;
import eu.ggnet.dwoss.uniqueunit.ui.product.ProductFx.Prices;
import eu.ggnet.saft.Ui;
import eu.ggnet.saft.UiAlert;
import eu.ggnet.saft.api.ui.FxController;
import eu.ggnet.saft.api.ui.ResultProducer;
import eu.ggnet.saft.core.ui.UiAlertBuilder;

/**
 * FXML Controller class for ressources/ProductEditorView.fxml This UI is an
 * editor for the class Product it is loaded with the information to display an
 * existing Product instance for editing or to create a new one class ProductFX
 * is used as a JavaFX Bean of class product and ProductDTO for database
 * transactions of UI generated data
 *
 *
 * @author jacob.weinhold
 */
public class ProductEditorController implements Initializable, Consumer<Product>, FxController, ResultProducer<ProductDto> {

    @FXML
    private TextField partNoTextField;

    @FXML
    private TextField gtinTextField;

    @FXML
    private TextField imageIdTextField;

    @FXML
    private ComboBox<TradeName> tradeNameComboBox;

    @FXML
    private TextField nameTextArea;

    @FXML
    private ComboBox<SalesChannel> salesChannelComboBox;

    @FXML
    private ComboBox<ProductGroup> productGroupComboBox;

    @FXML
    private TextArea descriptionTextArea;

    @FXML
    private TableView<ProductFx.Prices> priceTable;

    @FXML
    private TableView<ProductFx.AdditionalPartNo> additionalPartNoTable;

    @FXML
    private Button submitButton;

    @FXML
    private Button exitButton;

    @FXML
    private ComboBox<PriceType> priceTableComboBox;

    @FXML
    private TextField priceTableTextField;

    @FXML
    private Button priceTableSubmitButton;

    @FXML
    private ComboBox<TradeName> additionalPartNoTableComboBox;

    @FXML
    private Button additionalPartNoTableSubmitButton;

    @FXML
    private DatePicker eolDatePicker;

    @FXML
    private TableColumn<AdditionalPartNo, TradeName> tradeNameColumn;

    @FXML
    private TableColumn<AdditionalPartNo, String> partNoColumn;

    @FXML
    private TableColumn<Prices, PriceType> priceTypeColumn;

    @FXML
    private TableColumn<Prices, Double> priceDoubleColumn;

    @FXML
    private TextField additionalPartNoTextField;

    private final ProductFx pfx = new ProductFx();

    private ProductDto result = null;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        fillTableComboBoxes();

        additionalPartNoTable.setItems(pfx.getAdditionalPartNos());
        priceTable.setItems(pfx.getPrices());

        additionalPartNoTable.setEditable(true);
        priceTable.setEditable(true);

        tradeNameColumn.setCellValueFactory(new PropertyValueFactory<>("contractor"));
        partNoColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        partNoColumn.setCellValueFactory(new PropertyValueFactory<>("partNo"));

        partNoColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<AdditionalPartNo, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<AdditionalPartNo, String> e) {
                ((AdditionalPartNo)e.getTableView().getItems().get(e.getTablePosition().getRow())).setPartNo(e.getNewValue());
            }
        });
        partNoColumn.setEditable(true);

        priceTypeColumn.setCellValueFactory(new PropertyValueFactory<>("priceType"));
        priceDoubleColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        // see CustomTableCell documentation
        // this is necessary if we want an editable Double Column representing a FXProperty
//        priceDoubleColumn.setCellFactory(CustomTableCell.forTableColumn(new DoubleStringConverter()));
        priceDoubleColumn.setEditable(true);
        priceDoubleColumn.setCellFactory(
                TextFieldTableCell.<Prices, Double>forTableColumn(new MyDoubleStringConverter()));
        priceDoubleColumn.setOnEditCommit(event
                -> {
            final Double value = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();

            ((Prices)event.getTableView().getItems().get(event.getTablePosition().getRow())).setPrice(value);

            event.getTableView().refresh();
        });

        pfx.nameProperty()
                .bindBidirectional(nameTextArea.textProperty());
        pfx.descriptionProperty()
                .bindBidirectional(descriptionTextArea.textProperty());
        pfx.partNoProperty()
                .bindBidirectional(partNoTextField.textProperty());
        pfx.eolProperty()
                .bindBidirectional(eolDatePicker.valueProperty());
        imageIdTextField.textProperty()
                .bindBidirectional(pfx.imageIdProperty(), new NumberStringConverter());
        gtinTextField.textProperty()
                .bindBidirectional(pfx.gtinProperty(), new NumberStringConverter());
        pfx.tradeNameProperty()
                .bindBidirectional(tradeNameComboBox.valueProperty());
        pfx.productGroupProperty()
                .bindBidirectional(productGroupComboBox.valueProperty());
        pfx.salesChannelProperty()
                .bindBidirectional(salesChannelComboBox.valueProperty());

        // Defaults
        pfx.setProductGroup(ProductGroup.values()[0]);
        pfx.setSalesChannel(SalesChannel.UNKNOWN);
        pfx.setTradeName(TradeName.values()[0]);

    }

    @FXML
    private void handleSubmitButtonAction(ActionEvent event) {

        ProductDto p = new ProductDto();
        p.setId(pfx.getId());
        p.setOptLock(pfx.getOptLock());

        if ( pfx.getName().length() == 0 ) {
            UiAlert.title("Fehler!").message("Speichern des Produkts nicht möglich.")
                    .nl("Es ist kein Name angegeben.")
                    .parent(submitButton).show(UiAlertBuilder.Type.ERROR);
            return;
        }
        if ( StringUtils.isBlank(pfx.getDescription()) ) {
            UiAlert.title("Fehler!").message("Speichern des Produkts nicht möglich.")
                    .nl("Es ist keine Beschreibung angegeben.")
                    .parent(submitButton).show(UiAlertBuilder.Type.ERROR);
            return;
        }

        if ( StringUtils.isBlank(pfx.getPartNo()) ) {
            UiAlert.title("Fehler!").message("Speichern des Produkts nicht möglich.")
                    .nl("Es ist keine Artikelnummer gesetzt.")
                    .parent(submitButton).show(UiAlertBuilder.Type.ERROR);
            return;
        }

        p.setName(pfx.getName());
        p.setDescription(pfx.getDescription());
        if ( pfx.getEol() == null ) {
            p.setEol(null);
        } else {
            p.setEol(Date.from(pfx.getEol().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }
        p.setGroup(pfx.getProductGroup());
        p.setGtin(pfx.getGtin());
        p.setImageId(pfx.getImageId());
        p.setPartNo(pfx.getPartNo());
        p.setTradeName(pfx.getTradeName());
        p.setSalesChannel(pfx.getSalesChannel());
        Map<PriceType, Double> prices = new EnumMap<>(PriceType.class);
        pfx.getPrices().forEach((price)
                -> {
            prices.put(price.getPriceType(), price.getPrice());
        });

        p.setPrices(prices);

        Map<TradeName, String> addPartNos = new EnumMap<>(TradeName.class);

        pfx.getAdditionalPartNos().forEach((additionalPartNo)
                -> {
            addPartNos.put(additionalPartNo.getContractor(), additionalPartNo.getPartNo());
        });
        p.setAdditionalPartNo(addPartNos);

        this.result = p;

        Ui.closeWindowOf(partNoTextField);
    }

    @FXML
    private void handleExitButtonAction(ActionEvent event) {
        Ui.closeWindowOf(partNoTextField);
    }

    @Override
    public void accept(Product product) {
        this.pfx.setId(product.getId());
        this.pfx.setOptLock(product.getOptLock());

        this.pfx.setName(product.getName());
        this.pfx.setDescription(product.getDescription());
        this.pfx.setGtin(product.getGtin());
        this.pfx.setImageId(product.getImageId());
        this.pfx.setPartNo(product.getPartNo());
        if ( product.getEol() == null ) {
            pfx.setEol(null);
        } else {
            this.pfx.setEol(product.getEol().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        }

        this.pfx.setTradeName(product.getTradeName());
        if ( product.getSalesChannel() != null ) {
            this.pfx.setSalesChannel(product.getSalesChannel());
        } else {
            this.pfx.setSalesChannel(SalesChannel.UNKNOWN);
        }
        this.pfx.setProductGroup(product.getGroup());

        // product.getAdditionalPartNos().forEach((t, u) -> this.pfx.getAdditionalPartNos().add(new AdditionalPartNo(t, u)));
        this.pfx.getAdditionalPartNos().addAll(product.getAdditionalPartNos()
                .entrySet()
                .stream()
                .map(e -> new AdditionalPartNo(e.getKey(), e.getValue()))
                .collect(Collectors.toList()));

        product.getPrices().forEach((PriceType pT, Double price)
                -> {

            Prices pr = new Prices();
            pr.setPrice(price);
            pr.setPriceType(pT);
            this.pfx.getPrices().add(pr);

        });

    }

    @FXML
    private void handlePriceTableSubmitButtonAction(ActionEvent event) {
        String s = priceTableTextField.getText();
        final PriceType selectedPriceType = priceTableComboBox.getValue();

        if ( priceTable.getItems().stream().map(Prices::getPriceType).anyMatch((t) -> t == selectedPriceType) ) {
            UiAlert.title("Fehler!").message("Hinzufügen des Preistyps nicht möglich")
                    .nl("Der Preistyp ist bereits gesetzt.")
                    .parent(priceTableSubmitButton).show(UiAlertBuilder.Type.ERROR);
            return;

        }
        if ( priceTableComboBox.getSelectionModel().isEmpty() ) {
            UiAlert.title("Fehler!").message("Hinzufügen des Preistyps nicht möglich")
                    .nl("Kein Preistyp ausgewählt.")
                    .parent(priceTableSubmitButton).show(UiAlertBuilder.Type.ERROR);
            return;
        }
        Pattern pattern = Pattern.compile("[-+]?[0-9]*(\\.|,)?[0-9]+([eE][-+]?[0-9]+)?");
        Matcher m = pattern.matcher(s);
        if ( !m.matches() ) {
            UiAlert.title("Fehler!").message("Hinzufügen des Preistyps nicht möglich")
                    .nl("Die Preisangabe entspricht keiner gültigen Zahl")
                    .parent(priceTableSubmitButton).show(UiAlertBuilder.Type.ERROR);
            return;
        }

        s = s.replace(",", ".");

        Prices p = new Prices();

        p.setPriceType(priceTableComboBox.getValue());
        p.setPrice(Double.valueOf(s));

        pfx.getPrices().add(p);
    }

    private void fillTableComboBoxes() {
        additionalPartNoTableComboBox.getItems().addAll(TradeName.values());

        priceTableComboBox.getItems().addAll(PriceType.values());

        salesChannelComboBox.getItems().addAll(SalesChannel.values());

        productGroupComboBox.getItems().addAll(ProductGroup.values());

        tradeNameComboBox.getItems().addAll(TradeName.values());

    }

    @FXML
    private void handleAdditionalPartNoTableSubmitButtonAction(ActionEvent event) {

        String textFieldString = additionalPartNoTextField.getText().trim();
        TradeName tradeName = additionalPartNoTableComboBox.getValue();

        if ( textFieldString.length() == 0 ) {
            UiAlert.title("Fehler!").message("Hinzufügen der Artikelnummer nicht möglich.")
                    .nl("Es ist keine Artikelnummer angegeben.")
                    .parent(additionalPartNoTableSubmitButton).show(UiAlertBuilder.Type.ERROR);
            return;
        }

        if ( additionalPartNoTable.getItems().stream().map(AdditionalPartNo::getContractor).anyMatch(contractor -> contractor == tradeName) ) {
            UiAlert.title("Fehler!").message("Hinzufügen der Artikelnummer nicht möglich")
                    .nl("Der Lieferant ist bereits vorhanden.")
                    .parent(additionalPartNoTableSubmitButton).show(UiAlertBuilder.Type.ERROR);
            return;
        }

        AdditionalPartNo apN = new AdditionalPartNo();

        apN.setContractor(additionalPartNoTableComboBox.getValue());

        apN.setPartNo(textFieldString);

        pfx.getAdditionalPartNos().add(apN);
    }

    @Override
    public ProductDto getResult() {
        return result;
    }

}

class MyDoubleStringConverter extends DoubleStringConverter {

    @Override
    public Double fromString(final String value) {
        return value.isEmpty() || !isNumber(value) ? null : super.fromString(value);
    }

    public boolean isNumber(String value) {
        int size = value.length();
        for (int i = 0; i < size; i++) {
            if ( !Character.isDigit(value.charAt(i)) ) {
                return false;
            }
        }
        return size > 0;
    }
}
