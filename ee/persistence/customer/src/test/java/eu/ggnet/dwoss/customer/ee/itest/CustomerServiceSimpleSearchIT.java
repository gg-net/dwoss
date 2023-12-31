package eu.ggnet.dwoss.customer.ee.itest;

import java.util.List;

import jakarta.ejb.EJB;
import jakarta.inject.Inject;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.customer.api.CustomerService;
import eu.ggnet.dwoss.customer.api.UiCustomer;
import eu.ggnet.dwoss.customer.ee.CustomerAgent;
import eu.ggnet.dwoss.customer.ee.eao.CustomerEao;
import eu.ggnet.dwoss.customer.ee.entity.dto.SimpleCustomer;
import eu.ggnet.dwoss.customer.ee.itest.support.ArquillianProjectArchive;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

@RunWith(Arquillian.class)
public class CustomerServiceSimpleSearchIT extends ArquillianProjectArchive {

    private final static Logger L = LoggerFactory.getLogger(CustomerServiceSimpleSearchIT.class);

    @EJB
    private CustomerService customerService;

    @EJB
    private CustomerAgent agent;

    @Inject
    private CustomerEao eao;

    @Test
    public void testFind() {

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

        assertThat(eao.findAll()).as("findAll()").hasSize(4);

        List<UiCustomer> r = customerService.asUiCustomers("Die Firma", null, null, null, true);
        assertThat(r).as("called asUiCustomers('Die Firma', null, null, null, true) found: " + r)
                .hasSize(1)
                .first().returns(c1.getCompanyName(), UiCustomer::company);

        r = customerService.asUiCustomers("Die Firma", null, "", "   ", true);
        assertThat(r).as("called asUiCustomers('Die Firma', null, '', '   ', true) found: " + r)
                .hasSize(1)
                .first().returns(c1.getCompanyName(), UiCustomer::company);

        r = customerService.asUiCustomers("Die Firma", "Max", null, null, true);
        assertThat(r).as("called asUiCustomers('Die Firma', 'Max', null, null, true) found: " + r)
                .hasSize(1)
                .first().returns(c1.getCompanyName(), UiCustomer::company);

        r = customerService.asUiCustomers("Die Fi", "Max", null, null, true);
        assertThat(r).as("called asUiCustomers('Die Fi', 'Max', null, null, true) found: " + r)
                .hasSize(1)
                .first().returns(c1.getCompanyName(), UiCustomer::company);

        r = customerService.asUiCustomers("Die Fi", "Max", "", "   ", true);
        assertThat(r).as("called asUiCustomers('Die Fi', 'Max', '', '   ', true)" + r)
                .hasSize(1)
                .first().returns(c1.getCompanyName(), UiCustomer::company);

        r = customerService.asUiCustomers("Die Fi", "Max", "Muster", "   ", true);
        assertThat(r).as("called asUiCustomers('Die Fi', 'Max', 'Muster', '   ', true)" + r)
                .hasSize(1)
                .first().returns(c1.getCompanyName(), UiCustomer::company);

        r = customerService.asUiCustomers("Die Fi", "Moritz", "", "   ", true);
        assertThat(r).as("called asUiCustomers('Die Fi', 'Moritz', '', '   ', true)" + r)
                .isEmpty();

        r = customerService.asUiCustomers("Die Fam", null, "", "   ", true);
        assertThat(r).as("called asUiCustomers('Die Fam', null, '', '   ', true)" + r)
                .hasSize(0);

        r = customerService.asUiCustomers(null, null, null, "lisa", true);
        assertThat(r).as("called asUiCustomers(null, null, null, 'lisa', true)" + r)
                .hasSize(1)
                .first().returns(c4.getLastName(), UiCustomer::lastName);

        r = customerService.asUiCustomers("Die Fi", null, "", "   ", false);
        assertThat(r).as("called asUiCustomers('Die Fi', null, '', '   ', false)" + r)
                .hasSize(0);

        r = customerService.asUiCustomers("Schla", null, null, null, true);
        assertThat(r).as("called asUiCustomers('Schla', null, null, null, true) found: " + r)
                .hasSize(1)
                .first().returns(c3.getCompanyName(), UiCustomer::company);

                r = customerService.asUiCustomers(null, "Marr", null, null, true);
        assertThat(r).as("called asUiCustomers(null,'Marr', null, null, true) found: " + r)
                .hasSize(1)
                .first().returns(c2.getLastName(), UiCustomer::lastName);

                r = customerService.asUiCustomers(null,"M", null, null, true);
        assertThat(r).as("called asUiCustomers(null, 'M', null, null, true) found: " + r)
                .hasSize(3);
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
