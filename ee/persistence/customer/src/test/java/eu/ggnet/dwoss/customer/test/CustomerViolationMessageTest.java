/*
 * Copyright (C) 2018 GG-Net GmbH
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.ggnet.dwoss.customer.test;

import java.util.Arrays;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import eu.ggnet.dwoss.customer.ee.entity.Communication.Type;
import eu.ggnet.dwoss.customer.ee.entity.Contact.Sex;
import eu.ggnet.dwoss.customer.ee.entity.*;
import eu.ggnet.dwoss.customer.ee.entity.projection.AddressLabel;
import eu.ggnet.dwoss.rules.AddressType;
import eu.ggnet.dwoss.rules.CustomerFlag;

import static eu.ggnet.dwoss.customer.test.CustomerTest.makeValidBusinessCustomer;
import static eu.ggnet.dwoss.rules.AddressType.INVOICE;
import static eu.ggnet.dwoss.rules.AddressType.SHIPPING;
import static java.util.Locale.GERMANY;
import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author jacob.weinhold
 */
public class CustomerViolationMessageTest {

    /**
     *
     * @return Valid Company without Contact or Communication
     */
    public static Company makeValidCompany() {
        Company validcompany = new Company();
        validcompany.setName("Firma ABC");
        validcompany.setTaxId("textid123456789");

        validcompany.getAddresses().add(makeValidAddress());
        assertThat(validcompany.getViolationMessage()).as("valid Company").isNull();
        return validcompany;
    }

    /**
     *
     * @return Valid Contact as in Contact.getViolationMessage()
     *         with one Address and no Communication.
     */
    public static Contact makeValidContact() {
        Contact validContact = new Contact(Sex.MALE, true, "Dr", "Max", "Mustermann");
        validContact.getAddresses().add(makeValidAddress());
        assertThat(validContact.getViolationMessage()).as("Valid Contact does not violate any Rule").isNull();

        return validContact;
    }

    /**
     *
     * @param type       Not Null
     * @param identifier Not Null
     * @return Valid Communication as in Communication.getViolationMessage()
     */
    public static Communication makeValidCommunication(Type type, String identifier) {
        if ( type == null || identifier == null || StringUtils.isBlank(identifier) )
            throw new NullPointerException();
        Communication validCommunication = new Communication(type, true);
        validCommunication.setIdentifier(identifier);

        assertThat(validCommunication.getViolationMessage()).as("Communication does not violate any rule").isNull();
        return validCommunication;
    }

    /**
     *
     * @return Valid Address as in Address.getViolationMessage()
     */
    public static Address makeValidAddress() {
        Address validAddress = new Address();
        validAddress.setCity("Munich");
        validAddress.setIsoCountry(Locale.GERMANY);
        validAddress.setStreet("Straße");
        validAddress.setZipCode("123456");

        assertThat(validAddress.getViolationMessage()).as("valid Address").isNull();
        return validAddress;
    }

    /**
     * Uses makeValidCompany, makeValidContact and makeValidAddress.
     *
     * @return Valid AddressLabel as in AddressLabel.getViolationMessage()
     *
     */
    public static AddressLabel makeValidAddressLabel() {
        AddressLabel validAddressLabel = new AddressLabel(makeValidCompany(), makeValidContact(), makeValidAddress(), AddressType.INVOICE);

        assertThat(validAddressLabel.getViolationMessage()).as("valid validAddressLabel").isNull();
        return validAddressLabel;
    }

