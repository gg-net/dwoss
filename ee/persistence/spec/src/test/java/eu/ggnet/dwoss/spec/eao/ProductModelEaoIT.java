package eu.ggnet.dwoss.spec.eao;

import javax.persistence.*;

import org.junit.*;

import eu.ggnet.dwoss.rules.ProductGroup;
import eu.ggnet.dwoss.rules.TradeName;
import eu.ggnet.dwoss.spec.assist.SpecPu;
import eu.ggnet.dwoss.spec.entity.*;

import static org.junit.Assert.*;

/**
 *
 * @author bastian.venz
 */
public class ProductModelEaoIT {

    public ProductModelEaoIT() {
    }

    private EntityManagerFactory emf;

    private EntityManager em;

    private ProductModel model;

    @Before
    public void setUp() {
        emf = Persistence.createEntityManagerFactory(SpecPu.NAME, SpecPu.JPA_IN_MEMORY);
        em = emf.createEntityManager();
        em.getTransaction().begin();
        ProductFamily family = new ProductFamily("Family1");
        ProductSeries series = new ProductSeries(TradeName.SAMSUNG, ProductGroup.MISC, "TestSeries");
        em.persist(series);
        family.setSeries(series);
        em.persist(family);
        model = new ProductModel("Model 1");
        model.setFamily(family);
        em.persist(model);
        em.getTransaction().commit();
    }

    @After
    public void tearDown() {
        em.close();
        emf.close();
    }

    @Test
    public void testFind() {
        em.getTransaction().begin();
        ProductModelEao productModelEao = new ProductModelEao(em);
        ProductModel productModel = productModelEao.find("Model 1");
        assertNotNull(productModel);
        assertEquals(model, productModel);
        assertNull(productModelEao.find("No Model"));
        em.getTransaction().commit();
    }
}
