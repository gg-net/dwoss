package eu.ggnet.dwoss.customer.test;

import org.junit.*;

import eu.ggnet.dwoss.customer.ee.assist.gen.CustomerGenerator;
import eu.ggnet.dwoss.customer.ee.entity.Communication.Type;
import eu.ggnet.dwoss.customer.ee.entity.*;
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

    @Before
    public void executedBeforeEach() {
        customer = new Customer();
        company = gen.makeCompany();
        contact = gen.makeContact();
        address = gen.makeAddress();
        communication = gen.makeCommunication();
    }

    @Test
    public void testOutPut() {
        assertThat(customer).describedAs("customer").isNotNull();
    }

    @Test
    public void testIsBussines() {
        customer.getContacts().clear();
        customer.add(company);
        company.add(communication);
        company.add(address);
        assertThat(customer.isBussines()).as("Customer is a Bussines Customer").isTrue();
    }

    @Test
    public void testIsConsumer() {
        customer.getCompanies().clear();
        customer.add(contact);
        contact.add(communication);
        contact.add(address);
        assertThat(customer.isConsumer()).as("Customer is a Consumer Customer").isTrue();
    }

    @Test
    public void testIsVaildForConsumerCustomer() {
        customer.add(contact);
        customer.getAddressLabels().add(new AddressLabel(company, contact, address, AddressType.INVOICE));
        assertThat(customer.isVaild()).as("Consumer Customer is vaild").isTrue();
    }

    @Test
    public void testIsVaildForBussniesCustomer() {
        customer.getContacts().clear();
        customer.add(company);
        customer.getAddressLabels().add(new AddressLabel(company, contact, address, AddressType.INVOICE));

        assertThat(customer.isVaild()).as("Bussnis Customer is vaild").isTrue();
    }

    @Test
    public void testIsVaildForANoneValidCustomer() {
        customer.add(contact);
        customer.getAddressLabels().clear();
        assertThat(customer.isVaild()).as("Customer is not vaild, because there is no AddressLable").isFalse();
    }

    @Test
    public void testIsVaildForANoneValidCustomer2() {
        customer.add(contact);
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
        customer.add(contact);
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
        customer.getAddressLabels().add(new AddressLabel(company, contact, address, AddressType.INVOICE));
        customer.add(CustomerFlag.ITC_CUSTOMER);
        customer.setKeyAccounter("Herr Meier");
        customer.add(gen.makeMandatorMetadata());

        customer.getContacts().add(contact);
        customer.getContacts().get(0).getCommunications().clear();

        communication.setType(Type.EMAIL);
        communication.setIdentifier("test@test.de");

        customer.getContacts().get(0).getCommunications().add(communication);

        assertThat(customer.isSimple()).as("Consumer Customer is possible convert to SimpleCustomer").isTrue();
    }

    @Test
    public void testIsSimplerForBussniesCustomer() {
        customer.add(CustomerFlag.ITC_CUSTOMER);
        customer.setKeyAccounter("Herr Meier");
        customer.add(gen.makeMandatorMetadata());
        customer.getContacts().clear();
        customer.add(company);
        customer.getAddressLabels().add(new AddressLabel(company, contact, address, AddressType.INVOICE));

        assertThat(customer.isSimple()).as("Bussnis Customer is possible convert to SimpleCustomer").isTrue();
    }

    @Test
    public void testIsVaildForANoneValidSimpleCustomer() {
        customer.add(CustomerFlag.ITC_CUSTOMER);
        customer.setKeyAccounter("Herr Meier");
        customer.add(gen.makeMandatorMetadata());

        customer.getAddressLabels().clear();

        assertThat(customer.isSimple()).as("SimpleCustomer is not vaild, because there is no AddressLable").isFalse();
    }

    @Test
    public void testIsVaildForANoneValidSimpleCustomer2() {
        customer.add(CustomerFlag.ITC_CUSTOMER);
        customer.setKeyAccounter("Herr Meier");
        customer.add(gen.makeMandatorMetadata());
        customer.getAddressLabels().add(new AddressLabel(gen.makeCompany(), gen.makeContact(), gen.makeAddress(), AddressType.SHIPPING));

        assertThat(customer.isSimple()).as("SimpleCustomer is not vaild, because the AddressLable is from worng type").isFalse();
    }

    @Test
    public void testIsVaildForANoneValidSimpleCustomer3() {
        customer.add(CustomerFlag.ITC_CUSTOMER);
        customer.setKeyAccounter("Herr Meier");
        customer.add(gen.makeMandatorMetadata());

        customer.getContacts().clear();
        customer.add(company);
        customer.add(company);

        assertThat(customer.isSimple()).as("SimpleCustomer is not vaild, because there are 2 Companies").isFalse();
    }

    @Test
    public void testIsVaildForANoneValidSimpleCustomer4() {
        customer.setKeyAccounter("Herr Meier");
        customer.add(gen.makeMandatorMetadata());

        customer.getFlags().clear();

        assertThat(customer.isSimple()).as("SimpleCustomer is not vaild, because there is no Customerflag").isFalse();
    }

    @Test
    public void testIsVaildForANoneValidSimpleCustomer5() {
        customer.add(contact);
        customer.add(CustomerFlag.ITC_CUSTOMER);
        customer.add(gen.makeMandatorMetadata());

        customer.setKeyAccounter("");

        assertThat(customer.isSimple()).as("SimpleCustomer is not vaild, because the KeyAccounter is blank").isFalse();
    }

    @Test
    public void testIsVaildForANoneValidSimpleCustomer6() {
        customer.add(contact);
        customer.add(CustomerFlag.ITC_CUSTOMER);
        customer.setKeyAccounter("Herr Meier");

        assertThat(customer.isSimple()).as("SimpleCustomer is not vaild, because the MandatorMetadata is empty").isFalse();
    }

    @Test
    public void testIsVaildForANoneValidCunsomerSimpleCustomer() {
        customer.add(contact);
        customer.add(CustomerFlag.ITC_CUSTOMER);
        customer.setKeyAccounter("Herr Meier");
        customer.add(gen.makeMandatorMetadata());

        assertThat(customer.isSimple()).as("SimpleCustomer is not vaild, because there are 2 Contacst").isFalse();
    }

    @Test
    public void testIsVaildForANoneValidCunsomerSimpleCustomer2() {
        customer.add(contact);
        customer.add(CustomerFlag.ITC_CUSTOMER);
        customer.setKeyAccounter("Herr Meier");
        customer.add(gen.makeMandatorMetadata());

        customer.getContacts().get(0).add(address);

        assertThat(customer.isSimple()).as("SimpleCustomer is not vaild, because there are Contacst with 2 Address").isFalse();
    }

    @Test
    public void testIsVaildForANoneValidCunsomerSimpleCustomer3() {
        customer.add(contact);
        customer.add(CustomerFlag.ITC_CUSTOMER);
        customer.setKeyAccounter("Herr Meier");
        customer.add(gen.makeMandatorMetadata());

        customer.getContacts().get(0).add(communication);
        customer.getContacts().get(0).add(communication);
        customer.getContacts().get(0).add(communication);

        assertThat(customer.isSimple()).as("SimpleCustomer is not vaild, because there are Contacst have 4 Communications").isFalse();
    }

    @Test
    public void testIsVaildForANoneValidCunsomerSimpleCustomer4() {
        customer.add(contact);
        customer.add(CustomerFlag.ITC_CUSTOMER);
        customer.setKeyAccounter("Herr Meier");
        customer.add(gen.makeMandatorMetadata());

        Communication communicationEmail = new Communication();
        communicationEmail.setType(Type.EMAIL);
        communicationEmail.setIdentifier("040123456789");

        customer.getContacts().get(0).add(communicationEmail);
        customer.getContacts().get(0).add(communicationEmail);

        assertThat(customer.isSimple()).as("SimpleCustomer is not vaild, because there are Contacst have 2 Communications form the same typ").isFalse();
    }

    @Test
    public void testIsVaildForANoneValidCunsomerSimpleCustomer5() {
        customer.add(contact);
        customer.add(CustomerFlag.ITC_CUSTOMER);
        customer.setKeyAccounter("Herr Meier");
        customer.add(gen.makeMandatorMetadata());

        Communication icq = new Communication();
        icq.setType(Type.ICQ);

        customer.getContacts().get(0).add(icq);

        assertThat(customer.isSimple()).as("SimpleCustomer is not vaild, because there are Contacst have a Communications that is not allowed").isFalse();
    }

    @Test
    public void testToSimple() {
        customer.getAddressLabels().add(new AddressLabel(company, contact, address, AddressType.INVOICE));
        customer.add(CustomerFlag.ITC_CUSTOMER);
        customer.setKeyAccounter("Herr Meier");
        customer.add(gen.makeMandatorMetadata());
        
        contact.getCommunications().get(0).setType(Type.PHONE);
        contact.getCommunications().get(0).setIdentifier("040123456789");
        customer.add(contact);
        
        assertThat(customer.isConsumer()).as("Customer is a ConsumerCustomer").isTrue();
        assertThat(customer.toSimple()).as("Customer convert to SimpleCustomer and is not null").isNotNull();
    }

    @Test
    public void testToNotSimple() {
        customer.add(contact);
        assertThat(customer.isConsumer()).as("Customer is a ConsumerCustomer").isTrue();
        assertThat(customer.toSimple()).as("Customer can not convert to SimpleCustomer and is null").isEmpty();
    }

    @Test
    public void testGetViolationMessage() {
        customer.getAddressLabels().add(new AddressLabel(company, contact, address, AddressType.INVOICE));
        customer.getContacts().clear();
        customer.add(company);

        assertThat(customer.getViolationMessage()).as("Bussnis Customer is vaild").isNull();
    }

    @Test
    public void testGetViolationMessageForANoneValidCustomer() {
        customer.add(company);

        assertThat(customer.getViolationMessage()).as("Bussnis Customer is not vaild").isNotBlank();
    }

}
