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
import eu.ggnet.dwoss.spec.ee.assist.Specs;
import eu.ggnet.dwoss.spec.ee.eao.*;
import eu.ggnet.dwoss.spec.ee.entity.*;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

/**
 * Product Model Entity Manipulation Object.
 * <p>
 * @author oliver.guenther
 */
@Stateless
public class ProductModelEmo {

    @Inject
    @Specs
    private EntityManager em;

    public ProductModelEmo() {
    }
    
    public ProductModelEmo(EntityManager em) {
        this.em = em;
    }

    /**
     * Requests a Model, never returning null.
     * <p>
     * @param seriesBrand the brand of the series
     * @param seriesGroup the group of the series
     * @param seriesName  the name of the series
     * @param familyName  the name of the family
     * @param modelName   the name of the model
     * @return the model, either newly persisted or old existing.
     */
    public ProductModel request(final TradeName seriesBrand, final ProductGroup seriesGroup, final String seriesName, final String familyName, final String modelName) {
        ProductModel model = weakRequest(seriesBrand, seriesGroup, seriesName, familyName, modelName);
        if ( model.getId() == 0 ) em.persist(model);
        return model;
    }

    /**
     * Requests a Model, never returning null, but a new Instance will not be persisted.
     * <p>
     * @param seriesBrand the brand of the series
     * @param seriesGroup the group of the series
     * @param seriesName  the name of the series
     * @param familyName  the name of the family
     * @param modelName   the name of the model
     * @return the model, either newly persisted or old existing.
     */
    public ProductModel weakRequest(final TradeName seriesBrand, final ProductGroup seriesGroup, final String seriesName, final String familyName, final String modelName) {
        ProductModel model = new ProductModelEao(em).find(seriesBrand, seriesGroup, seriesName, familyName, modelName);
        if ( model == null ) {
            ProductFamily family = new ProductFamilyEao(em).find(seriesBrand, seriesGroup, seriesName, familyName);
            if ( family == null ) {
                ProductSeries series = new ProductSeriesEao(em).find(seriesBrand, seriesGroup, seriesName);
                if ( series == null ) {
                    series = new ProductSeries(seriesBrand, seriesGroup, seriesName);
                }
                family = new ProductFamily(familyName, series);
            }
            model = new ProductModel(modelName, family);
        }
        return model;
    }
}
