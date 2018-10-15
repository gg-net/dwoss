/*
 * Copyright (C) 2014 GG-Net GmbH - Oliver Günther
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

import java.util.function.Consumer;

import org.openide.util.lookup.ServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.customer.ee.CustomerAgent;
import eu.ggnet.dwoss.customer.ee.entity.Customer;
import eu.ggnet.dwoss.customer.ui.neo.*;
import eu.ggnet.dwoss.customer.upi.CustomerUpi;
import eu.ggnet.saft.core.Dl;
import eu.ggnet.saft.core.ui.UiParent;

/**
 *
 * @author pascal.perau
 */
@ServiceProvider(service = CustomerUpi.class)
public class NeoCustomerUpi implements CustomerUpi {

    private static final Logger L = LoggerFactory.getLogger(NeoCustomerUpi.class);

    @Override
    public void createCustomer(UiParent parent, Consumer<Long> csmr) {
        CustomerConnectorFascade.selectOrEdit(parent, csmr);
    }

    @Override
    public boolean updateCustomer(UiParent parent, long customerId) {
        Customer customer = Dl.remote().lookup(CustomerAgent.class).findByIdEager(Customer.class, customerId);
        if ( customer == null ) return false;

        CustomerConnectorFascade.edit(customer,parent);
        // TODO: Change Result also to a consumer pattern. See first if the result is needed.
        return false;
    }
}
