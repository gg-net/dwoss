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

import eu.ggnet.dwoss.customer.ee.assist.gen.Assure;
import eu.ggnet.dwoss.customer.ee.assist.gen.CustomerGenerator;
import eu.ggnet.dwoss.customer.ee.entity.Communication.Type;
import eu.ggnet.dwoss.customer.ee.entity.*;
import eu.ggnet.dwoss.customer.ee.entity.dto.SimpleCustomer;

import static eu.ggnet.dwoss.customer.ee.entity.Communication.Type.*;
import static eu.ggnet.dwoss.customer.ee.make.StaticCustomerMaker.makeValidCommunication;
import static eu.ggnet.dwoss.customer.ee.make.StaticCustomerMaker.makeValidCompanyContact;
import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author jacob.weinhold
 */
public class CustomerSimpleTest {

    @Test
    public void testToSimpleConsumer() {
        Customer validSimpleConsumer = CustomerGenerator.makeCustomer(new Assure.Builder().simple(true).consumer(true).build());
        validSimpleConsumer.getContacts().get(0).getCommunications().clear();
        Communication email = makeValidCommunication(Type.EMAIL, "Max.mustermann@mustermail.de");
        validSimpleConsumer.getContacts().get(0).getCommunications().add(email);
        validSimpleConsumer.getContacts().get(0).getCommunications().add(makeValidCommunication(Type.MOBILE, "0174 123456789"));
        validSimpleConsumer.getContacts().get(0).getCommunications().add(makeValidCommunication(Type.PHONE, "040 123456789"));
        validSimpleConsumer.setResellerListEmailCommunication(email);
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
        assertThat(simpleConsumerCustomer.isUseEmailForResellerList()).as("useEmailForResellerList").isTrue();

    }

    @Test
    public void testToSimpleBusiness() {
        Customer validBusinessCustomer = CustomerGenerator.makeCustomer(new Assure.Builder().simple(true).business(true).build());
        validBusinessCustomer.getCompanies().get(0).getCommunications().clear();
        assertThat(validBusinessCustomer.isSimple()).as("still simplecustomer").isTrue();

        Contact makeValidContact = makeValidCompanyContact();
        makeValidContact.getCommunications().clear();
        Communication email = makeValidCommunication(Type.EMAIL, "Max.mustermann@mustermail.de");
        makeValidContact.getCommunications().add(email);
        makeValidContact.getCommunications().add(makeValidCommunication(Type.MOBILE, "0174 123456789"));
        makeValidContact.getCommunications().add(makeValidCommunication(Type.PHONE, "040 123456789"));
        assertThat(makeValidContact.getViolationMessage()).as("valid contact").isNull();

        validBusinessCustomer.getCompanies().get(0).getContacts().clear();
        validBusinessCustomer.getCompanies().get(0).getContacts().add(makeValidContact);

        assertThat(validBusinessCustomer.isSimple()).as("still simplecustomer").isTrue();

        assertThat(validBusinessCustomer.toSimple().isPresent()).as("to simple").isTrue();
        validBusinessCustomer.setResellerListEmailCommunication(email);

        SimpleCustomer simpleBusinessCustomer = validBusinessCustomer.toSimple().get();

        assertThat(simpleBusinessCustomer.getTitle()).as("title").isEqualTo(validBusinessCustomer.getCompanies().get(0).getContacts().get(0).getTitle());
        assertThat(simpleBusinessCustomer.getFirstName()).as("firstname").isEqualTo(validBusinessCustomer.getCompanies().get(0).getContacts().get(0).getFirstName());
        assertThat(simpleBusinessCustomer.getLastName()).as("lastname").isEqualTo(validBusinessCustomer.getCompanies().get(0).getContacts().get(0).getLastName());

        assertThat(simpleBusinessCustomer.getSex()).as("sex").isEqualTo(validBusinessCustomer.getCompanies().get(0).getContacts().get(0).getSex());

        //address tests no longer needed as company contcts can not have any adresses due to getViolationMessage
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

        assertThat(validBusinessCustomer.isSimple()).as("still simplecustomer").isTrue();
        assertThat(simpleBusinessCustomer.isUseEmailForResellerList()).as("useEmailForResellerList").isTrue();
    }

}
