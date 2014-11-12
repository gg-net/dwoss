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
package eu.ggnet.dwoss.stock.eao;

import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;

import eu.ggnet.dwoss.stock.entity.Stock;
import eu.ggnet.dwoss.stock.entity.StockLocation;

import eu.ggnet.dwoss.util.persistence.eao.AbstractEao;

/**
 * JPA Service for {@link StockLocation}
 *
 */
public class StockLocationEao extends AbstractEao<StockLocation> {

    private EntityManager em;

    public StockLocationEao(EntityManager em) {
        super(StockLocation.class);
        this.em = em;
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    /**
     * Returns a list containing all stock location where the name is like the search.
     *
     * @param search a string to identify the stock location, a * or % will be interpreted as wildcard.
     * @return a list which contains stock locations or is empty
     */
    public List<StockLocation> find(String search) {
        if ( search == null ) return Collections.EMPTY_LIST;
        return em.createNamedQuery("StockLocation.likeName", StockLocation.class)
                .setParameter(1, search.replaceAll("\\*", "%")).getResultList();
    }

    /**
     * Returns a list containing all stock location of the supplied stock where the name is like the search.
     *
     * @param stock  the stock to be searched
     * @param search a string to identify the stock location, a * or % will be interpreted as wildcard.
     * @return a list which contains stock locations or is empty
     */
    public List<StockLocation> find(Stock stock, String search) {
        if ( search == null ) return Collections.EMPTY_LIST;
        return em.createNamedQuery("StockLocation.byStockLikeName", StockLocation.class)
                .setParameter(1, stock).setParameter(2, search.replaceAll("\\*", "%")).getResultList();
    }
}
