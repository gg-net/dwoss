package eu.ggnet.dwoss.misc.op.itest;

import java.util.*;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.UserTransaction;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;

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
import eu.ggnet.dwoss.search.api.*;
import eu.ggnet.dwoss.search.op.Searcher;
import eu.ggnet.dwoss.uniqueunit.assist.UniqueUnits;
import eu.ggnet.dwoss.uniqueunit.eao.UniqueUnitEao;
import eu.ggnet.dwoss.uniqueunit.entity.Product;
import eu.ggnet.dwoss.uniqueunit.entity.UniqueUnit;
import eu.ggnet.dwoss.uniqueunit.entity.UniqueUnit.Condition;
import eu.ggnet.dwoss.util.validation.ConstraintViolationFormater;

import static eu.ggnet.dwoss.uniqueunit.entity.UniqueUnit.Identifier.REFURBISHED_ID;
import static eu.ggnet.dwoss.uniqueunit.entity.UniqueUnit.Identifier.SERIAL;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test the Searcher, very bad solution.
 *
 * @author Oliver Günther
 */
@RunWith(Arquillian.class)
public class SearcherOperationIT extends ArquillianProjectArchive {

    private final static String PARTNO_1 = "AA.BBBBB.CCC";

    @Inject
    @Customers
    private EntityManager cem;

    @Inject
    @UniqueUnits
    private EntityManager uuem;

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
     * The SearcherOperation only discovers all defined SearchProvider and uses them together. This test makes use of the knowledge of the implemented
     * SearchProviders for UniqueUnit and Customer and their internal work. If the providers ever change in the way they search, this test will also fail.
     * TODO: create an extra test, that uses multiple testing implementations of searchproviders. So we can see if a broken test results from the Searcher or
     * the SearchProviders.
     *
     * @throws Exception if transaction fails
     */
    @Test
    public void testGlobalSearch() throws Exception {
        // Testdate for Customer
        OldCustomer c1 = new OldCustomer("Die Firma", "Herr", "Max", "Mustermann", "Keine Bemerkungen", null, "Helle Strasse 22", "12345", "Musterhausen");
        OldCustomer c2 = new OldCustomer(null, "Frau", "Marria", "Mustermann", "Grosse Tüten", null, "Dunkle Allee 7", "12345", "Musterhausen", "Der Abnehmer", "Dünne Gasse 2", "22222", "Wolfsstaaad");
        c2.setPaymentMethod(PaymentMethod.DIRECT_DEBIT);
        c2.addFlag(CustomerFlag.CONFIRMS_DOSSIER);
        c1.setPaymentCondition(PaymentCondition.CUSTOMER);

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
        cem.joinTransaction();

        Customer nc1 = convert(c1);
        Customer nc2 = convert(c2);
        Customer nc3 = convert(c3);
        Customer nc4 = convert(c4);

        cem.persist(nc1);
        cem.persist(nc2);
        cem.persist(nc3);
        cem.persist(nc4);

        utx.commit();

        // Testdata for UniqueUnit
        Product p = new Product(ProductGroup.MONITOR, TradeName.FUJITSU, PARTNO_1, "The Notebook");

        UniqueUnitEao eao = new UniqueUnitEao(uuem);

        UniqueUnit unit1 = new UniqueUnit();
        unit1.setMfgDate(new Date());
        unit1.setIdentifier(SERIAL, "ROFFFLAASSS");
        unit1.setIdentifier(REFURBISHED_ID, "22223");
        unit1.setContractor(TradeName.ONESELF);
        unit1.setCondition(Condition.AS_NEW);
        unit1.setProduct(p);

        UniqueUnit unit2 = new UniqueUnit();
        unit2.setIdentifier(SERIAL, "FASREAGFSGSWE");
        unit2.setIdentifier(REFURBISHED_ID, "54422");
        unit2.setContractor(TradeName.ONESELF);
        unit2.setCondition(Condition.AS_NEW);
        unit2.setProduct(p);

        UniqueUnit unit3 = new UniqueUnit();
        unit3.setIdentifier(SERIAL, "FHASKHFUAERHFAGTQ5");
        unit3.setIdentifier(REFURBISHED_ID, "33323");
        unit3.setContractor(TradeName.ONESELF);
        unit3.setCondition(Condition.AS_NEW);
        unit3.setProduct(p);

        UniqueUnit unit4 = new UniqueUnit();
        unit4.setIdentifier(SERIAL, "GFSKLFDIACVHDAIERQ");
        unit4.setIdentifier(REFURBISHED_ID, "54142");
        unit4.setContractor(TradeName.ONESELF);
        unit4.setCondition(Condition.AS_NEW);
        unit4.setProduct(p);

        UniqueUnit unit5 = new UniqueUnit();
        unit5.setIdentifier(SERIAL, "FDALOIFUOWE");
        unit5.setIdentifier(REFURBISHED_ID, "29471");
        unit5.setContractor(TradeName.ONESELF);
        unit5.setCondition(Condition.AS_NEW);
        unit5.setProduct(p);
        Set<ConstraintViolation<UniqueUnit>> validate = Validation.buildDefaultValidatorFactory().getValidator().validate(unit1);
        assertThat(validate).as(ConstraintViolationFormater.toMultiLine(new HashSet<>(validate), true)).isEmpty();

        utx.begin();
        uuem.joinTransaction();
        uuem.persist(p);
        uuem.persist(unit1);
        uuem.persist(unit2);
        uuem.persist(unit3);
        uuem.persist(unit4);
        uuem.persist(unit5);
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

        searcher.initSearch(new SearchRequest("*5*"));
        assertThat(searcher.estimateMaxResults()).as("estimatedMax").isEqualTo(6);

        List<ShortSearchResult> allResult = new ArrayList<>();
        while (searcher.hasNext()) {
            allResult.addAll(searcher.next());
        }

        assertThat(allResult).as("Find of *5*").extracting(ShortSearchResult::getKey).
                contains(
                        new GlobalKey(GlobalKey.Component.UNIQUE_UNIT, unit2.getId()),
                        new GlobalKey(GlobalKey.Component.UNIQUE_UNIT, unit3.getId()),
                        new GlobalKey(GlobalKey.Component.UNIQUE_UNIT, unit4.getId()),
                        new GlobalKey(GlobalKey.Component.CUSTOMER, nc1.getId()),
                        new GlobalKey(GlobalKey.Component.CUSTOMER, nc2.getId()),
                        new GlobalKey(GlobalKey.Component.CUSTOMER, nc4.getId())
                );

    }

    private Customer convert(OldCustomer old) {
        Customer customer = new Customer();
        ConverterUtil.mergeFromOld(old, customer, mandator.getMatchCode(), salesData);
        return customer;
    }

}
