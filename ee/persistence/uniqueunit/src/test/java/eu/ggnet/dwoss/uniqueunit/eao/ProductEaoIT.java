package eu.ggnet.dwoss.uniqueunit.eao;

import eu.ggnet.dwoss.uniqueunit.eao.ProductEao;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import eu.ggnet.dwoss.rules.ProductGroup;
import eu.ggnet.dwoss.rules.TradeName;
import eu.ggnet.dwoss.uniqueunit.assist.UniqueUnitPu;
import eu.ggnet.dwoss.uniqueunit.entity.Product;

import static org.junit.Assert.*;

/**
 *
 * @author pascal.perau
 */
public class ProductEaoIT {

    EntityManagerFactory emf;

    EntityManager em;

    Product p;

    @Before
    public void setUp() {
        emf = Persistence.createEntityManagerFactory(UniqueUnitPu.NAME, UniqueUnitPu.JPA_IN_MEMORY);
        em = emf.createEntityManager();
        em.getTransaction().begin();
        p = new Product(ProductGroup.MISC, TradeName.ACER, "AA.BBBBB.CCC", "Evil Acer Handy of Doom");
        em.persist(p);
        em.getTransaction().commit();
    }

    @After
    public void tearDown() {
        em.close();
        emf.close();
    }

    @Test
    public void testFindPartNo() {
        em.getTransaction().begin();
        ProductEao productEao = new ProductEao(em);
        assertNotNull(productEao.findByPartNo(p.getPartNo()));
        assertNull(productEao.findByPartNo("bb.ccccc.aa"));
        assertEquals(p, productEao.findByPartNo("AA.BBBBB.CCC"));
        em.getTransaction().commit();
    }
}
