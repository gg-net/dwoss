package eu.ggnet.dwoss.customer.itest;

import java.util.EnumSet;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.UserTransaction;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.customer.assist.Customers;
import eu.ggnet.dwoss.customer.eao.CustomerEao;
import eu.ggnet.dwoss.customer.entity.Customer;
import eu.ggnet.dwoss.customer.itest.support.ArquillianProjectArchive;
import eu.ggnet.dwoss.customer.priv.ConverterUtil;
import eu.ggnet.dwoss.customer.priv.OldCustomer;
import eu.ggnet.dwoss.mandator.api.value.DefaultCustomerSalesdata;
import eu.ggnet.dwoss.mandator.api.value.Mandator;
import eu.ggnet.dwoss.rules.*;
import eu.ggnet.dwoss.search.api.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author Bastian Venz
 */
@RunWith(Arquillian.class)
public class CustomerSearchIT extends ArquillianProjectArchive {

    @Inject
    private CustomerEao eao;

    @Inject
    @Customers
    private EntityManager em;

    @Inject
    private UserTransaction utx;

    @Inject
    private Mandator mandator;

    @Inject
    private DefaultCustomerSalesdata salesData;

    @Inject
    private SearchProvider searchProvider;

    @Test
    public void testLucenceSearch() throws Exception {
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

        utx.begin();
        em.joinTransaction();

        em.persist(convert(c1));
        em.persist(convert(c2));
        em.persist(convert(c3));
        em.persist(convert(c4));

        utx.commit();

        List<Customer> find = eao.find("schlag*");

        assertThat(find).as("Result of search").hasSize(1);
        assertTrue("Only One element should be here: " + find, find.size() == 1);
        find = eao.find("schlag");
        assertTrue("No element should be here: " + find, find.isEmpty());
        find = eao.find("schlagstock ltd");
        assertTrue("One element should be here: " + find, find.size() == 1);

        // Testing Search via Provider
        assertThat(searchProvider).as("Searchprovider").isNotNull().returns(GlobalKey.Component.CUSTOMER, e -> e.getSource());
        SearchRequest req = new SearchRequest("schlag*");
        int estimateMaxResults = searchProvider.estimateMaxResults(req);
        assertThat(estimateMaxResults).as("estimated max results").isEqualTo(1);

        List<ShortSearchResult> result = searchProvider.search(req, 0, 10);
        assertThat(result).as("Searchresult").hasSize(1);
    }

    private Customer convert(OldCustomer old) {
        Customer customer = new Customer();
        ConverterUtil.mergeFromOld(old, customer, mandator.getMatchCode(), salesData);
        return customer;
    }

}
