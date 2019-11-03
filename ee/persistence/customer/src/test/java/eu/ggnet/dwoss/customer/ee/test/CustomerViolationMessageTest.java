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

import eu.ggnet.dwoss.common.api.values.AddressType;

import org.junit.Test;

import eu.ggnet.dwoss.customer.ee.entity.Communication.Type;
import eu.ggnet.dwoss.customer.ee.entity.Contact.Sex;
import eu.ggnet.dwoss.customer.ee.entity.*;
import eu.ggnet.dwoss.customer.ee.entity.AddressLabel;

import static eu.ggnet.dwoss.customer.ee.make.StaticCustomerMaker.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author jacob.weinhold
 */
public class CustomerViolationMessageTest {

    @Test
    public void testGetViolationMessageBusinessCustomer() {
        Customer businessCustomer = makeValidBusinessCustomer();
        businessCustomer.getCompanies().clear();
        assertThat(businessCustomer.getViolationMessage()).as("BusinessCustomer without companies is invalid").isNotNull();

        businessCustomer = makeValidBusinessCustomer();
        businessCustomer.getContacts().add(makeValidContact());
        assertThat(businessCustomer.getViolationMessage()).as("BusinessCustomer with a Contact is invalid").isNotNull();
        
        businessCustomer = makeValidBusinessCustomer();
        businessCustomer.getAddressLabels().clear();
        assertThat(businessCustomer.getViolationMessage()).as("BusinessCustomer without AddressLabels is invalid").isNotNull();

        businessCustomer = makeValidBusinessCustomer();
        businessCustomer.getAddressLabels().add(makeValidShippingAddressLabel());
        assertThat(businessCustomer.getViolationMessage()).as("BusinessCustomer with two AddressLabels of different Types is invalid").isNotNull();

        businessCustomer = makeValidBusinessCustomer();
        businessCustomer.getCompanies().forEach(cmp -> cmp.getCommunications().clear());
        businessCustomer.getCompanies().forEach(cmp -> cmp.getContacts().forEach(cntct -> cntct.getCommunications().clear()));
        assertThat(businessCustomer.getViolationMessage()).as("BusinessCustomer without Communication is invalid").isNotNull();

        businessCustomer = makeValidBusinessCustomer();
        Communication invalidPhoneCommunication = new Communication(Type.PHONE);
        Communication invalidEmailCommunication = new Communication(Type.EMAIL);
        businessCustomer.getCompanies().get(0).getCommunications().add(invalidPhoneCommunication);
        businessCustomer.getCompanies().get(0).getContacts().get(0).getCommunications().add(invalidEmailCommunication);
        assertThat(businessCustomer.getViolationMessage()).as("BusinessCustomer with invalid Communication is invalid").isNotNull();

        businessCustomer = makeValidBusinessCustomer();
        Company invalidCompany = new Company("", 0, "3486");
        assertThat(invalidCompany.getViolationMessage()).as("Invalid Company is invalid").isNotNull();
        businessCustomer.getCompanies().add(invalidCompany);
        assertThat(businessCustomer.getViolationMessage()).as("BusinessCustomer with invalid Company is invalid").isNotNull();

        businessCustomer = makeValidBusinessCustomer();
        Contact invalidContact = new Contact(Sex.MALE, "", "Hans", "");
        assertThat(invalidContact.getViolationMessage()).as("Invalid Contact is invalid").isNotNull();
        businessCustomer.getCompanies().get(0).getContacts().add(invalidContact);
        assertThat(businessCustomer.getViolationMessage()).as("BusinessCustomer with invalid Contact is invalid").isNotNull();

        businessCustomer = makeValidBusinessCustomer();
        Address invalidAddress = new Address();
        invalidAddress.setCountry(Country.GERMANY);
        invalidAddress.setCity("city");
        invalidAddress.setStreet("street");
        invalidAddress.setZipCode("");
        assertThat(invalidAddress.getViolationMessage()).as("Invalid Address is invalid").isNotNull();
        businessCustomer.getCompanies().get(0).getAddresses().add(invalidAddress);
        assertThat(businessCustomer.getViolationMessage()).as("BusinessCustomer with invalid Address is invalid").isNotNull();
    }

