package eu.ggnet.dwoss.spec.emo;

import javax.persistence.EntityManager;

import eu.ggnet.dwoss.rules.ProductGroup;
import eu.ggnet.dwoss.rules.TradeName;
import eu.ggnet.dwoss.spec.eao.ProductSeriesEao;
import eu.ggnet.dwoss.spec.entity.ProductSeries;

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
