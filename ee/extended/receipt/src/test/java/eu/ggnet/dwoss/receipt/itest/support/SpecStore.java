package eu.ggnet.dwoss.receipt.itest.support;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import eu.ggnet.dwoss.core.common.values.ProductGroup;
import eu.ggnet.dwoss.core.common.values.tradename.TradeName;
import eu.ggnet.dwoss.spec.ee.assist.Specs;
import eu.ggnet.dwoss.spec.ee.entity.ProductFamily;
import eu.ggnet.dwoss.spec.ee.entity.ProductSeries;

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
