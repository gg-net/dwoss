package eu.ggnet.dwoss.customer.test;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

import eu.ggnet.dwoss.customer.ee.assist.gen.CustomerGenerator;
import eu.ggnet.dwoss.customer.ee.entity.Communication.Type;
import eu.ggnet.dwoss.customer.ee.entity.*;
import eu.ggnet.dwoss.customer.ee.entity.dto.SimpleCustomer;
import eu.ggnet.dwoss.customer.ee.entity.projection.AddressLabel;
import eu.ggnet.dwoss.rules.AddressType;
import eu.ggnet.dwoss.rules.CustomerFlag;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author oliver.guenther
 */
// Testtarget: isSimple, toSimple, isValid
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

        contact = new Contact();
        contact.setFirstName("Max");
        contact.setLastName("Mustermann");

        company = new Company();
        company.setName("Firma ABC");

        address = new Address();
        address.setStreet("Postallee 23");
        address.setZipCode("21234");
        address.setCity("Hamburg");
        address.setIsoCountry(Locale.GERMANY);

        communication = new Communication();
        communication.setType(Type.EMAIL);
        communication.setIdentifier("test@test.de");

    }

    public static Customer makeValidSimpleBusiness() {
        return null;
    }

    public static Customer makeValidSimpleConsumer() {
        Customer customer = new Customer();

        Contact contact = new Contact();
        contact.setFirstName("Max");
        contact.setLastName("Mustermann");
        customer.getContacts().add(contact);

        Address address = new Address();
        address.setStreet("Postallee 23");
        address.setZipCode("21234");
        address.setCity("Hamburg");
        address.setIsoCountry(Locale.GERMANY);
        contact.getAddresses().add(address);

        Communication communication = new Communication();
        communication.setType(Type.EMAIL);
        communication.setIdentifier("test@test.de");
        contact.getCommunications().add(communication);

        assertThat(customer.isVaild()).isTrue(); // optional
        assertThat(customer.isSimple()).overridingErrorMessage("Customer not simple, becaus: " + customer.getSimpleViolationMessage()).isTrue();
        assertThat(customer.isConsumer()).isTrue();

        return customer;
    }

    @Test
    public void testSimpleConsumer() {
//        Customer c = new Customer();
//
//        //use an non default firstname
//        contact.setFirstName("Moris");
//
//        //build the AddressLabel
//        c.getAddressLabels().add(new AddressLabel(company, contact, address, AddressType.INVOICE));
//        c.getFlags().add(CustomerFlag.ITC_CUSTOMER);
//        c.setKeyAccounter("Herr Meier");
//
//        //build the MandatorMetadata
//        MandatorMetadata m = new MandatorMetadata();
//        m.setShippingCondition(ShippingCondition.DEALER_ONE);
//        m.setPaymentCondition(PaymentCondition.CUSTOMER);
//        m.setPaymentMethod(PaymentMethod.DIRECT_DEBIT);
//        m.add(SalesChannel.UNKNOWN);
//        c.add(m);
//
//        contact.add(address);
//        contact.add(communication);
//        c.add(contact);
//
//        c = makeValidSimpleConsumer();
//        assertThat(c.isVaild()).isTrue(); // optional
//        assertThat(c.isSimple()).overridingErrorMessage("Customer not simple, becaus: " + c.getSimpleViolationMessage()).isTrue();
//        assertThat(c.isConsumer()).isTrue();
//
//        SimpleCustomer sc = c.toSimple().get();
//        assertThat(sc.getFirstName()).as("simpleCustomer.firstName").isEqualTo(contact.getFirstName());
//        // Test jeden parameter.
//
//        c.getContacts().clear();
//        company.add(address);
//        company.add(communication);
//        c.getCompanies().add(company);
//
//        assertThat(c.isSimple()).overridingErrorMessage("Customer not simple, becaus: " + c.getSimpleViolationMessage()).isTrue();
//        assertThat(c.isBussines()).isTrue();
//        assertThat(c.getViolationMessage()).as("Bussnis Customer is vaild").isNull();
    }

    @Test
    public void testOutPut() {
        assertThat(customer).describedAs("customer").isNotNull();
    }

    public void testToSimpleFirstName() {
        Customer c = makeValidSimpleConsumer();
        SimpleCustomer sc = c.toSimple().get();
        assertThat(sc.getFirstName()).as("simpleCustomer.firstName").isEqualTo(contact.getFirstName());
    }

    public void testViolateMissingFirstName() {
        Customer c = makeValidSimpleConsumer();
        c.getContacts().get(0).setFirstName(null); // Violation
        assertThat(c.isVaild()).as("customer.isValid").isFalse();
        c.getContacts().get(0).setFirstName("Max"); // Repaierd
        assertThat(c.isVaild()).as("customer.isValid").isTrue();
    }

    @Test
    public void testIsVaild() {
        customer.getCompanies().clear();
        customer.add(contact);
        contact.add(communication);
        contact.add(address);
        assertThat(customer.isConsumer()).as("Customer is a Consumer Customer").isTrue();

        customer.getAddressLabels().add(new AddressLabel(company, contact, address, AddressType.INVOICE));
        assertThat(customer.isVaild()).as("Consumer Customer is vaild").isTrue();

        customer.getAddressLabels().clear();
        assertThat(customer.isVaild()).as("Customer is not vaild, because there is no AddressLable").isFalse();

        customer.getAddressLabels().add(new AddressLabel(gen.makeCompany(), gen.makeContact(), gen.makeAddress(), AddressType.SHIPPING));
        assertThat(customer.isVaild()).as("Customer is not vaild, because there is no AddressLable with Type Invoice").isFalse();

        customer.getAddressLabels().clear();
        customer.getAddressLabels().add(new AddressLabel(company, contact, address, AddressType.INVOICE));
        customer.getAddressLabels().add(new AddressLabel(company, contact, address, AddressType.INVOICE));
        customer.getAddressLabels().add(new AddressLabel(company, contact, address, AddressType.INVOICE));
        assertThat(customer.isVaild()).as("Customer is not vaild, because there are more than two AddressLables").isFalse();

        customer.getCompanies().clear();
        customer.getContacts().clear();
        assertThat(customer.isVaild()).as("Customer is not vaild, because there is no Company and no Contact is set").isFalse();

        customer.add(contact);
        customer.getContacts().get(0).setLastName("");
        assertThat(customer.isVaild()).as("Consumer Customer is not vaild, because the Contact do not have a LastName").isFalse();

        //transfrom to bussnes Customer
        customer.getContacts().clear();
        customer.getCompanies().add(company);
        company.add(communication);
        company.add(address);
        customer.getAddressLabels().clear();
        customer.getAddressLabels().add(new AddressLabel(company, contact, address, AddressType.INVOICE));
        assertThat(customer.isBussines()).as("Customer is a Bussines Customer").isTrue();
        assertThat(customer.isVaild()).as("Bussnis Customer is vaild").isTrue();

        customer.getCompanies().clear();
        company.setName("");
        customer.getCompanies().add(company);
        assertThat(customer.isVaild()).as("Bussnis Customer is not vaild, because the Name of the Compnay is blank").isFalse();

        customer.getCompanies().clear();
        company.getAddresses().clear();
        customer.add(company);
        assertThat(customer.isVaild()).as("Bussnis Customer is not vaild, the Compnay has no an Address").isFalse();

        customer.getCompanies().clear();
        company.add(address);
        company.getCommunications().clear();
        assertThat(customer.isVaild()).as("Bussnis Customer is not vaild, the Compnay has no Communication").isFalse();

    }

    @Test
    public void testIsVaildForANoneValidSimpleCustomerBecauseNoAddressLable() {
        customer.add(contact);
        customer.add(CustomerFlag.ITC_CUSTOMER);
        customer.setKeyAccounter("Herr Meier");
        customer.add(gen.makeMandatorMetadata());

        customer.getAddressLabels().clear();

        assertThat(customer.isSimple()).as("SimpleCustomer is not vaild, because there is no AddressLable").isFalse();
    }

    @Test
    public void testIsVaildForANoneValidSimpleCustomerBecauseWrongAddressLable() {
        customer.add(contact);
        customer.add(CustomerFlag.ITC_CUSTOMER);
        customer.setKeyAccounter("Herr Meier");
        customer.add(gen.makeMandatorMetadata());

        customer.getAddressLabels().add(new AddressLabel(gen.makeCompany(), gen.makeContact(), gen.makeAddress(), AddressType.SHIPPING));

        assertThat(customer.isSimple()).as("SimpleCustomer is not vaild, because the AddressLable is from wrong type").isFalse();
    }

    @Test
    public void testIsVaildForANoneValidSimpleCustomerBecauseTwoCompanies() {
        customer.add(CustomerFlag.ITC_CUSTOMER);
        customer.setKeyAccounter("Herr Meier");
        customer.add(gen.makeMandatorMetadata());

        customer.getContacts().clear();
        customer.add(company);
        customer.add(company);

        assertThat(customer.isSimple()).as("SimpleCustomer is not vaild, because there are 2 Companies").isFalse();
    }

    @Test
    public void testIsVaildForANoneValidSimpleCustomerBecauseNoCustomerFlag() {
        customer.add(contact);
        customer.setKeyAccounter("Herr Meier");
        customer.add(gen.makeMandatorMetadata());

        customer.getFlags().clear();

        assertThat(customer.isSimple()).as("SimpleCustomer is not vaild, because there is no CustomerFlag").isFalse();
    }

    @Test
    public void testIsVaildForANoneValidSimpleCustomerBecauseBlankKeyAccount() {
        customer.add(contact);
        customer.add(CustomerFlag.ITC_CUSTOMER);
        customer.add(gen.makeMandatorMetadata());

        customer.setKeyAccounter("");

        assertThat(customer.isSimple()).as("SimpleCustomer is not vaild, because the KeyAccounter is blank").isFalse();
    }

    @Test
    public void testIsVaildForANoneValidSimpleCustomerBecauseEmptyMandatorMetadata() {
        customer.add(contact);
        customer.add(CustomerFlag.ITC_CUSTOMER);
        customer.setKeyAccounter("Herr Meier");

        assertThat(customer.isSimple()).as("SimpleCustomer is not vaild, because the MandatorMetadata is empty").isFalse();
    }

    @Test
    public void testIsVaildForANoneValidCunsomerSimpleCustomerBecauseTwoContacts() {
        customer.add(contact);
        customer.add(CustomerFlag.ITC_CUSTOMER);
        customer.setKeyAccounter("Herr Meier");
        customer.add(gen.makeMandatorMetadata());

        assertThat(customer.isSimple()).as("SimpleCustomer is not vaild, because there are 2 Contacts").isFalse();
    }

    @Test
    public void testIsVaildForANoneValidCunsomerSimpleCustomerBecauseTwoAddress() {
        customer.add(contact);
        customer.add(CustomerFlag.ITC_CUSTOMER);
        customer.setKeyAccounter("Herr Meier");
        customer.add(gen.makeMandatorMetadata());

        customer.getContacts().get(0).add(address);

        assertThat(customer.isSimple()).as("SimpleCustomer is not vaild, because there are Contacst with 2 Address").isFalse();
    }

    @Test
    public void testIsVaildForANoneValidCunsomerSimpleCustomerBecauseFourCommunications() {
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
    public void testIsVaildForANoneValidCunsomerSimpleCustomerBecauseDoubleCommunicationsTyp() {
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
    public void testIsVaildForANoneValidCunsomerSimpleCustomerBecauseNotAllowedCommunication() {
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
        customer.add(contact);
        customer.getAddressLabels().add(new AddressLabel(company, contact, address, AddressType.INVOICE));
        customer.add(CustomerFlag.ITC_CUSTOMER);
        customer.setKeyAccounter("Herr Meier");
        customer.add(gen.makeMandatorMetadata());

        contact.add(communication);
        contact.getCommunications().get(0).setType(Type.PHONE);
        contact.getCommunications().get(0).setIdentifier("040123456789");

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

//        Company validCompany = new Company("Test Gmbh", 0, true, "01238321hd");
//        validCompany.add(new Address(AddressType.INVOICE));
//        Contact validContact = new Contact(Sex.MALE, true, "", "Testkunde", "Testkunde");
//
//        Customer c = new Customer();
//        assertThat(c.getViolationMessage()).as("customer is invalid").isNotNull();
//        c.add(validCompany);
//        assertThat(c.getViolationMessage()).as("businessCustomer is invalid").isNotNull();
//        c.getAddressLabels().add(new AddressLabel(c.getCompanies().get(0), null, new Address(), AddressType.INVOICE));
//        assertThat(c.getViolationMessage()).as("customern is valid").isNull();
    }

    @Test
    public void testGetViolationMessageForANoneValidCustomer() {
        customer.add(company);

        assertThat(customer.getViolationMessage()).as("Bussnis Customer is not vaild").isNotBlank();
    }

}
