package eu.ggnet.dwoss.customer.itest;

import java.util.EnumSet;
import java.util.List;

import javax.ejb.EJB;
import javax.inject.Inject;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.customer.opi.CustomerService;
import eu.ggnet.dwoss.customer.opi.UiCustomer;
import eu.ggnet.dwoss.customer.ee.eao.CustomerEao;
import eu.ggnet.dwoss.customer.itest.support.ArquillianProjectArchive;
import eu.ggnet.dwoss.customer.ee.priv.OldCustomer;
import eu.ggnet.dwoss.customer.ee.priv.OldCustomerAgent;
import eu.ggnet.dwoss.rules.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

@RunWith(Arquillian.class)
public class CustomerServiceSimpleSearchIT extends ArquillianProjectArchive {

    private final static Logger L = LoggerFactory.getLogger(CustomerServiceSimpleSearchIT.class);

    @EJB
    private CustomerService customerService;

    @EJB
    private OldCustomerAgent agent;

    @Inject
    private CustomerEao eao;

    @Test
    public void testFind() {
        OldCustomer c1 = new OldCustomer("Die Firma", "Herr", "Max", "Mustermann", "Keine Bemerkungen", "Helle Strasse 22", "12345", "Musterhausen");
        OldCustomer c2 = new OldCustomer(null, "Frau", "Marria", "Mustermann", "Grosse Tüten", "Dunkle Allee 7", "12345", "Musterhausen", "Dünne Gasse 2", "22222", "Wolfsstaaad");
        c2.setPaymentMethod(PaymentMethod.DIRECT_DEBIT);
        c2.addFlag(CustomerFlag.CONFIRMS_DOSSIER);
        c1.setPaymentCondition(PaymentCondition.CUSTOMER);

        //by pp
        OldCustomer c3 = new OldCustomer("Schlagstock Ltd.", "Herr", "Michael", "Wankelmeier", "Bloß freundlich sein !!!", "Adamsweg 3", "00666", "Eisenhüttenstadt");
        c3.addFlag(CustomerFlag.CONFIRMS_DOSSIER);
        c3.addFlag(CustomerFlag.CONFIRMED_CASH_ON_DELIVERY);
        c3.setPaymentMethod(PaymentMethod.CASH_ON_DELIVERY);
        c3.setAllowedSalesChannels(EnumSet.of(SalesChannel.CUSTOMER, SalesChannel.RETAILER));
        c3.setPaymentCondition(PaymentCondition.DEALER_3_PERCENT_DISCOUNT);
        c3.setShippingCondition(ShippingCondition.DEALER_ONE);
        OldCustomer c4 = new OldCustomer(null, "Frau", "Lisa", "Lüstling", null, "Freie Straße 2", "98745", "Heimwehrhausen", "Dünne Gasse 2", "22222", "Heimwehrhausen");
        c4.addFlag(CustomerFlag.CONFIRMS_DOSSIER);
        c4.setAllowedSalesChannels(EnumSet.of(SalesChannel.CUSTOMER));
        c4.setPaymentCondition(PaymentCondition.EMPLOYEE);
        c4.setShippingCondition(ShippingCondition.DEALER_ONE);
        c4.setPaymentMethod(PaymentMethod.INVOICE);
        c4.setEmail("lisa@xxx.com");

        // --------
        agent.store(c1);
        agent.store(c2);
        agent.store(c3);
        agent.store(c4);

        assertEquals("Finding all Customers", 4, eao.findAll().size());

        List<UiCustomer> asUiCustomers = customerService.asUiCustomers("Die Firma", null, "", "   ", true);
        assertThat(asUiCustomers).hasSize(1);
        L.info("Y(" + asUiCustomers.size() + "):" + asUiCustomers);

        asUiCustomers = customerService.asUiCustomers("Die Fi", "Max", "", "   ", true);
        L.info("Y(" + asUiCustomers.size() + "):" + asUiCustomers);

        asUiCustomers = customerService.asUiCustomers("Die Fi", "Max", "Muster", "   ", true);
        L.info("Y(" + asUiCustomers.size() + "):" + asUiCustomers);

        asUiCustomers = customerService.asUiCustomers("Die Fi", "Moritz", "", "   ", true);
        L.info("N(" + asUiCustomers.size() + "):" + asUiCustomers);

        asUiCustomers = customerService.asUiCustomers("Die Fi", null, "", "   ", false);
        L.info("N(" + asUiCustomers.size() + "):" + asUiCustomers);

        asUiCustomers = customerService.asUiCustomers("Die Fam", null, "", "   ", true);
        L.info("N(" + asUiCustomers.size() + "):" + asUiCustomers);

        asUiCustomers = customerService.asUiCustomers(null, null, null, "lisa", true);
        L.info("Y(" + asUiCustomers.size() + "):" + asUiCustomers);

        assertEquals("Finding all Customers", 4, customerService.asUiCustomers(null, null, "", "   ", true).size());
        assertEquals(1, customerService.asUiCustomers("Die Firma", null, null, null, true).size());
        assertEquals(1, customerService.asUiCustomers("Schla", null, null, null, true).size());
        assertEquals(1, customerService.asUiCustomers(null, "Mic", null, null, true).size());
        assertEquals(3, customerService.asUiCustomers(null, "M", null, null, true).size());
    }

}
