package eu.ggnet.dwoss.redtape.itest;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.UserTransaction;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.redtape.ee.assist.RedTapes;
import eu.ggnet.dwoss.redtape.ee.eao.SalesProductEao;
import eu.ggnet.dwoss.redtape.ee.entity.SalesProduct;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author oliver.guenther
 */
@RunWith(Arquillian.class)
public class SalesProductIT extends ArquillianProjectArchive {

    @Inject
    @RedTapes
    private EntityManager em;

    @Inject
    private UserTransaction utx;

    @Test
    public void testSalesProduct() throws Exception {
        utx.begin();
        em.joinTransaction();
        SalesProduct product1 = new SalesProduct("This.IsPart.One", "Part One", 12d, 1, "This is a description");
        SalesProduct product2 = new SalesProduct("This.IsPart.Two", "Part Two", 12d, 2, "This is a descriptionThis is a description");
        SalesProduct product3 = new SalesProduct("This.IsPart.Three", "Part Three", 12d, 3, "This is a descriptionThis is a descriptionThis is a description");
        SalesProduct product4 = new SalesProduct("This.IsPart.Four", "Part Four", 12d, 4, "This is a descriptionThis is a descriptionThis is a descriptionThis is a descriptionThis is a description");
        em.persist(product1);
        em.persist(product2);
        em.persist(product3);
        em.persist(product4);

        utx.commit();
        utx.begin();
        em.joinTransaction();

        SalesProductEao eao = new SalesProductEao(em);

        SalesProduct findByPartNo = eao.findById("This.IsPart.One");
        assertEquals("FindbyPartNo returns a other Object as expected!", findByPartNo, product1);
        SalesProduct findByUniqueUnitProductId = eao.findByUniqueUnitProductId(3);
        assertEquals("Die Objekte der Methode findByUniqueUnitProductId waren nicht gleich", product3, findByUniqueUnitProductId);
        utx.commit();
    }

}
