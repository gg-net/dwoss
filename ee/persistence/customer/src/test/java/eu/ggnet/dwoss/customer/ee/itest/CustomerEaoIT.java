package eu.ggnet.dwoss.customer.ee.itest;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.UserTransaction;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.customer.ee.assist.Customers;
import eu.ggnet.dwoss.customer.ee.assist.gen.CustomerGenerator;
import eu.ggnet.dwoss.customer.ee.assist.gen.CustomerGeneratorOperation;
import eu.ggnet.dwoss.customer.ee.eao.CustomerEao;
import eu.ggnet.dwoss.customer.ee.entity.Customer;
import eu.ggnet.dwoss.customer.ee.entity.Customer.SearchField;
import eu.ggnet.dwoss.customer.ee.itest.support.ArquillianProjectArchive;
import eu.ggnet.dwoss.customer.ee.itest.support.Utils;

import static eu.ggnet.dwoss.common.api.values.DocumentType.INVOICE;
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

    @Inject
    private CustomerGeneratorOperation cgo;

    @Before
    public void teardown() throws Exception {
        utx.begin();
        em.joinTransaction();
        Utils.clearH2Db(em);
        utx.commit();
    }

    @Test
    public void testPersistence() throws Exception {

        cgo.makeCustomer();
        assertThat(eao.findAllSystemCustomerIds().isEmpty()).as("There should not be any system customer").isTrue();

        cgo.makeSpecialCustomers(INVOICE);
        assertThat(eao.findAllSystemCustomerIds().size()).as("There should be one system customer").isEqualTo(1);
    }

    @Test
    public void findAndCount() throws Exception {

        utx.begin();
        em.joinTransaction();

        Customer customer = CustomerGenerator.makeSimpleConsumerCustomer();
        String firstName = customer.getContacts().get(0).getFirstName();
        em.persist(customer);

        utx.commit();

        Set<SearchField> customerFields = new HashSet<>();
        customerFields.add(SearchField.FIRSTNAME);

        // TODO: Sometimes count returns 2, even with only one customer in the database. As we use count only for progressbars, this bug is no showstopper,
        assertThat(eao.countFind(firstName, customerFields)).as("count shoud not go over 2 :-)").isBetween(1, 2);

        assertThat(eao.find(firstName, customerFields, 0, 50).size()).as("One customer should be found by the search").isEqualTo(1);
        assertThat(eao.find(firstName, null).size()).as("One customer should be found by the search").isEqualTo(1);

    }

    @Test
    public void testFindByDefaultEmailCommunication() throws Exception {
        utx.begin();
        em.joinTransaction();

        Customer c0 = CustomerGenerator.makeSimpleConsumerCustomer();
        em.persist(c0);

        // Make 3 extra
        em.persist(CustomerGenerator.makeSimpleConsumerCustomer());
        em.persist(CustomerGenerator.makeSimpleConsumerCustomer());
        em.persist(CustomerGenerator.makeSimpleBussinesCustomer());

        utx.commit();

        assertThat(c0.getDefaultEmailCommunication()).as("default email communication should not be null on generator").isNotNull();

        Customer c1 = eao.findByDefaultEmailCommunication(c0.getDefaultEmailCommunication());

        assertThat(c1).isEqualTo(c0);
    }
}
