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

import java.util.*;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import eu.ggnet.dwoss.spec.ee.assist.Specs;
import eu.ggnet.dwoss.spec.ee.entity.ProductSpec;
import eu.ggnet.dwoss.core.system.persistence.AbstractEao;

/**
 * Entity Access Object for the ProductSpec.
 *
 * @author oliver.guenther
 */
@Stateless
public class ProductSpecEao extends AbstractEao<ProductSpec> {

    @Inject
    @Specs
    private EntityManager em;

    public ProductSpecEao(EntityManager em) {
        super(ProductSpec.class);
        this.em = em;
    }

    public ProductSpecEao() {
        super(ProductSpec.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public ProductSpec findByPartNo(String partNo) {
        if ( partNo == null ) return null;
        TypedQuery<ProductSpec> query = em.createNamedQuery("ProductSpec.byPartNo", ProductSpec.class);
        query.setParameter(1, partNo);
        List<ProductSpec> result = query.getResultList();
        if ( result.isEmpty() ) return null;
        if ( result.size() == 1 ) return result.get(0);
        throw new RuntimeException("More than one ProductSpec for partNo=" + partNo + ", unlikely but possible scenario. Olli should have removed this excpetion");
    }

    public ProductSpec findByProductId(long id) {
        TypedQuery<ProductSpec> query = em.createNamedQuery("ProductSpec.byProductId", ProductSpec.class);
        query.setParameter(1, id);
        List<ProductSpec> result = query.getResultList();
        if ( result.isEmpty() ) return null;
        return result.get(0);
    }

    @SuppressWarnings("unchecked")
    public List<ProductSpec> findByProductIds(Collection<Long> productIds) {
        if ( productIds == null || productIds.isEmpty() ) return Collections.EMPTY_LIST;
        return em.createNamedQuery("ProductSpec.byProductIds", ProductSpec.class).setParameter(1, productIds).getResultList();
    }
    
}
