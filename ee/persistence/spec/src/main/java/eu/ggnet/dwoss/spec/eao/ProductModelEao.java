package eu.ggnet.dwoss.spec.eao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import eu.ggnet.dwoss.rules.ProductGroup;
import eu.ggnet.dwoss.rules.TradeName;
import eu.ggnet.dwoss.util.persistence.eao.AbstractEao;
import eu.ggnet.dwoss.spec.entity.ProductModel;

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
