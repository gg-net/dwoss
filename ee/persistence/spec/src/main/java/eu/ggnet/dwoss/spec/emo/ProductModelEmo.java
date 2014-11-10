package eu.ggnet.dwoss.spec.emo;

import javax.persistence.EntityManager;

import eu.ggnet.dwoss.rules.ProductGroup;
import eu.ggnet.dwoss.rules.TradeName;
import eu.ggnet.dwoss.spec.eao.*;
import eu.ggnet.dwoss.spec.entity.*;

/**
 * Product Model Entity Manipulation Object.
 * <p>
 * @author oliver.guenther
 */
public class ProductModelEmo {

    private EntityManager em;

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
