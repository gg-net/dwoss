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
package eu.ggnet.dwoss.customer.ui.neo;

import eu.ggnet.dwoss.customer.ee.entity.Customer;
import eu.ggnet.dwoss.customer.ee.entity.dto.SimpleCustomer;

/**
 * A command for continues usage in completable future.
 *
 * @author oliver.guenther
 */
public class CustomerCommand {

    public final SimpleCustomer simpleCustomer;

    public final Customer customer;

    public final boolean simpleStore;

    public final boolean enhance;

    /**
     * Default constructor, either simpleCustomer or customer can be null
     * 
     * @param simpleCustomer
     * @param customer
     * @param simpleStore
     * @param enhance 
     */
    public CustomerCommand(SimpleCustomer simpleCustomer, Customer customer, boolean simpleStore, boolean enhance) {
        this.simpleCustomer = simpleCustomer;
        this.customer = customer;
        this.simpleStore = simpleStore;
        this.enhance = enhance;
    }
    
    public static CustomerCommand select(Customer cu) {
        return new CustomerCommand(null, cu, false, false);
    }
    
    public static CustomerCommand enhance(Customer cu) {
        return new CustomerCommand(null, cu, false, true);
    }

    public static CustomerCommand store(SimpleCustomer sc) {
        return new CustomerCommand(sc, null, true, false);
    }

    public static CustomerCommand storeAndEnhance(SimpleCustomer sc) {
        return new CustomerCommand(sc, null, true, true);
    }

}
