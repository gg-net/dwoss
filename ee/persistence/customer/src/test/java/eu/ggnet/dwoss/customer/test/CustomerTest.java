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

import static eu.ggnet.dwoss.customer.test.CustomerTest.makeValidConsumer;
import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author oliver.guenther
 */
// Testtarget: isSimple, toSimple, isValid
public class CustomerTest {

    public static Company makeValidCompany() {
        Company validcompany = new Company();
        validcompany.setName("Firma ABC");
        validcompany.getAddresses().add(makeValidAddress());
        validcompany.getCommunications().add(makeValidCommunication());

        assertThat(validcompany.getViolationMessage()).as("valid Company").isNull();
        return validcompany;
    }

    public static Contact makeValidContact() {
        Contact validContact = new Contact(Sex.MALE, true, "", "Testkunde", "Testkunde");
        validContact.getCommunications().add(makeValidCommunication());
        validContact.getAddresses().add(makeValidAddress());

        assertThat(validContact.getViolationMessage()).as("valid Contact").isNull();
        return validContact;
    }

    public static Communication makeValidCommunication() {
        Communication validCommunication = new Communication(Type.EMAIL, true);
        validCommunication.setIdentifier("Max.mustermann@mustermail.de");

        assertThat(validCommunication.getViolationMessage()).as("valid Address").isNull();
        return validCommunication;
    }

    public static Address makeValidAddress() {
        Address validAddress = new Address();
        validAddress.setCity("Munich");
        validAddress.setIsoCountry(Locale.GERMANY);
        validAddress.setStreet("Straße");
        validAddress.setZipCode("123456");

        assertThat(validAddress.getViolationMessage()).as("valid Address").isNull();
        return validAddress;
    }

    public static AddressLabel makeValidAddressLabel() {
        AddressLabel validAddressLabel = new AddressLabel(makeValidCompany(), makeValidContact(), makeValidAddress(), AddressType.INVOICE);

        assertThat(validAddressLabel.getViolationMessage()).as("valid validAddressLabel").isNull();
        return validAddressLabel;
    }

    public static Customer makeValidConsumer() {
        Contact validContact = makeValidContact();

        Customer customer = new Customer();
        customer.getContacts().add(validContact);

        customer.getAddressLabels().add(makeValidAddressLabel());

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
        Customer customer = new Customer();
        customer.getContacts().clear();
        customer.getCompanies().add(makeValidCompany());
        customer.getAddressLabels().add(makeValidAddressLabel());

        assertThat(customer.getViolationMessage()).as("customer does not violate any rule").isNull();
        assertThat(customer.isBusiness()).as("customer is a business customer").isTrue();
        assertThat(customer.isSimple()).overridingErrorMessage("Customer is not simple, because: " + customer.getSimpleViolationMessage()).isTrue();

        customer.getFlags().add(CustomerFlag.CS_UPDATE_CANDIDATE);
        assertThat(customer.isSimple()).overridingErrorMessage("Customer is not simple, because: " + customer.getSimpleViolationMessage()).isFalse();
        assertThat(customer.getViolationMessage()).as("customer does not violate any rule").isNull();

        assertThat(customer.isValid()).as("customer is a simple valid business customer").isTrue();
        return customer;
    }

    @Test
    public void testOutPut() {
        Customer makeValidConsumer = makeValidConsumer();
        assertThat(makeValidConsumer).describedAs("customer").isNotNull();
    }

    @Test
    public void testIsVaildConsumerCostmer() {
        Customer makeValidConsumer = makeValidConsumer();
        assertThat(makeValidConsumer.isConsumer()).as("Customer is a Consumer Customer").isTrue();

        makeValidConsumer = makeValidConsumer();
        makeValidConsumer.getAddressLabels().add(makeValidAddressLabel());
        assertThat(makeValidConsumer.isValid()).as("Consumer Customer is vaild").isTrue();

        makeValidConsumer = makeValidConsumer();
        makeValidConsumer.getAddressLabels().clear();
        assertThat(makeValidConsumer.isValid()).as("Customer is not vaild, because there is no AddressLable").isFalse();

        makeValidConsumer = makeValidConsumer();
        AddressLabel shoppingAddressLabel = makeValidAddressLabel();
        shoppingAddressLabel.setType(AddressType.SHIPPING);
        assertThat(shoppingAddressLabel.getViolationMessage()).as("Valid AddressLabel").isNull();

        makeValidConsumer.getAddressLabels().clear();
        makeValidConsumer.getAddressLabels().add(shoppingAddressLabel);
        assertThat(makeValidConsumer.isValid()).as("Customer is not vaild, because there is no AddressLable with Type Invoice").isFalse();

        makeValidConsumer = makeValidConsumer();
        makeValidConsumer.getAddressLabels().clear();
        makeValidConsumer.getAddressLabels().add(makeValidAddressLabel());
        makeValidConsumer.getAddressLabels().add(makeValidAddressLabel());
        makeValidConsumer.getAddressLabels().add(makeValidAddressLabel());
        assertThat(makeValidConsumer.isValid()).as("Customer is not vaild, because there are more than two AddressLables").isFalse();

        makeValidConsumer = makeValidConsumer();
        makeValidConsumer.getCompanies().clear();
        makeValidConsumer.getContacts().clear();
        assertThat(makeValidConsumer.isValid()).as("Customer is not vaild, because there is no Company and no Contact is set").isFalse();

        makeValidConsumer = makeValidConsumer();

        Contact makeValidContact = makeValidContact();
        makeValidContact.setLastName("");
        makeValidConsumer.getContacts().add(makeValidContact);
        assertThat(makeValidConsumer.isValid()).as("Consumer Customer is not vaild, because the Contact do not have a LastName").isFalse();

    }

    @Test
    public void testIsVaildBussnisCostmer() {

        Customer makeValidBusinessCustomer = makeValidBusinessCustomer();

        assertThat(makeValidBusinessCustomer.isBusiness()).as("Customer is a Bussines Customer").isTrue();
        assertThat(makeValidBusinessCustomer.isValid()).as("Bussnis Customer is vaild").isTrue();

        makeValidBusinessCustomer.getCompanies().clear();
        Company makeValidCompany = makeValidCompany();
        makeValidCompany.setName("");
        makeValidBusinessCustomer.getCompanies().add(makeValidCompany);
        assertThat(makeValidBusinessCustomer.isValid()).as("Bussnis Customer is not vaild, because the Name of the Compnay is blank").isFalse();

        makeValidBusinessCustomer = makeValidBusinessCustomer();
        makeValidBusinessCustomer.getCompanies().clear();
        makeValidCompany = makeValidCompany();
        makeValidCompany.getAddresses().clear();

        makeValidBusinessCustomer.getCompanies().add(makeValidCompany);
        assertThat(makeValidBusinessCustomer.isValid()).as("Bussnis Customer is not vaild, the Compnay has no an Address").isFalse();

        makeValidBusinessCustomer = makeValidBusinessCustomer();
        makeValidBusinessCustomer.getCompanies().clear();
        makeValidCompany = makeValidCompany();
        makeValidCompany.getCommunications().clear();
        assertThat(makeValidBusinessCustomer.isValid()).as("Bussnis Customer is not vaild, the Compnay has no Communication").isFalse();

    }

}
