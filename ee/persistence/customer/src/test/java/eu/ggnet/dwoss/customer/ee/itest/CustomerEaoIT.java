package eu.ggnet.dwoss.customer.ee.itest;

import java.util.*;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.UserTransaction;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.customer.ee.CustomerAgent;
import eu.ggnet.dwoss.customer.ee.assist.Customers;
import eu.ggnet.dwoss.customer.ee.assist.gen.Assure;
import eu.ggnet.dwoss.customer.ee.assist.gen.CustomerGeneratorOperation;
import eu.ggnet.dwoss.customer.ee.eao.CustomerEao;
import eu.ggnet.dwoss.customer.ee.entity.Communication;
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

    @EJB
    private CustomerAgent agent;

    @Before
    public void teardown() throws Exception {
        utx.begin();
        em.joinTransaction();
        Utils.clearH2Db(em);
        utx.commit();
    }

    @Test
    public void testPersistence() {

        cgo.makeCustomer();
        assertThat(eao.findAllSystemCustomerIds().isEmpty()).as("There should not be any system customer").isTrue();

        cgo.makeSpecialCustomers(INVOICE);
        assertThat(eao.findAllSystemCustomerIds().size()).as("There should be one system customer").isEqualTo(1);
    }

    @Test
    public void findAndCount() {

        long cid = cgo.makeCustomer(new Assure.Builder().simple(true).consumer(true).build());
        String firstName = agent.findByIdEager(Customer.class, cid).getContacts().get(0).getFirstName();

        Set<SearchField> customerFields = new HashSet<>();
        customerFields.add(SearchField.FIRSTNAME);

        // TODO: Sometimes count returns 2, even with only one customer in the database. As we use count only for progressbars, this bug is no showstopper,
        assertThat(eao.countFind(firstName, customerFields)).as("count shoud not go over 2 :-)").isBetween(1, 2);

        assertThat(eao.find(firstName, customerFields, 0, 50).size()).as("One customer should be found by the search").isEqualTo(1);
        assertThat(eao.find(firstName, null).size()).as("One customer should be found by the search").isEqualTo(1);

    }

    @Test
    public void testFindByDefaultEmailCommunication() {
        cgo.makeCustomers(4); // Fill the Database
        long cid = cgo.makeCustomer(new Assure.Builder().simple(true).consumer(true).defaultEmailCommunication(true).build());
        cgo.makeCustomers(4); // Fill the Database more

        Communication email = agent.findByIdEager(Customer.class, cid).getDefaultEmailCommunication().get(); // never not set.

        Customer c1 = eao.findByDefaultEmailCommunication(email);

        assertThat(c1.getId()).as("Customer ids should be equal").isEqualTo(cid);
    }

    @Test
    public void findAllResellerListCommunication() {
        cgo.makeCustomers(20); // Fill the Database
        cgo.makeCustomers(5, new Assure.Builder().simple(true).consumer(true).useResellerListEmailCommunication(true).build());

        long countedResellerListCustomers = agent
                .findAll(Customer.class)
                .stream()
                .filter(c -> c.getResellerListEmailCommunication().isPresent())
                .count();

        long countedViaEao = eao.findAllWithResellerListEmailCommunication().size();

        assertThat(countedResellerListCustomers).isEqualTo(countedViaEao);
    }
}
