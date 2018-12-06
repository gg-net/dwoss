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
package eu.ggnet.dwoss.customer.ee.itest;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.UserTransaction;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.common.api.values.AddressType;
import eu.ggnet.dwoss.customer.ee.CustomerAgent;
import eu.ggnet.dwoss.customer.ee.CustomerAgent.Root;
import eu.ggnet.dwoss.customer.ee.assist.Customers;
import eu.ggnet.dwoss.customer.ee.assist.gen.CustomerGenerator;
import eu.ggnet.dwoss.customer.ee.entity.*;
import eu.ggnet.dwoss.customer.ee.itest.support.ArquillianProjectArchive;
import eu.ggnet.dwoss.customer.ee.itest.support.Utils;

import static org.assertj.core.api.Assertions.*;

/**
 *
 * @author lucas.huelsen
 */
@RunWith(Arquillian.class)
public class CustomerAgentDeleteIT extends ArquillianProjectArchive {

    @EJB
    private CustomerAgent agent;

    @Inject
    @Customers
    private EntityManager em;

    @Inject
    UserTransaction utx;

    @Inject
    private CustomerGenerator GEN;

    private final static Logger L = LoggerFactory.getLogger(CustomerAgentDeleteIT.class);

    @Before
    public void teardown() throws Exception {
        utx.begin();
        em.joinTransaction();
        Utils.clearH2Db(em);
        utx.commit();
    }

    @Test
    /**
     * Test delete for all supported entities on a contact.
     */
    public void testDeleteOnContact() throws Exception {

        utx.begin();
        em.joinTransaction();

        //create a contact 
        Contact contact = GEN.makeContact();
        em.persist(contact);

        AddressLabel label = new AddressLabel(contact, contact.getAddresses().get(0), AddressType.INVOICE);
        em.persist(label);

        utx.commit();

        //delete each address,contact and check if it got deleted correctly
        Contact foundContact = agent.findByIdEager(Contact.class, 1l);
        assertThat(foundContact.getAddresses().size()).as("Not the correct amount of addresses on the contact").isEqualTo(1);
        Address foundAddress = foundContact.getAddresses().get(0);
        foundAddress.setStreet("newStreet");

        List<AddressLabel> labels = agent.findAll(AddressLabel.class);
        assertThat(labels.size()).as("Exactly one AddressLabel should have been found").isEqualTo(1);
        long labelId = labels.get(0).getId();

        assertThatThrownBy(() -> {
            agent.delete(new Root(Contact.class, 1l), foundAddress);
        }).hasMessageContaining("AddressLabel is still referenced");

        foundContact = agent.findByIdEager(Contact.class, 1l);
        assertThat(foundContact.getAddresses().isEmpty()).as("Delete somehow worked address for contact with referenced addresslabel").isFalse();

        assertThat(foundContact.getCommunications().size()).as("Not the correct amount of communications on the contact").isEqualTo(1);
        Communication foundCommunication = foundContact.getCommunications().get(0);
        foundCommunication.setIdentifier("newIdentifier");
        agent.delete(new Root(Contact.class, 1l), foundCommunication);
        foundContact = agent.findByIdEager(Contact.class, 1l);
        assertThat(foundContact.getCommunications().isEmpty()).as("Delete didn't work on communication for contact").isTrue();

        utx.begin();
        em.joinTransaction();
        em.remove(em.find(AddressLabel.class, labelId));
        utx.commit();

        labels = agent.findAll(AddressLabel.class);
        assertThat(labels.size()).as("No AddressLabel should have been found").isEqualTo(0);

        agent.delete(new Root(Contact.class, 1l), foundAddress);
        foundContact = agent.findByIdEager(Contact.class, 1l);
        assertThat(foundContact.getAddresses().isEmpty()).as("Delete didn't work on address for contact with referenced addresslabel").isTrue();

    }

