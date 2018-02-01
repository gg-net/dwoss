package eu.ggnet.dwoss.customer.test;

import eu.ggnet.dwoss.customer.ee.entity.Contact;
import eu.ggnet.dwoss.customer.ee.entity.Address;
import eu.ggnet.dwoss.customer.ee.entity.Communication;
import eu.ggnet.dwoss.customer.ee.entity.Customer;
import eu.ggnet.dwoss.customer.ee.entity.Company;

import org.junit.*;

import eu.ggnet.dwoss.customer.ee.assist.gen.CustomerGenerator;
import eu.ggnet.dwoss.customer.ee.entity.Communication.Type;
import eu.ggnet.dwoss.customer.ee.entity.projection.AddressLabel;
import eu.ggnet.dwoss.rules.AddressType;
import eu.ggnet.dwoss.rules.CustomerFlag;

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
    
    private Communication communication;

    private Customer forceToSimpleCustomer(Customer c) {
        c.add(CustomerFlag.ITC_CUSTOMER);
        c.setKeyAccounter("Herr Meier");
        c.add(gen.makeMandatorMetadata());

        c.getContacts().clear();
        c.add(contact);

        return c;
    }

    @Before
    public void executedBeforeEach() {
        customer = gen.makeCustomer();
        company = gen.makeCompany();
        contact = gen.makeContact();
        address = gen.makeAddress();
        communication = gen.makeCommunication();
    }

    @After
    public void cleanUp() {

    }

    @Test
    public void testOutPut() {
        assertThat(customer).describedAs("customer").isNotNull();
    }

    @Test
    public void testIsBussines() {
        customer.add(company);
        company.add(gen.makeCommunication());
        company.add(address);
        assertThat(customer.isBussines()).as("Customer is a Bussines Customer").isTrue();
    }

    @Test
    public void testIsConsumer() {
        customer.add(contact);
        contact.add(gen.makeCommunication());
        contact.add(address);
        assertThat(customer.isConsumer()).as("Customer is a Consumer Customer").isTrue();
    }

    @Test
    public void testIsVaildForConsumerCustomer() {
        assertThat(customer.isVaild()).as("Consumer Customer is vaild").isTrue();
    }

    @Test
    public void testIsVaildForBussniesCustomer() {
        customer.getContacts().clear();
        customer.add(company);

        assertThat(customer.isVaild()).as("Bussnis Customer is vaild").isTrue();
    }

    @Test
    public void testIsVaildForANoneValidCustomer() {
        customer.getAddressLabels().clear();
        assertThat(customer.isVaild()).as("Customer is not vaild, because there is no AddressLable").isFalse();
    }

    @Test
    public void testIsVaildForANoneValidCustomer2() {
        customer.getAddressLabels().clear();
        customer.getAddressLabels().add(new AddressLabel(gen.makeCompany(), gen.makeContact(), gen.makeAddress(), AddressType.SHIPPING));
        assertThat(customer.isVaild()).as("Customer is not vaild, because there is no AddressLable with Type Invoice").isFalse();
    }

    @Test
    public void testIsVaildForANoneValidCustomer3() {
        customer.getCompanies().clear();
        customer.getContacts().clear();
        assertThat(customer.isVaild()).as("Customer is not vaild, because there is no Company and no Contact is set").isFalse();
    }

    @Test
    public void testIsVaildForANoneValidConsumerCustomer() {
        customer.getContacts().get(0).setLastName("");
        assertThat(customer.isVaild()).as("Consumer Customer is not vaild, because the Contact do not have a LastName").isFalse();
    }

    @Test
    public void testIsVaildForANoneValidBussniesCustomer() {
        customer.getContacts().clear();

        company.setName("");
        customer.getCompanies().add(company);

        assertThat(customer.isVaild()).as("Bussnis Customer is not vaild, because the Name of the Compnay is blank").isFalse();
    }

    @Test
    public void testIsVaildForANoneValidBussniesCustomer2() {
        customer.getContacts().clear();
        customer.add(company);
        customer.getCompanies().get(0).getAddresses().clear();

        assertThat(customer.isVaild()).as("Bussnis Customer is not vaild, the Compnay has no an Address").isFalse();
    }

    @Test
    public void testIsVaildForANoneValidBussniesCustomer3() {
        customer.getContacts().clear();
        customer.add(company);

        customer.getCompanies().clear();

        assertThat(customer.isVaild()).as("Bussnis Customer is not vaild, the Compnay has no Communication").isFalse();
    }

    @Test
    public void testIsSimplerForConsumerCustomer() {
        Customer theSimpleCustomer = forceToSimpleCustomer(customer);

        theSimpleCustomer.getContacts().clear();
        theSimpleCustomer.getContacts().add(gen.makeContact());
        theSimpleCustomer.getContacts().get(0).getCommunications().clear();
        communication.setType(Type.EMAIL);
        communication.setIdentifier("test@test.de");
        theSimpleCustomer.getContacts().get(0).getCommunications().add(communication);

        assertThat(theSimpleCustomer.isSimple()).as("Consumer Customer is possible convert to SimpleCustomer").isTrue();
    }

    @Test
    public void testIsSimplerForBussniesCustomer() {
        Customer theSimpleCustomer = forceToSimpleCustomer(customer);
        theSimpleCustomer.getContacts().clear();
        theSimpleCustomer.add(company);

        assertThat(theSimpleCustomer.isSimple()).as("Bussnis Customer is possible convert to SimpleCustomer").isTrue();
    }

    @Test
    public void testIsVaildForANoneValidSimpleCustomer() {
        Customer theSimpleCustomer = forceToSimpleCustomer(customer);
        theSimpleCustomer.getAddressLabels().clear();

        assertThat(theSimpleCustomer.isSimple()).as("SimpleCustomer is not vaild, because there is no AddressLable").isFalse();
    }

    @Test
    public void testIsVaildForANoneValidSimpleCustomer2() {
        Customer theSimpleCustomer = forceToSimpleCustomer(customer);
        theSimpleCustomer.getAddressLabels().add(new AddressLabel(gen.makeCompany(), gen.makeContact(), gen.makeAddress(), AddressType.SHIPPING));

        assertThat(theSimpleCustomer.isSimple()).as("SimpleCustomer is not vaild, because the AddressLable is from worng type").isFalse();
    }

    @Test
    public void testIsVaildForANoneValidSimpleCustomer3() {
        Customer theSimpleCustomer = forceToSimpleCustomer(customer);
        theSimpleCustomer.getContacts().clear();
        theSimpleCustomer.add(company);
        theSimpleCustomer.add(company);

        assertThat(theSimpleCustomer.isSimple()).as("SimpleCustomer is not vaild, because there are 2 Companies").isFalse();
    }

    @Test
    public void testIsVaildForANoneValidSimpleCustomer4() {
        Customer theSimpleCustomer = forceToSimpleCustomer(customer);
        theSimpleCustomer.getFlags().clear();

        assertThat(theSimpleCustomer.isSimple()).as("SimpleCustomer is not vaild, because there is no Customerflag").isFalse();
    }

    @Test
    public void testIsVaildForANoneValidSimpleCustomer5() {
        Customer theSimpleCustomer = forceToSimpleCustomer(customer);
        theSimpleCustomer.setKeyAccounter("");

        assertThat(theSimpleCustomer.isSimple()).as("SimpleCustomer is not vaild, because the KeyAccounter is blank").isFalse();
    }

    @Test
    public void testIsVaildForANoneValidSimpleCustomer6() {
        Customer theSimpleCustomer = forceToSimpleCustomer(customer);
        theSimpleCustomer.getMandatorMetadata().clear();

        assertThat(theSimpleCustomer.isSimple()).as("SimpleCustomer is not vaild, because the MandatorMetadata is empty").isFalse();
    }

    @Test
    public void testIsVaildForANoneValidCunsomerSimpleCustomer() {
        Customer theSimpleCustomer = forceToSimpleCustomer(customer);
        theSimpleCustomer.add(contact);

        assertThat(theSimpleCustomer.isSimple()).as("SimpleCustomer is not vaild, because there are 2 Contacst").isFalse();
    }

    @Test
    public void testIsVaildForANoneValidCunsomerSimpleCustomer2() {
        Customer theSimpleCustomer = forceToSimpleCustomer(customer);
        theSimpleCustomer.getContacts().get(0).add(address);

        assertThat(theSimpleCustomer.isSimple()).as("SimpleCustomer is not vaild, because there are Contacst with 2 Address").isFalse();
    }

    @Test
    public void testIsVaildForANoneValidCunsomerSimpleCustomer3() {
        Customer theSimpleCustomer = forceToSimpleCustomer(customer);
        theSimpleCustomer.getContacts().get(0).add(communication);
        theSimpleCustomer.getContacts().get(0).add(communication);
        theSimpleCustomer.getContacts().get(0).add(communication);

        assertThat(theSimpleCustomer.isSimple()).as("SimpleCustomer is not vaild, because there are Contacst have 4 Communications").isFalse();
    }

    @Test
    public void testIsVaildForANoneValidCunsomerSimpleCustomer4() {
        Customer theSimpleCustomer = forceToSimpleCustomer(customer);
        Communication communicationEmail = new Communication();
        communicationEmail.setType(Type.EMAIL);
        communicationEmail.setIdentifier("040123456789");

        theSimpleCustomer.getContacts().get(0).add(communicationEmail);
        theSimpleCustomer.getContacts().get(0).add(communicationEmail);

        assertThat(theSimpleCustomer.isSimple()).as("SimpleCustomer is not vaild, because there are Contacst have 2 Communications form the same typ").isFalse();
    }

    @Test
    public void testIsVaildForANoneValidCunsomerSimpleCustomer5() {
        Customer theSimpleCustomer = forceToSimpleCustomer(customer);
        Communication icq = new Communication();
        icq.setType(Type.ICQ);

        theSimpleCustomer.getContacts().get(0).add(icq);

        assertThat(theSimpleCustomer.isSimple()).as("SimpleCustomer is not vaild, because there are Contacst have a Communications that is not allowed").isFalse();
    }

    @Test
    public void testToSimple() {
        Customer theSimpleCustomer = forceToSimpleCustomer(customer);
        assertThat(theSimpleCustomer.isConsumer()).as("Customer is a ConsumerCustomer").isTrue();
        assertThat(theSimpleCustomer.toSimple()).as("Customer convert to SimpleCustomer and is not null").isNotNull();

    }
    
    @Test
    public void testGetViolationMessage(){
        customer.getContacts().clear();
        customer.add(company);

        assertThat(customer.getViolationMessage()).as("Bussnis Customer is vaild").isNull();
    }
    
     @Test
    public void testGetViolationMessageForANoneValidCustomer(){
        customer.add(company);

        assertThat(customer.getViolationMessage()).as("Bussnis Customer is not vaild").isNotBlank();
    }

}
