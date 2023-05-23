/*
 * Copyright (C) 2014 GG-Net GmbH - Oliver Günther
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
package eu.ggnet.dwoss.uniqueunit.ee.entity;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

import org.apache.commons.lang3.builder.ToStringExclude;

import eu.ggnet.dwoss.core.common.values.ProductGroup;
import eu.ggnet.dwoss.core.common.values.SalesChannel;
import eu.ggnet.dwoss.core.common.values.tradename.TradeName;
import eu.ggnet.dwoss.core.system.persistence.*;
import eu.ggnet.dwoss.core.system.util.TwoDigits;

import static javax.persistence.CascadeType.*;

/**
 * Represents a Product.
 *
 * @author oliver.guenther
 */
@Entity
@NamedQuery(name = "Product.byContractor", query = "SELECT DISTINCT p FROM Product p JOIN p.units u WHERE u.contractor = ?1")
@SuppressWarnings("PersistenceUnitPresent")
public class Product extends BaseEntity implements Serializable, EagerAble, Comparable<Product> {

    public static NavigableMap<String, Product> asMapByPartNos(Collection<Product> products) {
        return new TreeMap<>(products.stream().collect(Collectors.toMap(p -> p.getPartNo(), p -> p)));
    }

    public static enum Flag {

        PRICE_FIXED
    }

    @Id
    @GeneratedValue
    private long id;

    @Version
    private short optLock;

    /**
     * The name of the Product.
     * <p>
     * It sounds weird but, it can happen, that a Spec overwrites the value
     */
    @Basic(optional = false)
    private String name;

    @Basic
    @Column(length = 65536)
    @Lob
    private String description;

    /**
     * Represents the trade name of the Product.
     * Normally this is the manufacturer, oder the company responsible, but may also be a brand or else.
     */
    @Basic(optional = false)
    private TradeName tradeName;

    @Basic(optional = false)
    @Column(name = "productGroup")
    private ProductGroup group;

    /**
     * This is the primary PartNo of the Product. It should be the PartNo of the TradeName
     */
    @Basic(optional = false)
    private String partNo;

    @Temporal(javax.persistence.TemporalType.DATE)
    private Date eol;

    @NotNull
    @ElementCollection(fetch = FetchType.EAGER)
    @MapKeyEnumerated
    @SuppressWarnings("FieldMayBeFinal")
    private Map<TradeName, String> additionalPartNo = new EnumMap<>(TradeName.class);

    /**
     * Represents Flags the user can set for this element.
     * This is a better aproacht, than creating multiple boolean falues.
     */
    @NotNull
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Flag> flags = EnumSet.noneOf(Flag.class);

    // TODO: Add validation, that you cannot remove a Product if it has one or more units
    @ToStringExclude
    @NotNull
    @OneToMany(cascade = {MERGE, REFRESH, PERSIST, DETACH}, mappedBy = "product")
    List<UniqueUnit> units = new ArrayList<>();

    private int imageId;

    /**
     * Global Trade Item Number, was EAN.
     */
    // TODO: Add validation for GTIN
    private long gtin;

    @NotNull
    @ElementCollection(fetch = FetchType.EAGER)
    @MapKeyEnumerated
    @SuppressWarnings("FieldMayBeFinal")
    private Map<PriceType, Double> prices = new EnumMap<>(PriceType.class);

    @ToStringExclude
    @NotNull
    @OneToMany(cascade = ALL)
    @SuppressWarnings("FieldMayBeFinal")
    private List<PriceHistory> priceHistories = new ArrayList<>();

    @NotNull
    @Basic(optional = false)
    private SalesChannel salesChannel = SalesChannel.UNKNOWN;

    @ManyToOne(cascade = {DETACH, MERGE, PERSIST, REFRESH})
    @JoinColumn(foreignKey = @ForeignKey(name = "FK_Product_ShopCategory"))
    private ShopCategory shopCategory = null;

    private boolean rch = false;

    public Product() {
    }

    public Product(ProductGroup group, TradeName tradeName, String partNo, String name) {
        this.group = group;
        this.tradeName = tradeName;
        this.partNo = partNo;
        this.name = name;
    }

    //<editor-fold defaultstate="collapsed" desc="getter/setter">
    @Override
    public long getId() {
        return id;
    }

    public short getOptLock() {
        return optLock;
    }

