/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.ggnet.dwoss.uniqueunit.ui.categoryProduct;

import java.util.*;

import javafx.beans.property.*;
import javafx.collections.*;

import eu.ggnet.dwoss.rules.SalesChannel;
import eu.ggnet.dwoss.uniqueunit.api.PicoProduct;
import eu.ggnet.dwoss.uniqueunit.ee.entity.PriceType;

import lombok.ToString;

/**
 * The Fx variant of a CategoryProduct.
 *
 * @author lucas.huelsen
 */
@ToString
public class CategoryProductFx {

    private LongProperty idProperty = new SimpleLongProperty();

    private StringProperty nameProperty = new SimpleStringProperty();

    private StringProperty descriptionProperty = new SimpleStringProperty();

    private ObjectProperty<SalesChannel> salesChannelProperty = new SimpleObjectProperty<>(SalesChannel.UNKNOWN);

    private ObservableList<PicoProduct> productsProperty = FXCollections.observableList(new ArrayList<>());

    private ObservableMap<PriceType, Double> pricesProperty = FXCollections.observableHashMap();

    public CategoryProductFx() {
    }

    public CategoryProductFx(long id, String name, String description, SalesChannel salesChannel, List<PicoProduct> products, Map<PriceType, Double> prices) {
        this.idProperty.set(id);
        this.nameProperty.set(name);
        this.descriptionProperty.set(description);
        this.salesChannelProperty.set(salesChannel);
        this.productsProperty.addAll(products);
        this.pricesProperty.putAll(prices);
    }

    public LongProperty getIdProperty() {
        return idProperty;
    }

    public void setIdProperty(LongProperty idProperty) {
        this.idProperty = idProperty;
    }

    public final void setId(Long value) {
        idProperty.set(value);
    }

    public final Long getId() {
        return idProperty.get();
    }

    public final LongProperty idProperty() {
        return idProperty;
    }

    public final void setName(String value) {
        nameProperty.set(value);
    }

    public final String getName() {
        return nameProperty.get();
    }

    public final StringProperty nameProperty() {
        return nameProperty;
    }

    public final void setDescription(String value) {
        descriptionProperty.set(value);
    }

    public final String getDescription() {
        return descriptionProperty.get();
    }

    public final StringProperty descriptionProperty() {
        return descriptionProperty;
    }

    public StringProperty getNameProperty() {
        return nameProperty;
    }

    public void setNameProperty(StringProperty nameProperty) {
        this.nameProperty = nameProperty;
    }

    public StringProperty getDescriptionProperty() {
        return descriptionProperty;
    }

    public void setDescriptionProperty(StringProperty descriptionProperty) {
        this.descriptionProperty = descriptionProperty;
    }

    public ObjectProperty<SalesChannel> getSalesChannelProperty() {
        return salesChannelProperty;
    }

    public void setSalesChannel(SalesChannel salesChannel) {
        this.salesChannelProperty.set(salesChannel);
    }

    public SalesChannel getSalesChannel() {
        return salesChannelProperty.getValue();
    }

    public ObservableList<PicoProduct> getProductsProperty() {
        return productsProperty;
    }

    public List<PicoProduct> getProducts() {
        return productsProperty;
    }

    public ObservableMap<PriceType, Double> getPricesProperty() {
        return pricesProperty;
    }

    public Map<PriceType, Double> getPrices() {
        return pricesProperty;
    }

}
