/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.ggnet.dwoss.uniqueunit.ui.product;

import java.time.LocalDate;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import eu.ggnet.dwoss.common.api.values.*;
import eu.ggnet.dwoss.uniqueunit.ee.entity.PriceType;

/**
 *
 * @author jacob.weinhold
 */
public class ProductFx {

    /**
     * constructor with test data
     */
    public ProductFx() {
    }

    public static class AdditionalPartNo {

        public AdditionalPartNo(TradeName contractor, String partNo) {
            this.contractor = contractor;
            this.partNoProperty.set(partNo);
        }

        public AdditionalPartNo() {
        }

        private TradeName contractor;

        private StringProperty partNoProperty = new SimpleStringProperty(this, "partNo");

        public final void setPartNo(String value) {
            partNoProperty.set(value);
        }

        public final String getPartNo() {
            return partNoProperty.get();
        }

        public final StringProperty partNoProperty() {
            return partNoProperty;
        }

        public TradeName getContractor() {
            return contractor;
        }

        public void setContractor(TradeName contractor) {
            this.contractor = contractor;
        }

    }

    public static class Prices {

        private DoubleProperty price = new SimpleDoubleProperty(this, "price");

        private PriceType priceType;

        public final void setPrice(Double value) {
            price.set(value);
        }

        public final Double getPrice() {
            return price.get();
        }

        public final DoubleProperty priceProperty() {
            return price;
        }

        public PriceType getPriceType() {
            return this.priceType;
        }

        public void setPriceType(PriceType priceType) {
            this.priceType = priceType;
        }

    }

    private ObservableList<AdditionalPartNo> additionalPartNos = FXCollections.observableArrayList();

    private ObservableList<Prices> prices = FXCollections.observableArrayList();

    private ObjectProperty<TradeName> tradeName = new SimpleObjectProperty<>();

    private ObjectProperty<ProductGroup> productGroup = new SimpleObjectProperty<>();

    private ObjectProperty<SalesChannel> salesChannel = new SimpleObjectProperty<>();

    private ObjectProperty<LocalDate> eol = new SimpleObjectProperty<>();

    private StringProperty name = new SimpleStringProperty();

    private StringProperty description = new SimpleStringProperty();

    private StringProperty partNo = new SimpleStringProperty();

    private IntegerProperty imageId = new SimpleIntegerProperty();

    private LongProperty gtin = new SimpleLongProperty();

    private long id;

    private short optLock;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public short getOptLock() {
        return optLock;
    }

    public void setOptLock(short optLock) {
        this.optLock = optLock;
    }

    public SalesChannel getSalesChannel() {
        return this.salesChannel.getValue();
    }

    public TradeName getTradeName() {
        return this.tradeName.getValue();
    }

    public ProductGroup getProductGroup() {
        return this.productGroup.getValue();
    }

    public LocalDate getEol() {
        return eol.getValue();
    }

    public void setTradeName(TradeName tr) {
        this.tradeName.setValue(tr);
    }

    public void setProductGroup(ProductGroup tr) {
        this.productGroup.setValue(tr);
    }

    public void setSalesChannel(SalesChannel tr) {
        this.salesChannel.setValue(tr);
    }

    public void setEol(LocalDate ld) {
        this.eol.set(ld);
    }

    public final ObservableList<AdditionalPartNo> getAdditionalPartNos() {
        return additionalPartNos;
    }

    public final ObservableList<Prices> getPrices() {
        return prices;
    }

    public final ObjectProperty<TradeName> tradeNameProperty() {
        return tradeName;
    }

    public final ObjectProperty<ProductGroup> productGroupProperty() {
        return productGroup;
    }

    public final ObjectProperty<SalesChannel> salesChannelProperty() {
        return salesChannel;
    }

    public final ObjectProperty<LocalDate> eolProperty() {
        return eol;
    }

    public final void setName(String value) {
        name.set(value);
    }

    public final String getName() {
        return name.get();
    }

    public final StringProperty nameProperty() {
        return name;
    }

    public final void setDescription(String value) {
        description.set(value);
    }

    public final String getDescription() {
        return description.get();
    }

    public final StringProperty descriptionProperty() {
        return description;
    }

    public final void setPartNo(String value) {
        partNo.set(value);
    }

    public final String getPartNo() {
        return partNo.get();
    }

    public final StringProperty partNoProperty() {
        return partNo;
    }

    public final void setImageId(Integer value) {
        imageId.set(value);
    }

    public final Integer getImageId() {
        return imageId.get();
    }

    public final IntegerProperty imageIdProperty() {
        return imageId;
    }

    public final void setGtin(Long value) {
        gtin.set(value);
    }

    public final Long getGtin() {
        return gtin.get();
    }

    public final LongProperty gtinProperty() {
        return gtin;
    }

}
