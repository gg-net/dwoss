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

import org.junit.Test;

import eu.ggnet.dwoss.customer.ee.entity.Communication.Type;
import eu.ggnet.dwoss.customer.ee.entity.*;
import eu.ggnet.dwoss.customer.ee.entity.dto.SimpleCustomer;

import static eu.ggnet.dwoss.customer.ee.make.StaticCustomerMaker.*;

import static eu.ggnet.dwoss.customer.ee.entity.Communication.Type.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author jacob.weinhold
 */
public class CustomerSimpleTest {

    @Test
    public void testToSimpleConsumer() {
        Customer validSimpleConsumer = makeValidSimpleConsumer();
        validSimpleConsumer.getContacts().get(0).getCommunications().clear();
        validSimpleConsumer.getContacts().get(0).getCommunications().add(makeValidCommunication(Type.EMAIL, "Max.mustermann@mustermail.de"));
        validSimpleConsumer.getContacts().get(0).getCommunications().add(makeValidCommunication(Type.MOBILE, "0174 123456789"));
        validSimpleConsumer.getContacts().get(0).getCommunications().add(makeValidCommunication(Type.PHONE, "040 123456789"));
        assertThat(validSimpleConsumer.isSimple()).as("still simplecustomer").isTrue();

        SimpleCustomer simpleConsumerCustomer = validSimpleConsumer.toSimple().get();

        assertThat(simpleConsumerCustomer.getTitle()).as("title").isEqualTo(validSimpleConsumer.getContacts().get(0).getTitle());
        assertThat(simpleConsumerCustomer.getFirstName()).as("firstname").isEqualTo(validSimpleConsumer.getContacts().get(0).getFirstName());
        assertThat(simpleConsumerCustomer.getLastName()).as("lastname").isEqualTo(validSimpleConsumer.getContacts().get(0).getLastName());
        assertThat(simpleConsumerCustomer.getStreet()).as("street").isEqualTo(validSimpleConsumer.getContacts().get(0).getAddresses().get(0).getStreet());
        assertThat(simpleConsumerCustomer.getZipCode()).as("zipcode").isEqualTo(validSimpleConsumer.getContacts().get(0).getAddresses().get(0).getZipCode());
        assertThat(simpleConsumerCustomer.getCity()).as("city").isEqualTo(validSimpleConsumer.getContacts().get(0).getAddresses().get(0).getCity());
        assertThat(simpleConsumerCustomer.getCountry()).as("country").isEqualTo(validSimpleConsumer.getContacts().get(0).getAddresses().get(0).getCountry());
        assertThat(simpleConsumerCustomer.getMobilePhone()).as("mobilePhone").isEqualTo(validSimpleConsumer.getContacts().get(0).getCommunications().stream().filter(c -> c.getType() == MOBILE).map(Communication::getIdentifier).findFirst().get());
        assertThat(simpleConsumerCustomer.getLandlinePhone()).as("landlinePhone").isEqualTo(validSimpleConsumer.getContacts().get(0).getCommunications().stream().filter(c -> c.getType() == PHONE).map(Communication::getIdentifier).findFirst().get());
        assertThat(simpleConsumerCustomer.getEmail()).as("email").isEqualTo(validSimpleConsumer.getContacts().get(0).getCommunications().stream().filter(c -> c.getType() == EMAIL).map(Communication::getIdentifier).findFirst().get());

        assertThat(simpleConsumerCustomer.getSex()).as("sex").isEqualTo(validSimpleConsumer.getContacts().get(0).getSex());
        assertThat(simpleConsumerCustomer.getSource()).as("source").isEqualTo(validSimpleConsumer.getSource());
        assertThat(simpleConsumerCustomer.getComment()).as("comment").isEqualTo(validSimpleConsumer.getComment());

        assertThat(simpleConsumerCustomer.getCompanyName()).as("companyName").isEqualTo(null);
        assertThat(simpleConsumerCustomer.getTaxId()).as("taxId").isEqualTo(null);

    }

