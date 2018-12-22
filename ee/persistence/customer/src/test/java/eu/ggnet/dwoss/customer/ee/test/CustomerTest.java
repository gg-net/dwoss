package eu.ggnet.dwoss.customer.ee.test;

import org.junit.Test;

import eu.ggnet.dwoss.common.api.values.AddressType;
import eu.ggnet.dwoss.customer.ee.assist.gen.CustomerGenerator;
import eu.ggnet.dwoss.customer.ee.entity.*;

import static eu.ggnet.dwoss.customer.ee.entity.Communication.Type.EMAIL;
import static eu.ggnet.dwoss.customer.ee.entity.Communication.Type.PHONE;
import static eu.ggnet.dwoss.customer.ee.make.StaticCustomerMaker.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author oliver.guenther
 */
public class CustomerTest {

    private final CustomerGenerator GEN = new CustomerGenerator();

    @Test
    public void vaildConsumerCustomer() {
        Customer makeValidConsumer = makeValidConsumerCustomer();
        assertThat(makeValidConsumer.isConsumer()).as("Customer is a Consumer Customer").isTrue();

        makeValidConsumer = makeValidConsumerCustomer();
        makeValidConsumer.getAddressLabels().add(makeValidInvoiceAddressLabel());
        assertThat(makeValidConsumer.isValid()).as("Consumer should be invalid due to a third address label").isFalse();

        makeValidConsumer = makeValidConsumerCustomer();
        makeValidConsumer.getAddressLabels().clear();
        assertThat(makeValidConsumer.isValid()).as("Customer is not vaild, because there is no AddressLable").isFalse();

        makeValidConsumer = makeValidConsumerCustomer();
        AddressLabel shoppingAddressLabel = makeValidInvoiceAddressLabel();
        shoppingAddressLabel.setType(AddressType.SHIPPING);
        assertThat(shoppingAddressLabel.getViolationMessage()).as("Valid AddressLabel").isNull();

        makeValidConsumer.getAddressLabels().clear();
        makeValidConsumer.getAddressLabels().add(shoppingAddressLabel);
        assertThat(makeValidConsumer.isValid()).as("Customer is not vaild, because there is no AddressLable with Type Invoice").isFalse();

        makeValidConsumer = makeValidConsumerCustomer();
        makeValidConsumer.getAddressLabels().clear();
        makeValidConsumer.getAddressLabels().add(makeValidInvoiceAddressLabel());
        makeValidConsumer.getAddressLabels().add(makeValidInvoiceAddressLabel());
        makeValidConsumer.getAddressLabels().add(makeValidInvoiceAddressLabel());
        assertThat(makeValidConsumer.isValid()).as("Customer is not vaild, because there are more than two AddressLables").isFalse();

        makeValidConsumer = makeValidConsumerCustomer();
        makeValidConsumer.getCompanies().clear();
        makeValidConsumer.getContacts().clear();
        assertThat(makeValidConsumer.isValid()).as("Customer is not vaild, because there is no Company and no Contact is set").isFalse();

        makeValidConsumer = makeValidConsumerCustomer();
        Contact invalidContact = makeValidContact();
        invalidContact.setLastName("");
        assertThat(invalidContact.getViolationMessage()).as("not valid Contact").isNotNull();

        makeValidConsumer.getContacts().add(invalidContact);
        assertThat(makeValidConsumer.isValid()).as("Consumer Customer is not vaild, because the Contact do not have a LastName").isFalse();

    }

    @Test
    public void testIsVaildBusinessCustomer() {

        Customer makeValidBusinessCustomer = makeValidBusinessCustomer();

        assertThat(makeValidBusinessCustomer.isBusiness()).as("Customer should be a bussines customer").isTrue();
        assertThat(makeValidBusinessCustomer.isValid()).as("Bussnis Customer is vaild").isTrue();

        makeValidBusinessCustomer.getCompanies().clear();
        Company makeValidCompany = makeValidCompany();
        makeValidCompany.setName("");
        makeValidBusinessCustomer.getCompanies().add(makeValidCompany);
        assertThat(makeValidBusinessCustomer.isValid()).as("Bussnis Customer is not vaild, because the Name of the Compnay is blank").isFalse();

        makeValidBusinessCustomer = makeValidBusinessCustomer();
        makeValidBusinessCustomer.getAddressLabels().clear();
        assertThat(makeValidBusinessCustomer.isValid()).as("Bussnis Customer is not vaild, because there is no AddressLable").isFalse();

        makeValidBusinessCustomer = makeValidBusinessCustomer();
        AddressLabel shoppingAddressLabel = makeValidInvoiceAddressLabel();
        shoppingAddressLabel.setType(AddressType.SHIPPING);
        assertThat(shoppingAddressLabel.getViolationMessage()).as("Valid AddressLabel").isNull();

        makeValidBusinessCustomer.getAddressLabels().clear();
        makeValidBusinessCustomer.getAddressLabels().add(shoppingAddressLabel);
        assertThat(makeValidBusinessCustomer.isValid()).as("Bussnis Customer is not vaild, because there is no AddressLable with Type Invoice").isFalse();

        makeValidBusinessCustomer = makeValidBusinessCustomer();
        makeValidBusinessCustomer.getAddressLabels().clear();
        makeValidBusinessCustomer.getAddressLabels().add(makeValidInvoiceAddressLabel());
        makeValidBusinessCustomer.getAddressLabels().add(makeValidInvoiceAddressLabel());
        makeValidBusinessCustomer.getAddressLabels().add(makeValidInvoiceAddressLabel());
        assertThat(makeValidBusinessCustomer.isValid()).as("Bussnis Customer is not vaild, because there are more than two AddressLables").isFalse();

        makeValidBusinessCustomer = makeValidBusinessCustomer();
        makeValidBusinessCustomer.getCompanies().clear();
        makeValidCompany = makeValidCompany();
        makeValidCompany.getCommunications().clear();
        makeValidBusinessCustomer.getCompanies().add(makeValidCompany);
        assertThat(makeValidCompany.getViolationMessage()).as("valid company without communication").isNull();
        assertThat(makeValidBusinessCustomer).as("Bussnis Customer is not vaild, the Compnay is valid but has no an Communications").returns(false, c -> c.isValid());

    }

    /**
     * Tests that a invalid communication type on the defaultEmailCommunication is detected.
     */
    @Test
    public void validationOfDefaultEmailCommunication() {
        Customer consumer = GEN.makeSimpleConsumerCustomer();
        Contact contact = consumer.getContacts().get(0); // This is by definition correct.
        if ( contact.getCommunications().stream().anyMatch(c -> c.getType() == EMAIL) ) {
            // Make sure, there is one email communication.
            Communication comm = new Communication(EMAIL, "demo@demo.com");
            contact.getCommunications().add(comm);
            consumer.setDefaultEmailCommunication(comm);
        }

        assertThat(consumer.getViolationMessage()).as("Should be a valid customer with a default email communication").isNull();

        consumer.setDefaultEmailCommunication(null);
        assertThat(consumer.getViolationMessage()).as("Should be a valid customer with a 'null' default email communication").isNull();

        consumer.setDefaultEmailCommunication(new Communication(PHONE, "123123123"));
        assertThat(consumer.getViolationMessage()).as("Should be a invalid customer with an invalid default email communication, type is wrong").isNotNull();
    }

}
