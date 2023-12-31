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
package eu.ggnet.dwoss.spec.ee.emo;

import jakarta.persistence.EntityManager;

import eu.ggnet.dwoss.core.common.values.ProductGroup;
import eu.ggnet.dwoss.core.common.values.tradename.TradeName;
import eu.ggnet.dwoss.spec.ee.eao.ProductSeriesEao;
import eu.ggnet.dwoss.spec.ee.entity.ProductSeries;

/**
 * Product Series Entity Manipulation Object.
 * <p>
 * @author oliver.guenther
 */
public class ProductSeriesEmo {

    private EntityManager em;

    public ProductSeriesEmo(EntityManager em) {
        this.em = em;
    }

    /**
     * Requests a Series.
     * <p>
     * @param brand the brand
     * @param group the group
     * @param name  the name
     * @return the series, either newly persisted or old existing.
     */
    public ProductSeries request(final TradeName brand, final ProductGroup group, final String name) {
        ProductSeries series = new ProductSeriesEao(em).find(brand, group, name);
        if ( series == null ) {
            series = new ProductSeries(brand, group, name);
            em.persist(series);
        }
        return series;
    }

}
