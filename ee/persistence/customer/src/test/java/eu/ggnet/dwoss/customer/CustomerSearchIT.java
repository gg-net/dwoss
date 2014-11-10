package eu.ggnet.dwoss.customer;

import eu.ggnet.dwoss.rules.CustomerFlag;
import eu.ggnet.dwoss.rules.ShippingCondition;
import eu.ggnet.dwoss.rules.SalesChannel;
import eu.ggnet.dwoss.rules.PaymentCondition;
import eu.ggnet.dwoss.rules.PaymentMethod;

import java.util.*;

import javax.ejb.Stateless;
import javax.ejb.embeddable.EJBContainer;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.naming.NamingException;
import javax.persistence.*;

import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.configuration.SystemConfig;
import eu.ggnet.dwoss.mandator.api.value.DefaultCustomerSalesdata;
import eu.ggnet.dwoss.mandator.api.value.Mandator;
import eu.ggnet.dwoss.mandator.api.value.DeleteCustomers;
import eu.ggnet.dwoss.customer.assist.CustomerPu;
import eu.ggnet.dwoss.customer.assist.Customers;
import eu.ggnet.dwoss.customer.eao.CustomerEao;
import eu.ggnet.dwoss.customer.entity.Customer;
import eu.ggnet.dwoss.customer.priv.OldCustomer;

import static eu.ggnet.dwoss.customer.priv.ConverterUtil.mergeFromOld;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author Bastian Venz
 */
public class CustomerSearchIT {

    private EJBContainer container;

    @Inject
    private CustomerSearchITHelper helper;

    @Produces
    private DeleteCustomers dc = new DeleteCustomers(new HashMap<>());

    @Before
    public void setUp() throws NamingException {
        Map<String, Object> c = new HashMap<>();
        c.putAll(CustomerPu.CMP_IN_MEMORY);
        c.putAll(SystemConfig.OPENEJB_EJB_XML_DISCOVER);
        c.putAll(SystemConfig.OPENEJB_LOG_TESTING);
        container = EJBContainer.createEJBContainer(c);
        container.getContext().bind("inject", this);
    }

    @After
    public void tearDown() {
        container.close();
    }

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
        find = helper.testLucenceSearch("schlag");
        assertTrue("No element should be here: " + find, find.size() == 0);
        find = helper.testLucenceSearch("schlagstock ltd");
        assertTrue("No element should be here: " + find, find.size() == 1);
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

        private static final Logger LOG = LoggerFactory.getLogger(CustomerSearchITHelper.class);

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
