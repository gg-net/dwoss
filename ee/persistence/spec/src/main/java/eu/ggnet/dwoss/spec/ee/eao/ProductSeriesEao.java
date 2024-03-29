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
package eu.ggnet.dwoss.spec.ee.eao;

import java.util.List;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.*;

import eu.ggnet.dwoss.core.common.values.ProductGroup;
import eu.ggnet.dwoss.core.common.values.tradename.TradeName;
import eu.ggnet.dwoss.spec.ee.assist.Specs;
import eu.ggnet.dwoss.spec.ee.entity.ProductSeries;
import eu.ggnet.dwoss.core.system.persistence.AbstractEao;
import eu.ggnet.dwoss.spec.api.SpecApi;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;

import static eu.ggnet.dwoss.spec.ee.entity.QProductSeries.productSeries;

/**
 * Entity Access Object for the CPU.
 *
 * @author oliver.guenther
 */
@Stateless
public class ProductSeriesEao extends AbstractEao<ProductSeries> {

    @Inject
    @Specs
    private EntityManager em;

    public ProductSeriesEao() {
        super(ProductSeries.class);
    }

    public ProductSeriesEao(EntityManager em) {
        super(ProductSeries.class);
        this.em = em;
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public ProductSeries find(TradeName brand, ProductGroup group, String name) {
        if ( brand == null || group == null || name == null ) throw new RuntimeException("One Parameter is null");
        TypedQuery<ProductSeries> query = em.createNamedQuery("ProductSeries.byBrandGroupName", ProductSeries.class);
        query.setParameter(1, brand);
        query.setParameter(2, group);
        query.setParameter(3, name);
        try {
            return query.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    public ProductSeries find(String name) {
        return new JPAQuery<ProductSeries>(em).from(productSeries).where(productSeries.name.eq(name)).fetchFirst();
    }
    
    public List<SpecApi.NameId> findAsNameId(TradeName brand, ProductGroup group) {
        return new JPAQuery<SpecApi.NameId>(em)
                .select(Projections.constructor(SpecApi.NameId.class, productSeries.id,productSeries.name))
                .from(productSeries)
                .where(productSeries.brand.eq(brand).and(productSeries.group.eq(group)))
                .orderBy(productSeries.name.asc())
                .fetch();
    } 
}
