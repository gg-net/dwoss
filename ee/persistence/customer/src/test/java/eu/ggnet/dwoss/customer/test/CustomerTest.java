package eu.ggnet.dwoss.customer.test;

import org.junit.Test;

import eu.ggnet.dwoss.customer.assist.gen.CustomerGenerator;
import eu.ggnet.dwoss.customer.entity.Company;
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
    }

    @Test
    public void testIsBussines() {
        CustomerGenerator gen = new CustomerGenerator();
        Customer customer = gen.makeCustomer();
        if ( customer.getCompanies().isEmpty() ) {
            Company tempCompany = gen.makeCompany();
            customer.add(tempCompany);
        }
        assertThat(customer.isBussines()).as("Customer is a Bussines").isTrue();
    }
    
    
    

}
