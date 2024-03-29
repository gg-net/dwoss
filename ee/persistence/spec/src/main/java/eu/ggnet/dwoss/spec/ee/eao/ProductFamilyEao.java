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

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import eu.ggnet.dwoss.core.common.values.ProductGroup;
import eu.ggnet.dwoss.core.common.values.tradename.TradeName;
import eu.ggnet.dwoss.core.system.persistence.AbstractEao;
import eu.ggnet.dwoss.spec.api.SpecApi;
import eu.ggnet.dwoss.spec.ee.assist.Specs;
import eu.ggnet.dwoss.spec.ee.entity.ProductFamily;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import static eu.ggnet.dwoss.spec.ee.entity.QProductFamily.productFamily;

/**
 * Entity Access Object for the CPU.
 *
 * @author oliver.guenther
 */
@Stateless
public class ProductFamilyEao extends AbstractEao<ProductFamily> {

    @Inject
    @Specs
    private EntityManager em;

    public ProductFamilyEao() {
        super(ProductFamily.class);
    }

    public ProductFamilyEao(EntityManager em) {
        super(ProductFamily.class);
        this.em = em;
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    /**
     * Finds a Family or null if not existent.
     * <p>
     * @param seriesBrand the brand of the series
     * @param seriesGroup the group of the series
     * @param seriesName  the name of the series
     * @param familyName  the name of the family
     * <p>
     * @return the family, or null if not existent.
     */
    public ProductFamily find(final TradeName seriesBrand, final ProductGroup seriesGroup, final String seriesName, final String familyName) {
        TypedQuery<ProductFamily> query = em.createNamedQuery("ProductFamily.byNameSeries", ProductFamily.class);
        query.setParameter(1, seriesBrand);
        query.setParameter(2, seriesGroup);
        query.setParameter(3, seriesName);
        query.setParameter(4, familyName);
        List<ProductFamily> familys = query.getResultList();
        if ( familys.isEmpty() ) return null;
        return familys.get(0);
    }

    /**
     * Finds a Family or null if not existent.
     * <p>
     * @param name the name
     * @return a Family or null if not existent.
     */
    public ProductFamily find(String name) {
        if ( name == null ) return null;
        TypedQuery<ProductFamily> query = em.createNamedQuery("ProductFamily.byName", ProductFamily.class);
        query.setParameter(1, name);
        List<ProductFamily> familys = query.getResultList();
        if ( familys.isEmpty() ) return null;
        return familys.get(0);
    }

    public List<SpecApi.NameId> findAsNameId(long seriesId) {
        return new JPAQuery<SpecApi.NameId>(em)
                .select(Projections.constructor(SpecApi.NameId.class, productFamily.id, productFamily.name))
                .from(productFamily)
                .where(productFamily.series.id.eq(seriesId))
                .orderBy(productFamily.name.asc())
                .fetch();
    }
}
