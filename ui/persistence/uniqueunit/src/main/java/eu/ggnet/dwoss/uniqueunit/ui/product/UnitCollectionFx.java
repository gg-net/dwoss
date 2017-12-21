/*
 * Copyright (C) 2017 GG-Net GmbH
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
package eu.ggnet.dwoss.uniqueunit.ui.product;

import java.util.List;
import java.util.Map;

import javafx.beans.property.*;
import javafx.collections.*;

import eu.ggnet.dwoss.rules.SalesChannel;
import eu.ggnet.dwoss.uniqueunit.api.PicoProduct;
import eu.ggnet.dwoss.uniqueunit.api.PicoUnit;
import eu.ggnet.dwoss.uniqueunit.entity.PriceHistory;
import eu.ggnet.dwoss.uniqueunit.entity.PriceType;

import lombok.ToString;

/**
 * The Fx variant of a UnitCollection.
 * 
 * @author jens.papenhagen
 */
@ToString
public class UnitCollectionFx {

    private LongProperty idProperty;

    private StringProperty nameExtensionProperty;

    private StringProperty descriptionExtensionProperty;

    private StringProperty partNoExtensionProperty;

    private ObjectProperty<PicoProduct> productProperty;

    private ObservableList<PicoUnit> unitsProperty;

    private ObservableMap<PriceType, Double> pricesProperty;

    private ObservableList<PriceHistory> priceHistoriesProperty;

    private ObjectProperty<SalesChannel> salesChannelProperty;

    public UnitCollectionFx(long id, String nameExtension, String descriptionExtension, String partNoExtension, PicoProduct product, List<PicoUnit> units, Map<PriceType, Double> prices, List<PriceHistory> priceHistories, SalesChannel salesChannel) {
        this.idProperty = new SimpleLongProperty(id);
        this.nameExtensionProperty = new SimpleStringProperty(nameExtension);
        this.descriptionExtensionProperty = new SimpleStringProperty(descriptionExtension);
        this.partNoExtensionProperty = new SimpleStringProperty(partNoExtension);
        this.productProperty = new SimpleObjectProperty<>(product);
        this.unitsProperty = FXCollections.observableList(units);
        this.pricesProperty = FXCollections.observableHashMap();
        pricesProperty.putAll(prices);
        this.priceHistoriesProperty = FXCollections.observableList(priceHistories);
        this.salesChannelProperty = new SimpleObjectProperty<>(salesChannel);
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

    public final void setNameExtension(String value) {
        nameExtensionProperty.set(value);
    }

    public final String getNameExtension() {
        return nameExtensionProperty.get();
    }

    public final StringProperty nameExtensionProperty() {
        return nameExtensionProperty;
    }

    public StringProperty getNameExtensionProperty() {
        return nameExtensionProperty;
    }

    public void setNameExtensionProperty(StringProperty nameExtension) {
        this.nameExtensionProperty = nameExtension;
    }

    public final void setDescriptionExtension(String value) {
        descriptionExtensionProperty.set(value);
    }

    public final String getDescriptionExtension() {
        return descriptionExtensionProperty.get();
    }

    public final StringProperty descriptionExtensionProperty() {
        return descriptionExtensionProperty;
    }

    public StringProperty getDescriptionExtensionProperty() {
        return descriptionExtensionProperty;
    }

    public void setDescriptionExtensionProperty(StringProperty descriptionExtension) {
        this.descriptionExtensionProperty = descriptionExtension;
    }

    public final void setPartNoExtension(String value) {
        partNoExtensionProperty.set(value);
    }

    public final String getPartNoExtension() {
        return partNoExtensionProperty.get();
    }

    public final StringProperty partNoExtensionProperty() {
        return partNoExtensionProperty;
    }

    public StringProperty getPartNoExtensionProperty() {
        return partNoExtensionProperty;
    }

    public void setPartNoExtensionProperty(StringProperty partNoExtension) {
        this.partNoExtensionProperty = partNoExtension;
    }

    public ObjectProperty<PicoProduct> getProductProperty() {
        return productProperty;
    }

    public void setProductProperty(ObjectProperty<PicoProduct> productProperty) {
        this.productProperty = productProperty;
    }

    public PicoProduct getProduct() {
        return productProperty.getValue();
    }

    public ObservableList<PicoUnit> getUnitsProperty() {
        return unitsProperty;
    }

    public void setUnitsProperty(ObservableList<PicoUnit> unitsProperty) {
        this.unitsProperty = unitsProperty;
    }

    public ObservableMap<PriceType, Double> getPricesProperty() {
        return pricesProperty;
    }

    public void setPricesProperty(ObservableMap<PriceType, Double> pricesProperty) {
        this.pricesProperty = pricesProperty;
    }

    public ObservableList<PriceHistory> getpriceHistoriesProperty() {
        return priceHistoriesProperty;
    }

    public void setPriceHistoriesProperty(ObservableList<PriceHistory> priceHistoriesProperty) {
        this.priceHistoriesProperty = priceHistoriesProperty;
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

}
