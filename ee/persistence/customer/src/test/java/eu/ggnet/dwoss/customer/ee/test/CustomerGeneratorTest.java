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

import org.junit.Test;

import eu.ggnet.dwoss.customer.ee.assist.gen.CustomerGenerator;
import eu.ggnet.dwoss.customer.ee.entity.Customer;
import eu.ggnet.dwoss.customer.ee.make.StaticCustomerMaker;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author oliver.guenther
 */
public class CustomerGeneratorTest {
    
    private final static CustomerGenerator GEN = new CustomerGenerator();
   
    @Test
    public void makeSimpleConsumerCustomers() {
        for (int i = 0; i < 500; i++) {
            Customer c = StaticCustomerMaker.makeValidSimpleConsumer();
            assertThat(c.isSimple()).as("SimpleViolations:" + c.getSimpleViolationMessage()).isTrue();
            assertThat(c.isConsumer()).isTrue();            
        }
    
    }
    
    @Test
    public void makeSimpleBussinesCustomers() {
        for (int i = 0; i < 500; i++) {
            Customer c = StaticCustomerMaker.makeValidSimpleBusiness();
            assertThat(c.isSimple()).as("SimpleViolations:" + c.getSimpleViolationMessage()).isTrue();
            assertThat(c.isBusiness()).isTrue();            
        }
    
    }
}
