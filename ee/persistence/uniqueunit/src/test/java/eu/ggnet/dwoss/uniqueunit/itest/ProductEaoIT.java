package eu.ggnet.dwoss.uniqueunit.itest;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.UserTransaction;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.common.api.values.ProductGroup;
import eu.ggnet.dwoss.common.api.values.TradeName;
import eu.ggnet.dwoss.uniqueunit.ee.assist.UniqueUnits;
import eu.ggnet.dwoss.uniqueunit.ee.eao.ProductEao;
import eu.ggnet.dwoss.uniqueunit.ee.entity.Product;
import eu.ggnet.dwoss.uniqueunit.itest.support.ArquillianProjectArchive;

import static org.junit.Assert.*;

/**
 *
 * @author pascal.perau
 */
@RunWith(Arquillian.class)
public class ProductEaoIT extends ArquillianProjectArchive {

    @Inject
    UserTransaction utx;

    @Inject
    @UniqueUnits
    EntityManager em;

    Product p;

    @Test
    public void testFindPartNo() throws Exception {
        utx.begin();
        em.joinTransaction();
        p = new Product(ProductGroup.MISC, TradeName.ACER, "AA.BBBBB.CCC", "Evil Acer Handy of Doom");
        em.persist(p);
        utx.commit();

        utx.begin();
        em.joinTransaction();

        ProductEao productEao = new ProductEao(em);
        assertNotNull(productEao.findByPartNo(p.getPartNo()));
        assertNull(productEao.findByPartNo("bb.ccccc.aa"));
        assertEquals(p, productEao.findByPartNo("AA.BBBBB.CCC"));
        utx.commit();
    }
}
