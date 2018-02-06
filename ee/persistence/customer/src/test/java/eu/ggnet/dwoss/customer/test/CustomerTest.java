package eu.ggnet.dwoss.customer.test;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

import eu.ggnet.dwoss.customer.ee.assist.gen.CustomerGenerator;
import eu.ggnet.dwoss.customer.ee.entity.Communication.Type;
import eu.ggnet.dwoss.customer.ee.entity.Contact.Sex;
import eu.ggnet.dwoss.customer.ee.entity.*;
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

    public static Customer makeValidConsumer() {
        Contact validContact = new Contact(Sex.MALE, true, "", "Testkunde", "Testkunde");
        validContact.getCommunications().add(new Communication(Type.EMAIL, true));
        validContact.getCommunications().get(0).setIdentifier("Max.mustermann@mustermail.de");
        Address validAddress = new Address();
        validAddress.setCity("Munich");
        validAddress.setIsoCountry(Locale.GERMANY);
        validAddress.setStreet("Straße");
        validAddress.setZipCode("123456");
        validContact.getAddresses().add(validAddress);
        Customer customer = new Customer();
        customer.getContacts().add(validContact);

        customer.getAddressLabels().add(new AddressLabel(null, validContact, customer.getContacts().get(0).getAddresses().get(0), AddressType.INVOICE));
        assertThat(customer.getViolationMessage()).as("customer does not violate any role").isNull();
        assertThat(customer.isConsumer()).as("customer is a consumer").isTrue();
        assertThat(customer.isSimple()).overridingErrorMessage("Customer is not simple, because: " + customer.getSimpleViolationMessage()).isTrue();

        customer.getFlags().add(CustomerFlag.CS_UPDATE_CANDIDATE);
        assertThat(customer.isSimple()).overridingErrorMessage("Customer is not simple, because: " + customer.getSimpleViolationMessage()).isFalse();
        assertThat(customer.getViolationMessage()).as("customer does not violate any role").isNull();
        assertThat(customer.isValid()).as("customer is valid customer").isTrue();

        return customer;

    }

    public static Customer makeValidBusinessCustomer() {

        Address address = new Address();
        address.setStreet("Postallee 23");
        address.setZipCode("21234");
        address.setCity("Hamburg");
        address.setIsoCountry(Locale.GERMANY);
        assertThat(address.getViolationMessage()).as("address does not violate any rule").isNull();

        Company company = new Company("Musterfirma", 0, true, "1203223");
        company.getAddresses().add(address);
        company.getCommunications().add(new Communication(Type.EMAIL, true));
        company.getCommunications().get(0).setIdentifier("ceoA@gmx.net");

        company.getCommunications().add(new Communication(Type.EMAIL, false));
        company.getCommunications().get(1).setIdentifier("ceoB@gmx.net");

        assertThat(company.getViolationMessage()).as("company does not violate any rule").isNull();

        Customer customer = new Customer();
        customer.getCompanies().add(company);

        customer.getAddressLabels().add(new AddressLabel(company, null, customer.getCompanies().get(0).getAddresses().get(0), AddressType.INVOICE));
        assertThat(customer.getViolationMessage()).as("customer does not violate any rule").isNull();
        assertThat(customer.isSimple()).overridingErrorMessage("Customer is not simple, because: " + customer.getSimpleViolationMessage()).isFalse();
        assertThat(customer.isBusiness()).as("customer is a business customer").isTrue();
        assertThat(customer.isValid()).as("customer is a simple valid business customer").isTrue();

        return customer;

    }

    @Test
    public void testOutPut() {
        assertThat(customer).describedAs("customer").isNotNull();
    }

    @Test
    public void testIsVaild() {
        customer.getCompanies().clear();
        customer.getContacts().add(contact);
        contact.getCommunications().add(communication);
        contact.getAddresses().add(address);
        assertThat(customer.isConsumer()).as("Customer is a Consumer Customer").isTrue();

        customer.getAddressLabels().add(new AddressLabel(company, contact, address, AddressType.INVOICE));
        assertThat(customer.isValid()).as("Consumer Customer is vaild").isTrue();

        customer.getAddressLabels().clear();
        assertThat(customer.isValid()).as("Customer is not vaild, because there is no AddressLable").isFalse();

        customer.getAddressLabels().add(new AddressLabel(gen.makeCompany(), gen.makeContact(), gen.makeAddress(), AddressType.SHIPPING));
        assertThat(customer.isValid()).as("Customer is not vaild, because there is no AddressLable with Type Invoice").isFalse();

        customer.getAddressLabels().clear();
        customer.getAddressLabels().add(new AddressLabel(company, contact, address, AddressType.INVOICE));
        customer.getAddressLabels().add(new AddressLabel(company, contact, address, AddressType.INVOICE));
        customer.getAddressLabels().add(new AddressLabel(company, contact, address, AddressType.INVOICE));
        assertThat(customer.isValid()).as("Customer is not vaild, because there are more than two AddressLables").isFalse();

        customer.getCompanies().clear();
        customer.getContacts().clear();
        assertThat(customer.isValid()).as("Customer is not vaild, because there is no Company and no Contact is set").isFalse();

        customer.getContacts().add(contact);
        customer.getContacts().get(0).setLastName("");
        assertThat(customer.isValid()).as("Consumer Customer is not vaild, because the Contact do not have a LastName").isFalse();

        //transfrom to bussnes Customer
        customer.getContacts().clear();
        customer.getCompanies().add(company);
        company.getCommunications().add(communication);
        company.getAddresses().add(address);
        customer.getAddressLabels().clear();
        customer.getAddressLabels().add(new AddressLabel(company, contact, address, AddressType.INVOICE));
        assertThat(customer.isBusiness()).as("Customer is a Bussines Customer").isTrue();
        assertThat(customer.isValid()).as("Bussnis Customer is vaild").isTrue();

        customer.getCompanies().clear();
        company.setName("");
        customer.getCompanies().add(company);
        assertThat(customer.isValid()).as("Bussnis Customer is not vaild, because the Name of the Compnay is blank").isFalse();

        customer.getCompanies().clear();
        company.getAddresses().clear();
        customer.getCompanies().add(company);
        assertThat(customer.isValid()).as("Bussnis Customer is not vaild, the Compnay has no an Address").isFalse();

        customer.getCompanies().clear();
        company.getAddresses().add(address);
        company.getCommunications().clear();
        assertThat(customer.isValid()).as("Bussnis Customer is not vaild, the Compnay has no Communication").isFalse();

    }

    @Test
    public void testIsVaildForANoneValidSimpleCustomerBecauseNoAddressLable() {
        customer.getContacts().add(contact);
        customer.getFlags().add(CustomerFlag.ITC_CUSTOMER);
        customer.setKeyAccounter("Herr Meier");
        customer.getMandatorMetadata().add(gen.makeMandatorMetadata());

        customer.getAddressLabels().clear();

        assertThat(customer.isSimple()).as("SimpleCustomer is not vaild, because there is no AddressLable").isFalse();
    }

    @Test
    public void testIsVaildForANoneValidSimpleCustomerBecauseWrongAddressLable() {
        customer.getContacts().add(contact);
        customer.getFlags().add(CustomerFlag.ITC_CUSTOMER);
        customer.setKeyAccounter("Herr Meier");
        customer.getMandatorMetadata().add(gen.makeMandatorMetadata());

        customer.getAddressLabels().add(new AddressLabel(gen.makeCompany(), gen.makeContact(), gen.makeAddress(), AddressType.SHIPPING));

        assertThat(customer.isSimple()).as("SimpleCustomer is not vaild, because the AddressLable is from wrong type").isFalse();
    }

    @Test
    public void testIsVaildForANoneValidSimpleCustomerBecauseTwoCompanies() {
        customer.getFlags().add(CustomerFlag.ITC_CUSTOMER);
        customer.setKeyAccounter("Herr Meier");
        customer.getMandatorMetadata().add(gen.makeMandatorMetadata());

        customer.getContacts().clear();
        customer.getCompanies().add(company);
        customer.getCompanies().add(company);

        assertThat(customer.isSimple()).as("SimpleCustomer is not vaild, because there are 2 Companies").isFalse();
    }

    @Test
    public void testIsVaildForANoneValidSimpleCustomerBecauseNoCustomerFlag() {
        customer.getContacts().add(contact);
        customer.setKeyAccounter("Herr Meier");
        customer.getMandatorMetadata().add(gen.makeMandatorMetadata());

        customer.getFlags().clear();

        assertThat(customer.isSimple()).as("SimpleCustomer is not vaild, because there is no CustomerFlag").isFalse();
    }

    @Test
    public void testIsVaildForANoneValidSimpleCustomerBecauseBlankKeyAccount() {
        customer.getContacts().add(contact);
        customer.getFlags().add(CustomerFlag.ITC_CUSTOMER);
        customer.getMandatorMetadata().add(gen.makeMandatorMetadata());

        customer.setKeyAccounter("");

        assertThat(customer.isSimple()).as("SimpleCustomer is not vaild, because the KeyAccounter is blank").isFalse();
    }

    @Test
    public void testIsVaildForANoneValidSimpleCustomerBecauseEmptyMandatorMetadata() {
        customer.getContacts().add(contact);
        customer.getFlags().add(CustomerFlag.ITC_CUSTOMER);
        customer.setKeyAccounter("Herr Meier");

        assertThat(customer.isSimple()).as("SimpleCustomer is not vaild, because the MandatorMetadata is empty").isFalse();
    }

    @Test
    public void testIsVaildForANoneValidCunsomerSimpleCustomerBecauseTwoContacts() {
        customer.getContacts().add(contact);
        customer.getFlags().add(CustomerFlag.ITC_CUSTOMER);
        customer.setKeyAccounter("Herr Meier");
        customer.getMandatorMetadata().add(gen.makeMandatorMetadata());

        assertThat(customer.isSimple()).as("SimpleCustomer is not vaild, because there are 2 Contacts").isFalse();
    }

    @Test
    public void testIsVaildForANoneValidCunsomerSimpleCustomerBecauseTwoAddress() {
        customer.getContacts().add(contact);
        customer.getFlags().add(CustomerFlag.ITC_CUSTOMER);
        customer.setKeyAccounter("Herr Meier");
        customer.getMandatorMetadata().add(gen.makeMandatorMetadata());

        customer.getContacts().get(0).getAddresses().add(address);

        assertThat(customer.isSimple()).as("SimpleCustomer is not vaild, because there are Contacst with 2 Address").isFalse();
    }

    @Test
    public void testIsVaildForANoneValidCunsomerSimpleCustomerBecauseFourCommunications() {
        customer.getContacts().add(contact);
        customer.getFlags().add(CustomerFlag.ITC_CUSTOMER);
        customer.setKeyAccounter("Herr Meier");
        customer.getMandatorMetadata().add(gen.makeMandatorMetadata());

        customer.getContacts().get(0).getCommunications().add(communication);
        customer.getContacts().get(0).getCommunications().add(communication);
        customer.getContacts().get(0).getCommunications().add(communication);

        assertThat(customer.isSimple()).as("SimpleCustomer is not vaild, because there are Contacst have 4 Communications").isFalse();
    }

    @Test
    public void testIsVaildForANoneValidCunsomerSimpleCustomerBecauseDoubleCommunicationsTyp() {
        customer.getContacts().add(contact);
        customer.getFlags().add(CustomerFlag.ITC_CUSTOMER);
        customer.setKeyAccounter("Herr Meier");
        customer.getMandatorMetadata().add(gen.makeMandatorMetadata());

        Communication communicationEmail = new Communication();
        communicationEmail.setType(Type.EMAIL);
        communicationEmail.setIdentifier("040123456789");

        customer.getContacts().get(0).getCommunications().add(communicationEmail);
        customer.getContacts().get(0).getCommunications().add(communicationEmail);

        assertThat(customer.isSimple()).as("SimpleCustomer is not vaild, because there are Contacst have 2 Communications form the same typ").isFalse();
    }

    @Test
    public void testIsVaildForANoneValidCunsomerSimpleCustomerBecauseNotAllowedCommunication() {
        customer.getContacts().add(contact);
        customer.getFlags().add(CustomerFlag.ITC_CUSTOMER);
        customer.setKeyAccounter("Herr Meier");
        customer.getMandatorMetadata().add(gen.makeMandatorMetadata());

        Communication icq = new Communication();
        icq.setType(Type.ICQ);

        customer.getContacts().get(0).getCommunications().add(icq);

        assertThat(customer.isSimple()).as("SimpleCustomer is not vaild, because there are Contacst have a Communications that is not allowed").isFalse();
    }

}
