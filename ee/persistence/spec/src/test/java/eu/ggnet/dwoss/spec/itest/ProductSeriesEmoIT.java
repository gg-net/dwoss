package eu.ggnet.dwoss.spec.itest;

import eu.ggnet.dwoss.spec.itest.support.ArquillianProjectArchive;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.UserTransaction;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.rules.ProductGroup;
import eu.ggnet.dwoss.rules.TradeName;
import eu.ggnet.dwoss.spec.ee.assist.Specs;
import eu.ggnet.dwoss.spec.ee.eao.ProductSeriesEao;
import eu.ggnet.dwoss.spec.ee.emo.ProductSeriesEmo;
import eu.ggnet.dwoss.spec.ee.entity.ProductSeries;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 *
 * @author oliver.guenther
 */
@RunWith(Arquillian.class)

public class ProductSeriesEmoIT extends ArquillianProjectArchive {

    @Inject
    @Specs
    private EntityManager em;

    @Inject
    private UserTransaction utx;

    @Test
    public void testRequestBrandGroupName() throws Exception {
        TradeName b1 = TradeName.APPLE;
        ProductGroup g1 = ProductGroup.PROJECTOR;
        String n1 = "SERIES";

        TradeName b2 = TradeName.LENOVO;
        ProductGroup g2 = ProductGroup.DESKTOP;
        String n2 = "SERIES";

        utx.begin();
        em.joinTransaction();
        em.persist(new ProductSeries(b1, g1, n1));
        utx.commit();

        utx.begin();
        em.joinTransaction();
        ProductSeriesEmo seriesEmo = new ProductSeriesEmo(em);
        ProductSeries productSeries = seriesEmo.request(b1, g1, n1);
        assertNotNull(productSeries);
        assertEquals(b1, productSeries.getBrand());
        assertEquals(g1, productSeries.getGroup());
        assertEquals(n1, productSeries.getName());
        utx.commit();

        utx.begin();
        em.joinTransaction();
        productSeries = seriesEmo.request(b2, g2, n2);
        assertNotNull(productSeries);
        assertEquals(b2, productSeries.getBrand());
        assertEquals(g2, productSeries.getGroup());
        assertEquals(n2, productSeries.getName());
        utx.commit();

        utx.begin();
        em.joinTransaction();
        seriesEmo.request(b2, g2, n2);
        seriesEmo.request(b2, g2, n2);
        seriesEmo.request(b2, g2, n2);

        List<ProductSeries> pss = new ProductSeriesEao(em).findAll();
        assertNotNull(pss);
        assertEquals("Only Two Elements should exist", 2, pss.size());
        utx.commit();
    }
}
