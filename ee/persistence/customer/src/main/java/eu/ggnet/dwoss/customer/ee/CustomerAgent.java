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
import java.util.*;

import jakarta.ejb.Remote;

import eu.ggnet.dwoss.core.system.persistence.RemoteAgent;
import eu.ggnet.dwoss.customer.ee.entity.Customer;
import eu.ggnet.dwoss.customer.ee.entity.MandatorMetadata;
import eu.ggnet.dwoss.customer.ee.entity.dto.AddressLabelDto;
import eu.ggnet.dwoss.customer.ee.entity.dto.SimpleCustomer;
import eu.ggnet.dwoss.customer.ee.entity.projection.PicoCustomer;

/**
 *
 * @author jens.papenhagen
 */
@Remote
public interface CustomerAgent extends RemoteAgent {

    /**
     * Idicates a root element for create or delete.
     */
    public static class Root implements Serializable {

        public final Class<?> clazz;

        public final long id;

        public Root(Class<?> clazz, long id) {
            this.clazz = Objects.requireNonNull(clazz, "clazz must not be null");
            this.id = id;
        }

        @Override
        public String toString() {
            return "Root{" + "clazz=" + clazz + ", id=" + id + '}';
        }

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
     * @param company        the company field
     * @param firstName      the firstname
     * @param lastName       the lastname
     * @param email          a email
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
     * @throws IllegalArgumentException if customer is invalid.
     */
    Customer store(SimpleCustomer simpleCustomer) throws IllegalArgumentException;

    /**
     * Stores the addresslabels on the customer, all addresslabels must be from one customer.
     * Creating all labels with an id == 0. updateing all with an id <> 0. Deleting all that are missing.
     *
     * @param aldtos
     * @return
     * @throws IllegalArgumentException if the collection is empty.
     */
    Customer autostore(Collection<AddressLabelDto> aldtos) throws IllegalArgumentException;

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
     * Create, Update or even delete MandatorMetadata based on the difference between the mandator defaults.
     * If the MandatorMetadata is equal to the defaults, it will not be stored and if it exists, it will be deleted.
     * If the it differs it becomes normalized and than stored.
     *
     * @param customerId the customer of this metadata
     * @param mm         the mandator metadata.
     * @return the updated customer.
     */
    Customer normalizedStoreMandatorMetadata(long customerId, MandatorMetadata mm);

    /**
     * Delete an entity on the root element
     *
     * @param root
     * @param t
     */
    void delete(Root root, Object t);

    /**
     * Set {@link Customer#defaultEmailCommunication } to null.
     *
     * @param customerid the customer to be manipulated
     * @return returns the updated customer
     */
    Customer clearDefaultEmailCommunication(long customerid);

    /**
     * Set {@link Customer#defaultEmailCommunication } to the supplied communication.
     *
     * @param customerId      the customer to be manipulated
     * @param communicationId the communication, that should be set. Must be on the same customer and of type email.
     * @return the updated customer
     */
    Customer setDefaultEmailCommunication(long customerId, long communicationId);

    /**
     * Removes the default reseller list email communication.
     *
     * @param customerId the customer id.
     * @return the updated customer.
     */
    public Customer clearResellerListEmailCommunication(long customerId);

    /**
     * Updates the reseller list email communication with id.
     *
     * @param customerId      the customer id.
     * @param communicationId the id of the communication to use.
     * @return the updated customer
     */
    public Customer setResellerListEmailCommunication(long customerId, long communicationId);

    /**
     * Returns all customers which have a resellerlistcommunication set.
     *
     * @return customers
     */
    public List<Customer> findAllResellerListCustomersEager();
}
