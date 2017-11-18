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
package eu.ggnet.dwoss.uniqueunit.entity;

import java.io.Serializable;
import java.util.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import eu.ggnet.dwoss.rules.SalesChannel;
import eu.ggnet.dwoss.util.MathUtil;

import lombok.*;

import static javax.persistence.CascadeType.*;
import static javax.persistence.FetchType.EAGER;

/**
 * Represents a collection of units from the same product but not only the product.
 * An example: Asume a product "lila handy" with 200 assosiated uniqueunits. Now you what to splitt the products in three categories: new units, units without
 * a case and units you want to keep in stock for an special paralell event. So you create three unit collections an assign the 200 units apropriatly.
 *
 * @has n - 1 SalesChannel
 * @has 0 - n PriceHistory
 * @has 0 - n UniqueUnit
 *
 * @author oliver.guenther
 */
@Entity
@Getter
@EqualsAndHashCode(of = {"id"}, callSuper = false)
@SuppressWarnings("PersistenceUnitPresent")
public class UnitCollection implements Serializable {

    @Id
    @GeneratedValue
    private long id;

    @Version
    private short optLock;

    /**
     * Extension to {@link Product#name}. Default combind format: {
     *
     * @ product.name} - {
     * @ unitcollection.nameExtenstion}.
     */
    @Setter
    @Basic(optional = false)
    private String nameExtension;

    /**
     * Extension to {@link Product#desciption}. Default combind format: {@code product.description}, {@code unitcollection.descriptionExtension}.
     */
    @Setter
    @Basic
    @Column(length = 65536)
    @Lob
    private String descriptionExtension;

    /**
     * Extenstion to {@link Product#partNo }. Default combind format: {@code product.partNo}-{@code unitcollection.descriptionExtension}
     */
    @Setter
    @Basic(optional = false)
    private String partNoExtension;

    @NotNull
    @ManyToOne(cascade = {PERSIST, REFRESH, DETACH, MERGE}, fetch = EAGER) // Eager fetching, cause it's an esential part.
    private Product product;

    @NotNull
    @OneToMany(cascade = {MERGE, REFRESH, PERSIST, DETACH}, mappedBy = "unitCollection")
    List<UniqueUnit> units = new ArrayList<>(); // Package private for bidirectional handling.

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

    public void setPrice(PriceType type, double price, String comment) {
        if ( MathUtil.equals(getPrice(type), price) ) {
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
     * Sets the {@link Product} in consideration of equalancy and bidirectional
     * behaviour.
     * <p>
     * @param product
     */
    @SuppressWarnings("null")
    public void setProduct(Product product) {
        if ( product == null && this.product == null ) {
            return;
        }
        if ( this.product != null && this.product.equals(product) ) {
            return;
        }
        if ( this.product != null ) {
            this.product.unitCollections.remove(this);
        }
        if ( product != null ) {
            product.unitCollections.add(this);
        }
        this.product = product;
    }

    public void addUnit(UniqueUnit unit) {
        if ( unit == null ) return;
        unit.setUnitCollection(this);
    }

    public void removeUnit(UniqueUnit unit) {
        if ( unit == null ) return;
        unit.setUnitCollection(null);
    }

    @Override
    public String toString() {
        String productString = "[Product is null, invalid]";
        if ( product != null ) {
            productString = "[" + product.getPartNo() + "]" + product.getTradeName() + " " + product.getName();
        }

        return "UnitCollection{" + "id=" + id + ", optLock=" + optLock + ", nameExtension=" + nameExtension + ", descriptionExtension=" + descriptionExtension
                + ", product=" + productString + '}';
    }

}
