package eu.ggnet.dwoss.spec.emo;

import java.util.List;

import javax.persistence.*;

import org.junit.*;

import eu.ggnet.dwoss.rules.ProductGroup;
import eu.ggnet.dwoss.rules.TradeName;
import eu.ggnet.dwoss.spec.assist.SpecPu;
import eu.ggnet.dwoss.spec.eao.ProductSeriesEao;
import eu.ggnet.dwoss.spec.entity.ProductSeries;

import static org.junit.Assert.*;

/**
 *
 * @author oliver.guenther
 */
public class ProductSeriesEmoIT {

    EntityManagerFactory emf;

    EntityManager em;

    ;

    @Before
    public void setUp() {
        emf = Persistence.createEntityManagerFactory(SpecPu.NAME, SpecPu.JPA_IN_MEMORY);
        em = emf.createEntityManager();
    }

    @After
    public void tearDown() {
        em.close();
        emf.close();
    }

    @Test
    public void testRequestBrandGroupName() {
        TradeName b1 = TradeName.APPLE;
        ProductGroup g1 = ProductGroup.PROJECTOR;
        String n1 = "SERIES";

        TradeName b2 = TradeName.LENOVO;
        ProductGroup g2 = ProductGroup.DESKTOP;
        String n2 = "SERIES";

        em.getTransaction().begin();
        em.persist(new ProductSeries(b1, g1, n1));
        em.getTransaction().commit();

        em.getTransaction().begin();
        ProductSeriesEmo seriesEmo = new ProductSeriesEmo(em);
        ProductSeries productSeries = seriesEmo.request(b1, g1, n1);
        assertNotNull(productSeries);
        assertEquals(b1, productSeries.getBrand());
        assertEquals(g1, productSeries.getGroup());
        assertEquals(n1, productSeries.getName());
        em.getTransaction().commit();

        em.getTransaction().begin();
        productSeries = seriesEmo.request(b2, g2, n2);
        assertNotNull(productSeries);
        assertEquals(b2, productSeries.getBrand());
        assertEquals(g2, productSeries.getGroup());
        assertEquals(n2, productSeries.getName());
        em.getTransaction().commit();

        em.getTransaction().begin();
        seriesEmo.request(b2, g2, n2);
        seriesEmo.request(b2, g2, n2);
        seriesEmo.request(b2, g2, n2);

        List<ProductSeries> pss = new ProductSeriesEao(em).findAll();
        assertNotNull(pss);
        assertEquals("Only Two Elements should exist", 2, pss.size());
        em.getTransaction().commit();
    }
}
