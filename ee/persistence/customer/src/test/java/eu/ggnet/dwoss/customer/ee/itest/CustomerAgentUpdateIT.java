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

import jakarta.ejb.EJB;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.UserTransaction;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.*;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.core.common.values.CustomerFlag;
import eu.ggnet.dwoss.customer.ee.CustomerAgent;
import eu.ggnet.dwoss.customer.ee.assist.Customers;
import eu.ggnet.dwoss.customer.ee.assist.gen.*;
import eu.ggnet.dwoss.customer.ee.entity.*;
import eu.ggnet.dwoss.customer.ee.itest.support.ArquillianProjectArchive;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author lucas.huelsen
 */
@RunWith(Arquillian.class)
public class CustomerAgentUpdateIT extends ArquillianProjectArchive {

    private final static Logger L = LoggerFactory.getLogger(CustomerAgentUpdateIT.class);

    @EJB
    private CustomerAgent agent;

    @Inject
    @Customers
    private EntityManager em;

    @Inject
    private UserTransaction utx;

    @Inject
    private CustomerGeneratorOperation cgo;

    @After
    public void teardown() throws Exception {
        utx.begin();
        em.joinTransaction();
        CustomerDeleteUtils.deleteAll(em);
        assertThat(CustomerDeleteUtils.validateEmpty(em)).isNull();
        utx.commit();
    }

    @Test
    public void testUpdateOnContact() throws Exception {

        utx.begin();
        em.joinTransaction();

        //create a contact with an address and a communication
        Contact contact = CustomerGenerator.makeFullContact();
        em.persist(contact);

        utx.commit();

        //update each address,contact and check if it got updated correctly
        Contact found = agent.findByIdEager(Contact.class, contact.getId());
        assertThat(found.getAddresses().size()).as("Not the correct amount of addresses on the contact").isEqualTo(1);
        Address foundAddress = found.getAddresses().get(0);
        foundAddress.setStreet("newStreet");
        agent.update(foundAddress);
        found = agent.findByIdEager(Contact.class, contact.getId());
        assertThat(found.getAddresses().get(0).getStreet()).as("Update didn't work on address for contact").isEqualTo("newStreet");

        assertThat(found.getCommunications().size()).as("Not the correct amount of communications on the contact").isEqualTo(1);
        Communication foundCommunication = found.getCommunications().get(0);
        foundCommunication.setIdentifier("newIdentifier");
        agent.update(foundCommunication);
        found = agent.findByIdEager(Contact.class, contact.getId());
        assertThat(found.getCommunications().get(0).getIdentifier()).as("Update didn't work on communication for contact").isEqualTo("newIdentifier");

    }

    @Test
    public void testUpdateOnCustomer() {
        long cid = cgo.makeCustomer(new Assure.Builder().simple(true).consumer(true).mutateMandatorMetadataMatchCodes(c -> c.add("MMM")).build());

        //update each contact and mandatorMetadata and check if it got updated correctly
        Customer customer = agent.findByIdEager(Customer.class, cid);
        assertThat(customer.getContacts().size()).as("Not the correct amount of contacts on the customer").isEqualTo(1);
        Contact foundContact = customer.getContacts().get(0);
        foundContact.setFirstName("newFirstName");
        agent.update(foundContact);
        customer = agent.findByIdEager(Customer.class, cid);
        assertThat(customer.getContacts().get(0).getFirstName()).as("Update didn't work on address for customer").isEqualTo("newFirstName");

        customer = agent.findByIdEager(Customer.class, cid);
        assertThat(customer.getMandatorMetadata().size()).as("Not the correct amount of mandatorMetadata on the customer").isEqualTo(1);
        MandatorMetadata foundMandatorMetadata = customer.getMandatorMetadata().get(0);
        foundMandatorMetadata.setMandatorMatchcode("newMatchcode");
        agent.update(foundMandatorMetadata);
        customer = agent.findByIdEager(Customer.class, cid);
        assertThat(customer.getMandatorMetadata().get(0).getMandatorMatchcode()).as("Update didn't work on mandatorMetadata for customer").isEqualTo("newMatchcode");

    }

    @Test
    public void testUpdateOnCompany() throws Exception {
        long cid = cgo.makeCustomer(new Assure.Builder().simple(true).business(true).build());

        Customer customer = agent.findByIdEager(Customer.class, cid);
        long coid = customer.getCompanies().get(0).getId();

        Company foundCompany = agent.findByIdEager(Company.class, coid);
        assertThat(foundCompany.getAddresses().size()).as("simple business customer must have exactly one address on the company").isEqualTo(1);
        Address foundAddress = foundCompany.getAddresses().get(0);
        foundAddress.setStreet("newStreet");
        agent.update(foundAddress);
        foundCompany = agent.findByIdEager(Company.class, coid);
        assertThat(foundCompany.getAddresses().get(0).getStreet()).as("Update didn't work on address for company").isEqualTo("newStreet");

        foundCompany = agent.findByIdEager(Company.class, coid);
        assertThat(foundCompany.getContacts().size()).as("simple business customer must have exactly one contact").isEqualTo(1);
        Contact foundContact = foundCompany.getContacts().get(0);
        foundContact.setFirstName("newFirstName");
        agent.update(foundContact);
        foundCompany = agent.findByIdEager(Company.class, coid);
        assertThat(foundCompany.getContacts().get(0).getFirstName()).as("Update didn't work on contact for company").isEqualTo("newFirstName");

        foundCompany = agent.findByIdEager(Company.class, coid);
        assertThat(foundCompany.getContacts().get(0).getCommunications().size()).as("simple business customer must have at least one communication").isGreaterThanOrEqualTo(1);

        Communication foundCommunication = foundCompany.getContacts().get(0).getCommunications().get(0);
        foundCommunication.setIdentifier("newIdentifier");
        agent.update(foundCommunication);
        foundCompany = agent.findByIdEager(Company.class, coid);
        assertThat(foundCompany.getContacts().get(0).getCommunications().get(0).getIdentifier()).as("Update didn't work on communication for company").isEqualTo("newIdentifier");

    }

    /**
     * Wierd UI situation, if we add more that one flage, the addessalabeles get duplicated.
     */
    @Test
    public void multipleFlagesCreateMultipeAddresslabels() throws Exception {
        L.info("Test: multipleFlagesCreateMultipeAddresslabels()");

        L.info("create customer");
        utx.begin();
        em.joinTransaction();
        Customer c = CustomerGenerator.makeSimpleConsumerCustomer();
        em.persist(c);
        utx.commit();

        List<Customer> all = agent.findAllEager(Customer.class);
        assertThat(all).hasSize(1);

        long id = all.get(0).getId();
        c = all.get(0);
        L.info("Add first flag to customer");

        c.getFlags().add(CustomerFlag.ITC_CUSTOMER);
        agent.update(c);

        c = agent.findByIdEager(Customer.class, id);
        assertThat(c.getFlags()).hasSize(1);
        assertThat(c.getAddressLabels()).hasSize(1);
        L.info("Add second flag to customer");

        c.getFlags().add(CustomerFlag.CONFIRMS_DOSSIER);
        agent.update(c);
        c = agent.findByIdEager(Customer.class, id);
        assertThat(c.getFlags()).hasSize(2);
        assertThat(c.getAddressLabels()).hasSize(1);
        L.info("Finished: multipleFlagesCreateMultipeAddresslabels()");
    }

}
