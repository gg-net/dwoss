/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.ggnet.dwoss.uniqueunit.ui.categoryProduct;

import eu.ggnet.dwoss.rules.SalesChannel;
import eu.ggnet.dwoss.uniqueunit.api.PicoProduct;
import eu.ggnet.dwoss.uniqueunit.entity.PriceType;
import java.util.List;
import java.util.Map;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import lombok.ToString;

/**
 * The Fx variant of a CategoryProduct.
 *
 * @author lucas.huelsen
 */
@ToString
public class CategoryProductFx {

    private LongProperty idProperty;
    private StringProperty nameProperty;
    private StringProperty descriptionProperty;
    private ObjectProperty<SalesChannel> salesChannelProperty;
    private ObservableList<PicoProduct> productsProperty;
    private ObservableMap<PriceType, Double> pricesProperty;

    public CategoryProductFx(long id, String name, String description, SalesChannel salesChannel, List<PicoProduct> products, Map<PriceType, Double> prices) {
        this.idProperty = new SimpleLongProperty(id);
        this.nameProperty = new SimpleStringProperty(name);
        this.descriptionProperty = new SimpleStringProperty(description);
        this.salesChannelProperty = new SimpleObjectProperty<>(salesChannel);
        this.productsProperty = FXCollections.observableList(products);
        this.pricesProperty = FXCollections.observableHashMap();
        pricesProperty.putAll(prices);
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

    public void setSalesChannelProperty(ObjectProperty<SalesChannel> salesChannelProperty) {
        this.salesChannelProperty = salesChannelProperty;
    }

    public SalesChannel getSalesChannel() {
        return salesChannelProperty.getValue();
    }

    public ObservableList<PicoProduct> getProductsProperty() {
        return productsProperty;
    }

    public void setProductsProperty(ObservableList<PicoProduct> productsProperty) {
        this.productsProperty = productsProperty;
    }

    public ObservableMap<PriceType, Double> getPricesProperty() {
        return pricesProperty;
    }

    public void setPricesProperty(ObservableMap<PriceType, Double> pricesProperty) {
        this.pricesProperty = pricesProperty;
    }

}
