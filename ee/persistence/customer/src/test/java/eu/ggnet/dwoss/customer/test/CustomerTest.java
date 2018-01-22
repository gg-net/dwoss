package eu.ggnet.dwoss.customer.test;

import org.junit.Before;
import org.junit.Test;

import eu.ggnet.dwoss.customer.assist.gen.CustomerGenerator;
import eu.ggnet.dwoss.customer.entity.*;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author oliver.guenther
 */
public class CustomerTest {

    private final CustomerGenerator gen = new CustomerGenerator();

    private Customer customer;

    private final Company company = gen.makeCompany();

    private final Contact contact = gen.makeContact();

    @Before
    public void executedBeforeEach() {
        customer = gen.makeCustomer();
        customer.getContacts().clear();
        customer.getCompanies().clear();
    }

    @Test
    public void testOutPut() {
        assertThat(customer).describedAs("customer").isNotNull();
    }

    @Test
    public void testIsBussines() {
        customer.add(company);
        assertThat(customer.isBussines()).as("Customer is a Bussines Customer").isTrue();
    }

    @Test
    public void testIsConsumer() {
        customer.add(contact);
        assertThat(customer.isConsumer()).as("Customer is a Consumer Customer").isTrue();
    }

    
    //this test are commentout, because CustomerGenerator not allways generate EMAIL, PHONE, MOBILE for Communication, this is needed for the SimpleCustomer
    @Test
    public void testIsSimplerBussniesCustomer() {
//        customer.add(company);
//        assertThat(customer.isBussines()).as("Customer is a BussinesCustomer").isTrue();       
//        assertThat(customer.isSimple()).as("Bussnis Customer is possible convert to SimpleCustomer").isTrue();
    }

    @Test
    public void testIsSimplerConsumerCustomer() {
//        customer.add(contact);
//        assertThat(customer.isConsumer()).as("Customer is a ConsumerCustomer").isTrue();
//        assertThat(customer.isSimple()).as("Consumer Customer is possible convert to SimpleCustomer").isTrue();
   }

    @Test
    public void testToSimple() {
        assertThat(customer.toSimple()).as("Customer convert to SimpleCustomer and is not null").isNotNull();
    }

}
