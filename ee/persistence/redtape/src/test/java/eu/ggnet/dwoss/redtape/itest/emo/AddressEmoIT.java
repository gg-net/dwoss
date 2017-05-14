package eu.ggnet.dwoss.redtape.itest.emo;

import javax.persistence.*;

import org.junit.*;

import eu.ggnet.dwoss.redtape.assist.RedTapePu;
import eu.ggnet.dwoss.redtape.emo.AddressEmo;
import eu.ggnet.dwoss.redtape.entity.Address;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author pascal.perau
 */
public class AddressEmoIT {

    private EntityManager em;

    private EntityManagerFactory emf;

    @Before
    public void setUp() {
        emf = Persistence.createEntityManagerFactory(RedTapePu.NAME, RedTapePu.JPA_IN_MEMORY);
        em = emf.createEntityManager();
    }

    @After
    public void tearDown() {
        em.close();
        emf.close();
    }

    @Test
    @Ignore // Arqme

    public void testRequest() {

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
