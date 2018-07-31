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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.customer.ee.CustomerAgent;
import eu.ggnet.dwoss.customer.ee.CustomerAgent.Root;
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
public class CustomerAgentCreateIT extends ArquillianProjectArchive {

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
    public void testCreateOnContact() throws Exception {

        utx.begin();
        em.joinTransaction();

        //create a contact
        Contact contact = new Contact();
        contact.setFirstName("firstName");
        contact.setLastName("lastName");
        em.persist(contact);

        utx.commit();

        //address that gets created on the contact
        Address address = new Address();
        address.setStreet("street");
        address.setCity("city");
        address.setZipCode("12345");

        agent.create(new Root(Contact.class, 1l), address);

        Contact found = agent.findByIdEager(Contact.class, 1l);
        assertThat(found.getAddresses().size()).as("Not the correct amount of addresses on the contact").isEqualTo(1);

        //communication that gets created on the contact
        Communication communication = new Communication();
        communication.setType(Type.SKYPE);
        communication.setIdentifier("identifier");

        agent.create(new Root(Contact.class, 1l), communication);
        found = agent.findByIdEager(Contact.class, 1l);
        assertThat(found.getCommunications().size()).as("Not the correct amount of communications on the contact").isEqualTo(1);

    }

    @Test
    public void testCreateOnCustomer() throws Exception {

        utx.begin();
        em.joinTransaction();

        //create a customer
        Customer c = new Customer();
        em.persist(c);

        utx.commit();

        //contact that gets created on the customer
        Contact contact = new Contact();
        contact.setFirstName("firstName");
        contact.setLastName("lastName");

        Customer found = agent.findByIdEager(Customer.class, 1l);
        assertThat(found.getContacts().size()).as("The customer should habe no contacts").isEqualTo(0);

        agent.create(new Root(Customer.class, 1l), contact);
        found = agent.findByIdEager(Customer.class, 1l);
        assertThat(found.getContacts().size()).as("Not the correct amount of contact on the Customer").isEqualTo(1);

        //company that gets created on the customer
        Company company = new Company();
        company.setName("company");

        assertThat(found.getCompanies().size()).as("The customer should have no companies").isEqualTo(0);
        agent.create(new Root(Customer.class, 1l), company);
        found = agent.findByIdEager(Customer.class, 1l);
        assertThat(found.getCompanies().size()).as("Not the correct amount of companys on the customer").isEqualTo(1);

        //mandatorMetadata that gets cerated on the customer
        MandatorMetadata mm = new MandatorMetadata("matchcode");

        assertThat(found.getMandatorMetadata().size()).as("The customer should have no mandatorMetadata").isEqualTo(0);
        agent.create(new Root(Customer.class, 1l), mm);
        found = agent.findByIdEager(Customer.class, 1l);
        assertThat(found.getMandatorMetadata().size()).as("Not the correct amount of mandatorMetadata on the customer").isEqualTo(1);

    }

    @Test
    public void testCreateOnCompany() throws Exception {

        utx.begin();
        em.joinTransaction();

        //create a company
        Company c = new Company();
        c.setName("company");
        em.persist(c);

        //communication that gets created on the company
        Communication communication = new Communication();
        communication.setType(Type.SKYPE);
        communication.setIdentifier("identifier");

        Company found = agent.findByIdEager(Company.class, 1l);
        assertThat(found.getCommunications().size()).as("The customer should have no communications").isEqualTo(0);
        agent.create(new Root(Company.class, 1l), communication);
        found = agent.findByIdEager(Company.class, 1l);
        assertThat(found.getCommunications().size()).as("Not the correct amount of communications on the Customer").isEqualTo(1);

        //address that gets cerated on the company
        Address address = new Address();
        address.setStreet("street");
        address.setCity("city");
        address.setZipCode("12345");

        found = agent.findByIdEager(Company.class, 1l);
        assertThat(found.getAddresses().size()).as("The customer should have no addresses").isEqualTo(0);
        agent.create(new Root(Company.class, 1l), address);
        found = agent.findByIdEager(Company.class, 1l);
        assertThat(found.getAddresses().size()).as("Not the correct amount of addresses on the Customer").isEqualTo(1);

        //contact that gets created in the company
        Contact contact = new Contact();
        contact.setFirstName("firstName");
        contact.setLastName("lastName");

        assertThat(found.getContacts().size()).as("The customer should have no contacts").isEqualTo(0);
        agent.create(new Root(Company.class, 1l), contact);
        found = agent.findByIdEager(Company.class, 1l);
        assertThat(found.getContacts().size()).as("Not the correct amount of contacts on the Customer").isEqualTo(1);

    }

}
