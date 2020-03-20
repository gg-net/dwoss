package eu.ggnet.dwoss.redtape.itest.emo;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.UserTransaction;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.redtape.ee.assist.RedTapes;
import eu.ggnet.dwoss.redtape.ee.emo.AddressEmo;
import eu.ggnet.dwoss.redtape.ee.entity.Address;
import eu.ggnet.dwoss.redtape.itest.ArquillianProjectArchive;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author pascal.perau
 */
@RunWith(Arquillian.class)
public class AddressEmoIT extends ArquillianProjectArchive {

    @Inject
    @RedTapes
    private EntityManager em;

    @Inject
    private UserTransaction utx;

    @Test
    public void testRequest() throws Exception {
        utx.begin();
        em.joinTransaction();

        Address a1 = new Address("abcd");
        Address a2 = new Address("efgh");
        em.persist(a1);
        em.persist(a2);

        utx.commit();
        utx.begin();
        em.joinTransaction();

        AddressEmo adEmo = new AddressEmo(em);
        Address a3 = adEmo.request(a2.getDescription());
        Address a4 = adEmo.request("ijkl");

        assertTrue(a3.getDescription().equals(a2.getDescription()));
        assertEquals(a4.getDescription(), "ijkl");

        utx.commit();
    }

}
