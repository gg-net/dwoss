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
package eu.ggnet.dwoss.customer.priv;

import eu.ggnet.dwoss.mandator.api.value.DefaultCustomerSalesdata;
import eu.ggnet.dwoss.mandator.api.value.Mandator;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.*;

import org.slf4j.*;

import eu.ggnet.dwoss.customer.entity.Customer;

import eu.ggnet.dwoss.customer.assist.Customers;

import static eu.ggnet.dwoss.customer.priv.ConverterUtil.mergeFromOld;

/**
 * Implementation to suplly customer data in the form of the old customer.
 * <p>
 * @author oliver.guenther
 */
@Stateless
public class OldCustomerAgentBean implements OldCustomerAgent {

    private static final Logger LOG = LoggerFactory.getLogger(OldCustomerAgentBean.class);

    @Inject
    @Customers
    private EntityManager customerEm;

    @Inject
    private Mandator mandator;

    @Inject
    private DefaultCustomerSalesdata salesData;

    @Override
    public OldCustomer store(OldCustomer old) {
        if ( old == null ) return null;
        Customer customer = new Customer();
        if ( old.getId() > 0 ) customer = customerEm.find(Customer.class, (long)old.getId());
        mergeFromOld(old, customer, mandator.getMatchCode(), salesData);
        if ( old.getId() == 0 ) customerEm.persist(customer);
        return ConverterUtil.convert(customer, mandator.getMatchCode(), salesData);
    }

    @Override
    public OldCustomer findById(long id) {
        return ConverterUtil.convert(customerEm.find(Customer.class, id), mandator.getMatchCode(), salesData);
    }
}
