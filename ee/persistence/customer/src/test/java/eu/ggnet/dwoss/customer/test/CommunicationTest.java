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

import org.junit.Ignore;
import org.junit.Test;

import eu.ggnet.dwoss.customer.ee.entity.Communication;

import static eu.ggnet.dwoss.customer.ee.entity.Communication.Type.EMAIL;
import static eu.ggnet.dwoss.customer.ee.entity.Communication.Type.PHONE;
import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author jens.papenhagen
 */
public class CommunicationTest {

    public static Communication makeValidCommunication() {
        Communication validCommunication = new Communication(EMAIL, "max.mustermann@gmail.com");

        assertThat(validCommunication.getViolationMessage()).as("valid Communication").isNull();
        return validCommunication;
    }

    @Test
    public void testValidMessage() {
        Communication makeValidCommunication = makeValidCommunication();
        assertThat(makeValidCommunication.getViolationMessage()).as("Communication with valid values").isNull();
    }

    @Test
    public void testValidEmail() {
        Communication makeValidCommunication = makeValidCommunication();
        makeValidCommunication.setType(EMAIL);
        makeValidCommunication.setIdentifier("test@test.de");
        assertThat(makeValidCommunication.getViolationMessage()).as("Communication have a vaild email").isNull();
    }

    @Test
    public void testValidPhonenumber() {
        Communication makeValidCommunication = makeValidCommunication();
        makeValidCommunication.setType(PHONE);
        makeValidCommunication.setIdentifier("0401234567");
        assertThat(makeValidCommunication.getViolationMessage()).as("Communication with valid phonenumber").isNull();
    }

    @Test
    public void testNonValidEmail() {
        Communication makeInvalidCommunication = makeValidCommunication();
        makeInvalidCommunication.setType(EMAIL);
        makeInvalidCommunication.setIdentifier("falscheemail@@test.de");
        assertThat(makeInvalidCommunication.getViolationMessage()).as("Communication with nonvalid email").isNotBlank();
    }

    @Test
    public void testNonValidPhonenumber() {
        Communication makeInvalidCommunication = makeValidCommunication();
        makeInvalidCommunication.setType(PHONE);
        makeInvalidCommunication.setIdentifier("0123586Buchstaben");
        assertThat(makeInvalidCommunication.getViolationMessage()).as("Communication with nonvalid phonenumber").isNotBlank();
    }

    @Ignore
    @Test
    public void phoneNumbers() {
        String phonePattern = Communication.PHONE_PATTERN;
        assertThat(phonePattern).as("phonePattern").isNotBlank();
        assertThat("012345").matches(phonePattern);
        assertThat("  012345").doesNotMatch(phonePattern);
        assertThat("  012345  ").doesNotMatch(phonePattern);
        assertThat("012345   ").doesNotMatch(phonePattern);
        assertThat("0123 12345").matches(phonePattern);

        assertThat("0049 (123) 12345").matches(phonePattern);
        assertThat("+49 (123) 12345").matches(phonePattern);
    }

}
