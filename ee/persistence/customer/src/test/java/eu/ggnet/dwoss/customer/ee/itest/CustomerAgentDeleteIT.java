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

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.UserTransaction;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.customer.ee.CustomerAgent;
import eu.ggnet.dwoss.customer.ee.CustomerAgent.Root;
import eu.ggnet.dwoss.customer.ee.assist.Customers;
import eu.ggnet.dwoss.customer.ee.entity.*;
import eu.ggnet.dwoss.customer.ee.entity.Communication.Type;
import eu.ggnet.dwoss.customer.ee.itest.support.ArquillianProjectArchive;
import eu.ggnet.dwoss.customer.ee.itest.support.Utils;

import static org.assertj.core.api.Assertions.assertThat;

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

        //create a contact with an address and a communication
        Address address = new Address();
        address.setStreet("street");
        address.setCity("city");
        address.setZipCode("12345");

        Communication communication = new Communication();
        communication.setType(Type.SKYPE);
        communication.setIdentifier("identifier");

        Contact contact = new Contact();
        contact.setFirstName("firstName");
        contact.setLastName("lastName");
        contact.getAddresses().add(address);
        contact.getCommunications().add(communication);
        em.persist(contact);

        utx.commit();

        //delete each address,contact and check if it got deleted correctly
        Contact foundContact = agent.findByIdEager(Contact.class, 1l);
        assertThat(foundContact.getAddresses().size()).as("Not the correct amount of addresses on the contact").isEqualTo(1);
        Address foundAddress = foundContact.getAddresses().get(0);
        foundAddress.setStreet("newStreet");
        agent.delete(new Root(Contact.class, 1l), foundAddress);
        foundContact = agent.findByIdEager(Contact.class, 1l);
        assertThat(foundContact.getAddresses().isEmpty()).as("Delete didn't work on address for contact").isTrue();

        assertThat(foundContact.getCommunications().size()).as("Not the correct amount of communications on the contact").isEqualTo(1);
        Communication foundCommunication = foundContact.getCommunications().get(0);
        foundCommunication.setIdentifier("newIdentifier");
        agent.delete(new Root(Contact.class, 1l), foundCommunication);
        foundContact = agent.findByIdEager(Contact.class, 1l);
        assertThat(foundContact.getCommunications().isEmpty()).as("Delete didn't work on communication for contact").isTrue();

    }

    @Test
    /**
     * Test delete for all supported entities on a Customer.
     */
    public void testDeleteOnCustomer() throws Exception {

        utx.begin();
        em.joinTransaction();

        //create a customer with a contact, a company and one mandatorMetadata
        Contact contact = new Contact();
        contact.setFirstName("firstName");
        contact.setLastName("lastName");

        Company company = new Company();
        company.setName("company");

        MandatorMetadata mm = new MandatorMetadata("matchcode");

        Customer c = new Customer();
        c.getContacts().add(contact);
        c.getCompanies().add(company);
        c.getMandatorMetadata().add(mm);
        em.persist(c);

        utx.commit();

        //delete each contact,company and mandatorMetadata and check if it got deleted correctly
        Customer customer = agent.findByIdEager(Customer.class, 1l);
        assertThat(customer.getContacts().size()).as("Not the correct amount of contacts on the customer").isEqualTo(1);
        Contact foundContact = customer.getContacts().get(0);
        foundContact.setFirstName("newFirstName");
        agent.delete(new Root(Customer.class, 1l), foundContact);
        customer = agent.findByIdEager(Customer.class, 1l);
        assertThat(customer.getContacts().isEmpty()).as("Delete didn't work on address for customer").isTrue();

        customer = agent.findByIdEager(Customer.class, 1l);
        assertThat(customer.getCompanies().size()).as("Not the correct amount of companies on the customer").isEqualTo(1);
        Company foundCompany = customer.getCompanies().get(0);
        foundCompany.setName("newCompany");
        agent.delete(new Root(Customer.class, 1l), foundCompany);
        customer = agent.findByIdEager(Customer.class, 1l);
        assertThat(customer.getCompanies().isEmpty()).as("Delete didn't work on company for customer").isTrue();

    }

    @Test
    /**
     * Test delete for all supported entities on a company.
     */
    public void testDeleteOnCompany() throws Exception {

        utx.begin();
        em.joinTransaction();

        // create a company with an address a communication and a contact
        Address address = new Address();
        address.setStreet("street");
        address.setCity("city");
        address.setZipCode("12345");

        Communication communication = new Communication();
        communication.setType(Type.SKYPE);
        communication.setIdentifier("identifier");

        Contact contact = new Contact();
        contact.setFirstName("firstName");
        contact.setLastName("lastName");

        //create a company
        Company c = new Company();
        c.setName("company");
        c.getAddresses().add(address);
        c.getCommunications().add(communication);
        c.getContacts().add(contact);
        em.persist(c);

        //delete each address, communication and contact and check if it got deleted correctly
        Company company = agent.findByIdEager(Company.class, 1l);
        assertThat(company.getAddresses().size()).as("Not the correct amount of addresses on the company").isEqualTo(1);
        Address foundAddress = company.getAddresses().get(0);
        foundAddress.setStreet("newStreet");
        agent.delete(new Root(Company.class, 1l), foundAddress);
        company = agent.findByIdEager(Company.class, 1l);
        assertThat(company.getAddresses().isEmpty()).as("Delete didn't work on address for company").isTrue();

        company = agent.findByIdEager(Company.class, 1l);
        assertThat(company.getCommunications().size()).as("Not the correct amount of communications on the company").isEqualTo(1);
        Communication foundCommunication = company.getCommunications().get(0);
        foundCommunication.setIdentifier("newIdentifier");
        agent.delete(new Root(Company.class, 1l), foundCommunication);
        company = agent.findByIdEager(Company.class, 1l);
        assertThat(company.getCommunications().isEmpty()).as("Delete didn't work on communication for company").isTrue();

        company = agent.findByIdEager(Company.class, 1l);
        assertThat(company.getContacts().size()).as("Not the correct amount of contacts on the company").isEqualTo(1);
        Contact foundContact = company.getContacts().get(0);
        foundContact.setFirstName("newFirstName");
        agent.delete(new Root(Company.class, 1l), foundContact);
        company = agent.findByIdEager(Company.class, 1l);
        assertThat(company.getContacts().isEmpty()).as("Delete didn't work on contact for company").isTrue();

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
