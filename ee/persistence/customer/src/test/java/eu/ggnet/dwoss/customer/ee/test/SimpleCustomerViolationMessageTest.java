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
package eu.ggnet.dwoss.customer.ee.test;

import java.util.Arrays;

import org.junit.Test;

import eu.ggnet.dwoss.common.api.values.AddressType;
import eu.ggnet.dwoss.common.api.values.CustomerFlag;
import eu.ggnet.dwoss.customer.ee.entity.Communication.Type;
import eu.ggnet.dwoss.customer.ee.entity.Contact.Sex;
import eu.ggnet.dwoss.customer.ee.entity.*;

import static eu.ggnet.dwoss.common.api.values.AddressType.INVOICE;
import static eu.ggnet.dwoss.common.api.values.AddressType.SHIPPING;
import static eu.ggnet.dwoss.customer.ee.entity.Communication.Type.*;
import static eu.ggnet.dwoss.customer.ee.make.StaticCustomerMaker.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author jacob.weinhold
 */
public class SimpleCustomerViolationMessageTest {

    @Test
    public void testGetViolationMessageSimpleConsumer() {
        Customer simpleConsumer = makeValidSimpleConsumer();
        simpleConsumer.getCompanies().add(makeValidCompany());
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
        assertThat(simpleConsumer.getViolationMessage()).as("SimpleConsumer with two AddressLabels of unequal types is valid").isNull();
        simpleConsumer.getAddressLabels().add(new AddressLabel(null, simpleConsumer.getContacts().get(0), simpleConsumer.getContacts().get(0).getAddresses().get(0), SHIPPING));
        assertThat(simpleConsumer.getViolationMessage()).as("SimpleConsumer with three AddressLabels is invalid").isNotNull();

        simpleConsumer = makeValidSimpleConsumer();
        simpleConsumer.getContacts().clear();
        Contact invalidContact = new Contact(Sex.MALE, null, "invalid", "");
        assertThat(invalidContact.getViolationMessage()).as("Contact without lastName is invalid").isNotNull();
        simpleConsumer.getContacts().add(invalidContact);
        assertThat(simpleConsumer.getViolationMessage()).as("SimpleConsumer with invalid contact is invalid").isNotNull();

        simpleConsumer = makeValidSimpleConsumer();
        simpleConsumer.getContacts().get(0).getAddresses().clear();
        assertThat(simpleConsumer.getViolationMessage()).as("SimpleConsumer without Address is invalid").isNotNull();

        simpleConsumer = makeValidSimpleConsumer();
        simpleConsumer.getContacts().get(0).getCommunications().clear();
        assertThat(simpleConsumer.getViolationMessage()).as("SimpleConsumer without communications is invalid").isNotNull();

        simpleConsumer = makeValidSimpleConsumer();
        Communication invalidEmailCommunication = new Communication(Type.EMAIL);
        simpleConsumer.getContacts().get(0).getCommunications().clear();
        simpleConsumer.getContacts().get(0).getCommunications().add(invalidEmailCommunication);
        assertThat(simpleConsumer.getViolationMessage()).as("SimpleConsumer with invalid Communication is invalid").isNotNull();

        simpleConsumer = makeValidSimpleConsumer();
        Address invalidAddress = new Address();
        invalidAddress.setCountry(Country.GERMANY);
        invalidAddress.setCity("city");
        invalidAddress.setStreet("street");
        invalidAddress.setZipCode("");
        assertThat(invalidAddress.getViolationMessage()).as("Invalid Address is invalid").isNotNull();
        simpleConsumer.getContacts().get(0).getAddresses().clear();
        simpleConsumer.getContacts().get(0).getAddresses().add(invalidAddress);
        assertThat(simpleConsumer.getViolationMessage()).as("SimpleConsumer with invalid Address is invalid").isNotNull();

    }

