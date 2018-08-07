package eu.ggnet.dwoss.customer.ee.itest;

import java.util.List;

import javax.inject.Inject;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.customer.ee.assist.gen.CustomerGeneratorOperation;
import eu.ggnet.dwoss.customer.ee.eao.CustomerEao;
import eu.ggnet.dwoss.customer.ee.itest.support.ArquillianProjectArchive;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author pascal.perau
 */
@RunWith(Arquillian.class)
public class CustomerGeneratorIT extends ArquillianProjectArchive {

    @Inject
    private CustomerGeneratorOperation genOp;

    @Inject
    private CustomerEao customerEao;

    @Test
    public void make200Customers() {
        final int AMOUNT = 200;

        List<Long> ids = genOp.makeCustomers(AMOUNT);
        assertThat(ids).as("Generated Ids").isNotNull().isNotEmpty().hasSize(AMOUNT);

        assertThat(customerEao.count()).isEqualTo(AMOUNT);
    }

}
