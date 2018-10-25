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
package eu.ggnet.dwoss.customer.ee;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import javax.ejb.Remote;

import eu.ggnet.dwoss.customer.api.UiCustomer;
import eu.ggnet.dwoss.customer.ee.entity.Customer;
import eu.ggnet.dwoss.customer.ee.entity.dto.SimpleCustomer;
import eu.ggnet.dwoss.customer.ee.entity.projection.PicoCustomer;
import eu.ggnet.dwoss.util.persistence.RemoteAgent;
import eu.ggnet.saft.api.Reply;

import lombok.Value;

/**
 *
 * @author jens.papenhagen
 */
@Remote
public interface CustomerAgent extends RemoteAgent {

    /**
     * Idicates a root element for create or delete.
     */
    @Value
    public static class Root implements Serializable {

        private final Class<?> clazz;

        private final long id;
    }

    /**
     * Returns a list of customers, based on the search parameter, reduced by the fields.
     *
     * @param search         the search parameter
     * @param customerFields optional fileds to be only used.
     * @return a list of customers.
     */
    List<PicoCustomer> search(String search, Set<Customer.SearchField> customerFields);

    /**
     * Returns a list of customers, based on the search parameter, reduced by the fields and a start/limit
     *
     * @param search         search the search parameter
     * @param customerFields optional fileds to be only used.
     * @param start          the starting result
     * @param limit          the ending result
     * @return a list of customers.
     */
    List<PicoCustomer> search(String search, Set<Customer.SearchField> customerFields, int start, int limit);

    /**
     * More detailed search.
     * 
     * @param company the company field
     * @param firstName the firstname
     * @param lastName the lastname
     * @param email a email
     * @param appendWildcard append a wildcard to all strings
     * @return a list of customers found by the search
     */
    List<Customer> search(String company, String firstName, String lastName, String email, boolean appendWildcard);
    
    /**
     * Count the result of the search.
     *
     * @param search         search the search parameter
     * @param customerFields optional fileds to be only used.
     * @return the estimated amount for the search
     */
    int countSearch(String search, Set<Customer.SearchField> customerFields);

    /**
     * Stores a simple customer.
     *
     * @param simpleCustomer the simpleCustomer
     * @return returns a reply with the stored customer or empty with failure.
     */
    Reply<Customer> store(SimpleCustomer simpleCustomer);

    /**
     * Returns a html representation of the customer enhanced by the actual active mandator.
     *
     * @param id the id of the customer
     * @return a html representation of the customer enhanced by the actual active mandator.
     */
    String findCustomerAsMandatorHtml(long id);

    /**
     * Returns a full html representation of the customer.
     *
     * @param id the id of the customer
     * @return a html representation of the customer.
     */
    String findCustomerAsHtml(long id);

    /**
     * Create a new entity on the root element.
     *
     * @param <T> 
     * @param root
     * @param t
     * @return the stored object
     */
     <T> T create(Root root, T t);

    /**
     * Update an enitiy
     *
     * @param <T> 
     * @param t
     * @return the stored object
     */
    <T> T update(T t);

    /**
     * Delete an entity on the root element
     *
     * @param root
     * @param t
     */
    void delete(Root root, Object t);
    
    @Deprecated
    List<String> mergeCustomerAfterAddressLabel();
}
