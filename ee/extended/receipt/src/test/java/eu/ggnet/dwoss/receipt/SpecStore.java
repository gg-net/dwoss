package eu.ggnet.dwoss.receipt;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import eu.ggnet.dwoss.rules.ProductGroup;
import eu.ggnet.dwoss.rules.TradeName;
import eu.ggnet.dwoss.spec.assist.Specs;
import eu.ggnet.dwoss.spec.entity.ProductFamily;
import eu.ggnet.dwoss.spec.entity.ProductSeries;

/**
 *
 * @author oliver.guenther
 */
@Stateless
public class SpecStore {

    @Inject
    @Specs
    private EntityManager em;

    public ProductSeries makeSeries(TradeName brand, ProductGroup group, String name) {
        ProductSeries series = new ProductSeries(brand, group, name);
        em.persist(series);
        return series;
    }

    public ProductFamily makeFamily(String name, ProductSeries series) {
        series = em.find(ProductSeries.class, series.getId());
        ProductFamily family = new ProductFamily("Family 2");
        family.setSeries(series);
        em.persist(family);
        return family;
    }

    public <T> T persist(T entity) {
        em.persist(entity);
        return entity;
    }
}
