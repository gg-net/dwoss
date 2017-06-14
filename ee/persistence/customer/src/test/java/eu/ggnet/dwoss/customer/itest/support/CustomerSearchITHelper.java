/*
 * Copyright (C) 2017 GG-Net GmbH
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
package eu.ggnet.dwoss.customer.itest.support;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import eu.ggnet.dwoss.customer.assist.Customers;
import eu.ggnet.dwoss.customer.eao.CustomerEao;
import eu.ggnet.dwoss.customer.entity.Customer;
import eu.ggnet.dwoss.customer.priv.ConverterUtil;
import eu.ggnet.dwoss.customer.priv.OldCustomer;
import eu.ggnet.dwoss.mandator.api.value.DefaultCustomerSalesdata;
import eu.ggnet.dwoss.mandator.api.value.Mandator;

/**
 *
 * @author olive
 */
@Stateless
public class CustomerSearchITHelper {

    @Inject
    @Customers
    private EntityManager em;

    @Inject
    private Mandator mandator;

    @Inject
    private CustomerEao eao;

    @Inject
    private DefaultCustomerSalesdata salesData;

    public void persist(OldCustomer old) {
        Customer customer = new Customer();
        ConverterUtil.mergeFromOld(old, customer, mandator.getMatchCode(), salesData);
        em.persist(customer);
    }

    public List<Customer> testLucenceSearch(String s) throws InterruptedException {
        List<Customer> find = eao.find(s);
        for (Customer customer : find) {
            customer.toMultiLine();
        }
        return find;
    }

}
