package eu.ggnet.dwoss.redtape.emo;

import eu.ggnet.dwoss.redtape.emo.AddressEmo;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import eu.ggnet.dwoss.redtape.assist.RedTapePu;
import eu.ggnet.dwoss.redtape.entity.Address;

import static org.junit.Assert.*;

/**
 *
 * @author pascal.perau
 */

public class AddressEmoIT {

    private EntityManager em;

    private EntityManagerFactory emf;

    @Before
    public void setUp(){
        emf = Persistence.createEntityManagerFactory(RedTapePu.NAME, RedTapePu.JPA_IN_MEMORY);
        em = emf.createEntityManager();
    }

    @After
    public void tearDown(){
        em.close();
        emf.close();
    }

    @Test
    public void testRequest(){

        em.getTransaction().begin();
        Address a1 = new Address("abcd");
        Address a2 = new Address("efgh");
        em.persist(a1);
        em.persist(a2);
        em.getTransaction().commit();

        AddressEmo adEmo = new AddressEmo(em);
        Address a3 = adEmo.request(a2.getDescription());
        Address a4 = adEmo.request("ijkl");

        assertTrue(a3.getDescription().equals(a2.getDescription()));
        assertEquals(a4.getDescription(), "ijkl");
    }

}