    public ShopCategory getShopCategory() {
        return shopCategory;
    }

    public void setShopCategory(ShopCategory shopCategory) {
        this.shopCategory = shopCategory;
    }

    public boolean isRch() {
        return rch;
    }

    public void setRch(boolean rch) {
        this.rch = rch;
    }

    public Set<Flag> getFlags() {
        return flags;
    }

    public void setFlags(Set<Flag> flags) {
        this.flags = flags;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public long getGtin() {
        return gtin;
    }

    public void setGtin(long gtin) {
        this.gtin = gtin;
    }

    public SalesChannel getSalesChannel() {
        return salesChannel;
    }

    public void setSalesChannel(SalesChannel salesChannel) {
        this.salesChannel = salesChannel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TradeName getTradeName() {
        return tradeName;
    }

    public void setTradeName(TradeName tradeName) {
        this.tradeName = tradeName;
    }

    public ProductGroup getGroup() {
        return group;
    }

    public void setGroup(ProductGroup group) {
        this.group = group;
    }

    public String getPartNo() {
        return partNo;
    }

    public void setPartNo(String partNo) {
        this.partNo = partNo;
    }

    public Date getEol() {
        return eol;
    }

    public void setEol(Date eol) {
        this.eol = eol;
    }
    //</editor-fold>

    public void setPrice(PriceType type, double price, String comment) {
        price = TwoDigits.round(price);
        if ( TwoDigits.equals(getPrice(type), price) ) {
            return; // Don't set the same price
        }
        prices.put(type, price);
        priceHistories.add(new PriceHistory(type, price, new Date(), comment));
    }

    public boolean hasPrice(PriceType type) {
        return prices.get(type) != null && prices.get(type) > 0.01;
    }

    public double getPrice(PriceType type) {
        return prices.get(type) == null ? 0 : prices.get(type);
    }

    public Map<PriceType, Double> getPrices() {
        return Collections.unmodifiableMap(prices);
    }

    public List<PriceHistory> getPriceHistory() {
        return priceHistories;
    }

    /**
     * Returns a bidirectional wrapper List, mapping changes to the UniqueUnit.
     *
     * @return a bidirectional wrapper List
     */
    public List<UniqueUnit> getUniqueUnits() {
        return new AbstractBidirectionalListWrapper<UniqueUnit>(units) {
            @Override
            protected void update(UniqueUnit e, boolean add) {
                if ( add ) e.setProduct(Product.this);
                else e.setProduct(null);
            }
        };
    }

    public void setAdditionalPartNo(TradeName tradeName, String partNo) {
        additionalPartNo.put(tradeName, partNo);
    }

    public String getAdditionalPartNo(TradeName tradeName) {
        return additionalPartNo.get(tradeName);
    }

    public void removeAdditionalPartNo(TradeName tradeName) {
        additionalPartNo.remove(tradeName);
    }

    public Map<TradeName, String> getAdditionalPartNos() {
        return additionalPartNo;
    }

    public boolean removeFlag(Flag flag) {
        return flags.remove(flag);
    }

    public boolean addFlag(Flag flag) {
        return flags.add(flag);
    }

    /**
     * Returns null if the instance is valid, or a string representing the error.
     * <p>
     * @return null if the instance is valid, or a string representing the error.
     */
    // TODO: Add validation for GTIN
    @Null(message = "ViolationMessage is not null, but '${validatedValue}'")
    public String getViolationMessage() {
        if ( tradeName == null ) return null;
        if ( !tradeName.isBrand() ) return tradeName + " is not a Brand";
        if ( tradeName.getManufacturer().getPartNoSupport() == null ) return null; // No Support, so everything is ok.
        return tradeName.getManufacturer().getPartNoSupport().violationMessages(partNo);
    }

    @Override
    public int compareTo(Product other) {
        if ( other == null ) return +1;
        if ( this.group != other.group ) return this.group.compareTo(other.group);
        if ( this.tradeName != other.tradeName ) return this.tradeName.compareTo(other.tradeName);
        if ( !this.partNo.equals(other.partNo) ) return this.partNo.compareTo(other.partNo);
        return (int)(this.id - other.id);
    }

    @Override
    public void fetchEager() {
        units.forEach(u -> u.fetchEager());
        priceHistories.size();
    }

}
