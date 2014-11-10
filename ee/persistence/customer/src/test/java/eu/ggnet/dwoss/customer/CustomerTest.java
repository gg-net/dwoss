package eu.ggnet.dwoss.customer;

import org.junit.Ignore;
import org.junit.Test;

import eu.ggnet.dwoss.customer.assist.gen.CustomerGenerator;
import eu.ggnet.dwoss.customer.entity.Customer;

/**
 *
 * @author oliver.guenther
 */
public class CustomerTest {

    @Test
    @Ignore
    public void testOutPut() {
        CustomerGenerator gen = new CustomerGenerator();
        Customer customer = gen.makeCustomer();
        System.out.println(customer.getMandatorMetadata());
    }

}