    /**
     *
     * @return Valid SimpleBusinessCustomer as in Customer.isValid()
     *         with Communications of type PHONE on it's Company and of type EMAIL on it's Company's
     *         contact
     */
    public static Customer makeValidSimpleBusiness() {
        Address address = makeValidAddress();
        assertThat(address.getViolationMessage()).as("address does not violate any rule").isNull();

        Company company = new Company("Musterfirma", 0, true, "1203223");
        company.getAddresses().add(address);
        company.getCommunications().add(new Communication(Type.PHONE, true));
        company.getCommunications().get(0).setIdentifier("01234567");

        assertThat(company.getViolationMessage()).as("company does not violate any rule").isNull();

        Contact validContact = new Contact(Sex.FEMALE, true, "", "Testkunde", "Testkunde");
        Communication validCommunication = new Communication(Type.EMAIL, true);
        validCommunication.setIdentifier("Max.mustermann@mustermail.de");
        validContact.getCommunications().add(validCommunication);
        validContact.getAddresses().add(makeValidAddress());
        assertThat(validContact.getViolationMessage()).as("valid Contact").isNull();
        company.getContacts().add(validContact);

        Customer customer = new Customer();
        customer.getCompanies().add(company);

        customer.getAddressLabels().add(makeValidAddressLabel());

        assertThat(customer.getViolationMessage()).as("SimpleBusinessCustomer does not violate any rule").isNull();
        assertThat(customer.isSimple()).overridingErrorMessage("SimpleBusinessCustomer is not simple, because: " + customer.getSimpleViolationMessage()).isTrue();
        assertThat(customer.isBusiness()).as("SimpleBusinessCustomer is a business customer").isTrue();
        assertThat(customer.isValid()).as("SimpleBusinessCustomer is a valid customer").isTrue();

        return customer;
    }

    /**
     *
     * @return Valid SimpleCustomer as in Customer.getSimpleViolationMessage() with one Communication
     *         of Type EMAIL and one Address on it's Contact and (Customer.isConsumer() && Customer.isSimple()) == true, Customer.isValid() == True and
     *         Customer.getViolationMessage
     *         == null.
     *
     */
    public static Customer makeValidSimpleConsumer() {
        Customer customer = new Customer();

        Contact makeValidContact = makeValidContact();
        makeValidContact.getCommunications().add(makeValidCommunication(Communication.Type.EMAIL, "testMail@test.net"));
        customer.getContacts().add(makeValidContact);

        customer.getAddressLabels().add(makeValidAddressLabel());

        assertThat(customer.isSimple()).overridingErrorMessage("Customer is not simple, because: " + customer.getSimpleViolationMessage()).isTrue();
        assertThat(customer.getSimpleViolationMessage()).as("customer does not violate any rule").isNull();
        assertThat(customer.isValid()).isTrue();
        assertThat(customer.isConsumer()).isTrue();

        return customer;
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
        simpleConsumer.getContacts().clear();
        Contact invalidContact = new Contact(Sex.MALE, true, null, "invalid", "");
        assertThat(invalidContact.getViolationMessage()).as("contact without lastName is invalid").isNotNull();
        simpleConsumer.getContacts().add(invalidContact);
        assertThat(simpleConsumer.getViolationMessage()).as("SimpleConsumer with invalid contact is invalid").isNotNull();

        simpleConsumer = makeValidSimpleConsumer();
        Company validCompany = makeValidCompany();
        simpleConsumer.getCompanies().add(validCompany);
        assertThat(simpleConsumer.getViolationMessage()).as("SimpleConsumer with a company is invalid").isNotNull();

        simpleConsumer = makeValidSimpleConsumer();
        simpleConsumer.getContacts().get(0).getAddresses().clear();
        assertThat(simpleConsumer.getViolationMessage()).as("SimpleConsumer without Address is invalid").isNotNull();

        simpleConsumer = makeValidSimpleConsumer();
        simpleConsumer.getContacts().get(0).getCommunications().clear();
        assertThat(simpleConsumer.getViolationMessage()).as("SimpleConsumer without communications is invalid").isNotNull();

    }

