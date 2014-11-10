package eu.ggnet.dwoss.customer;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaQuery;

import org.junit.*;

import eu.ggnet.dwoss.customer.assist.CustomerPu;
import eu.ggnet.dwoss.customer.assist.gen.CustomerGenerator;
import eu.ggnet.dwoss.customer.entity.*;

import static org.junit.Assert.assertFalse;

/**
 *
 * @author pascal.perau
 */
public class PersistenceIT {

    private final static boolean SOUT = false;

    private EntityManagerFactory emf;

    private EntityManager em;

    private final static CustomerGenerator GEN = new CustomerGenerator();

    @Before
    public void setUp() {
        emf = Persistence.createEntityManagerFactory(CustomerPu.NAME, CustomerPu.JPA_IN_MEMORY);
        em = emf.createEntityManager();
    }

    @After
    public void after() {
        if ( em != null && em.isOpen() ) em.close();
        if ( emf != null && emf.isOpen() ) emf.close();
    }

    @Test
    public void testPersistence() {
        em.getTransaction().begin();
        Customer c = GEN.makeCustomer();
        em.persist(c);
        em.getTransaction().commit();

        CriteriaQuery<Customer> customerQ = em.getCriteriaBuilder().createQuery(Customer.class);

        c = em.createQuery(customerQ.select(customerQ.from(Customer.class))).getSingleResult();

        assertFalse("No contacts persisted", c.getContacts().isEmpty());
        if ( SOUT ) {
            System.out.println("============Available Contacts============");
            for (Contact contact : c.getContacts()) {
                System.out.println(contact);
                System.out.println("============Communications:");
                for (Communication communication : contact.getCommunications()) {
                    System.out.println(communication);
                }
            }

            System.out.println("============Available Companies============");
            for (Company company : c.getCompanies()) {
                System.out.println(company);
                System.out.println("============Communications:");
                for (Communication communication : company.getCommunications()) {
                    System.out.println(communication);
                }
            }

            System.out.println("============Available MandatorMetaData============");
            for (MandatorMetadata man : c.getMandatorMetadata()) {
                System.out.println(man);
            }
        }

    }

}
