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

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

import eu.ggnet.dwoss.customer.ee.entity.Address;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author lucas.huelsen
 */
public class AddressTest {

    private Address address;

    @Before
    public void executedBeforeEach() {
        address = new Address();
    }

    @Test
    public void testGetViolationMessages() {
        address.setStreet("street");
        address.setCity("city");
        address.setZipCode("12345");
        address.setIsoCountry(Locale.GERMANY);
        assertThat(address.getViolationMessages()).as("Address with valid values").isNull();
    }

    @Test
    public void testGetViolationMessagesNonValid() {
        address.setCity("city");
        address.setZipCode("12345");
        address.setIsoCountry(Locale.GERMANY);
        assertThat(address.getViolationMessages()).as("Address without street").isNotBlank();
    }

    @Test
    public void testGetViolationMessagesNonValid2() {
        address.setStreet("street");
        address.setZipCode("12345");
        address.setIsoCountry(Locale.GERMANY);
        assertThat(address.getViolationMessages()).as("Address without city").isNotBlank();
    }

    @Test
    public void testGetViolationMessagesNonValid3() {
        address.setStreet("street");
        address.setCity("city");
        address.setIsoCountry(Locale.GERMANY);
        assertThat(address.getViolationMessages()).as("Address without zipcode").isNotBlank();
    }

    @Test
    public void testGetViolationMessagesNonValid4() {
        address.setStreet("street");
        address.setCity("city");
        address.setZipCode("12345");
        address.setIsoCountry(Locale.GERMAN);
        assertThat(address.getViolationMessages()).as("Address without Iso Country").isNotBlank();
    }

}
