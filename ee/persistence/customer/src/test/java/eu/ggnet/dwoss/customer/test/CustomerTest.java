package eu.ggnet.dwoss.customer.test;

import org.junit.Test;

import eu.ggnet.dwoss.customer.assist.gen.CustomerGenerator;
import eu.ggnet.dwoss.customer.entity.*;

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
        assertThat(customer.isBussines()).as("Customer is a BussinesCustomer").isTrue();
    }

    @Test
    public void testIsConsumer() {
        CustomerGenerator gen = new CustomerGenerator();
        Customer customer = gen.makeCustomer();
        if ( customer.getContacts().isEmpty() ) {
            Contact tempContact = gen.makeContact();
            customer.add(tempContact);
        }
        assertThat(customer.isConsumer()).as("Customer is a ConsumerCustomer").isTrue();
    }

    @Test
    public void testIsSimpler() {
        CustomerGenerator gen = new CustomerGenerator();
        Customer bussineCustomer = gen.makeCustomer();
        bussineCustomer.add(gen.makeCompany());

        assertThat(bussineCustomer.isBussines()).as("Customer is a BussinesCustomer").isTrue();
        assertThat(bussineCustomer.isSimple()).as("Bussnis Customer is possible convert to SimpleCustomer").isTrue();

        Customer consumerCustomer = gen.makeCustomer();
        consumerCustomer.add(gen.makeCompany());
        
        assertThat(consumerCustomer.isConsumer()).as("Customer is a ConsumerCustomer").isTrue();
        assertThat(bussineCustomer.isSimple()).as("Consumer Customer is possible convert to SimpleCustomer").isTrue();
    }

    @Test
    public void testToSimple() {
        CustomerGenerator gen = new CustomerGenerator();
        Customer customer = gen.makeCustomer();
        assertThat(customer.toSimple()).as("Customer convert to SimpleCustomer and is not null").isNotNull();
    }

}
