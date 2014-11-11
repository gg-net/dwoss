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
package eu.ggnet.dwoss.uniqueunit.entity;

import eu.ggnet.dwoss.rules.ProductGroup;
import eu.ggnet.dwoss.rules.TradeName;

import java.io.Serializable;
import java.util.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

import eu.ggnet.dwoss.util.MathUtil;
import eu.ggnet.dwoss.util.persistence.EagerAble;

import lombok.*;

import static javax.persistence.CascadeType.*;

/**
 * Represents a Product.
 *
 * @has 1 - 1 TradeName
 * @has 1 - 1 ProductGroup
 * @has 1 - n PriceHistory
 * @has n - m Flag
 * @has 1 - n UniqueUnit
 * @author oliver.guenther
 */
@Entity
@EqualsAndHashCode(of = "id")
@NamedQueries({
    @NamedQuery(name = "Product.byTradeNames", query = "select p from Product p where p.tradeName in (?1)"),
    @NamedQuery(name = "Product.byPartNos", query = "select p from Product p where p.partNo in (?1)"),
    @NamedQuery(name = "Product.byContractor", query = "SELECT DISTINCT p FROM Product p JOIN p.units u WHERE u.contractor = ?1")
})
public class Product implements Serializable, EagerAble, Comparable<Product> {

    public static NavigableMap<String, Product> asMapByPartNos(Collection<Product> products) {
        NavigableMap<String, Product> result = new TreeMap<>();
        for (Product product : products) {
            result.put(product.getPartNo(), product);
        }
        return result;
    }

    public static enum Flag {

        PRICE_FIXED
    }

    @Getter
    @Id
    @GeneratedValue
    private long id;

    @Version
    private short optLock;

    /**
     * The name of the Product.
     *
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
    private Map<TradeName, String> additionalPartNo = new EnumMap<>(TradeName.class);

    @NotNull
    @ElementCollection(fetch = FetchType.EAGER)
    @MapKeyEnumerated
    private Map<PriceType, Double> prices = new EnumMap<>(PriceType.class);

    @NotNull
    @OneToMany(cascade = ALL)
    private List<PriceHistory> priceHistories = new ArrayList<>();

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

    @Setter
    @Getter
    private int imageId;

    /**
     * Global Trade Item Number, was EAN.
     */
    @Setter
    @Getter
    private String gtin;

    public Product() {
    }

    public Product(ProductGroup group, TradeName tradeName, String partNo, String name) {
        this.group = group;
        this.tradeName = tradeName;
        this.partNo = partNo;
        this.name = name;
    }

    public void addUnit(UniqueUnit unit) {
        if ( unit == null ) return;
        unit.setProduct(this);
    }

    public void removeUnit(UniqueUnit unit) {
        if ( unit == null ) return;
        unit.setProduct(null);
    }

    public void setPrice(PriceType type, double price, String comment) {
        if ( MathUtil.equals(getPrice(type), price) ) return; // Don't set the same price
        prices.put(type, price);
        priceHistories.add(new PriceHistory(type, price, new Date(), comment));
    }

    public double getPrice(PriceType type) {
        return prices.get(type) == null ? 0 : prices.get(type);
    }

    public boolean hasPrice(PriceType type) {
        return prices.get(type) != null && prices.get(type) > 0.01;
    }

    public Map<PriceType, Double> getPrices() {
        return prices;
    }

    public List<PriceHistory> getPriceHistory() {
        return priceHistories;
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
     * <p/>
     * @return null if the instance is valid, or a string representing the error.
     */
    @Null
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
        priceHistories.size();
        units.size();
    }

    @Override
    public String toString() {
        return "Product{" + "id=" + id + ", partNo=" + partNo + ", group=" + group + ", tradeName=" + tradeName + ", name=" + name
                + ", eol=" + eol + ", gtin=" + gtin + ", description=" + description + ", additionalPartNo=" + additionalPartNo
                + ", prices=" + prices + ", flags=" + flags + ", imageId=" + imageId + '}';
    }
}
