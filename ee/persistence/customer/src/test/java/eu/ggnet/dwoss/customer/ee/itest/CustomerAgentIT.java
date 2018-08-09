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


import javax.ejb.EJB;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.customer.ee.CustomerAgent;
import eu.ggnet.dwoss.customer.ee.itest.support.ArquillianProjectArchive;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author jens.papenhagen
 */
@RunWith(Arquillian.class)
public class CustomerAgentIT extends ArquillianProjectArchive {

    @EJB
    private CustomerAgent customerAgent;

    @Test
    public void testFindCustomerAsMandatorHtml() {
        String feedback = "Kein Kunde mit id 123 vorhanden";
        String findCustomerAsMandatorHtml = customerAgent.findCustomerAsMandatorHtml(123);
        assertThat(findCustomerAsMandatorHtml).as("give back the Error Message").isEqualToIgnoringCase(feedback);
    }

    @Test
    public void testFindCustomerAsHtml() {
        String feedback = "Kein Kunde mit id 123 vorhanden";
        String findCustomerAsHtml = customerAgent.findCustomerAsHtml(123);
        assertThat(findCustomerAsHtml).as("give back the Error Message").isEqualToIgnoringCase(feedback);
    }

}