    @Test
    public void testGetSimpleViolationMessageSimpleConsumer() {
        Customer simpleConsumer = makeValidSimpleConsumer();
        simpleConsumer.getContacts().add(makeValidContact());
        assertThat(simpleConsumer.getSimpleViolationMessage()).as("SimpleConsumer with more than one Contact violates SimpleConsumer's rules").isNotNull();

        simpleConsumer = makeValidSimpleConsumer();
        simpleConsumer.getFlags().add(CustomerFlag.values()[(int)Math.random() * (CustomerFlag.values().length - 1)]);
        assertThat(simpleConsumer.getSimpleViolationMessage()).as("SimpleConsumer with a flag violates SimpleConsumer's rules").isNotNull();

        simpleConsumer = makeValidSimpleConsumer();
        simpleConsumer.setKeyAccounter("keyAccounter");
        assertThat(simpleConsumer.getSimpleViolationMessage()).as("SimpleConsumer with keyAccounter violates SimpleConsumer's rules").isNotNull();

        simpleConsumer = makeValidSimpleConsumer();
        simpleConsumer.getMandatorMetadata().add(new MandatorMetadata());
        assertThat(simpleConsumer.getSimpleViolationMessage()).as("SimpleConsumer with mandatorMetadata violates SimpleConsumer's rules").isNotNull();

        simpleConsumer = makeValidSimpleConsumer();
        simpleConsumer.getAddressLabels().clear();
        assertThat(simpleConsumer.getSimpleViolationMessage()).as("SimpleConsumer without AddressLabel violates SimpleConsumer's rules").isNotNull();

        simpleConsumer = makeValidSimpleConsumer();
        simpleConsumer.getAddressLabels().add(new AddressLabel(null, simpleConsumer.getContacts().get(0), simpleConsumer.getContacts().get(0).getAddresses().get(0), AddressType.SHIPPING));
        assertThat(simpleConsumer.getSimpleViolationMessage()).as("SimpleConsumer with two AddressLabel is not simple").isNotNull();

        simpleConsumer = makeValidSimpleConsumer();
        Address address = makeValidAddress();
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
        assertThat(simpleConsumer.getSimpleViolationMessage()).as("SimpleConsumer with more than three communications is invalid").isNotNull();

        simpleConsumer = makeValidSimpleConsumer();
        Communication skype = new Communication(Type.SKYPE, false);
        skype.setIdentifier("skypeUser4832");
        simpleConsumer.getContacts().get(0).getCommunications().add(skype);
        assertThat(simpleConsumer.getSimpleViolationMessage()).as("SimpleConsumer with communication of type SKYPE is invalid").isNotNull();

        simpleConsumer = makeValidSimpleConsumer();
        simpleConsumer.getContacts().get(0).getCommunications().add(email);
        assertThat(simpleConsumer.getSimpleViolationMessage()).as("SimpleConsumer with two communications of same type is invalid").isNotNull();

        simpleConsumer = makeValidSimpleConsumer();
        simpleConsumer.getContacts().forEach(contact -> contact.getCommunications().clear());
        simpleConsumer.getContacts().get(0).getCommunications().add(email);
        assertThat(simpleConsumer.getContacts().isEmpty() && simpleConsumer.getCompanies().isEmpty()).as("either Contact or Company is set").isFalse();
        assertThat(simpleConsumer.getSimpleViolationMessage()).as("SimpleConsumer with one communication of type EMAIL is valid").isNull();
        Communication otherEmail = new Communication(Type.EMAIL, false);
        email.setIdentifier("otherEmail@mail.com");
        simpleConsumer.getContacts().get(0).getCommunications().add(otherEmail);
        assertThat(simpleConsumer.getSimpleViolationMessage()).as("SimpleConsumer with two communications of same type is invalid").isNotNull();

        simpleConsumer = makeValidSimpleConsumer();
        simpleConsumer.getContacts().forEach(contact -> contact.getCommunications().clear());
        simpleConsumer.getContacts().get(0).getCommunications().add(mobile);
        assertThat(simpleConsumer.getSimpleViolationMessage()).as("SimpleConsumer with one communication of type MOBILE is valid").isNull();
        Communication otherMobile = new Communication(Type.MOBILE, false);
        mobile.setIdentifier("16461385");
        simpleConsumer.getContacts().stream().findAny().get().getCommunications().add(otherMobile);
        assertThat(simpleConsumer.getSimpleViolationMessage()).as("SimpleConsumer with two communications of same type  is invalid").isNotNull();

        simpleConsumer = makeValidSimpleConsumer();
        simpleConsumer.getContacts().forEach(contact -> contact.getCommunications().clear());
        simpleConsumer.getContacts().get(0).getCommunications().add(phone);
        assertThat(simpleConsumer.getSimpleViolationMessage()).as("SimpleConsumer with one communication of type PHONE is valid").isNull();
        Communication otherPhone = new Communication(Type.PHONE, false);
        mobile.setIdentifier("6541351");
        simpleConsumer.getContacts().stream().findAny().get().getCommunications().add(otherPhone);
        assertThat(simpleConsumer.getSimpleViolationMessage()).as("SimpleConsumer with two communications of same type is invalid").isNotNull();

        simpleConsumer = makeValidSimpleConsumer();

    }

