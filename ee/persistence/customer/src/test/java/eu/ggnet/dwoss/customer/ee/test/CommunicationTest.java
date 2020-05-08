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

import java.util.Arrays;
import java.util.List;

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
    public void validPhoneNumber() {
        Communication makeValidCommunication = makeValidCommunication();
        makeValidCommunication.setType(PHONE);
        makeValidCommunication.setIdentifier("040 1234567");
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
    public void phoneNumberPatternValidation() {
        for (String n : validPhoneNumbers()) {
            assertThat(n).as("Value " + n + " should match Communication.PHONE_PATTERN").matches(Communication.PHONE_PATTERN);
        }

        for (String n : invalidPhoneNumbers()) {
            assertThat(n).as("Value " + n + " should not match Communication.PHONE_PATTERN").doesNotMatch(Communication.PHONE_PATTERN);
        }
    }

    private List<String> validPhoneNumbers() {
        return Arrays.asList("+49 (123) 12345", "0049 123 12345", "0123 12345", "012 345");
    }

    private List<String> invalidPhoneNumbers() {
        return Arrays.asList("  012 345", "  012 345  ", "123 12345", "++233 21", "+43 0100 321321", "31231a1321");
    }

}
