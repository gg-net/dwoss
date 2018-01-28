package eu.ggnet.dwoss.customer.itest;

import java.util.List;

import javax.inject.Inject;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.customer.ee.assist.gen.CustomerGeneratorOperation;
import eu.ggnet.dwoss.customer.ee.eao.CustomerEao;
import eu.ggnet.dwoss.customer.itest.support.ArquillianProjectArchive;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author pascal.perau
 */
@RunWith(Arquillian.class)
public class CustomerGeneratorIT extends ArquillianProjectArchive {

    @Inject
    private CustomerGeneratorOperation gen;

    @Inject
    private CustomerEao customerEao;

    @Test
    public void make200Customers() {
        final int AMOUNT = 100;

        List<Long> ids = gen.makeCustomers(AMOUNT);
        assertThat(ids).as("Generated Ids").isNotNull().isNotEmpty().hasSize(AMOUNT);

        assertThat(customerEao.count()).isEqualTo(AMOUNT);
    }

}
