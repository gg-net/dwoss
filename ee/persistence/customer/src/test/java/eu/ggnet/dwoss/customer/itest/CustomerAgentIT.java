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
package eu.ggnet.dwoss.customer.itest;

import java.util.Locale;

import javax.ejb.EJB;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.customer.ee.CustomerAgent;
import eu.ggnet.dwoss.customer.ee.entity.Communication.Type;
import eu.ggnet.dwoss.customer.ee.entity.Contact.Sex;
import eu.ggnet.dwoss.customer.ee.entity.*;
import eu.ggnet.dwoss.customer.ee.entity.projection.AddressLabel;
import eu.ggnet.dwoss.customer.itest.support.ArquillianProjectArchive;
import eu.ggnet.dwoss.rules.AddressType;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author jens.papenhagen
 */
@RunWith(Arquillian.class)
public class CustomerAgentIT extends ArquillianProjectArchive {

    @EJB
    private CustomerAgent agent;

    private static Company makeValidCompany() {
        Company validcompany = new Company();
        validcompany.setName("Firma ABC");
        validcompany.setTaxId("textid123456789");
        validcompany.getAddresses().add(makeValidAddress());

        assertThat(validcompany.getViolationMessage()).as("valid Company").isNull();
        return validcompany;
    }

    private static Contact makeValidContact() {
        Contact validContact = new Contact(Sex.MALE, true, "Dr", "Max", "Mustermann");
        validContact.getAddresses().add(makeValidAddress());
        validContact.getCommunications().add(makeValidCommunication());

        assertThat(validContact.getViolationMessage()).as("valid Contact").isNull();
        return validContact;
    }

    private static Communication makeValidCommunication() {
        Communication validCommunication = new Communication(Type.EMAIL, "Max.mustermann@mustermail.de");

        assertThat(validCommunication.getViolationMessage()).as("valid Communication").isNull();
        return validCommunication;
    }

    private static Address makeValidAddress() {
        Address validAddress = new Address();
        validAddress.setCity("Munich");
        validAddress.setIsoCountry(Locale.GERMANY);
        validAddress.setStreet("Straße");
        validAddress.setZipCode("123456");

        assertThat(validAddress.getViolationMessage()).as("valid Address").isNull();
        return validAddress;
    }

    private static AddressLabel makeValidAddressLabel() {
        AddressLabel validAddressLabel = new AddressLabel(makeValidCompany(), makeValidContact(), makeValidAddress(), AddressType.INVOICE);

        assertThat(validAddressLabel.getViolationMessage()).as("valid validAddressLabel").isNull();
        return validAddressLabel;
    }

    /**
     * store i will test the store methode
     *
     * @throws Exception
     */
    @Ignore
    @Test
    public void testStore() throws Exception {

        Customer c1 = new Customer();
        c1.getContacts().add(makeValidContact());
        assertThat(c1.isConsumer()).as("Consumer Customer").isTrue();
        assertThat(c1.isValid()).as("Consumer Customer is Valid").isTrue();

        Customer c2 = new Customer();
        c2.getCompanies().add(makeValidCompany());
        c2.getCompanies().get(0).getContacts().add(makeValidContact());
        c2.getCompanies().get(0).getContacts().get(0).getAddresses().add(makeValidAddress());
        c2.getCompanies().get(0).getAddresses().add(makeValidAddress());
        c2.getAddressLabels().add(makeValidAddressLabel());

        assertThat(c1.isConsumer()).as("Consumer Customer").isTrue();
        assertThat(c1.isValid()).as("Consumer Customer is Valid").isTrue();
        assertThat(c1.isSimple()).as("Customer can be transform to a simple customer").isTrue();
        Customer payload = agent.store(c1.toSimple().get()).getPayload();

        assertThat(payload.isValid()).as("the payload is a valid customer").isTrue();
        assertThat(payload.isConsumer()).as("Consumer Customer").isTrue();
        assertThat(payload.isSimple()).as("the payload can be transform to a simple customer").isTrue();
        assertThat(payload).as("check that store the same customer").isEqualTo(c1);

        assertThat(c2.isBusiness()).as("Business Customer").isTrue();
        assertThat(c2.isValid()).as("Business Customer is Valid").isTrue();
        assertThat(c2.isSimple()).as("Customer can be transform to a simple customer").isTrue();
        Customer businesspayload = agent.store(c2.toSimple().get()).getPayload();

        assertThat(businesspayload.isValid()).as("the payload is a valid customer").isTrue();
        assertThat(businesspayload.isBusiness()).as("Business Customer").isTrue();
        assertThat(businesspayload.isSimple()).as("the payload can be transform to a simple customer").isTrue();
        assertThat(businesspayload).as("check that store the same customer").isEqualTo(c2);

    }
}
