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
package eu.ggnet.dwoss.uniqueunit.op;

import javax.ejb.Stateless;
import javax.inject.Inject;

import eu.ggnet.dwoss.uniqueunit.assist.UniqueUnitSupport;
import eu.ggnet.dwoss.uniqueunit.eao.ProductEao;
import eu.ggnet.dwoss.uniqueunit.entity.PriceType;
import eu.ggnet.dwoss.uniqueunit.entity.Product;

/**
 * ProductOperation.
 * <p/>
 * @author oliver.guenther
 */
@Stateless
public class ProductOperation {

    @Inject
    private UniqueUnitSupport uus;

    /**
     * Update a price of a product by id.
     * <p/>
     * @param productId the product id
     * @param priceType the type of price to update
     * @param price     the price
     * @param comment   the comment for the price history
     * @return the updated product
     */
    public Product updatePrice(long productId, PriceType priceType, double price, String comment) {
        Product product = new ProductEao(uus.getEntityManager()).findById(productId);
        if ( product == null ) return null;
        product.setPrice(priceType, price, comment);
        return product;
    }
}
