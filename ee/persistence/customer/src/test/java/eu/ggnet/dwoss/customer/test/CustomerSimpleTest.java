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
public class CustomerSimpleTest {
    
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
    public void testGetSimpleViolationMessageSimpleConsumer() {
        Customer simpleConsumer = makeValidSimpleConsumer();
        simpleConsumer.getFlags().add(CustomerFlag.values()[(int)Math.random() * (CustomerFlag.values().length - 1)]);
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
        assertThat(simpleBusinessCustomer.getSimpleViolationMessage()).as("SimpleBusinessCustomer with more than Cone COntact is invalid");

        simpleBusinessCustomer = makeValidSimpleBusiness();

    }

}
