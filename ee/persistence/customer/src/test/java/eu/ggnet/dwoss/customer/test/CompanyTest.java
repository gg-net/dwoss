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
package eu.ggnet.dwoss.customer.test;

import eu.ggnet.dwoss.customer.ee.entity.Contact;
import eu.ggnet.dwoss.customer.ee.entity.Address;
import eu.ggnet.dwoss.customer.ee.entity.Communication;
import eu.ggnet.dwoss.customer.ee.entity.Company;

import org.junit.Before;
import org.junit.Test;

import eu.ggnet.dwoss.customer.ee.assist.gen.CustomerGenerator;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author lucas.huelsen
 */
public class CompanyTest {

    private Company company;

    private CustomerGenerator GEN = new CustomerGenerator();

    @Before
    public void executeBeforeEach() {
        company = new Company();
        company.setName("name");
        company.getAddresses().add(GEN.makeAddress());
        company.getContacts().add(GEN.makeContact());
        company.getCommunications().add(GEN.makeCommunication());
    }

    @Test
    public void TestGetViolationMessages() {
        assertThat(company.getViolationMessages()).as("Company with valid values").isNull();
    }

    @Test
    public void TestGetViolationMessagesNonValid() {
        company.setName("");
        assertThat(company.getViolationMessages()).as("Company without name").isNotBlank();
    }

    @Test
    public void TestGetViolationMessagesNonValid2() {
        company.getAddresses().clear();
        assertThat(company.getViolationMessages()).as("Company without addresses").isNotBlank();
    }

    @Test
    public void TestGetViolationMessagesNonValid3() {
        company.getAddresses().clear();
        company.getAddresses().add(new Address());
        assertThat(company.getViolationMessages()).as("Company with invalid address").isNotBlank();
    }

    @Test
    public void TestGetViolationMessagesNonValid4() {
        company.getContacts().add(new Contact());
        assertThat(company.getViolationMessages()).as("Company with invalid contact").isNotBlank();
    }

    // Todo implement getViolationMessages() for Communication
    public void TestGetViolationMessagesNonValid5() {
        company.getCommunications().add(new Communication());
        assertThat(company.getViolationMessages()).as("Company with invalid Communication").isNotBlank();
    }

}
