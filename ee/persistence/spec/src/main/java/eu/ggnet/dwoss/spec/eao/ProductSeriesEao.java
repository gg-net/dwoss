package eu.ggnet.dwoss.spec.eao;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.*;

import eu.ggnet.dwoss.rules.ProductGroup;
import eu.ggnet.dwoss.rules.TradeName;
import eu.ggnet.dwoss.util.persistence.eao.AbstractEao;
import eu.ggnet.dwoss.spec.assist.Specs;
import eu.ggnet.dwoss.spec.entity.ProductSeries;

/**
 * Entity Access Object for the CPU.
 *
 * @author oliver.guenther
 */
@Stateless
public class ProductSeriesEao extends AbstractEao<ProductSeries> {

    @Inject
    @Specs
    private EntityManager em;

    public ProductSeriesEao() {
        super(ProductSeries.class);
    }

    public ProductSeriesEao(EntityManager em) {
        super(ProductSeries.class);
        this.em = em;
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public ProductSeries find(TradeName brand, ProductGroup group, String name) {
        if ( brand == null || group == null || name == null ) throw new RuntimeException("One Parameter is null");
        TypedQuery<ProductSeries> query = em.createNamedQuery("ProductSeries.byBrandGroupName", ProductSeries.class);
        query.setParameter(1, brand);
        query.setParameter(2, group);
        query.setParameter(3, name);
        try {
            return query.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }
}
