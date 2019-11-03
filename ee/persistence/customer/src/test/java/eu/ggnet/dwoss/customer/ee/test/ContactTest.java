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
package eu.ggnet.dwoss.customer.ee.test;

import eu.ggnet.dwoss.customer.ee.entity.Contact;
import eu.ggnet.dwoss.customer.ee.entity.Address;

import org.junit.Before;
import org.junit.Test;

import eu.ggnet.dwoss.customer.ee.assist.gen.CustomerGenerator;
import eu.ggnet.dwoss.customer.ee.entity.*;
import eu.ggnet.dwoss.customer.ee.entity.Communication.Type;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author lucas.huelsen
 */
public class ContactTest {

    private Contact contact;

    private CustomerGenerator GEN = new CustomerGenerator();

    @Before
    public void executeBeforeEach() {
        contact = GEN.makeFullContact();
    }

    @Test
    public void testGetViolationMessages() {
        assertThat(contact.getViolationMessage()).as("Contact with valid values").isNull();
    }

    @Test
    public void testGetViolationMessagesNonValid() {
        contact.setLastName("");
        assertThat(contact.getViolationMessage()).as("Contact without lastName").isNotBlank();
    }

    @Test
    public void testGetViolationMessagesNonValid2() {
        contact.getAddresses().add(new Address());
        assertThat(contact.getViolationMessage()).as("Contact with invalid address").isNotBlank();
    }

    @Test
    public void testGetViolationMessagesNonValid3() {
        contact.getCommunications().add(new Communication(Type.SKYPE));
        assertThat(contact.getViolationMessage()).as("Contact with invalid Communication").isNotBlank();
    }

}
