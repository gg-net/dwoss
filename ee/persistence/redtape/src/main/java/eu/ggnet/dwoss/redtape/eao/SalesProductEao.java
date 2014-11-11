/* 
 * Copyright (C) 2014 pascal.perau
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
package eu.ggnet.dwoss.redtape.eao;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import eu.ggnet.dwoss.redtape.entity.SalesProduct;

import eu.ggnet.dwoss.util.persistence.eao.AbstractEao;

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
