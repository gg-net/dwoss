package eu.ggnet.dwoss.customer.ee.itest;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.transaction.UserTransaction;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.customer.ee.assist.Customers;
import eu.ggnet.dwoss.customer.ee.assist.gen.CustomerGenerator;
import eu.ggnet.dwoss.customer.ee.entity.*;
import eu.ggnet.dwoss.customer.ee.itest.support.ArquillianProjectArchive;

/**
 *
 * @author pascal.perau, oliver.guenther
 */
@RunWith(Arquillian.class)
public class PersistenceIT extends ArquillianProjectArchive {

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
        Customer c = CustomerGenerator.makeCustomer();
        em.persist(c);
        utx.commit();

        utx.begin();
        em.joinTransaction();

        CriteriaQuery<Customer> q = em.getCriteriaBuilder().createQuery(Customer.class);

        c = em.createQuery(q.select(q.from(Customer.class))).getSingleResult();

        L.info("{}", c);
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

        L.info("===== AddressLabels");
        for (AddressLabel al : c.getAddressLabels()) {
            L.info(al.toString());
        }

        utx.commit();
    }

}
