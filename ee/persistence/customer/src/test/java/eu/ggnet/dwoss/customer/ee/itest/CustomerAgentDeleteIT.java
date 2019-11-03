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
import org.junit.*;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.core.common.values.AddressType;
import eu.ggnet.dwoss.customer.ee.CustomerAgent;
import eu.ggnet.dwoss.customer.ee.CustomerAgent.Root;
import eu.ggnet.dwoss.customer.ee.LastDeletionExecption;
import eu.ggnet.dwoss.customer.ee.assist.Customers;
import eu.ggnet.dwoss.customer.ee.assist.gen.*;
import eu.ggnet.dwoss.customer.ee.entity.*;
import eu.ggnet.dwoss.customer.ee.itest.support.ArquillianProjectArchive;
import eu.ggnet.dwoss.customer.ee.itest.support.Utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertNotNull;

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
    private CustomerGeneratorOperation cgo;

    private final static Logger L = LoggerFactory.getLogger(CustomerAgentDeleteIT.class);

    @Before
    public void teardown() throws Exception {
        utx.begin();
        em.joinTransaction();
        Utils.clearH2Db(em);
        utx.commit();
    }

    @Test
    public void testDeleteCommunication() throws Exception {
        utx.begin();
        em.joinTransaction();

        //create a customer with two communications, one on company, one und company contact
        Customer customer = CustomerGenerator.makeSimpleBussinesCustomer();
        Communication contactCom = customer.getCompanies().get(0).getContacts().get(0).getCommunications().get(0);

        //switch second company contact communication to company
        customer.getCompanies().get(0).getContacts().get(0).getCommunications().remove(contactCom);
        customer.getCompanies().get(0).getCommunications().add(contactCom);
        em.persist(customer);
        utx.commit();

        Customer cus = agent.findByIdEager(Customer.class, 1l);
        assertNotNull("No customer has been found.", cus);
        assertThat(cus.getCompanies().size()).as("Wrong amount of companies on the customer").isEqualTo(1);

        Company company = cus.getCompanies().get(0);
        assertThat(company.getCommunications().size()).as("Wrong amount of communications on company").isEqualTo(1);
        assertThat(company.getContacts().size()).as("Wrong amount of contacts on company").isEqualTo(1);

        Contact comContact = company.getContacts().get(0);
        assertThat(comContact.getCommunications().size()).as("Wrong amount of communocations on company contact").isEqualTo(1);

        agent.delete(new Root(Company.class, company.getId()), company.getCommunications().get(0));

        cus = agent.findByIdEager(Customer.class, 1l);
        company = cus.getCompanies().get(0);
        assertThat(company.getCommunications().size()).as("Wrong amount of communications on company").isEqualTo(0);

        comContact = company.getContacts().get(0);
        assertThat(comContact.getCommunications().size()).as("Wrong amount of communocations on company contact").isEqualTo(1);

        long comContactid = comContact.getId();
        Communication comunication = comContact.getCommunications().get(0);

        assertThatThrownBy(() -> {
            agent.delete(new Root(Contact.class, comContactid), comunication);
        }).as("Address in AddressLabel should not be deleteable.")
                .hasCauseInstanceOf(LastDeletionExecption.class);

    }

    @Test
    /**
     * Test delete for all supported entities on a contact.
     */
    public void testDeleteOnContact() throws Exception {

        utx.begin();
        em.joinTransaction();

        //create a contact
        Contact contact = CustomerGenerator.makeFullContact();
        em.persist(contact);

        AddressLabel label = new AddressLabel(contact, contact.getAddresses().get(0), AddressType.INVOICE);
        em.persist(label);

        utx.commit();

        //delete each address,contact and check if it got deleted correctly
        Contact foundContact = agent.findByIdEager(Contact.class, 1l);
        assertThat(foundContact.getAddresses().size()).as("Not the correct amount of addresses on the contact").isEqualTo(1);
        Address foundAddress = foundContact.getAddresses().get(0);
//        foundAddress.setStreet("newStreet");

        List<AddressLabel> labels = agent.findAll(AddressLabel.class);
        assertThat(labels.size()).as("Exactly one AddressLabel should have been found").isEqualTo(1);
        long labelId = labels.get(0).getId();

        assertThatThrownBy(() -> {
            agent.delete(new Root(Contact.class, 1l), foundAddress);
        }).as("Address in AddressLabel should not be deleteable.")
                .hasCauseInstanceOf(LastDeletionExecption.class);

        foundContact = agent.findByIdEager(Contact.class, 1l);
        assertThat(foundContact.getAddresses().isEmpty()).as("Delete somehow worked address for contact with referenced addresslabel").isFalse();

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
    public void testDeleteOnCustomer() throws Exception {

        long cid = cgo.makeCustomer(new Assure.Builder().simple(true).consumer(true).build());

        Customer customer = agent.findByIdEager(Customer.class, cid);
        assertThat(customer.getContacts().size()).as("A simple consumer customer must have exactly one contact").isEqualTo(1);
        Contact foundContact = customer.getContacts().get(0);

        assertThatThrownBy(() -> agent.delete(new Root(Customer.class, 1l), foundContact))
                .as("Delete of last contact must cause a LastDeletionExecption")
                .hasCauseInstanceOf(LastDeletionExecption.class);

        // TODO: Hier müssten noch die ganzen anderen exeptions mal getestet werden.
    }

    @Test
    /**
     * Test delete for all supported entities on a company.
     */
    @Ignore // Test ist fehlerhaft. Annahme war schon unsinn
    public void testDeleteOnCompany() throws Exception {

        long cid = cgo.makeCustomer(new Assure.Builder().simple(true).business(true).build());

        //delete each address, communication and contact and check if it got deleted correctly
        Customer customer = agent.findByIdEager(Customer.class, cid);
        assertThat(customer.getCompanies()).isNotEmpty(); // Safetynet

        Company foundCompany = customer.getCompanies().get(0);
        assertThat(foundCompany.getAddresses().size()).as("Simple business customer must have only one addresses on the company").isEqualTo(1);
        Address foundAddress = foundCompany.getAddresses().get(0);
        foundAddress.setStreet("newStreet");
        agent.delete(new Root(Company.class, 1l), foundAddress);
        foundCompany = agent.findByIdEager(Company.class, 1l);
        assertThat(foundCompany.getAddresses().isEmpty()).as("Delete didn't work on address for company").isTrue();

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