    @Test
    /**
     * Test delete for all supported entities on a Customer.
     */
    public void testDeleteOnCustomer() throws Exception {

        utx.begin();
        em.joinTransaction();

        //create a customer with a contact and one mandatorMetadata
        Contact contact = GEN.makeContact();

        MandatorMetadata mm = GEN.makeMandatorMetadata();

        Customer customer = GEN.makeCustomer();
        customer.getContacts().add(contact);
        customer.getMandatorMetadata().add(mm);
        em.persist(customer);

        utx.commit();

        //delete each contact and mandatorMetadata and check if it got deleted correctly
        Customer foundCustomer = agent.findByIdEager(Customer.class, 1l);
        assertThat(foundCustomer.getContacts().size()).as("Not the correct amount of contacts on the customer").isGreaterThan(1);
        Contact foundContact = foundCustomer.getContacts().get(0);
        foundContact.setFirstName("newFirstName");

        assertThatThrownBy(() -> {
            agent.delete(new Root(Customer.class, 1l), foundContact);
        }).hasMessageContaining("AddressLabel is still referenced");

        Contact otherContact = foundCustomer.getContacts().get(1);
        agent.delete(new Root(Customer.class, 1l), otherContact);
        foundCustomer = agent.findByIdEager(Customer.class, 1l);
        assertThat(foundCustomer.getContacts().size()).as("Delete didn't work on address for customer").isLessThanOrEqualTo(5);
    }

    @Test
    /**
     * Test delete for all supported entities on a company.
     */
    public void testDeleteOnCompany() throws Exception {
        utx.begin();
        em.joinTransaction();
        Customer customer = GEN.makeCustomer();
        customer.getContacts().clear();

        Company company = GEN.makeCompany();
        em.persist(company);
        customer.getCompanies().add(company);

        em.persist(customer);
        utx.commit();

        //delete each address, communication and contact and check if it got deleted correctly
        Company foundCompany = agent.findByIdEager(Company.class, 1l);
        assertThat(foundCompany.getAddresses().size()).as("Not the correct amount of addresses on the company").isEqualTo(1);
        Address foundAddress = foundCompany.getAddresses().get(0);
        foundAddress.setStreet("newStreet");
        agent.delete(new Root(Company.class, 1l), foundAddress);
        foundCompany = agent.findByIdEager(Company.class, 1l);
        assertThat(foundCompany.getAddresses().isEmpty()).as("Delete didn't work on address for company").isTrue();

        foundCompany = agent.findByIdEager(Company.class, 1l);
        assertThat(foundCompany.getCommunications().size()).as("Not the correct amount of communications on the company").isEqualTo(1);
        Communication foundCommunication = foundCompany.getCommunications().get(0);
        foundCommunication.setIdentifier("newIdentifier");
        agent.delete(new Root(Company.class, 1l), foundCommunication);
        foundCompany = agent.findByIdEager(Company.class, 1l);
        assertThat(foundCompany.getCommunications().isEmpty()).as("Delete didn't work on communication for company").isTrue();

        foundCompany = agent.findByIdEager(Company.class, 1l);
        assertThat(foundCompany.getContacts().size()).as("Not the correct amount of contacts on the company").isEqualTo(1);
        Contact foundContact = foundCompany.getContacts().get(0);
        foundContact.setFirstName("newFirstName");
        agent.delete(new Root(Company.class, 1l), foundContact);
        foundCompany = agent.findByIdEager(Company.class, 1l);
        assertThat(foundCompany.getContacts().isEmpty()).as("Delete didn't work on contact for company").isTrue();

    }

    @Test
    /**
     * Try the delete mehtod with a not existing root. Expecting IllegalArgumentException.
     */
    public void testDeleteOnContactNegative() {

        Address address = new Address();
        address.setStreet("street");
        address.setCity("city");
        address.setZipCode("12345");

        try {
            agent.delete(new Root(Contact.class, 1l), address);
        } catch (EJBException e) {
            assertThat(e.getCausedByException() instanceof IllegalArgumentException)
                    .as("Trying to catch IllegalArgumentException of deleting an address on a not existing Contact failed")
                    .isTrue();
        }
    }

    @Test
    /**
     * Try the delete mehtod with an unsupported raw. Expecting IllegalArgumentException.
     */
    public void testDeleteWithUnsupportedRaw() throws Exception {

        utx.begin();
        em.joinTransaction();

        //create a contact
        Contact contact = new Contact();
        contact.setFirstName("firstName");
        contact.setLastName("lastName");
        em.persist(contact);

        utx.commit();

        String unsupportedRaw = "stringInstance";

        try {
            agent.delete(new Root(Contact.class, 1l), unsupportedRaw);
        } catch (EJBException e) {
            assertThat(e.getCausedByException() instanceof IllegalArgumentException)
                    .as("Trying to catch IllegalArgumentException of deleting a unsupportedRaw on a existing Contact failed")
                    .isTrue();
        }

    }
}