    @Test
    public void testgetViolationMessageConsumerCustomer() {
        Customer consumerCustomer = makeValidConsumerCustomer();
        consumerCustomer.getContacts().clear();
        assertThat(consumerCustomer.getViolationMessage()).as("ConsumerCustomer without Contacts is invalid").isNotNull();

        consumerCustomer = makeValidConsumerCustomer();
        consumerCustomer.getCompanies().add(makeValidCompany());
        assertThat(consumerCustomer.getViolationMessage()).as("ConsumerCustomer with a Company is invalid").isNotNull();

        consumerCustomer = makeValidConsumerCustomer();
        consumerCustomer.getAddressLabels().clear();
        assertThat(consumerCustomer.getViolationMessage()).as("ConsumerCustomer without AddressLabels is invalid").isNotNull();

        consumerCustomer = makeValidConsumerCustomer();
        consumerCustomer.getAddressLabels().add(new AddressLabel(null, consumerCustomer.getContacts().get(0), consumerCustomer.getContacts().get(0).getAddresses().get(0), AddressType.SHIPPING));
        assertThat(consumerCustomer.getViolationMessage()).as("ConsumerCustomer with more than two AddressLabels is invalid").isNotNull();

        consumerCustomer = makeValidConsumerCustomer();
        consumerCustomer.getAddressLabels().clear();
        consumerCustomer.getAddressLabels().add(new AddressLabel(null, consumerCustomer.getContacts().get(0), consumerCustomer.getContacts().get(0).getAddresses().get(0), AddressType.SHIPPING));
        consumerCustomer.getAddressLabels().add(new AddressLabel(makeValidCompany(), consumerCustomer.getContacts().get(0), consumerCustomer.getContacts().get(0).getAddresses().get(0), AddressType.SHIPPING));
        assertThat(consumerCustomer.getViolationMessage()).as("ConsumerCustomer without an AddressLabel of Type INVOICE is invalid").isNotNull();

        consumerCustomer = makeValidConsumerCustomer();
        consumerCustomer.getContacts().forEach(e -> e.getCommunications().clear());
        assertThat(consumerCustomer.getViolationMessage()).as("ConsumerCustomer without Communications is invalid").isNotNull();

        consumerCustomer = makeValidConsumerCustomer();
        Communication invalidEmailCommunication = new Communication(Type.EMAIL);
        consumerCustomer.getContacts().get(0).getCommunications().add(invalidEmailCommunication);
        assertThat(consumerCustomer.getViolationMessage()).as("ConsumerCustomer with invalid Communication is invalid").isNotNull();

        consumerCustomer = makeValidConsumerCustomer();
        Contact invalidContact = new Contact(Sex.MALE, "", "Hans", "");
        assertThat(invalidContact.getViolationMessage()).as("Invalid Contact is invalid").isNotNull();
        consumerCustomer.getContacts().add(invalidContact);
        assertThat(consumerCustomer.getViolationMessage()).as("ConsumerCustomer with invalid Contact is invalid").isNotNull();

        consumerCustomer = makeValidConsumerCustomer();
        Address invalidAddress = new Address();
        invalidAddress.setCountry(Country.GERMANY);
        invalidAddress.setCity("city");
        invalidAddress.setStreet("street");
        invalidAddress.setZipCode("");
        assertThat(invalidAddress.getViolationMessage()).as("Invalid Address is invalid").isNotNull();
        consumerCustomer.getContacts().get(0).getAddresses().add(invalidAddress);
        assertThat(consumerCustomer.getViolationMessage()).as("ConsumerCustomer with invalid Address is invalid").isNotNull();

    }
}