    @Test
    public void testToSimpleBusiness() {
        Customer validBusinessCustomer = makeValidSimpleBusiness();
        validBusinessCustomer.getCompanies().get(0).getCommunications().clear();
        assertThat(validBusinessCustomer.isSimple()).as("still simplecustomer").isTrue();

        Contact makeValidContact = makeValidContact();
        makeValidContact.getCommunications().clear();
        makeValidContact.getCommunications().add(makeValidCommunication(Type.EMAIL, "Max.mustermann@mustermail.de"));
        makeValidContact.getCommunications().add(makeValidCommunication(Type.MOBILE, "0174 123456789"));
        makeValidContact.getCommunications().add(makeValidCommunication(Type.PHONE, "040 123456789"));
        assertThat(makeValidContact.getViolationMessage()).as("valid contact").isNull();

        validBusinessCustomer.getCompanies().get(0).getContacts().clear();
        validBusinessCustomer.getCompanies().get(0).getContacts().add(makeValidContact);

        assertThat(validBusinessCustomer.isSimple()).as("still simplecustomer").isTrue();

        assertThat(validBusinessCustomer.toSimple().isPresent()).as("to simple").isTrue();
        SimpleCustomer simpleBusinessCustomer = validBusinessCustomer.toSimple().get();

        assertThat(simpleBusinessCustomer.getTitle()).as("title").isEqualTo(validBusinessCustomer.getCompanies().get(0).getContacts().get(0).getTitle());
        assertThat(simpleBusinessCustomer.getFirstName()).as("firstname").isEqualTo(validBusinessCustomer.getCompanies().get(0).getContacts().get(0).getFirstName());
        assertThat(simpleBusinessCustomer.getLastName()).as("lastname").isEqualTo(validBusinessCustomer.getCompanies().get(0).getContacts().get(0).getLastName());

        assertThat(simpleBusinessCustomer.getSex()).as("sex").isEqualTo(validBusinessCustomer.getCompanies().get(0).getContacts().get(0).getSex());

        //did Companys have a Address this Address have to match the Address on Contact
        if ( !validBusinessCustomer.getCompanies().get(0).getAddresses().isEmpty() ) {
            assertThat(validBusinessCustomer.getCompanies().get(0).getContacts().get(0).getAddresses().get(0).getStreet())
                    .as("address of contact have to be the same as on Address Street")
                    .isEqualTo(validBusinessCustomer.getCompanies().get(0).getAddresses().get(0).getStreet());

            assertThat(validBusinessCustomer.getCompanies().get(0).getContacts().get(0).getAddresses().get(0).getCity())
                    .as("address of contact have to be the same as on Address City")
                    .isEqualTo(validBusinessCustomer.getCompanies().get(0).getAddresses().get(0).getCity());

            assertThat(validBusinessCustomer.getCompanies().get(0).getContacts().get(0).getAddresses().get(0).getZipCode())
                    .as("address of contact have to be the same as on Address zipcode")
                    .isEqualTo(validBusinessCustomer.getCompanies().get(0).getAddresses().get(0).getZipCode());

            assertThat(validBusinessCustomer.getCompanies().get(0).getContacts().get(0).getAddresses().get(0).getIsoCountry())
                    .as("address of contact have to be the same as on Address iso country")
                    .isEqualTo(validBusinessCustomer.getCompanies().get(0).getAddresses().get(0).getIsoCountry());

            assertThat(simpleBusinessCustomer.getStreet()).as("street").isEqualTo(validBusinessCustomer.getCompanies().get(0).getAddresses().get(0).getStreet());
            assertThat(simpleBusinessCustomer.getZipCode()).as("zipcode").isEqualTo(validBusinessCustomer.getCompanies().get(0).getAddresses().get(0).getZipCode());
            assertThat(simpleBusinessCustomer.getCity()).as("city").isEqualTo(validBusinessCustomer.getCompanies().get(0).getAddresses().get(0).getCity());
            assertThat(simpleBusinessCustomer.getCountry()).as("country").isEqualTo(validBusinessCustomer.getCompanies().get(0).getAddresses().get(0).getCountry());
        } else {
            assertThat(simpleBusinessCustomer.getStreet()).as("street").isEqualTo(validBusinessCustomer.getCompanies().get(0).getContacts().get(0).getAddresses().get(0).getStreet());
            assertThat(simpleBusinessCustomer.getZipCode()).as("zipcode").isEqualTo(validBusinessCustomer.getCompanies().get(0).getContacts().get(0).getAddresses().get(0).getZipCode());
            assertThat(simpleBusinessCustomer.getCity()).as("city").isEqualTo(validBusinessCustomer.getCompanies().get(0).getContacts().get(0).getAddresses().get(0).getCity());
            assertThat(simpleBusinessCustomer.getCountry()).as("country").isEqualTo(validBusinessCustomer.getCompanies().get(0).getContacts().get(0).getAddresses().get(0).getCountry());
        }

        assertThat(simpleBusinessCustomer.getMobilePhone()).as("mobilePhone")
                .isEqualTo(validBusinessCustomer.getCompanies().get(0).getContacts().get(0).getCommunications().stream().filter(c -> c.getType() == MOBILE).map(Communication::getIdentifier).findFirst().get());
        assertThat(simpleBusinessCustomer.getLandlinePhone()).as("landlinePhone")
                .isEqualTo(validBusinessCustomer.getCompanies().get(0).getContacts().get(0).getCommunications().stream().filter(c -> c.getType() == PHONE).map(Communication::getIdentifier).findFirst().get());
        assertThat(simpleBusinessCustomer.getEmail()).as("email")
                .isEqualTo(validBusinessCustomer.getCompanies().get(0).getContacts().get(0).getCommunications().stream().filter(c -> c.getType() == EMAIL).map(Communication::getIdentifier).findFirst().get());

        assertThat(simpleBusinessCustomer.getSource()).as("source").isEqualTo(validBusinessCustomer.getSource());
        assertThat(simpleBusinessCustomer.getComment()).as("comment").isEqualTo(validBusinessCustomer.getComment());

        assertThat(simpleBusinessCustomer.getCompanyName()).as("companyName").isEqualTo(validBusinessCustomer.getCompanies().get(0).getName());
        assertThat(simpleBusinessCustomer.getTaxId()).as("taxId").isEqualTo(validBusinessCustomer.getCompanies().get(0).getTaxId());

    }

}
