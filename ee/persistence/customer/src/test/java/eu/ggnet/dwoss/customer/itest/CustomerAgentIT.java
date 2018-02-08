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

import java.util.*;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.*;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.customer.ee.CustomerAgent;
import eu.ggnet.dwoss.customer.ee.assist.Customers;
import eu.ggnet.dwoss.customer.ee.entity.*;
import eu.ggnet.dwoss.customer.ee.entity.Communication.Type;
import eu.ggnet.dwoss.customer.ee.entity.Contact.Sex;
import eu.ggnet.dwoss.customer.ee.entity.Customer.SearchField;
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

    @Inject
    @Customers
    private EntityManager em;

    @Inject
    UserTransaction utx;

    public static Company makeValidCompany() {
        Company validcompany = new Company();
        validcompany.setName("Firma ABC");
        validcompany.setTaxId("textid123456789");
        validcompany.getAddresses().add(makeValidAddress());

        assertThat(validcompany.getViolationMessage()).as("valid Company").isNull();
        return validcompany;
    }

    public static Contact makeValidContact() {
        Contact validContact = new Contact(Sex.MALE, true, "Dr", "Max", "Mustermann");
        validContact.getAddresses().add(makeValidAddress());

        assertThat(validContact.getViolationMessage()).as("valid Contact").isNull();
        return validContact;
    }

    public static Communication makeValidCommunication() {
        Communication validCommunication = new Communication(Type.EMAIL, true);
        validCommunication.setIdentifier("Max.mustermann@mustermail.de");

        assertThat(validCommunication.getViolationMessage()).as("valid Communication").isNull();
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

    @Ignore
    @Test
    public void testSearch() throws Exception {

        utx.begin();
        em.joinTransaction();

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

        assertThat(c1.isBusiness()).as("Consumer Customer").isTrue();
        assertThat(c1.isValid()).as("Consumer Customer is Valid").isTrue();

        em.persist(c1);
        em.persist(c2);

        utx.commit();

        String searchString = "Max";

        Set<SearchField> customerFields = new HashSet<>();
        customerFields.add(SearchField.ID);
        customerFields.add(SearchField.ADDRESS);
        customerFields.add(SearchField.COMPANY);
        customerFields.add(SearchField.FIRSTNAME);
        customerFields.add(SearchField.LASTNAME);

        List<Customer> searchList = agent.search(searchString, customerFields);
        assertThat(searchList).as("list").isNotNull();
        assertThat(searchList.get(0).getContacts().get(0).getFirstName()).as("the name is max").isEqualTo(makeValidContact().getFirstName());
                

    }
}