    @Test
    public void testGetSimpleViolationMessageSimpleConsumer() {
        Customer simpleConsumer = makeValidSimpleConsumer();
        simpleConsumer.getContacts().add(makeValidContact());
        assertThat(simpleConsumer.getSimpleViolationMessage()).as("SimpleConsumer with more than one Contact violates SimpleConsumer's rules").isNotNull();

        simpleConsumer = makeValidSimpleConsumer();
        simpleConsumer.getAddressLabels().clear();
        assertThat(simpleConsumer.getSimpleViolationMessage()).as("SimpleConsumer without AddressLabel violates Customer's rules").isNotNull();

        simpleConsumer = makeValidSimpleConsumer();
        simpleConsumer.getAddressLabels().add(makeValidShippingAddressLabel());
        assertThat(simpleConsumer.getSimpleViolationMessage()).as("SimpleConsumer with more than one AddressLabel violates SimpleCustomer's rules").isNotNull();

        simpleConsumer = makeValidSimpleConsumer();
        simpleConsumer.getFlags().add(CustomerFlag.values()[(int)Math.random() * (CustomerFlag.values().length - 1)]);
        assertThat(simpleConsumer.getSimpleViolationMessage()).as("SimpleConsumer with a flag violates SimpleCustomer's Rules").isNotNull();

        simpleConsumer = makeValidSimpleConsumer();
        simpleConsumer.setKeyAccounter("keyAccounter");
        assertThat(simpleConsumer.getSimpleViolationMessage()).as("SimpleConsumer with keyAccounter violates SimpleCustomer's Rules").isNotNull();

        simpleConsumer = makeValidSimpleConsumer();
        simpleConsumer.getMandatorMetadata().add(new MandatorMetadata());
        assertThat(simpleConsumer.getSimpleViolationMessage()).as("SimpleConsumer with mandatorMetadata violates SimpleCustomer's Rules").isNotNull();

        simpleConsumer = makeValidSimpleConsumer();
        simpleConsumer.getAddressLabels().add(new AddressLabel(null, simpleConsumer.getContacts().get(0), simpleConsumer.getContacts().get(0).getAddresses().get(0), AddressType.SHIPPING));
        assertThat(simpleConsumer.getSimpleViolationMessage()).as("SimpleConsumer with two AddressLabels violates SimpleCustomer's Rules").isNotNull();

        simpleConsumer = makeValidSimpleConsumer();
        Address address = makeValidAddress();
        simpleConsumer.getContacts().get(0).getAddresses().add(address);
        assertThat(simpleConsumer.getSimpleViolationMessage()).as("SimpleConsumer with more than one Address violates SimpleCustomer's Rules").isNotNull();

        simpleConsumer = makeValidSimpleConsumer();
        Communication phone = new Communication(Type.PHONE, "040 36184165");
        Communication mobile = new Communication(Type.MOBILE, "0049 125 64682552");
        Communication email = new Communication(Type.EMAIL, "email@mail.com");

        simpleConsumer.getContacts().get(0).getCommunications().addAll(Arrays.asList(phone, mobile, email));
        assertThat(simpleConsumer.getSimpleViolationMessage()).as("SimpleConsumer with more than three communications is invalid").isNotNull();

        simpleConsumer = makeValidSimpleConsumer();
        Communication skype = new Communication(Type.SKYPE, "skypeUser4832");
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
        Communication otherEmail = new Communication(Type.EMAIL);
        email.setIdentifier("otherEmail@mail.com");
        simpleConsumer.getContacts().get(0).getCommunications().add(otherEmail);
        assertThat(simpleConsumer.getSimpleViolationMessage()).as("SimpleConsumer with two communications of same type is invalid").isNotNull();

        simpleConsumer = makeValidSimpleConsumer();
        simpleConsumer.getContacts().forEach(contact -> contact.getCommunications().clear());
        simpleConsumer.getContacts().get(0).getCommunications().add(mobile);
        assertThat(simpleConsumer.getSimpleViolationMessage()).as("SimpleConsumer with one communication of type MOBILE is valid").isNull();
        Communication otherMobile = new Communication(Type.MOBILE);
        mobile.setIdentifier("0172 16461385");
        simpleConsumer.getContacts().stream().findAny().get().getCommunications().add(otherMobile);
        assertThat(simpleConsumer.getSimpleViolationMessage()).as("SimpleConsumer with two communications of same type is invalid").isNotNull();

        simpleConsumer = makeValidSimpleConsumer();
        simpleConsumer.getContacts().forEach(contact -> contact.getCommunications().clear());
        simpleConsumer.getContacts().get(0).getCommunications().add(phone);
        assertThat(simpleConsumer.getSimpleViolationMessage()).as("SimpleConsumer with one communication of type PHONE is valid").isNull();
        Communication otherPhone = new Communication(Type.PHONE);
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
        assertThat(simpleBusinessCustomer.getViolationMessage()).as("Removal of any AddressLabel from a SimpleBusinessCustomer results invalid").isNotNull();

        simpleBusinessCustomer = makeValidSimpleBusiness();
        simpleBusinessCustomer.getAddressLabels().add(new AddressLabel(simpleBusinessCustomer.getCompanies().get(0), null, simpleBusinessCustomer.getCompanies().get(0).getAddresses().get(0), SHIPPING));
        simpleBusinessCustomer.getAddressLabels().add(new AddressLabel(simpleBusinessCustomer.getCompanies().get(0), null, simpleBusinessCustomer.getCompanies().get(0).getAddresses().get(0), SHIPPING));
        assertThat(simpleBusinessCustomer.getViolationMessage()).as("Addition of two AddressLabels to a BusinessCustomer always results invalid").isNotNull();

        simpleBusinessCustomer = makeValidSimpleBusiness();
        Company invalidCompany = new Company("", 0, "634855");
        assertThat(invalidCompany.getViolationMessage()).as("Company without a Name is invalid").isNotNull();
        simpleBusinessCustomer.getCompanies().add(invalidCompany);
        assertThat(simpleBusinessCustomer.getViolationMessage()).as("Adding an invalid Company to a SimpleBusinessCustomer results invalid").isNotNull();

        simpleBusinessCustomer = makeValidSimpleBusiness();
        simpleBusinessCustomer.getCompanies().get(0).getContacts().get(0).getCommunications().clear();
        assertThat(simpleBusinessCustomer.getViolationMessage()).as("SimpleBusinessCustomer without Communications is invalid").isNotNull();

    }

