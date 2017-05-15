package eu.ggnet.dwoss.spec.itest;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.UserTransaction;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.rules.ProductGroup;
import eu.ggnet.dwoss.rules.TradeName;
import eu.ggnet.dwoss.spec.assist.Specs;
import eu.ggnet.dwoss.spec.eao.ProductSeriesEao;
import eu.ggnet.dwoss.spec.entity.ProductSeries;

import static org.junit.Assert.*;

/**
 *
 * @author pascal.perau
 */
@RunWith(Arquillian.class)
public class ProductSeriesEaoIT extends ArquillianProjectArchive {

    @Inject
    @Specs
    private EntityManager em;

    @Inject
    private UserTransaction utx;

    @Test
    public void testFindBrandGroupName() throws Exception {
        utx.begin();
        em.joinTransaction();
        ProductSeries series = new ProductSeries(TradeName.SAMSUNG, ProductGroup.MISC, "GG-Net uber Multicore");
        em.persist(series);
        utx.commit();

        utx.begin();
        em.joinTransaction();
        ProductSeriesEao seriesEao = new ProductSeriesEao(em);
        ProductSeries productSeries = seriesEao.find(series.getBrand(), series.getGroup(), series.getName());
        assertNull(seriesEao.find(TradeName.SAMSUNG, ProductGroup.MISC, "Gibbet nich"));
        assertNotNull(productSeries);
        assertEquals(series, productSeries);
        utx.commit();
    }
}
