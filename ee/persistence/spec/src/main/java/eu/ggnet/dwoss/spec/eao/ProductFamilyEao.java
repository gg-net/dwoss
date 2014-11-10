package eu.ggnet.dwoss.spec.eao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import eu.ggnet.dwoss.rules.ProductGroup;
import eu.ggnet.dwoss.rules.TradeName;
import eu.ggnet.dwoss.util.persistence.eao.AbstractEao;
import eu.ggnet.dwoss.spec.entity.ProductFamily;

/**
 * Entity Access Object for the CPU.
 *
 * @author oliver.guenther
 */
public class ProductFamilyEao extends AbstractEao<ProductFamily> {

    private EntityManager em;

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
}
