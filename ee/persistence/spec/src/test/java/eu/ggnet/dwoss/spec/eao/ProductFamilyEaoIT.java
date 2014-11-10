package eu.ggnet.dwoss.spec.eao;

import javax.persistence.*;

import org.junit.*;

import eu.ggnet.dwoss.rules.ProductGroup;
import eu.ggnet.dwoss.rules.TradeName;
import eu.ggnet.dwoss.spec.assist.SpecPu;
import eu.ggnet.dwoss.spec.entity.ProductFamily;
import eu.ggnet.dwoss.spec.entity.ProductSeries;

import static org.junit.Assert.*;

/**
 *
 * @author bastian.venz
 */
public class ProductFamilyEaoIT {

    public ProductFamilyEaoIT() {
    }

    private EntityManagerFactory emf;

    private EntityManager em;

    private ProductFamily productFamily;

    @Before
    public void setUp() {
        emf = Persistence.createEntityManagerFactory(SpecPu.NAME, SpecPu.JPA_IN_MEMORY);
        em = emf.createEntityManager();
        em.getTransaction().begin();
        productFamily = new ProductFamily("PF1");
        ProductSeries testSeries1 = new ProductSeries(TradeName.HP, ProductGroup.MISC, "TestSeries1");
        em.persist(testSeries1);
        productFamily.setSeries(testSeries1);
        em.persist(productFamily);

        ProductFamily productFamily1 = new ProductFamily("PF2");
        ProductSeries testSeries2 = new ProductSeries(TradeName.HP, ProductGroup.COMMENTARY, "TestSeries2");
        em.persist(testSeries2);
        productFamily1.setSeries(testSeries2);
        em.persist(productFamily1);

        em.getTransaction().commit();
    }

    @After
    public void tearDown() {
        em.close();
        emf.close();
    }

    /**
     * Test of find method, of class ProductFamilyEao.
     */
    @Test
    public void testFind() {
        em.getTransaction().begin();
        ProductFamilyEao familyEao = new ProductFamilyEao(em);
        ProductFamily testFamily = familyEao.find("PF1");
        assertNotNull(testFamily);
        assertNull(familyEao.find("NoFamily"));
        assertEquals(productFamily.getId(), testFamily.getId());
        em.getTransaction().commit();
    }
}
