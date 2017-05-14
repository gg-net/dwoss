package eu.ggnet.dwoss.customer.itest;

import java.util.EnumSet;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.customer.assist.Customers;
import eu.ggnet.dwoss.customer.eao.CustomerEao;
import eu.ggnet.dwoss.customer.entity.Customer;
import eu.ggnet.dwoss.customer.priv.OldCustomer;
import eu.ggnet.dwoss.mandator.api.value.DefaultCustomerSalesdata;
import eu.ggnet.dwoss.mandator.api.value.Mandator;
import eu.ggnet.dwoss.rules.*;

import static eu.ggnet.dwoss.customer.priv.ConverterUtil.mergeFromOld;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author Bastian Venz
 */
@RunWith(Arquillian.class)
public class CustomerSearchIT extends ArquillianProjectArchive {

    @Inject
    private CustomerSearchITHelper helper;

    @Test
    public void testLucenceSearch() throws InterruptedException {
        OldCustomer c1 = new OldCustomer("Die Firma", "Herr", "Max", "Mustermann", "Keine Bemerkungen", null, "Helle Strasse 22", "12345", "Musterhausen");
        OldCustomer c2 = new OldCustomer(null, "Frau", "Marria", "Mustermann", "Grosse Tüten", null, "Dunkle Allee 7", "12345", "Musterhausen", "Der Abnehmer", "Dünne Gasse 2", "22222", "Wolfsstaaad");
        c2.setPaymentMethod(PaymentMethod.DIRECT_DEBIT);
        c2.addFlag(CustomerFlag.CONFIRMS_DOSSIER);
        c1.setPaymentCondition(PaymentCondition.CUSTOMER);

        //by pp
        OldCustomer c3 = new OldCustomer("Schlagstock Ltd.", "Herr", "Michael", "Wankelmeier", "Bloß freundlich sein !!!", "John \"Die Rechte\" Jefferson", "Adamsweg 3", "00666", "Eisenhüttenstadt", null, null, null, null);
        c3.addFlag(CustomerFlag.CONFIRMS_DOSSIER);
        c3.addFlag(CustomerFlag.CONFIRMED_CASH_ON_DELIVERY);
        c3.setPaymentMethod(PaymentMethod.CASH_ON_DELIVERY);
        c3.setAllowedSalesChannels(EnumSet.of(SalesChannel.CUSTOMER, SalesChannel.RETAILER));
        c3.setPaymentCondition(PaymentCondition.DEALER_3_PERCENT_DISCOUNT);
        c3.setShippingCondition(ShippingCondition.DEALER_ONE);
        OldCustomer c4 = new OldCustomer(null, "Frau", "Lisa", "Lüstling", null, null, "Freie Straße 2", "98745", "Heimwehrhausen", "GanzSchnell GmbH", "Dünne Gasse 2", "22222", "Heimwehrhausen");
        c4.addFlag(CustomerFlag.CONFIRMS_DOSSIER);
        c4.setAllowedSalesChannels(EnumSet.of(SalesChannel.CUSTOMER));
        c4.setPaymentCondition(PaymentCondition.EMPLOYEE);
        c4.setShippingCondition(ShippingCondition.DEALER_ONE);
        c4.setPaymentMethod(PaymentMethod.INVOICE);

        helper.persist(c1);
        helper.persist(c2);
        helper.persist(c3);
        helper.persist(c4);
        List<Customer> find = helper.testLucenceSearch("schlag*");
        assertTrue("Only One element should be here: " + find, find.size() == 1);
        LoggerFactory.getLogger(CustomerSearchIT.class).info("Found: {}", find.get(0));
        find = helper.testLucenceSearch("schlag");
        assertTrue("No element should be here: " + find, find.isEmpty());
        find = helper.testLucenceSearch("schlagstock ltd");
        assertTrue("One element should be here: " + find, find.size() == 1);
    }

    @Stateless
    public static class CustomerSearchITHelper {

        @Inject
        @Customers
        private EntityManager em;

        @Inject
        private Mandator mandator;

        @Inject
        private CustomerEao eao;

        @Inject
        private DefaultCustomerSalesdata salesData;

        public void persist(OldCustomer old) {
            Customer customer = new Customer();
            mergeFromOld(old, customer, mandator.getMatchCode(), salesData);
            em.persist(customer);
        }

        public List<Customer> testLucenceSearch(String s) throws InterruptedException {
            List<Customer> find = eao.find(s);
            for (Customer customer : find) {
                customer.toMultiLine();
            }
            return find;
        }

    }

}
