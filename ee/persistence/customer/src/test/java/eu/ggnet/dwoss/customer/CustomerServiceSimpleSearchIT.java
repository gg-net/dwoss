package eu.ggnet.dwoss.customer;


import eu.ggnet.dwoss.rules.CustomerFlag;
import eu.ggnet.dwoss.rules.ShippingCondition;
import eu.ggnet.dwoss.rules.SalesChannel;
import eu.ggnet.dwoss.rules.PaymentCondition;
import eu.ggnet.dwoss.rules.PaymentMethod;
import eu.ggnet.dwoss.customer.api.CustomerService;

import java.util.*;

import javax.ejb.Stateless;
import javax.ejb.embeddable.EJBContainer;
import javax.inject.Inject;
import javax.naming.NamingException;
import javax.persistence.EntityManager;

import org.junit.*;

import eu.ggnet.dwoss.configuration.SystemConfig;
import eu.ggnet.dwoss.mandator.api.value.DefaultCustomerSalesdata;
import eu.ggnet.dwoss.mandator.api.value.Mandator;
import eu.ggnet.dwoss.customer.assist.CustomerPu;
import eu.ggnet.dwoss.customer.assist.Customers;
import eu.ggnet.dwoss.customer.eao.CustomerEao;
import eu.ggnet.dwoss.customer.entity.Customer;
import eu.ggnet.dwoss.customer.priv.OldCustomer;

import static eu.ggnet.dwoss.customer.priv.ConverterUtil.mergeFromOld;
import static org.junit.Assert.*;

public class CustomerServiceSimpleSearchIT {

    private EJBContainer container;

    @Inject
    private CustomerService customerService;

    @Inject
    private CustomerEao eao;

    @Inject
    private CustomerServiceSimpleSearchHelper helper;

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
    public void testFind() {
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
        c4.setEmail("lisa@xxx.com");

        // --------
        helper.persist(c1);
        helper.persist(c2);
        helper.persist(c3);
        helper.persist(c4);

        assertEquals("Finding all Customers", 4, eao.findAll().size());
//        List<UiCustomer> asUiCustomers = customerService.asUiCustomers("Die Firma", null, "", "   ", true);
//        System.out.println("Y:" + asUiCustomers);
//
//        asUiCustomers = customerService.asUiCustomers("Die Fi", "Max", "", "   ", true);
//        System.out.println("Y:" + asUiCustomers);
//
//        asUiCustomers = customerService.asUiCustomers("Die Fi", "Max", "Muster", "   ", true);
//        System.out.println("Y:" + asUiCustomers);
//
//        asUiCustomers = customerService.asUiCustomers("Die Fi", "Moritz", "", "   ", true);
//        System.out.println("N:" + asUiCustomers);
//
//        asUiCustomers = customerService.asUiCustomers("Die Fi", null, "", "   ", false);
//        System.out.println("N:" + asUiCustomers);
//
//        asUiCustomers = customerService.asUiCustomers("Die Fam", null, "", "   ", true);
//        System.out.println("N:" + asUiCustomers);
//
//        asUiCustomers = customerService.asUiCustomers(null, null, null, "lisa", true);
//        System.out.println("Y:" + asUiCustomers);

        assertEquals("Finding all Customers", 4, customerService.asUiCustomers(null, null, "", "   ", true).size());
        assertEquals(1, customerService.asUiCustomers("Die Firma", null, null, null, true).size());
        assertEquals(1, customerService.asUiCustomers("Schla", null, null, null, true).size());
        assertEquals(1, customerService.asUiCustomers(null, "Mic", null, null, true).size());
        assertEquals(3, customerService.asUiCustomers(null, "M", null, null, true).size());
    }

    @Stateless
    public static class CustomerServiceSimpleSearchHelper {

        @Inject
        @Customers
        private EntityManager customerEm;

        @Inject
        private Mandator mandator;

        @Inject
        private DefaultCustomerSalesdata salesData;

        public void persist(OldCustomer old) {
            Customer customer = new Customer();
            mergeFromOld(old, customer, mandator.getMatchCode(), salesData);
            customerEm.persist(customer);
        }
    }

}
