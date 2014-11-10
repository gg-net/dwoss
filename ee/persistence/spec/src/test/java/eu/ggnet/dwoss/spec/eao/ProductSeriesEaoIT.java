package eu.ggnet.dwoss.spec.eao;

import javax.persistence.*;

import org.junit.*;

import eu.ggnet.dwoss.rules.ProductGroup;
import eu.ggnet.dwoss.rules.TradeName;
import eu.ggnet.dwoss.spec.assist.SpecPu;
import eu.ggnet.dwoss.spec.entity.ProductSeries;

import static org.junit.Assert.*;

/**
 *
 * @author pascal.perau
 */
public class ProductSeriesEaoIT {

    EntityManagerFactory emf;

    EntityManager em;

    ProductSeries series;

    @Before
    public void setUp() {
        emf = Persistence.createEntityManagerFactory(SpecPu.NAME, SpecPu.JPA_IN_MEMORY);
        em = emf.createEntityManager();
        em.getTransaction().begin();
        series = new ProductSeries(TradeName.SAMSUNG, ProductGroup.MISC, "GG-Net uber Multicore");
        em.persist(series);
        em.getTransaction().commit();
    }

    @After
    public void tearDown() {
        em.close();
        emf.close();
    }

    @Test
    public void testFindBrandGroupName() {
        em.getTransaction().begin();
        ProductSeriesEao seriesEao = new ProductSeriesEao(em);
        ProductSeries productSeries = seriesEao.find(series.getBrand(), series.getGroup(), series.getName());
        assertNull(seriesEao.find(TradeName.SAMSUNG, ProductGroup.MISC, "Gibbet nich"));
        assertNotNull(productSeries);
        assertEquals(series, productSeries);
        em.getTransaction().commit();
    }
}
