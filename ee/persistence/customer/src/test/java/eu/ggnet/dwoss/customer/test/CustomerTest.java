package eu.ggnet.dwoss.customer.test;

import org.junit.Test;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.customer.assist.gen.CustomerGenerator;
import eu.ggnet.dwoss.customer.entity.Customer;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author oliver.guenther
 */
public class CustomerTest {

    @Test
    public void testOutPut() {
        CustomerGenerator gen = new CustomerGenerator();
        Customer customer = gen.makeCustomer();
        assertThat(customer).describedAs("customer").isNotNull();
        LoggerFactory.getLogger(CustomerTest.class).info(customer.getMandatorMetadata().toString());
    }

}