    @Test
    public void testGetSimpleViolationMessageSimpleBusinessCustomer() {
        Customer simpleBusinessCustomer = makeValidSimpleBusiness();
        simpleBusinessCustomer.getContacts().add(makeValidContact());
        assertThat(simpleBusinessCustomer.getSimpleViolationMessage()).as("SimpleBusinessCustomer with a Contact is invalid").isNotNull();

        simpleBusinessCustomer = makeValidSimpleBusiness();
        Company validCompany = makeValidCompany();
        simpleBusinessCustomer.getCompanies().add(validCompany);
        assertThat(simpleBusinessCustomer.getSimpleViolationMessage()).as("SimpleBusinessCustomer with more than one Company is invalid").isNotNull();

        simpleBusinessCustomer = makeValidSimpleBusiness();
        simpleBusinessCustomer.getAddressLabels().clear();
        assertThat(simpleBusinessCustomer.getSimpleViolationMessage()).as("SimpleBusinessCustomer without AddressLabel of Type INVOICE is invalid").isNotNull();

        simpleBusinessCustomer = makeValidSimpleBusiness();
        AddressLabel addressLabel = new AddressLabel(simpleBusinessCustomer.getCompanies().stream().findAny().get(), null, simpleBusinessCustomer.getCompanies().stream().findAny().get().getAddresses().stream().findAny().get(), SHIPPING);
        simpleBusinessCustomer.getAddressLabels().add(addressLabel);
        assertThat(simpleBusinessCustomer.getSimpleViolationMessage()).as("SimpleBusinessCustomer with more than one AddressLabel is invalid").isNotNull();

        simpleBusinessCustomer = makeValidSimpleBusiness();
        simpleBusinessCustomer.getFlags().add(CustomerFlag.values()[(int)Math.random() * (CustomerFlag.values().length - 1)]);
        assertThat(simpleBusinessCustomer.getSimpleViolationMessage()).as("SimpleBusinessCustomer with a flag is invalid").isNotNull();

        simpleBusinessCustomer = makeValidSimpleBusiness();
        simpleBusinessCustomer.setKeyAccounter("keyAccouter");
        assertThat(simpleBusinessCustomer.getSimpleViolationMessage()).as("SimpleBusinessCustomer with a keyAccounter is invalid").isNotNull();

        simpleBusinessCustomer = makeValidSimpleBusiness();
        simpleBusinessCustomer.getMandatorMetadata().add(new MandatorMetadata());
        assertThat(simpleBusinessCustomer.getSimpleViolationMessage()).as("SimpleBusinessCustomer with MandatorMetadata is invalid").isNotNull();

        simpleBusinessCustomer = makeValidSimpleBusiness();
        simpleBusinessCustomer.getCompanies().get(0).getContacts().get(0).getCommunications().add(makeValidCommunication(FACEBOOK, "username"));
        assertThat(simpleBusinessCustomer.getSimpleViolationMessage()).as("SimpleBusinessCustomer with a FACEBOOK Communication is invalid").isNotNull();

        simpleBusinessCustomer = makeValidSimpleBusiness();
        simpleBusinessCustomer.getCompanies().get(0).getContacts().get(0).getCommunications().add(makeValidCommunication(FAX, "040 1345678"));
        assertThat(simpleBusinessCustomer.getSimpleViolationMessage()).as("SimpleBusinessCustomer with a FAX Communication is invalid").isNotNull();

        simpleBusinessCustomer = makeValidSimpleBusiness();
        simpleBusinessCustomer.getCompanies().get(0).getContacts().get(0).getCommunications().add(makeValidCommunication(ICQ, "12345678"));
        assertThat(simpleBusinessCustomer.getSimpleViolationMessage()).as("SimpleBusinessCustomer with an ICQ Communication is invalid").isNotNull();

        simpleBusinessCustomer = makeValidSimpleBusiness();
        simpleBusinessCustomer.getCompanies().get(0).getContacts().get(0).getCommunications().add(makeValidCommunication(SKYPE, "skypeUser16"));
        assertThat(simpleBusinessCustomer.getSimpleViolationMessage()).as("SimpleBusinessCustomer with a SKYPE Communication is invalid").isNotNull();

        simpleBusinessCustomer = makeValidSimpleBusiness();
        simpleBusinessCustomer.getCompanies().get(0).getContacts().get(0).getCommunications().clear();
        simpleBusinessCustomer.getCompanies().get(0).getCommunications().add(makeValidCommunication(Communication.Type.EMAIL, "testMail@gmx.net"));
        assertThat(simpleBusinessCustomer.getSimpleViolationMessage()).as("SimpleBusinessCustomer with it's Communication on it's Company is invalid").isNotNull();

        simpleBusinessCustomer = makeValidSimpleBusiness();
        simpleBusinessCustomer.getCompanies().get(0).getContacts().get(0).getCommunications().add(makeValidCommunication(Communication.Type.EMAIL, "testMail@gmx.net"));
        assertThat(simpleBusinessCustomer.getSimpleViolationMessage()).as("SimpleBusinessCustomer with two Communications of same allowed Type is invalid").isNotNull();

        simpleBusinessCustomer = makeValidSimpleBusiness();
        simpleBusinessCustomer.getCompanies().get(0).getContacts().get(0).getCommunications().add(makeValidCommunication(Communication.Type.PHONE, "0123 456789"));
        simpleBusinessCustomer.getCompanies().get(0).getContacts().get(0).getCommunications().add(makeValidCommunication(Communication.Type.PHONE, "098 7654321"));
        assertThat(simpleBusinessCustomer.getSimpleViolationMessage()).as("SimpleBusinessCustomer with two Communications of same allowed Type is invalid").isNotNull();

        simpleBusinessCustomer = makeValidSimpleBusiness();
        simpleBusinessCustomer.getCompanies().get(0).getContacts().get(0).getCommunications().add(makeValidCommunication(Communication.Type.MOBILE, "0123 456789"));
        simpleBusinessCustomer.getCompanies().get(0).getContacts().get(0).getCommunications().add(makeValidCommunication(Communication.Type.MOBILE, "098 7654321"));
        assertThat(simpleBusinessCustomer.getSimpleViolationMessage()).as("SimpleBusinessCustomer with two Communications of same allowed Type is invalid").isNotNull();

        simpleBusinessCustomer = makeValidSimpleBusiness();
        simpleBusinessCustomer.getCompanies().get(0).getContacts().add(makeValidSimpleConsumer().getContacts().get(0));
        assertThat(simpleBusinessCustomer.getSimpleViolationMessage()).as("SimpleBusinessCustomer with more than one Contact is invalid").isNotNull();

        simpleBusinessCustomer = makeValidSimpleBusiness();
        simpleBusinessCustomer.getCompanies().get(0).getCommunications().add(makeValidCommunication(MOBILE, "0123 456789"));
        assertThat(simpleBusinessCustomer.getSimpleViolationMessage()).as("SimpleBusinessCustomer with a valid Communication on it's Company is invalid").isNotNull();

        simpleBusinessCustomer = makeValidSimpleBusiness();
        Address validAddress = new Address();
        validAddress.setIsoCountry("DE");
        validAddress.setCity("Munich");
        validAddress.setStreet("Teststraße");
        validAddress.setZipCode("34243");
        simpleBusinessCustomer.getCompanies().get(0).getAddresses().add(validAddress);
        assertThat(simpleBusinessCustomer.getSimpleViolationMessage()).as("SimpleBusinessCustomer with more than one Address is invalid").isNotNull();

        simpleBusinessCustomer = makeValidSimpleBusiness();
        simpleBusinessCustomer.getCompanies().get(0).getContacts().get(0).getAddresses().add(validAddress);
        assertThat(simpleBusinessCustomer.getSimpleViolationMessage()).as("SimpleBusinessCustomer with more than one Address is invalid").isNotNull();
    }
}
