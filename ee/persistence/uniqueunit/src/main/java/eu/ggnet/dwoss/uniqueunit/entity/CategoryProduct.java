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

/**
 * A human defined collection of products to be viewed together.
 *
 * @has n - 1 SalesChannel
 * @has 0 - n PriceHistory
 * @has 0 - n Product
 *
 * @author oliver.guenther
 */
@Entity
@SuppressWarnings("PersistenceUnitPresent")
@EqualsAndHashCode(of = {"id"}, callSuper = false)
public class CategoryProduct implements Serializable {

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

    @NotNull
    @OneToMany(cascade = {MERGE, REFRESH, PERSIST, DETACH}, mappedBy = "categoryProduct")
    List<Product> products = new ArrayList<>(); // Package private for bidirectional handling.

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

    /**
     * Returns true if a price is bigger than 0.
     *
     * @param type the type
     * @return true if if a price is bigger than 0.
     */
    public boolean hasPrice(PriceType type) {
        return prices.get(type) != null && prices.get(type) > 0.01;
    }

    /**
     * Returs the setted price or 0 if no price is set.
     *
     * @param type the type
     * @return the setted price or 0 if no price is set.
     */
    public double getPrice(PriceType type) {
        return prices.get(type) == null ? 0 : prices.get(type);
    }

    public Map<PriceType, Double> getPrices() {
        return Collections.unmodifiableMap(prices);
    }

    public List<PriceHistory> getPriceHistory() {
        return priceHistories;
    }

    public void add(Product product) {
        if ( product == null ) return;
        product.setCategoryProduct(this);
    }

    public void remove(Product product) {
        if ( product == null ) return;
        product.setCategoryProduct(null);
    }

    /**
     *
     * @return unmodifiable list of products.
     */
    public List<Product> getProducts() {
        //TODO: maybe grab them from the database instead?
        return Collections.unmodifiableList(products);
    }

    @Override
    public String toString() {
        return "CategoryProduct{" + "id=" + id + ", optLock=" + optLock + ", name=" + name + ", description=" + description + '}';
    }

}