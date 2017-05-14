package eu.ggnet.dwoss.customer.itest;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.UserTransaction;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.customer.assist.Customers;
import eu.ggnet.dwoss.customer.assist.gen.CustomerGenerator;
import eu.ggnet.dwoss.customer.eao.CustomerEao;
import eu.ggnet.dwoss.customer.entity.Customer;
import eu.ggnet.dwoss.rules.CustomerFlag;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author pascal.perau
 */
@RunWith(Arquillian.class)
public class CustomerEoaIT extends ArquillianProjectArchive {

    @Inject
    @Customers
    private EntityManager em;

    @Inject
    private UserTransaction utx;

    private final static CustomerGenerator GEN = new CustomerGenerator();

    @Test
    public void testPersistence() throws Exception {
        CustomerEao eao = new CustomerEao(em);

        utx.begin();
        em.joinTransaction();

        Customer c = GEN.makeCustomer();
        c.remove(CustomerFlag.SYSTEM_CUSTOMER); // Make sure no systemcustomer.
        em.persist(c);

        utx.commit();

        utx.begin();
        em.joinTransaction();

        assertTrue(eao.findAllSystemCustomerIds().isEmpty());
        c = GEN.makeCustomer();
        c.add(CustomerFlag.SYSTEM_CUSTOMER); // Make sure no systemcustomer.
        em.persist(c);
        utx.commit();

        utx.begin();
        em.joinTransaction();

        assertEquals(1, eao.findAllSystemCustomerIds().size());
        utx.commit();
    }

}
