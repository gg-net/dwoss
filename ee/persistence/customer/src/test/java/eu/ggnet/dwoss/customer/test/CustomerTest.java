package eu.ggnet.dwoss.customer.test;

import org.junit.*;

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

    private Company company;

    private Contact contact;

    private Address address;

    private Communication makeCommunication() {
        Communication communication = new Communication();
        communication.setType(Communication.Type.PHONE);
        communication.setIdentifier("01545452221");

        return communication;
    }

    @Before
    public void executedBeforeEach() {
        customer = gen.makeCustomer();
        company = gen.makeCompany();
        contact = gen.makeContact();
        address = gen.makeAddress();

        customer.getCompanies().clear();
    }

    @Ignore
    @Test
    public void testOutPut() {
        assertThat(customer).describedAs("customer").isNotNull();
    }

    @Ignore
    @Test
    public void testIsBussines() {
        customer.add(company);
        company.add(makeCommunication());
        company.add(address);
        assertThat(customer.isBussines()).as("Customer is a Bussines Customer").isTrue();
    }

    @Ignore
    @Test
    public void testIsConsumer() {
        customer.add(contact);
        contact.add(makeCommunication());
        contact.add(address);
        assertThat(customer.isConsumer()).as("Customer is a Consumer Customer").isTrue();
    }

    //this test are commentout, because CustomerGenerator not allways generate EMAIL, PHONE OR MOBILE for Communication, this is needed for the SimpleCustomer
    @Ignore
    @Test
    public void testIsSimplerForBussniesCustomer() {
        company.getCommunications().clear();
        company.add(makeCommunication());
        company.add(address);
        customer.add(company);

        assertThat(customer.isBussines()).as("Customer is a BussinesCustomer").isTrue();
        assertThat(customer.isSimple()).as("Bussnis Customer is possible convert to SimpleCustomer").isTrue();
    }

    @Ignore
    @Test
    public void testIsSimplerForConsumerCustomer() {
        contact.getCommunications().clear();
        contact.add(makeCommunication());
        contact.add(address);
        customer.add(contact);

        assertThat(customer.isConsumer()).as("Customer is a ConsumerCustomer").isTrue();
        assertThat(customer.isSimple()).as("Consumer Customer is possible convert to SimpleCustomer").isTrue();
    }

    @Ignore
    @Test
    public void testToSimple() {
        assertThat(customer.toSimple()).as("Customer convert to SimpleCustomer and is not null").isNotNull();
    }

    @Ignore
    @Test
    public void testIsVaildForConsumerCustomer() {
        assertThat(customer.isVaild()).as("Consumer Customer is vaild").isTrue();
    }

    @Ignore
    @Test
    public void testIsVaildForBussniesCustomer() {
        company.add(address);
        company.add(makeCommunication());
        customer.add(company);
        assertThat(customer.isVaild()).as("Bussnis Customer is vaild").isTrue();

    }

    @Ignore
    @Test
    public void testIsVaildForANoneValidConsumerCustomer() {
        //make a non-valid Customer without Contacts
        customer.getContacts().clear();
        assertThat(customer.isVaild()).as("Consumer Customer is not vaild").isFalse();
    }

    @Ignore
    @Test
    public void testIsVaildForANoneValidBussniesCustomer() {
        this.customer.getContacts().clear();
        this.customer.getCompanies().clear();

        assertThat(this.customer.isVaild()).as("Bussnis Nor a Consumer Customer").isFalse();
    }

}
