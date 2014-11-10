package eu.ggnet.dwoss.customer.eao;

import javax.persistence.*;

import org.junit.*;

import eu.ggnet.dwoss.rules.CustomerFlag;
import eu.ggnet.dwoss.util.persistence.JpaPersistenceConfiguration;
import eu.ggnet.dwoss.customer.assist.CustomerPu;
import eu.ggnet.dwoss.customer.assist.gen.CustomerGenerator;
import eu.ggnet.dwoss.customer.entity.Customer;

import static org.junit.Assert.*;

/**
 *
 * @author pascal.perau
 */
public class CustomerEoaIT {

    private EntityManagerFactory emf;

    private EntityManager em;

    private final static CustomerGenerator GEN = new CustomerGenerator();

    @Before
    public void setUp() {
        emf = Persistence.createEntityManagerFactory(CustomerPu.NAME, JpaPersistenceConfiguration.asHsqldbInMemoryWithSearchRam(CustomerPu.NAME));
        em = emf.createEntityManager();
    }

    @After
    public void after() {
        if ( em != null && em.isOpen() ) em.close();
        if ( emf != null && emf.isOpen() ) emf.close();
    }

    @Test
    public void testPersistence() {
        CustomerEao eao = new CustomerEao(em);
        em.getTransaction().begin();
        Customer c = GEN.makeCustomer();
        c.remove(CustomerFlag.SYSTEM_CUSTOMER); // Make sure no systemcustomer.
        em.persist(c);
        em.getTransaction().commit();

        em.getTransaction().begin();
        assertTrue(eao.findAllSystemCustomerIds().isEmpty());
        c = GEN.makeCustomer();
        c.add(CustomerFlag.SYSTEM_CUSTOMER); // Make sure no systemcustomer.
        em.persist(c);
        em.getTransaction().commit();

        em.getTransaction().begin();
        assertEquals(1, eao.findAllSystemCustomerIds().size());
        em.getTransaction().commit();
    }

}
