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

import eu.ggnet.dwoss.customer.ee.itest.support.Utils;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.UserTransaction;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.*;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.customer.ee.CustomerAgent;
import eu.ggnet.dwoss.customer.ee.assist.Customers;
import eu.ggnet.dwoss.customer.ee.entity.*;
import eu.ggnet.dwoss.customer.ee.entity.Communication.Type;
import eu.ggnet.dwoss.customer.ee.itest.support.ArquillianProjectArchive;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author lucas.huelsen
 */
@RunWith(Arquillian.class)
public class CustomerAgentUpdateIT extends ArquillianProjectArchive {

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
    public void testUpdateOnContact() throws Exception {

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

        //update each address,contact and check if it got updated correctly
        Contact found = agent.findByIdEager(Contact.class, 1l);
        assertThat(found.getAddresses().size()).as("Not the correct amount of addresses on the contact").isEqualTo(1);
        Address foundAddress = found.getAddresses().get(0);
        foundAddress.setStreet("newStreet");
        agent.update(foundAddress);
        found = agent.findByIdEager(Contact.class, 1l);
        assertThat(found.getAddresses().get(0).getStreet()).as("Update didn't work on address for contact").isEqualTo("newStreet");

        assertThat(found.getCommunications().size()).as("Not the correct amount of communications on the contact").isEqualTo(1);
        Communication foundCommunication = found.getCommunications().get(0);
        foundCommunication.setIdentifier("newIdentifier");
        agent.update(foundCommunication);
        found = agent.findByIdEager(Contact.class, 1l);
        assertThat(found.getCommunications().get(0).getIdentifier()).as("Update didn't work on communication for contact").isEqualTo("newIdentifier");

    }

    @Test
    public void testUpdateOnCustomer() throws Exception {

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

        //update each contact,company and mandatorMetadata and check if it got updated correctly
        Customer found = agent.findByIdEager(Customer.class, 1l);
        assertThat(found.getContacts().size()).as("Not the correct amount of contacts on the customer").isEqualTo(1);
        Contact foundContact = found.getContacts().get(0);
        foundContact.setFirstName("newFirstName");
        agent.update(foundContact);
        found = agent.findByIdEager(Customer.class, 1l);
        assertThat(found.getContacts().get(0).getFirstName()).as("Update didn't work on address for customer").isEqualTo("newFirstName");

        found = agent.findByIdEager(Customer.class, 1l);
        assertThat(found.getCompanies().size()).as("Not the correct amount of companies on the customer").isEqualTo(1);
        Company foundCompany = found.getCompanies().get(0);
        foundCompany.setName("newCompany");
        agent.update(foundCompany);
        found = agent.findByIdEager(Customer.class, 1l);
        assertThat(found.getCompanies().get(0).getName()).as("Update didn't work on company for customer").isEqualTo("newCompany");

        found = agent.findByIdEager(Customer.class, 1l);
        assertThat(found.getMandatorMetadata().size()).as("Not the correct amount of mandatorMetadata on the customer").isEqualTo(1);
        MandatorMetadata foundMandatorMetadata = found.getMandatorMetadata().get(0);
        foundMandatorMetadata.setMandatorMatchcode("newMatchcode");
        agent.update(foundMandatorMetadata);
        found = agent.findByIdEager(Customer.class, 1l);
        assertThat(found.getMandatorMetadata().get(0).getMandatorMatchcode()).as("Update didn't work on mandatorMetadata for customer").isEqualTo("newMatchcode");

    }

    @Test
    public void testUpdateOnCompany() throws Exception {

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

        //update each address, communication and contact and check if it got updated correctly
        Company found = agent.findByIdEager(Company.class, 1l);
        assertThat(found.getAddresses().size()).as("Not the correct amount of addresses on the company").isEqualTo(1);
        Address foundAddress = found.getAddresses().get(0);
        foundAddress.setStreet("newStreet");
        agent.update(foundAddress);
        found = agent.findByIdEager(Company.class, 1l);
        assertThat(found.getAddresses().get(0).getStreet()).as("Update didn't work on address for company").isEqualTo("newStreet");

        found = agent.findByIdEager(Company.class, 1l);
        assertThat(found.getCommunications().size()).as("Not the correct amount of communications on the company").isEqualTo(1);
        Communication foundCommunication = found.getCommunications().get(0);
        foundCommunication.setIdentifier("newIdentifier");
        agent.update(foundCommunication);
        found = agent.findByIdEager(Company.class, 1l);
        assertThat(found.getCommunications().get(0).getIdentifier()).as("Update didn't work on communication for company").isEqualTo("newIdentifier");

        found = agent.findByIdEager(Company.class, 1l);
        assertThat(found.getContacts().size()).as("Not the correct amount of contacts on the company").isEqualTo(1);
        Contact foundContact = found.getContacts().get(0);
        foundContact.setFirstName("newFirstName");
        agent.update(foundContact);
        found = agent.findByIdEager(Company.class, 1l);
        assertThat(found.getContacts().get(0).getFirstName()).as("Update didn't work on contact for company").isEqualTo("newFirstName");

    }

}
