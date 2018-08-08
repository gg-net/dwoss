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
import eu.ggnet.dwoss.customer.ee.assist.gen.CustomerGenerator;
import eu.ggnet.dwoss.customer.ee.entity.*;
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

    @Inject
    private CustomerGenerator GEN;

    @Before
    public void teardown() throws Exception {
        utx.begin();
        em.joinTransaction();
        Utils.clearH2Db(em);
        utx.commit();
    }

    @Test
    /**
     * Test update for all supported entities on a contact.
     */
    public void testUpdateOnContact() throws Exception {

        utx.begin();
        em.joinTransaction();

        //create a contact with an address and a communication
        Contact contact = GEN.makeContact();
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
    /**
     * Test update for all supported entities on a customer.
     */
    public void testUpdateOnCustomer() throws Exception {

        utx.begin();
        em.joinTransaction();

        //create a customer with a contact and one mandatorMetadata
        Contact contact = GEN.makeContact();

        MandatorMetadata mm = GEN.makeMandatorMetadata();

        Customer c = GEN.makeCustomer();
        c.getContacts().clear();
        c.getContacts().add(contact);

        c.getMandatorMetadata().clear();
        c.getMandatorMetadata().add(mm);

        em.persist(c);

        utx.commit();

        //update each contact and mandatorMetadata and check if it got updated correctly
        Customer foundCustomer = agent.findByIdEager(Customer.class, 1l);
        assertThat(foundCustomer.getContacts().size()).as("Not the correct amount of contacts on the customer").isEqualTo(1);
        Contact foundContact = foundCustomer.getContacts().get(0);
        foundContact.setFirstName("newFirstName");
        agent.update(foundContact);
        foundCustomer = agent.findByIdEager(Customer.class, 1l);
        assertThat(foundCustomer.getContacts().get(0).getFirstName()).as("Update didn't work on address for customer").isEqualTo("newFirstName");

        foundCustomer = agent.findByIdEager(Customer.class, 1l);
        assertThat(foundCustomer.getMandatorMetadata().size()).as("Not the correct amount of mandatorMetadata on the customer").isEqualTo(1);
        MandatorMetadata foundMandatorMetadata = foundCustomer.getMandatorMetadata().get(0);
        foundMandatorMetadata.setMandatorMatchcode("newMatchcode");
        agent.update(foundMandatorMetadata);
        foundCustomer = agent.findByIdEager(Customer.class, 1l);
        assertThat(foundCustomer.getMandatorMetadata().get(0).getMandatorMatchcode()).as("Update didn't work on mandatorMetadata for customer").isEqualTo("newMatchcode");

    }

    @Test
    /**
     * Test update for all supported entities on a company.
     */
    public void testUpdateOnCompany() throws Exception {

        utx.begin();
        em.joinTransaction();
        Customer customer = GEN.makeCustomer();
        customer.getContacts().clear();

        Company company = GEN.makeCompany();
        em.merge(company);
        Company findByIdEager = agent.findByIdEager(Company.class, 1l);
        customer.getCompanies().add(findByIdEager);

        em.merge(customer);
        utx.commit();

        //update each address, communication and contact and check if it got updated correctly
        Company foundCompany = agent.findByIdEager(Company.class, 1l);
        assertThat(foundCompany.getAddresses().size()).as("Not the correct amount of addresses on the company").isEqualTo(1);
        Address foundAddress = foundCompany.getAddresses().get(0);
        foundAddress.setStreet("newStreet");
        agent.update(foundAddress);
        foundCompany = agent.findByIdEager(Company.class, 1l);
        assertThat(foundCompany.getAddresses().get(0).getStreet()).as("Update didn't work on address for company").isEqualTo("newStreet");

        foundCompany = agent.findByIdEager(Company.class, 1l);
        assertThat(foundCompany.getCommunications().size()).as("Not the correct amount of communications on the company").isEqualTo(1);
        Communication foundCommunication = foundCompany.getCommunications().get(0);
        foundCommunication.setIdentifier("newIdentifier");
        agent.update(foundCommunication);
        foundCompany = agent.findByIdEager(Company.class, 1l);
        assertThat(foundCompany.getCommunications().get(0).getIdentifier()).as("Update didn't work on communication for company").isEqualTo("newIdentifier");

        foundCompany = agent.findByIdEager(Company.class, 1l);
        assertThat(foundCompany.getContacts().size()).as("Not the correct amount of contacts on the company").isEqualTo(1);
        Contact foundContact = foundCompany.getContacts().get(0);
        foundContact.setFirstName("newFirstName");
        agent.update(foundContact);
        foundCompany = agent.findByIdEager(Company.class, 1l);
        assertThat(foundCompany.getContacts().get(0).getFirstName()).as("Update didn't work on contact for company").isEqualTo("newFirstName");

    }

}
