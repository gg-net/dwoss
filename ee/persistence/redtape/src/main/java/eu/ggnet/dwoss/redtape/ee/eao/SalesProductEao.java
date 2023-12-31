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
package eu.ggnet.dwoss.redtape.ee.eao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;

import eu.ggnet.dwoss.redtape.ee.entity.SalesProduct;
import eu.ggnet.dwoss.core.system.persistence.AbstractEao;

/**
 * @author bastian.venz
 * @author oliver.guenther
 * @author pascal.perau
 */
public class SalesProductEao extends AbstractEao<SalesProduct> {

    private EntityManager em;

    public SalesProductEao(EntityManager em) {
        super(SalesProduct.class);
        this.em = em;
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public SalesProduct findByUniqueUnitProductId(long uniqueUnitProductId) {
        Query createNamedQuery = em.createNamedQuery("byUniqueUnitProductId");
        createNamedQuery.setParameter(1, uniqueUnitProductId);
        try {
            return (SalesProduct)createNamedQuery.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }
}
