package eu.ggnet.dwoss.customer.ee.itest;

import java.util.*;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.UserTransaction;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.customer.ee.assist.Customers;
import eu.ggnet.dwoss.customer.ee.assist.gen.CustomerGenerator;
import eu.ggnet.dwoss.customer.ee.eao.CustomerEao;
import eu.ggnet.dwoss.customer.ee.entity.Customer;
import eu.ggnet.dwoss.customer.ee.itest.support.ArquillianProjectArchive;
import eu.ggnet.dwoss.common.api.values.CustomerFlag;
import eu.ggnet.dwoss.customer.ee.entity.Customer.SearchField;
import eu.ggnet.dwoss.customer.ee.itest.support.Utils;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author pascal.perau
 */
@RunWith(Arquillian.class)
public class CustomerEaoIT extends ArquillianProjectArchive {

    @Inject
    @Customers
    private EntityManager em;

    @Inject
    private UserTransaction utx;

    @Inject
    private CustomerEao eao;

    private final static CustomerGenerator GEN = new CustomerGenerator();

    @Before
    public void teardown() throws Exception {
        utx.begin();
        em.joinTransaction();
        Utils.clearH2Db(em);
        utx.commit();
    }

    @Test
    public void testPersistence() throws Exception {
        utx.begin();
        em.joinTransaction();

        Customer customer = GEN.makeCustomer();
        customer.getFlags().remove(CustomerFlag.SYSTEM_CUSTOMER); // Make sure no systemcustomer.
        em.persist(customer);

        utx.commit();

        utx.begin();
        em.joinTransaction();

        assertThat(eao.findAllSystemCustomerIds().isEmpty()).as("found a SYSTEM_CUSTOMER").isTrue();
        customer = GEN.makeCustomer();
        customer.getFlags().add(CustomerFlag.SYSTEM_CUSTOMER); // Make sure it is a systemcustomer.
        em.persist(customer);
        utx.commit();

        utx.begin();
        em.joinTransaction();

        assertThat(eao.findAllSystemCustomerIds().size()).as("found more SYSTEM_CUSTOMER").isEqualTo(1);
        utx.commit();
    }

    @Test
    public void testFindWithCustomerField() throws Exception {

        utx.begin();
        em.joinTransaction();

        Customer customer = GEN.makeCustomer();
        String firstName = customer.getContacts().get(0).getFirstName();
        em.persist(customer);

        utx.commit();

        Set<SearchField> customerFields = new HashSet<>();
        customerFields.add(SearchField.FIRSTNAME);

        assertThat(eao.find(firstName, customerFields, 0, 50).size()).as("found more than one Customer").isEqualTo(1);
    }

    @Test
    public void testFind() throws Exception {
        utx.begin();
        em.joinTransaction();

        Customer customer = GEN.makeCustomer();
        String firstName = customer.getContacts().get(0).getFirstName();
        em.persist(customer);

        utx.commit();

        Set<SearchField> customerFields = new HashSet<>();
        customerFields.add(SearchField.FIRSTNAME);

        assertThat(eao.find(firstName, customerFields).size()).as("found more than one Customer").isEqualTo(1);
    }

    @Test
    public void testCountFind() throws Exception {
        utx.begin();
        em.joinTransaction();

        Customer customer = GEN.makeCustomer();
        String firstName = customer.getContacts().get(0).getFirstName();
        em.persist(customer);

        utx.commit();

        Set<SearchField> customerFields = new HashSet<>();
        customerFields.add(SearchField.FIRSTNAME);

        assertThat(eao.countFind(firstName, customerFields)).as("found more than one Customer").isEqualTo(1);
    }
    
    @Test
    public void testFindByDefaultEmailCommunication() throws Exception {
        utx.begin();
        em.joinTransaction();

        Customer c0 = GEN.makeSimpleConsumerCustomer();
        em.persist(c0);

        // Make 3 extra
        em.persist(GEN.makeSimpleConsumerCustomer());
        em.persist(GEN.makeSimpleConsumerCustomer());
        em.persist(GEN.makeSimpleBussinesCustomer());
        
        utx.commit();

        assertThat(c0.getDefaultEmailCommunication()).as("default email communication should not be null on generator").isNotNull();
        
        Customer c1 = eao.findByDefaultEmailCommunication(c0.getDefaultEmailCommunication());
        
        assertThat(c1).isEqualTo(c0);
    }
}
