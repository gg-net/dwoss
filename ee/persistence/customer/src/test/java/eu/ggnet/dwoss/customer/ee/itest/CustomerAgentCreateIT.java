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
import eu.ggnet.dwoss.customer.ee.assist.gen.CustomerGenerator;
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
     * Test create for all supported entities on a contact.
     */
    public void testCreateOnContact() throws Exception {

        utx.begin();
        em.joinTransaction();

        //create a contact
        Contact contact = GEN.makeContact();
        em.persist(contact);

        utx.commit();

        Address address = GEN.makeAddress();

        //create the address in the contact and check if it got added
        agent.create(new Root(Contact.class, 1l), address);
        Contact found = agent.findByIdEager(Contact.class, 1l);
        assertThat(found.getAddresses().size()).as("Not the correct amount of addresses on the contact").isGreaterThan(1);

        //communication that gets created on the contact
        Communication communication = new Communication();
        communication.setType(Type.SKYPE);
        communication.setIdentifier("identifier");

        //create the communication on the contact and check if it got added
        agent.create(new Root(Contact.class, 1l), communication);
        found = agent.findByIdEager(Contact.class, 1l);
        assertThat(found.getCommunications().size()).as("Not the correct amount of communications on the contact").isLessThanOrEqualTo(5);

    }

    @Test
    /**
     * Try the create mehtod with a not existing root. Expecting IllegalArgumentException.
     */
    public void testCreateOnContactNegative() {

        Address address = GEN.makeAddress();

        try {
            agent.create(new Root(Contact.class, 1l), address);
        } catch (EJBException e) {
            assertThat(e.getCausedByException() instanceof IllegalArgumentException)
                    .as("Trying to catch IllegalArgumentException of creating an address on a not existing Contact failed")
                    .isTrue();
        }
    }

    /**
     * Try the create mehtod with an unsupported raw. Expecting IllegalArgumentException.
     */
    public void testCreateWithUnsupportedRaw() throws Exception {

        utx.begin();
        em.joinTransaction();

        //create a contact
        Contact contact = GEN.makeContact();
        em.persist(contact);

        utx.commit();

        String unsupportedRaw = "stringInstance";

        try {
            agent.create(new Root(Contact.class, 1l), unsupportedRaw);
        } catch (EJBException e) {
            assertThat(e.getCausedByException() instanceof IllegalArgumentException)
                    .as("Trying to catch IllegalArgumentException of creating a unsupportedRaw on a existing Contact failed")
                    .isTrue();

        }

    }

    @Test
    /**
     * Test create for all supported entities on a customer.
     */
    public void testCreateOnCustomer() throws Exception {

        utx.begin();
        em.joinTransaction();

        //create a customer with a contact and one mandatorMetadata
        Contact contact = GEN.makeContact();
        MandatorMetadata mm = GEN.makeMandatorMetadata();

        Customer customer = GEN.makeCustomer();
        customer.getMandatorMetadata().add(mm);
        em.persist(customer);

        utx.commit();

        //create the contact on the customer and check if it got added
        Customer found = agent.findByIdEager(Customer.class, 1l);
        assertThat(found.getContacts().size()).as("The customer should habe no contacts").isLessThanOrEqualTo(5);
        agent.create(new Root(Customer.class, 1l), contact);
        found = agent.findByIdEager(Customer.class, 1l);
        assertThat(found.getContacts().size()).as("Not the correct amount of contact on the Customer").isGreaterThan(1);
    }

    @Test
    /**
     * Test create for all supported entities on a company.
     */
    public void testCreateOnCompany() throws Exception {
        utx.begin();
        em.joinTransaction();
        Customer customer = GEN.makeCustomer();
        customer.getContacts().clear();

        Company company = GEN.makeCompany();
        em.persist(company);
        customer.getCompanies().add(company);

        em.persist(customer);
        utx.commit();

        //communication that gets created on the company
        Communication communication = new Communication();
        communication.setType(Type.SKYPE);
        communication.setIdentifier("identifier");

        //create the communication on the company and check if it got added
        Company companyFromDb = agent.findByIdEager(Company.class, 1l);
        assertThat(companyFromDb.getCommunications().size()).as("The customer should have no communications").isLessThanOrEqualTo(5);
        agent.create(new Root(Company.class, 1l), communication);
        companyFromDb = agent.findByIdEager(Company.class, 1l);
        assertThat(companyFromDb.getCommunications().size()).as("Not the correct amount of communications on the Customer").isGreaterThan(1);

        //address that gets cerated on the company
        Address address = new Address();
        address.setStreet("street");
        address.setCity("city");
        address.setZipCode("12345");

        //create the address in the company and check if it got added
        companyFromDb = agent.findByIdEager(Company.class, 1l);
        assertThat(companyFromDb.getAddresses().size()).as("The customer should have no addresses").isGreaterThanOrEqualTo(1);
        agent.create(new Root(Company.class, 1l), address);
        companyFromDb = agent.findByIdEager(Company.class, 1l);
        assertThat(companyFromDb.getAddresses().size()).as("Not the correct amount of addresses on the Customer").isGreaterThanOrEqualTo(1);

        //contact that gets created in the company
        Contact contact = new Contact();
        contact.setFirstName("firstName");
        contact.setLastName("lastName");

        //create the contact in the comapny and check if it got added
        assertThat(companyFromDb.getContacts().size()).as("The customer should have no contacts").isGreaterThanOrEqualTo(0);
        agent.create(new Root(Company.class, 1l), contact);
        companyFromDb = agent.findByIdEager(Company.class, 1l);
        assertThat(companyFromDb.getContacts().size()).as("Not the correct amount of contacts on the Customer").isGreaterThanOrEqualTo(1);

    }

    @Test
    public void testCreateOnAddressLabel() throws Exception {
        utx.begin();
        em.joinTransaction();
        Customer customer = GEN.makeCustomer();
        Address address = GEN.makeAddress();
        customer.getContacts().get(0).getAddresses().add(address);
        em.persist(customer);
        utx.commit();

        Customer customerFromTheDb = agent.findByIdEager(Customer.class, 1l);

        Address addressesFromtheCustomerOutOfTheDb = customerFromTheDb.getContacts().get(1).getAddresses().get(0);

        AddressLabel makeShippingAddressLabel = GEN.makeShippingAddressLabel();
        makeShippingAddressLabel.setAddress(addressesFromtheCustomerOutOfTheDb);

        agent.create(new Root(Customer.class, 1l), makeShippingAddressLabel);

        Customer customerFormDb = agent.findByIdEager(Customer.class, 1l);
        assertThat(customerFormDb.getAddressLabels().size()).as("Shipping AddressLable is missing").isEqualTo(2);

    }

}
