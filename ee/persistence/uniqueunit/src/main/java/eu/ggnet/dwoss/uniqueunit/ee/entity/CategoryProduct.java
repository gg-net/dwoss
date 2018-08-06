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
package eu.ggnet.dwoss.uniqueunit.ee.entity;

import eu.ggnet.dwoss.util.persistence.entity.AbstractBidirectionalListWrapper;

import java.io.Serializable;
import java.util.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import eu.ggnet.dwoss.common.api.values.SalesChannel;
import eu.ggnet.dwoss.uniqueunit.ee.format.UniqueUnitFormater;
import eu.ggnet.dwoss.util.TwoDigits;
import eu.ggnet.dwoss.util.persistence.EagerAble;

import lombok.*;

import static eu.ggnet.dwoss.common.api.values.SalesChannel.UNKNOWN;
import static javax.persistence.CascadeType.*;
import static javax.persistence.FetchType.EAGER;

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
public class CategoryProduct implements Serializable, EagerAble {

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
    @OneToMany(cascade = {MERGE, REFRESH, PERSIST, DETACH}, mappedBy = "categoryProduct", fetch = EAGER)
    List<Product> products = new ArrayList<>(); // Package private for bidirectional handling. Eager as long as it doesn't slow down anything.

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
    @NotNull
    @Basic(optional = false)
    private SalesChannel salesChannel = SalesChannel.UNKNOWN;

    public void setPrice(PriceType type, double price, String comment) {
        price = TwoDigits.round(price);
        if ( TwoDigits.equals(getPrice(type), price) ) {
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
        return prices.getOrDefault(type, 0.0);
    }

    public Map<PriceType, Double> getPrices() {
        return Collections.unmodifiableMap(prices);
    }

    public List<PriceHistory> getPriceHistory() {
        return Collections.unmodifiableList(priceHistories);
    }

    /**
     * Sets the saleschanel, a null will set to unkonwn.
     *
     * @param salesChannel the saleschanel.
     */
    public void setSalesChannel(SalesChannel salesChannel) {
        if ( salesChannel == null ) this.salesChannel = UNKNOWN;
        else this.salesChannel = salesChannel;
    }

    /**
     * Returns a wrapped bidirectional list implementation, which executes all changes on the products collection and the product.
     *
     * @return a wrapped bidirectional list.
     */
    public List<Product> getProducts() {
        return new AbstractBidirectionalListWrapper<Product>(this.products) {

            @Override
            protected void update(Product e, boolean add) {
                if ( add ) e.setCategoryProduct(CategoryProduct.this);
                else e.setCategoryProduct(null);
            }
        };
    }

    @Override
    public String toString() {
        return "CategoryProduct{" + "id=" + id + ", optLock=" + optLock + ", name=" + name + ", description=" + description + '}';
    }

    public String toHtml(EnumSet<PriceType> priceTypes, boolean history) {
        if ( priceTypes == null ) return "Allowed PriceTypes are null";
        StringBuilder sb = new StringBuilder("<p><b>CategoryProduct</b><br />");
        sb.append("Name: ").append(name).append("<br />");
        sb.append("Description: ").append(description).append("<br />");
        sb.append("SalesChannel: ").append(salesChannel).append("<br />");
        Map<PriceType, Double> showPrices = new HashMap<>(prices);
        for (PriceType priceType : EnumSet.complementOf(priceTypes)) {
            showPrices.remove(priceType);
        }
        return sb.toString() + UniqueUnitFormater.toHtmlPriceInformation(showPrices, priceHistories) + "</p";
    }

    @Override
    public void fetchEager() {
        priceHistories.size();
        for (Product product : products) {
            product.fetchEager();
        }
    }

}
