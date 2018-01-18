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

import javax.ejb.Remote;

import eu.ggnet.dwoss.customer.entity.Customer;
import eu.ggnet.dwoss.util.persistence.RemoteAgent;

/**
 *
 * @author jens.papenhagen
 */
@Remote
public interface CustomerAgent extends RemoteAgent {

    /**
     * Returns a list of customers, based on the search parameter, reduced by the fields.
     * 
     * @param search the search parameter
     * @param customerFields optional fileds to be only used.
     * @return a list of customers.
     */
    List<Customer> search(String search, Set<Customer.SearchField> customerFields);


    /**
     * Returns a list of customers, based on the search parameter, reduced by the fields and a start/limit
     * 
     * @param search search the search parameter
     * @param customerFields optional fileds to be only used.
     * @param start the starting result
     * @param limit the ending result
     * @return a list of customers.
     */
    List<Customer> search(String search, Set<Customer.SearchField> customerFields, int start, int limit);
    
    
    /**
     * Count the result of the search.
     * 
     * @param search search the search parameter
     * @param customerFields optional fileds to be only used.
     * @return the estimated amount for the search
     */
    int countSearch(String search, Set<Customer.SearchField> customerFields);

    
}
