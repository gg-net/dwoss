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
package eu.ggnet.dwoss.uniqueunit.ee.eao;

import java.util.*;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import eu.ggnet.dwoss.common.api.values.TradeName;
import eu.ggnet.dwoss.uniqueunit.ee.assist.UniqueUnits;
import eu.ggnet.dwoss.uniqueunit.ee.entity.Product;
import eu.ggnet.dwoss.core.system.persistence.AbstractEao;

import com.querydsl.jpa.impl.JPAQuery;

import static eu.ggnet.dwoss.uniqueunit.ee.entity.QProduct.product;

/**
 *
 * @author oliver.guenther
 */
@Stateless
public class ProductEao extends AbstractEao<Product> {

    @Inject
    @UniqueUnits
    private EntityManager em;

    public ProductEao() {
        super(Product.class);
    }

    public ProductEao(EntityManager em) {
        this();
        this.em = em;
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    /**
     * Returns a Product by partNo.
     * <p>
     * @param partNo the partNo
     * @return Product by partNo, may be null.
     */
    public Product findByPartNo(String partNo) {
        return new JPAQuery<Product>(em).from(product).where(product.partNo.eq(partNo)).fetchFirst();
    }

    /**
     * Returns a Product by gtin.
     *
     * @param gtin the gtin
     * @return product by gtin, may be null.
     */
    public Product findByGtin(long gtin) {
        return new JPAQuery<Product>(em).from(product).where(product.gtin.eq(gtin)).fetchFirst();
    }

    public List<Product> findByPartNos(Collection<String> partNos) {
        if ( partNos == null || partNos.isEmpty() ) return new ArrayList<>();
        return new JPAQuery<Product>(em).from(product).where(product.partNo.in(partNos)).fetch();
    }

    /**
     * Returns all products which match the supplied tradenames.
     * <p>
     * @param tradenames the tradenames.
     * @return all products which match the supplied tradenames.
     */
    public List<Product> findByTradeNames(Collection<TradeName> tradenames) {
        if ( tradenames == null || tradenames.isEmpty() ) return new ArrayList<>();
        return new JPAQuery<Product>(em).from(product).where(product.tradeName.in(tradenames)).fetch();
    }

    /**
     * Return all UniqueUnits which have the supplied contractor.
     * <p/>
     * @param contractor the contractor.
     * @return all UniqueUnits which have the supplied contractor.
     */
    public List<Product> findByContractor(TradeName contractor) {
        return em.createNamedQuery("Product.byContractor", Product.class).setParameter(1, contractor).getResultList();
    }
}
