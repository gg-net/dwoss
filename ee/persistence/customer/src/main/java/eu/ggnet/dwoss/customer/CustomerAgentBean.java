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
package eu.ggnet.dwoss.customer;

import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import eu.ggnet.dwoss.customer.assist.Customers;
import eu.ggnet.dwoss.customer.eao.CustomerEao;
import eu.ggnet.dwoss.customer.entity.Customer;
import eu.ggnet.dwoss.customer.entity.Customer.SearchField;
import eu.ggnet.dwoss.util.persistence.AbstractAgentBean;

/**
 *
 * @author jens.papenhagen
 */
@Stateless
public class CustomerAgentBean extends AbstractAgentBean implements CustomerAgent {

    @Inject
    @Customers
    private EntityManager em;

    @Inject
    private CustomerEao eao;
    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public List<Customer> search(String search, Set<SearchField> customerFields) {
        return optionalFetchEager(eao.find(search));        
    }

    @Override
    public List<Customer> search(String search, Set<SearchField> customerFields, int start, int limit) {
        return optionalFetchEager(eao.find(search,start,limit));        
    }

    @Override
    public int countSearch(String search, Set<SearchField> customerFields) {
        return eao.countFind(search);
    }

}
