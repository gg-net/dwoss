package eu.ggnet.dwoss.spec.emo;

import javax.persistence.EntityManager;

import eu.ggnet.dwoss.rules.ProductGroup;
import eu.ggnet.dwoss.rules.TradeName;
import eu.ggnet.dwoss.spec.eao.ProductFamilyEao;
import eu.ggnet.dwoss.spec.eao.ProductSeriesEao;
import eu.ggnet.dwoss.spec.entity.ProductFamily;
import eu.ggnet.dwoss.spec.entity.ProductSeries;

/**
 * Product Series Entity Manipulation Object.
 * <p>
 * @author oliver.guenther
 */
public class ProductFamilyEmo {

    private EntityManager em;

    public ProductFamilyEmo(EntityManager em) {
        this.em = em;
    }

    /**
     * Requests a Family, never returning null.
     * <p>
     * @param seriesBrand the brand of the series
     * @param seriesGroup the group of the series
     * @param seriesName  the name of the series
     * @param familyName  the name of the family
     * @return the family, either newly persisted or old existing.
     */
    public ProductFamily request(final TradeName seriesBrand, final ProductGroup seriesGroup, final String seriesName, final String familyName) {
        ProductFamily family = new ProductFamilyEao(em).find(seriesBrand, seriesGroup, seriesName, familyName);
        if ( family == null ) {
            ProductSeries series = new ProductSeriesEao(em).find(seriesBrand, seriesGroup, seriesName);
            if ( series == null ) {
                series = new ProductSeries(seriesBrand, seriesGroup, seriesName);
            }
            family = new ProductFamily(familyName, series);
            em.persist(family);
        }
        return family;
    }
}
