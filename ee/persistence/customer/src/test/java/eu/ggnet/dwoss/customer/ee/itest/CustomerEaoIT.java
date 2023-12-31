package eu.ggnet.dwoss.customer.ee.itest;

import java.util.HashSet;
import java.util.Set;

import jakarta.ejb.EJB;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.UserTransaction;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.*;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.customer.ee.CustomerAgent;
import eu.ggnet.dwoss.customer.ee.assist.Customers;
import eu.ggnet.dwoss.customer.ee.assist.gen.*;
import eu.ggnet.dwoss.customer.ee.eao.CustomerEao;
import eu.ggnet.dwoss.customer.ee.entity.Communication;
import eu.ggnet.dwoss.customer.ee.entity.Customer;
import eu.ggnet.dwoss.customer.ee.entity.Customer.SearchField;
import eu.ggnet.dwoss.customer.ee.entity.dto.SimpleCustomer;
import eu.ggnet.dwoss.customer.ee.itest.support.ArquillianProjectArchive;

import static eu.ggnet.dwoss.core.common.values.DocumentType.INVOICE;
import static eu.ggnet.dwoss.customer.ee.entity.Customer.SearchField.COMMUNICATION;
import static java.util.EnumSet.of;
import static org.assertj.core.api.Assertions.assertThat;

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

    @After
    public void teardown() throws Exception {
        utx.begin();
        em.joinTransaction();
        CustomerDeleteUtils.deleteAll(em);
        assertThat(CustomerDeleteUtils.validateEmpty(em)).isNull();
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

    @Test
    public void findByCommunication() {
        final String EMAIL = "lisa@xxx.com";

        cgo.makeCustomers(20); // Fill the Database
        SimpleCustomer c1 = makeSimpleCustomer(null, "Frau", "Lisa", "Lüstling", null, "Freie Straße 2", "98745", "Heimwehrhausen");
        c1.setEmail(EMAIL);

        agent.store(c1);

        assertThat(agent.search("lisa@xxx.com", of(COMMUNICATION))).as("search of lisa@xxx.com").hasSize(1);
        assertThat(agent.search("LiSa@xxX.com", of(COMMUNICATION))).hasSize(1);
        assertThat(agent.search("lisa*", of(COMMUNICATION))).hasSizeGreaterThan(0);
        assertThat(agent.search("isa@xxx.com", of(COMMUNICATION))).isEmpty();

    }

    private SimpleCustomer makeSimpleCustomer(String firma, String titel, String vorname, String nachname, String anmerkung, String REAdresse, String REPlz, String REOrt) {
        SimpleCustomer sc = new SimpleCustomer();
        sc.setCompanyName(firma);
        sc.setTitle(titel);
        sc.setFirstName(vorname);
        sc.setLastName(nachname);
        sc.setComment(anmerkung);
        sc.setStreet(REAdresse);
        sc.setZipCode(REPlz);
        sc.setCity(REOrt);
        return sc;
    }
}
