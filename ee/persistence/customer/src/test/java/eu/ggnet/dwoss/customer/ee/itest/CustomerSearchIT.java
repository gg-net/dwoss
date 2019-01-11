package eu.ggnet.dwoss.customer.ee.itest;

import eu.ggnet.dwoss.search.api.ShortSearchResult;
import eu.ggnet.dwoss.search.api.GlobalKey;
import eu.ggnet.dwoss.search.api.SearchProvider;
import eu.ggnet.dwoss.search.api.SearchRequest;

import java.util.List;

import javax.ejb.EJB;
import javax.inject.Inject;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.customer.ee.CustomerAgent;
import eu.ggnet.dwoss.customer.ee.entity.dto.SimpleCustomer;
import eu.ggnet.dwoss.customer.ee.itest.support.ArquillianProjectArchive;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author Bastian Venz
 */
@RunWith(Arquillian.class)
public class CustomerSearchIT extends ArquillianProjectArchive {

    @Inject
    private SearchProvider searchProvider;

    @EJB
    private CustomerAgent agent;

    @Test
    @Ignore
    public void testLucenceSearch() throws Exception {
        SimpleCustomer c1 = makeSimpleCustomer("Die Firma", "Herr", "Max", "Mustermann", "Keine Bemerkungen", "Helle Strasse 22", "12345", "Musterhausen");
        c1.setLandlinePhone("040 1232123");
        SimpleCustomer c2 = makeSimpleCustomer(null, "Frau", "Marria", "Mustermann", "Grosse Tüten", "Dunkle Allee 7", "12345", "Musterhausen");
        c2.setMobilePhone("+49 172 12312131");
        SimpleCustomer c3 = makeSimpleCustomer("Schlagstock Ltd.", "Herr", "Michael", "Wankelmeier", "Bloß freundlich sein !!!", "Adamsweg 3", "00666", "Eisenhüttenstadt");
        c3.setEmail("rolf@rofl.de");
        SimpleCustomer c4 = makeSimpleCustomer(null, "Frau", "Lisa", "Lüstling", null, "Freie Straße 2", "98745", "Heimwehrhausen");
        c4.setEmail("lisa@xxx.com");

        // --------
        agent.store(c1);
        agent.store(c2);
        agent.store(c3);
        agent.store(c4);

        /* //this methode calling on 
        List<Customer> find = eao.find("schlag*");

        assertThat(find).as("Result of search").hasSize(1);
        assertTrue("Only One element should be here: " + find, find.size() == 1);
        find = eao.find("schlag");
        assertTrue("No element should be here: " + find, find.isEmpty());
        find = eao.find("schlagstock ltd");
        assertTrue("One element should be here: " + find, find.size() == 1);
         */
        // Testing Search via Provider
        assertThat(searchProvider).as("Searchprovider").isNotNull().returns(GlobalKey.Component.CUSTOMER, e -> e.getSource());
        SearchRequest req = new SearchRequest("schlag*");
        int estimateMaxResults = searchProvider.estimateMaxResults(req);
        assertThat(estimateMaxResults).as("estimated max results").isEqualTo(1);

        List<ShortSearchResult> result = searchProvider.search(req, 0, 10);
        assertThat(result).as("Searchresult").hasSize(1);
    }

    private SimpleCustomer makeSimpleCustomer(String firma, String titel, String vorname, String nachname, String anmerkung, String REAdresse, String REPlz, String REOrt) {
        SimpleCustomer sc = new SimpleCustomer();
        sc.setCompanyName(firma);
        sc.setTitle(titel);
        sc.setFirstName(vorname);
        sc.setLastName(nachname);
        sc.setComment(anmerkung);
        sc.setStreet(REAdresse);
        sc.setZipCode(REPlz);
        sc.setCity(REOrt);
        return sc;
    }
}
