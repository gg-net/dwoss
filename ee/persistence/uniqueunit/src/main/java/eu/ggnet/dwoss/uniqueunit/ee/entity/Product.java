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

import eu.ggnet.dwoss.rules.*;
import eu.ggnet.dwoss.util.TwoDigits;
import eu.ggnet.dwoss.util.persistence.EagerAble;

import lombok.*;

import static javax.persistence.CascadeType.*;

/**
 * Represents a Product.
 *
 * @has n - 1 TradeName
 * @has n - 1 ProductGroup
 * @has n - 1 SalesChannel
 * @has 1 - n PriceHistory
 * @has n - m Flag
 * @has 0 - n UniqueUnit
 * @has 0 - n UnitCollection
 * @author oliver.guenther
 */
@Entity
@EqualsAndHashCode(of = "id", callSuper = false)
@NamedQuery(name = "Product.byContractor", query = "SELECT DISTINCT p FROM Product p JOIN p.units u WHERE u.contractor = ?1")
@SuppressWarnings("PersistenceUnitPresent")
public class Product implements Serializable, EagerAble, Comparable<Product> {

    public static NavigableMap<String, Product> asMapByPartNos(Collection<Product> products) {
        return new TreeMap<>(products.stream().collect(Collectors.toMap(p -> p.getPartNo(), p -> p)));
    }

    public static enum Flag {

        PRICE_FIXED
    }

    @Getter
    @Id
    @GeneratedValue
    private long id;

    @Getter
    @Version
    private short optLock;

    /**
     * The name of the Product.
     * <p>
     * It sounds weird but, it can happen, that a Spec overwrites the value
     */
    @Getter
    @Setter
    @Basic(optional = false)
    private String name;

    @Getter
    @Setter
    @Basic
    @Column(length = 65536)
    @Lob
    private String description;

    /**
     * Represents the trade name of the Product.
     * Normally this is the manufacturer, oder the company responsible, but may also be a brand or else.
     */
    @Getter
    @Setter
    @Basic(optional = false)
    private TradeName tradeName;

    @Getter
    @Setter
    @Basic(optional = false)
    @Column(name = "productGroup")
    private ProductGroup group;

    /**
     * This is the primary PartNo of the Product. It should be the PartNo of the TradeName
     */
    @Getter
    @Setter
    @Basic(optional = false)
    private String partNo;

    @Getter
    @Setter
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date eol;

    @NotNull
    @ElementCollection(fetch = FetchType.EAGER)
    @MapKeyEnumerated
    @SuppressWarnings("FieldMayBeFinal")
    private Map<TradeName, String> additionalPartNo = new EnumMap<>(TradeName.class);

    @Getter
    @ManyToOne(cascade = {PERSIST, REFRESH, DETACH, MERGE})
    private CategoryProduct categoryProduct;

    /**
     * Represents Flags the user can set for this element.
     * This is a better aproacht, than creating multiple boolean falues.
     */
    @Getter
    @Setter
    @NotNull
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Flag> flags = EnumSet.noneOf(Flag.class);

    // TODO: Add validation, that you cannot remove a Product if it has one or more units
    @NotNull
    @OneToMany(cascade = {MERGE, REFRESH, PERSIST, DETACH}, mappedBy = "product")
    List<UniqueUnit> units = new ArrayList<>();

    @NotNull
    @OneToMany(cascade = {MERGE, REFRESH, PERSIST, DETACH}, mappedBy = "product")
    List<UnitCollection> unitCollections = new ArrayList<>();

    @Setter
    @Getter
    private int imageId;

    /**
     * Global Trade Item Number, was EAN.
     */
    @Setter
    @Getter
    private long gtin;

    @NotNull
    @ElementCollection(fetch = FetchType.EAGER)
    @MapKeyEnumerated
    @SuppressWarnings("FieldMayBeFinal")
    private Map<PriceType, Double> prices = new EnumMap<>(PriceType.class);

    @NotNull
    @OneToMany(cascade = ALL)
    @SuppressWarnings("FieldMayBeFinal")
    private List<PriceHistory> priceHistories = new ArrayList<>();

    @Getter
    @Setter
    @NotNull
    @Basic(optional = false)
    private SalesChannel salesChannel = SalesChannel.UNKNOWN;

    public Product() {
    }

    public Product(ProductGroup group, TradeName tradeName, String partNo, String name) {
        this.group = group;
        this.tradeName = tradeName;
        this.partNo = partNo;
        this.name = name;
    }

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

    /**
     * Returns a bidirectional wrapper List, mapping changes to the UnitCollection.
     *
     * @return a bidirectional wrapper List
     */
    public List<UnitCollection> getUnitCollections() {
        return new AbstractBidirectionalListWrapper<UnitCollection>(unitCollections) {
            @Override
            protected void update(UnitCollection e, boolean add) {
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
     * Sets the {@link Product} in consideration of equalancy and bidirectional
     * behaviour.
     * <p>
     * @param categoryProduct
     */
    @SuppressWarnings("null")
    public void setCategoryProduct(CategoryProduct categoryProduct) {
        if ( categoryProduct == null && this.categoryProduct == null ) {
            return;
        }
        if ( this.categoryProduct != null && this.categoryProduct.equals(categoryProduct) ) {
            return;
        }
        if ( this.categoryProduct != null ) {
            this.categoryProduct.products.remove(this);
        }
        if ( categoryProduct != null ) {
            categoryProduct.products.add(this);
        }
        this.categoryProduct = categoryProduct;
    }

    /**
     * Returns null if the instance is valid, or a string representing the error.
     * <p>
     * @return null if the instance is valid, or a string representing the error.
     */
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
        if ( categoryProduct != null ) categoryProduct.fetchEager();
        unitCollections.forEach(u -> u.fetchEager());
        units.forEach(u -> u.fetchEager());
        priceHistories.size();
    }

    @Override
    public String toString() {
        return "Product{" + "id=" + id + ", partNo=" + partNo + ", group=" + group + ", tradeName=" + tradeName + ", name=" + name
                + ", eol=" + eol + ", gtin=" + gtin + ", description=" + description + ", additionalPartNo=" + additionalPartNo
                + ", prices=" + getPrices() + ", flags=" + flags + ", imageId=" + imageId + '}';
    }
}
