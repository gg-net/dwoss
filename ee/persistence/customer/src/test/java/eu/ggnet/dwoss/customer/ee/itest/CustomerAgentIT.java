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

import java.util.*;

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
import eu.ggnet.dwoss.customer.ee.assist.Customers;
import eu.ggnet.dwoss.customer.ee.assist.gen.CustomerGenerator;
import eu.ggnet.dwoss.customer.ee.eao.CustomerEao;
import eu.ggnet.dwoss.customer.ee.entity.*;
import eu.ggnet.dwoss.customer.ee.entity.Customer.SearchField;
import eu.ggnet.dwoss.customer.ee.itest.support.ArquillianProjectArchive;

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

    @Inject
    private CustomerGenerator GEN;

    @Inject
    private CustomerEao eao;

    @Before
    public void teardown() throws Exception {
        utx.begin();
        em.joinTransaction();
        Utils.clearH2Db(em);
        utx.commit();
    }

    @Test
    public void testFindCustomerAsMandatorHtml() {
        String feedback = "Kein Kunde mit id 123 vorhanden";
        String findCustomerAsMandatorHtml = agent.findCustomerAsMandatorHtml(123);
        assertThat(findCustomerAsMandatorHtml).as("give back the Error Message").isEqualToIgnoringCase(feedback);
    }

    @Test
    public void testSearchWithCustomerFields() throws Exception {
        utx.begin();
        em.joinTransaction();
        Customer customer = GEN.makeCustomer();
        String firstName = customer.getContacts().get(0).getFirstName();

        em.persist(customer);
        utx.commit();

        assertThat(eao.findAll().size()).as("found more than one Customer").isEqualTo(1);

        Set<SearchField> customerFields = new HashSet<>();
        customerFields.add(SearchField.FIRSTNAME);

        assertThat(agent.search(firstName, customerFields, 0, 50).size()).as("can not find the City of Customer").isEqualTo(1);
    }

    @Test
    public void testSearch() throws Exception {
        utx.begin();
        em.joinTransaction();
        Customer customer = GEN.makeCustomer();
        String firstName = customer.getContacts().get(0).getFirstName();

        em.persist(customer);
        utx.commit();

        assertThat(eao.findAll().size()).as("found more than one Customer").isEqualTo(1);

        Set<SearchField> customerFields = new HashSet<>();
        customerFields.add(SearchField.FIRSTNAME);

        assertThat(agent.search(firstName, customerFields).size()).as("can not find the City of Customer").isEqualTo(1);
    }
    
    @Test
    public void testCountSearch() throws Exception {
        utx.begin();
        em.joinTransaction();
        Customer customer = GEN.makeCustomer();
        String firstName = customer.getContacts().get(0).getFirstName();

        em.persist(customer);
        utx.commit();

        assertThat(eao.findAll().size()).as("found more than one Customer").isEqualTo(1);

        Set<SearchField> customerFields = new HashSet<>();
        customerFields.add(SearchField.FIRSTNAME);

        assertThat(agent.countSearch(firstName, customerFields)).as("can not find the City of Customer").isEqualTo(1);
    }

    @Test
    public void testFindCustomerAsHtml() {
        String feedback = "Kein Kunde mit id 123 vorhanden";
        String findCustomerAsHtml = agent.findCustomerAsHtml(123);
        assertThat(findCustomerAsHtml).as("give back the Error Message").isEqualToIgnoringCase(feedback);
    }

}
