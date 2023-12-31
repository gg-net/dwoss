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
import eu.ggnet.dwoss.spec.ee.entity.ProductModel;

/**
 * Entity Access Object for the CPU.
 *
 * @author oliver.guenther
 */
public class ProductModelEao extends AbstractEao<ProductModel> {

    private EntityManager em;

    public ProductModelEao(EntityManager em) {
        super(ProductModel.class);
        this.em = em;
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    /**
     * Finds a Model or null if not existent.
     * 
     * @param seriesBrand the brand of the series
     * @param seriesGroup the group of the series
     * @param seriesName the name of the series
     * @param familyName the name of the family
     * @param modelName the name of the Model
     * 
     * @return the model, or null if not existent.
     */
    public ProductModel find(final TradeName seriesBrand, final ProductGroup seriesGroup, final String seriesName, final String familyName, final String modelName) {
        TypedQuery<ProductModel> query = em.createNamedQuery("ProductModel.byNameFamilySeries", ProductModel.class);
        query.setParameter(1, seriesBrand);
        query.setParameter(2, seriesGroup);
        query.setParameter(3, seriesName);
        query.setParameter(4, familyName);
        query.setParameter(5, modelName);
        List<ProductModel> models = query.getResultList();
        if (models.isEmpty()) return null;
        return models.get(0);
    }
    
    /**
     * Finds a Model or null if not existent.
     * 
     * @param modelName the name of the Model
     * 
     * @return the model, or null if not existent.
     */
    public ProductModel find(String modelName) {
        TypedQuery<ProductModel> query = em.createNamedQuery("ProductModel.byName", ProductModel.class);
        query.setParameter(1, modelName);
        List<ProductModel> models = query.getResultList();
        if (models.isEmpty()) return null;
        return models.get(0);
    }
}
