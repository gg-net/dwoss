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

import eu.ggnet.dwoss.customer.ee.assist.gen.CustomerGenerator;
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
    
    private final CustomerGenerator GEN = new CustomerGenerator();

    @Before
    public void executedBeforeEach() {
        communication = GEN.makeCommunication();
    }
    
    
    @Test
    public void GetViolationMessages(){
        communication.setType(SKYPE);
        assertThat(communication.getViolationMessages()).as("Communication with valid values").isNull();
    }
    
    
    @Test
    public void GetViolationMessagesForEMail(){
        communication.setType(EMAIL);
        communication.setIdentifier("test@test.de");
        assertThat(communication.getViolationMessages()).as("Communication have a vaild E-Mail").isNull();
    }
    
    @Test
    public void GetViolationMessagesForPhone(){
        communication.setType(FAX);
        communication.setIdentifier("0401234567");
        assertThat(communication.getViolationMessages()).as("Communication with valid Phonenumber").isNull();
    }
    
    
    @Test
    public void GetNonViolationMessagesForEMail(){
        communication.setType(EMAIL);
        communication.setIdentifier("falscheemail@@test.de");
        assertThat(communication.getViolationMessages()).as("Communication with nonvalid E-Mail").isNotBlank();
    }
    
    @Test
    public void GetNonViolationMessagesForPhone(){
        communication.setType(Type.FAX);
        communication.setIdentifier("0123586Buchstaben");
        assertThat(communication.getViolationMessages()).as("Communication with nonvalid Phonenumber").isNotBlank();
    }
    
}
