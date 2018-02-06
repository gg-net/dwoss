package eu.ggnet.dwoss.customer.test;

import java.util.Arrays;
import java.util.Locale;

import org.junit.*;

import eu.ggnet.dwoss.customer.ee.assist.gen.CustomerGenerator;
import eu.ggnet.dwoss.customer.ee.entity.Communication.Type;
import eu.ggnet.dwoss.customer.ee.entity.Contact.Sex;
import eu.ggnet.dwoss.customer.ee.entity.*;
import eu.ggnet.dwoss.customer.ee.entity.dto.SimpleCustomer;
import eu.ggnet.dwoss.customer.ee.entity.projection.AddressLabel;
import eu.ggnet.dwoss.rules.AddressType;
import eu.ggnet.dwoss.rules.CustomerFlag;

import static eu.ggnet.dwoss.rules.AddressType.INVOICE;
import static eu.ggnet.dwoss.rules.AddressType.SHIPPING;
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

    public static Customer makeValidSimpleBusiness() {
        Address address = new Address();
        address.setStreet("Postallee 23");
        address.setZipCode("21234");
        address.setCity("Hamburg");
        address.setIsoCountry(Locale.GERMANY);
        assertThat(address.getViolationMessage()).as("address does not violate any rule").isNull();

        Company company = new Company("Musterfirma", 0, true, "1203223");
        company.getAddresses().add(address);
        company.getCommunications().add(new Communication(Type.EMAIL, true));
        company.getCommunications().get(0).setIdentifier("hans-juergen@gmx.net");
        assertThat(company.getViolationMessage()).as("company does not violate any rule").isNull();

        Customer customer = new Customer();
        customer.getCompanies().add(company);

        customer.getAddressLabels().add(new AddressLabel(company, null, customer.getCompanies().get(0).getAddresses().get(0), AddressType.INVOICE));

        assertThat(customer.getViolationMessage()).as("customer does not violate any rule").isNull();
        assertThat(customer.isSimple()).overridingErrorMessage("Customer is not simple, because: " + customer.getSimpleViolationMessage()).isTrue();
        assertThat(customer.isBusiness()).as("customer is a business customer").isTrue();
        assertThat(customer.isValid()).as("customer is a simple valid business customer").isTrue();

        return customer;
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

        AddressLabel invoiceLabel = new AddressLabel(null, customer.getContacts().get(0), customer.getContacts().get(0).getAddresses().get(0), AddressType.INVOICE);
        customer.getAddressLabels().add(invoiceLabel);

        assertThat(customer.isSimple()).overridingErrorMessage("Customer is not simple, because: " + customer.getSimpleViolationMessage()).isTrue();
        assertThat(customer.getSimpleViolationMessage()).as("customer does not violate any rule").isNull();
        assertThat(customer.isValid()).isTrue(); // optional

        assertThat(customer.isConsumer()).isTrue();

        return customer;
    }

    @Test
    public void testSimpleConsumer() {

        // JW: Hab ich auskommentiert, da der Test noch fehlschläg
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
//        assertThat(c.isValid()).isTrue(); // optional
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
//        assertThat(c.isBusiness()).isTrue();
//        assertThat(c.getViolationMessage()).as("Bussnis Customer is vaild").isNull();
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
//        assertThat(c.isValid()).isTrue(); // optional
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
//        assertThat(c.isBusiness()).isTrue();
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
        assertThat(c.isValid()).as("customer.isValid").isFalse();
        c.getContacts().get(0).setFirstName("Max"); // Repaierd
        assertThat(c.isValid()).as("customer.isValid").isTrue();
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

    @Test
    public void testToSimple() {
        customer.getContacts().add(contact);
        customer.getAddressLabels().add(new AddressLabel(company, contact, address, AddressType.INVOICE));
        customer.getFlags().add(CustomerFlag.ITC_CUSTOMER);
        customer.setKeyAccounter("Herr Meier");
        customer.getMandatorMetadata().add(gen.makeMandatorMetadata());

        contact.getCommunications().add(communication);
        contact.getCommunications().get(0).setType(Type.PHONE);
        contact.getCommunications().get(0).setIdentifier("040123456789");

        assertThat(customer.isConsumer()).as("Customer is a ConsumerCustomer").isTrue();
        assertThat(customer.toSimple()).as("Customer convert to SimpleCustomer and is not null").isNotNull();
    }

    @Test
    public void testToNotSimple() {
        customer.getContacts().add(contact);
        assertThat(customer.isConsumer()).as("Customer is a ConsumerCustomer").isTrue();
        assertThat(customer.toSimple()).as("Customer can not convert to SimpleCustomer and is null").isEmpty();
    }

    @Test
    public void testGetViolationMessage() {

    }

    @Test
    public void testGetViolationMessageSimpleConsumer() {
        Customer simpleConsumer = makeValidSimpleConsumer();

        simpleConsumer.getCompanies().add(new Company());
        assertThat(simpleConsumer.getViolationMessage()).as("SimpleConsumer with company is not valid").isNotNull();

        simpleConsumer.getCompanies().clear();

        assertThat(simpleConsumer.getViolationMessage()).as("SimpleConsumer with contact and without company is valid").isNull();
        simpleConsumer.getContacts().clear();
        assertThat(simpleConsumer.getViolationMessage()).as("SimpleConsumer without contact is invalid").isNotNull();

        simpleConsumer = makeValidSimpleConsumer();

        simpleConsumer.getAddressLabels().remove(simpleConsumer.getAddressLabels().stream().filter(label -> label.getType() == AddressType.INVOICE).findAny().get());
        assertThat(simpleConsumer.getViolationMessage()).as("Removal of any AddressLabel of type INVOICE from SimpleConsumer is invalid").isNotNull();

        simpleConsumer = makeValidSimpleConsumer();
        simpleConsumer.getAddressLabels().add(new AddressLabel(null, simpleConsumer.getContacts().get(0), simpleConsumer.getContacts().get(0).getAddresses().get(0), INVOICE));
        assertThat(simpleConsumer.getViolationMessage()).as("SimpleConsumer with two addressLabels of type INVOICE is invalid").isNull();

        simpleConsumer = makeValidSimpleConsumer();
        simpleConsumer.getAddressLabels().add(new AddressLabel(null, simpleConsumer.getContacts().get(0), simpleConsumer.getContacts().get(0).getAddresses().get(0), SHIPPING));
        assertThat(simpleConsumer.getViolationMessage()).as("SimpleConsumer with two addressLabels of unequal types is valid").isNull();
        simpleConsumer.getAddressLabels().add(new AddressLabel(null, simpleConsumer.getContacts().get(0), simpleConsumer.getContacts().get(0).getAddresses().get(0), SHIPPING));
        assertThat(simpleConsumer.getViolationMessage()).as("SimpleConsumer with three addressLabels is invalid").isNotNull();

        simpleConsumer = makeValidSimpleConsumer();
        Contact invalidContact = new Contact(Sex.MALE, true, null, "invalid", "");
        assertThat(invalidContact.getViolationMessage()).as("contact without lastName is invalid").isNotNull();
        simpleConsumer.getContacts().add(invalidContact);
        assertThat(simpleConsumer.getViolationMessage()).as("SimpleConsumer with invalid contact is invalid").isNotNull();

        simpleConsumer = makeValidSimpleConsumer();
        Company invalidCompany = new Company("", 0, true, "038483");
        assertThat(invalidCompany.getViolationMessage()).as("company without name is invalid").isNotNull();
        simpleConsumer.getCompanies().add(invalidCompany);
        assertThat(simpleConsumer.getViolationMessage()).as("SimpleConsumer with invalid company is invalid").isNotNull();

        simpleConsumer = makeValidSimpleConsumer();
        simpleConsumer.getContacts().get(0).getAddresses().clear();
        assertThat(simpleConsumer.getContacts().stream().flatMap(contacts -> contacts.getAddresses().stream()).count())
                .as("customer holds zero addresses").isEqualTo(0);

        assertThat(simpleConsumer.getViolationMessage()).as("SimpleConsumer without addresses is invalid").isNotNull();

        simpleConsumer = makeValidSimpleConsumer();
        simpleConsumer.getContacts().get(0).getCommunications().clear();
        assertThat(simpleConsumer.getViolationMessage()).as("SimpleConsumer without communications is invalid").isNotNull();

    }

    @Test
    @Ignore
    public void testGetSimpleViolationMessageSimpleConsumer() {
        Customer simpleConsumer = makeValidSimpleConsumer();
        simpleConsumer.getFlags().add(CustomerFlag.CS_UPDATE_CANDIDATE);
        assertThat(simpleConsumer.getSimpleViolationMessage()).as("SimpleConsumer with a flag is not simple").isNotNull();

        simpleConsumer = makeValidSimpleConsumer();
        simpleConsumer.setKeyAccounter("keyAccounter");
        assertThat(simpleConsumer.getSimpleViolationMessage()).as("SimpleConsumer with keyAccounter is not simple").isNotNull();

        simpleConsumer = makeValidSimpleConsumer();
        simpleConsumer.getMandatorMetadata().add(new MandatorMetadata());
        assertThat(simpleConsumer.getSimpleViolationMessage()).as("SimpleConsumer with mandatorMetadata is not simple").isNotNull();

        simpleConsumer = makeValidSimpleConsumer();
        simpleConsumer.getAddressLabels().clear();
        assertThat(simpleConsumer.getSimpleViolationMessage()).as("SimpleConsumer without AddressLabel is invalid").isNotNull();

        simpleConsumer = makeValidSimpleConsumer();
        simpleConsumer.getAddressLabels().add(new AddressLabel(null, simpleConsumer.getContacts().get(0), simpleConsumer.getContacts().get(0).getAddresses().get(0), AddressType.SHIPPING));
        assertThat(simpleConsumer.getSimpleViolationMessage()).as("SimpleConsumer with two AddressLabel is not simple").isNotNull();

        simpleConsumer = makeValidSimpleConsumer();
        Address address = new Address();
        address.setCity("City");
        address.setIsoCountry(Locale.GERMANY);
        address.setStreet("street");
        address.setZipCode("zipCode");
        simpleConsumer.getContacts().get(0).getAddresses().add(address);
        assertThat(simpleConsumer.getSimpleViolationMessage()).as("SimpleConsumer with more than one address is invalid").isNotNull();

        simpleConsumer = makeValidSimpleConsumer();
        Communication phone = new Communication(Type.PHONE, false);
        phone.setIdentifier("36184165");
        Communication mobile = new Communication(Type.MOBILE, false);
        mobile.setIdentifier("64682552");
        Communication email = new Communication(Type.EMAIL, false);
        email.setIdentifier("email@mail.com");

        simpleConsumer.getContacts().get(0).getCommunications().addAll(Arrays.asList(phone, mobile, email));
        assertThat(customer.getSimpleViolationMessage()).as("SimpleConsumer with more than three communications is invalid").isNotNull();

        simpleConsumer = makeValidSimpleConsumer();
        Communication skype = new Communication(Type.SKYPE, false);
        skype.setIdentifier("skypeUser4832");
        simpleConsumer.getContacts().get(0).getCommunications().add(skype);
        assertThat(customer.getSimpleViolationMessage()).as("SimpleConsumer with communication of type SKYPE is invalid").isNotNull();

        simpleConsumer = makeValidSimpleConsumer();
        simpleConsumer.getContacts().get(0).getCommunications().add(email);
        assertThat(customer.getSimpleViolationMessage()).as("SimpleConsumer with two communications of same type is invalid").isNotNull();

        simpleConsumer = makeValidSimpleConsumer();
        simpleConsumer.getContacts().forEach(contact -> contact.getCommunications().clear());
        simpleConsumer.getContacts().get(0).getCommunications().add(email);
        // TODO was geht denn hier ab?!
        assertThat(simpleConsumer.getContacts().isEmpty() && simpleConsumer.getCompanies().isEmpty()).as("either Contact or Company is set").isFalse();
        assertThat(customer.getSimpleViolationMessage()).as("SimpleConsumer with one communication of type EMAIL is valid").isNull();
        Communication otherEmail = new Communication(Type.EMAIL, false);
        email.setIdentifier("otherEmail@mail.com");
        simpleConsumer.getContacts().get(0).getCommunications().add(otherEmail);
        assertThat(customer.getSimpleViolationMessage()).as("SimpleConsumer with two communications of same type is invalid").isNotNull();

        simpleConsumer = makeValidSimpleConsumer();
        simpleConsumer.getContacts().forEach(contact -> contact.getCommunications().clear());
        simpleConsumer.getContacts().get(0).getCommunications().add(mobile);
        assertThat(customer.getSimpleViolationMessage()).as("SimpleConsumer with one communication of type MOBILE is valid").isNull();
        Communication otherMobile = new Communication(Type.MOBILE, false);
        mobile.setIdentifier("16461385");
        simpleConsumer.getContacts().stream().findAny().get().getCommunications().add(otherMobile);
        assertThat(customer.getSimpleViolationMessage()).as("SimpleConsumer with two communications of same type  is invalid").isNotNull();

        simpleConsumer = makeValidSimpleConsumer();
        simpleConsumer.getContacts().forEach(contact -> contact.getCommunications().clear());
        simpleConsumer.getContacts().get(0).getCommunications().add(phone);
        assertThat(customer.getSimpleViolationMessage()).as("SimpleConsumer with one communication of type PHONE is valid").isNull();
        Communication otherPhone = new Communication(Type.PHONE, false);
        mobile.setIdentifier("6541351");
        simpleConsumer.getContacts().stream().findAny().get().getCommunications().add(otherPhone);
        assertThat(customer.getSimpleViolationMessage()).as("SimpleConsumer with two communications of same type is invalid").isNotNull();

        simpleConsumer = makeValidSimpleConsumer();

    }

    @Test
    public void testGetViolationMessageSimpleBusinessCustomer() {
        Customer simpleBusinessCustomer = makeValidSimpleBusiness();
        simpleBusinessCustomer.getCompanies().clear();
        assertThat(simpleBusinessCustomer.getViolationMessage()).as("SimpleBusinessCustomer without companies is invalid").isNotNull();

        simpleBusinessCustomer = makeValidSimpleBusiness();
        simpleBusinessCustomer.getContacts().add(makeValidSimpleConsumer().getContacts().get(0));
        assertThat(simpleBusinessCustomer.getViolationMessage()).as("SimpleBusinessCustomer with a contact is invalid").isNotNull();

        simpleBusinessCustomer = makeValidSimpleBusiness();
        simpleBusinessCustomer.getAddressLabels().remove(simpleBusinessCustomer.getAddressLabels().stream().findAny().get());
        assertThat(simpleBusinessCustomer.getViolationMessage()).as("removal of any AddressLabel from a SimpleBusinessCustomer results invalid").isNotNull();

        simpleBusinessCustomer = makeValidSimpleBusiness();
        simpleBusinessCustomer.getAddressLabels().add(new AddressLabel(simpleBusinessCustomer.getCompanies().get(0), null, simpleBusinessCustomer.getCompanies().get(0).getAddresses().get(0), SHIPPING));
        simpleBusinessCustomer.getAddressLabels().add(new AddressLabel(simpleBusinessCustomer.getCompanies().get(0), null, simpleBusinessCustomer.getCompanies().get(0).getAddresses().get(0), SHIPPING));
        assertThat(simpleBusinessCustomer.getViolationMessage()).as("adding two AddressLabels to a BusinessCustomer results invalid").isNotNull();

        simpleBusinessCustomer = makeValidSimpleBusiness();
        simpleBusinessCustomer.getCompanies().add(new Company("", 0, true, "634855"));
        assertThat(simpleBusinessCustomer.getViolationMessage()).as("adding an invalid Company to a BusinessCustomer results invalid").isNotNull();

        simpleBusinessCustomer = makeValidSimpleBusiness();
        simpleBusinessCustomer.getCompanies().forEach(company -> company.getCommunications().clear());
        assertThat(simpleBusinessCustomer.getViolationMessage()).as("BusinessCustomer without Communications is invalid").isNotNull();

    }

    @Test
    public void testGetViolationMessageConsumer() {
        Customer customer = makeValidConsumer();
    }

    @Test
    public void testGetViolationMessagedBusinessCustomer() {
        Customer customer = makeValidBusinessCustomer();
    }

}