    @Test
    public void testGetViolationMessageSimpleBusinessCustomer() {
        Customer simpleBusinessCustomer = makeValidSimpleBusiness();
        simpleBusinessCustomer.getCompanies().clear();
        assertThat(simpleBusinessCustomer.getViolationMessage()).as("SimpleBusinessCustomer without companies is invalid").isNotNull();

        simpleBusinessCustomer = makeValidSimpleBusiness();
        simpleBusinessCustomer.getContacts().add(makeValidContact());
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
    public void testGetSimpleViolationMessageSimpleBusinessCustomer() {
        Customer simpleBusinessCustomer = makeValidSimpleBusiness();
        simpleBusinessCustomer.getFlags().add(CustomerFlag.values()[(int)Math.random() * (CustomerFlag.values().length - 1)]);
        assertThat(simpleBusinessCustomer.getSimpleViolationMessage()).as("SimpleBusinessCustomer with a flag is invalid").isNotNull();

        simpleBusinessCustomer = makeValidSimpleBusiness();
        simpleBusinessCustomer.setKeyAccounter("keyAccouter");
        assertThat(simpleBusinessCustomer.getSimpleViolationMessage()).as("SimpleBusinessCustomer with a keyAccounter is invalid").isNotNull();

        simpleBusinessCustomer = makeValidSimpleBusiness();
        simpleBusinessCustomer.getMandatorMetadata().add(new MandatorMetadata());
        assertThat(simpleBusinessCustomer.getSimpleViolationMessage()).as("SimpleBusinessCustomer with MandatorMetadata is invalid").isNotNull();

        simpleBusinessCustomer = makeValidSimpleBusiness();
        AddressLabel addressLabel = new AddressLabel(simpleBusinessCustomer.getCompanies().stream().findAny().get(), null, simpleBusinessCustomer.getCompanies().stream().findAny().get().getAddresses().stream().findAny().get(), SHIPPING);
        simpleBusinessCustomer.getAddressLabels().add(addressLabel);
        assertThat(simpleBusinessCustomer.getSimpleViolationMessage()).as("SimpleBusinessCustomer with more than one AddressLabel is invalid").isNotNull();

        simpleBusinessCustomer = makeValidSimpleBusiness();
        Company validCompany = makeValidBusinessCustomer().getCompanies().stream().findAny().get();
        simpleBusinessCustomer.getCompanies().add(validCompany);
        assertThat(simpleBusinessCustomer.getSimpleViolationMessage()).as("SimpleBusinessCustomer with more than one Company is invalid").isNotNull();

        simpleBusinessCustomer = makeValidSimpleBusiness();
        Address validAddress = new Address();
        validAddress.setIsoCountry(GERMANY);
        validAddress.setCity("Munich");
        validAddress.setStreet("Teststraße");
        validAddress.setZipCode("34243");
        simpleBusinessCustomer.getCompanies().stream().findAny().get().getAddresses().add(validAddress);
        assertThat(simpleBusinessCustomer.getSimpleViolationMessage()).as("SimpleBusinessCustomer with more than one Address is invalid");

        simpleBusinessCustomer = makeValidSimpleBusiness();
        simpleBusinessCustomer.getCompanies().stream().findAny().get().getContacts().add(makeValidSimpleConsumer().getContacts().get(0));
        assertThat(simpleBusinessCustomer.getSimpleViolationMessage()).as("SimpleBusinessCustomer with more than one Contact is invalid");

    }
}
