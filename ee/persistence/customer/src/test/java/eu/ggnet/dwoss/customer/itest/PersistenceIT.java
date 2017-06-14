package eu.ggnet.dwoss.customer.itest;

import eu.ggnet.dwoss.customer.itest.support.ArquillianProjectArchive;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaQuery;
import javax.transaction.UserTransaction;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.customer.assist.Customers;
import eu.ggnet.dwoss.customer.assist.gen.CustomerGenerator;
import eu.ggnet.dwoss.customer.entity.*;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 *
 * @author pascal.perau, oliver.guenther
 */
@RunWith(Arquillian.class)
public class PersistenceIT extends ArquillianProjectArchive {

    private final static CustomerGenerator GEN = new CustomerGenerator();

    private final static Logger L = LoggerFactory.getLogger(PersistenceIT.class);

    @Inject
    @Customers
    private EntityManager em;

    @Inject
    UserTransaction utx;

    @Test
    public void testPersistence() throws Exception {
        utx.begin();
        em.joinTransaction();
        Customer c = GEN.makeCustomer();
        em.persist(c);
        utx.commit();

        utx.begin();
        em.joinTransaction();

        CriteriaQuery<Customer> customerQ = em.getCriteriaBuilder().createQuery(Customer.class);

        c = em.createQuery(customerQ.select(customerQ.from(Customer.class))).getSingleResult();

        assertThat(c.getContacts()).describedAs("customer.getContacts()").isNotEmpty();

        L.info("===== Contacts");
        for (Contact contact : c.getContacts()) {
            L.info(contact.toString());
            L.info("===== - Communications");
            for (Communication communication : contact.getCommunications()) {
                L.info(communication.toString());
            }
        }

        L.info("===== Companies");
        for (Company company : c.getCompanies()) {
            L.info(company.toString());
            L.info("===== - Communications:");
            for (Communication communication : company.getCommunications()) {
                L.info(communication.toString());
            }
        }

        L.info("===== MandatorMetaData");
        for (MandatorMetadata man : c.getMandatorMetadata()) {
            L.info(man.toString());
        }
        utx.commit();
    }

}
