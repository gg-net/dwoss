package eu.ggnet.dwoss.misc.op.itest;

import java.util.EnumSet;
import java.util.List;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.UserTransaction;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.customer.assist.Customers;
import eu.ggnet.dwoss.customer.entity.Customer;
import eu.ggnet.dwoss.customer.priv.ConverterUtil;
import eu.ggnet.dwoss.customer.priv.OldCustomer;
import eu.ggnet.dwoss.mandator.api.value.DefaultCustomerSalesdata;
import eu.ggnet.dwoss.mandator.api.value.Mandator;
import eu.ggnet.dwoss.rules.*;
import eu.ggnet.dwoss.search.api.SearchRequest;
import eu.ggnet.dwoss.search.api.ShortSearchResult;
import eu.ggnet.dwoss.search.op.Searcher;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test the Searcher, very bad solution.
 *
 * @author Oliver Günther
 */
@RunWith(Arquillian.class)
public class SearcherOperationIT extends ArquillianProjectArchive {

    @Inject
    @Customers
    private EntityManager em;

    @Inject
    private UserTransaction utx;

    @Inject
    private Mandator mandator;

    @Inject
    private DefaultCustomerSalesdata salesData;

    @EJB
    private Searcher searcher;

    /**
     * Very bad solution for the searcher test.
     * This test works, cause the searcher is getting only the customer provider, and the provider is working as asserted. If we every change the customer
     * search implementation or move the searcher in an extra package, this test must be reworked.
     *
     * @throws Exception
     */
    @Test
    public void testLucenceSearch() throws Exception {
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

        utx.begin();
        em.joinTransaction();

        em.persist(convert(c1));
        em.persist(convert(c2));
        em.persist(convert(c3));
        em.persist(convert(c4));

        utx.commit();

        // Testing Search via Provider
        assertThat(searcher).as("Searchprovider").isNotNull();
        SearchRequest req = new SearchRequest("schlag*");
        searcher.initSearch(req);

        assertThat(searcher.estimateMaxResults()).as("Estimated max Results").isEqualTo(1);
        assertThat(searcher.hasNext()).isTrue();

        List<ShortSearchResult> result = searcher.next();
        assertThat(result).as("First Searchresult").hasSize(1);

        result = searcher.next();
        assertThat(result).as("Second Searchresult").isEmpty();

        searcher.initSearch(req);
        result = searcher.next();
        assertThat(result).as("Searchresult after Reinit").hasSize(1);
    }

    private Customer convert(OldCustomer old) {
        Customer customer = new Customer();
        ConverterUtil.mergeFromOld(old, customer, mandator.getMatchCode(), salesData);
        return customer;
    }

}
