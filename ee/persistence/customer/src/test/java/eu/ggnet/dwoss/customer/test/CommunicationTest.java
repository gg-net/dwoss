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

import org.junit.*;

import eu.ggnet.dwoss.customer.ee.entity.Communication;
import eu.ggnet.dwoss.customer.ee.entity.Communication.Type;

import static eu.ggnet.dwoss.customer.ee.entity.Communication.Type.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author jens.papenhagen
 */
public class CommunicationTest {
    
    private Communication communication;
    
    @Before
    public void executedBeforeEach() {
        communication = new Communication();
        communication.setType(SKYPE);
        communication.setIdentifier("skypename");
    }
    
    
    @Test
    public void GetViolationMessages(){
        assertThat(communication.getViolationMessage()).as("Communication with valid values").isNull();
    }
    
    
    @Test
    public void GetViolationMessagesForEMail(){
        communication.setType(EMAIL);
        communication.setIdentifier("test@test.de");
        assertThat(communication.getViolationMessage()).as("Communication have a vaild E-Mail").isNull();
    }
    
    @Test
    public void GetViolationMessagesForPhone(){
        communication.setType(FAX);
        communication.setIdentifier("0401234567");
        assertThat(communication.getViolationMessage()).as("Communication with valid Phonenumber").isNull();
    }
    
    
    @Test
    public void GetNonViolationMessagesForEMail(){
        communication.setType(EMAIL);
        communication.setIdentifier("falscheemail@@test.de");
        assertThat(communication.getViolationMessage()).as("Communication with nonvalid E-Mail").isNotBlank();
    }
    
    @Test
    public void GetNonViolationMessagesForPhone(){
        communication.setType(Type.FAX);
        communication.setIdentifier("0123586Buchstaben");
        assertThat(communication.getViolationMessage()).as("Communication with nonvalid Phonenumber").isNotBlank();
    }
    
}
