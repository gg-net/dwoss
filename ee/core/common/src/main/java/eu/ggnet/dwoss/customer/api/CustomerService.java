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
package eu.ggnet.dwoss.customer.api;

import java.util.List;
import java.util.Set;

import javax.ejb.Remote;

import eu.ggnet.dwoss.rules.CustomerFlag;

/**
 *
 * @author pascal.perau
 */
@Remote
public interface CustomerService {

    /**
     * Method that collects/aggregates costumer purchase condition information in a wrapper class used in the user interfaces;
     * <p>
     * @param customerId customer identifier.
     * @return costumer information in a {@link CustomerMetaData} used in user interfaces;
     * @see CustomerMetaData
     */
    CustomerMetaData asCustomerMetaData(long customerId);

    /**
     * Method that collects/aggregates all costumers purchase condition information in a wrapper class used in the user interfaces;
     * <p>
     * @return list of all costumers information in a {@link CustomerMetaData} used in user interfaces;
     * @see CustomerMetaData
     */
    List<CustomerMetaData> allAsCustomerMetaData();

    /**
     * Method that collects/aggregates costumer information in a wrapper class used in user interfaces;
     * <p>
     * Data is trimmed to a simple form.
     * <p>
     * @param customerId customer identifier.
     * @return costumer information in a {@link UiCustomer} used in user interfaces;
     * @see UiCustomer
     */
    UiCustomer asUiCustomer(long customerId);

    /**
     * Method that collects/aggregates customer information in a wrapper class used in user interfaces;
     * <p>
     * Data is trimmed to a simple form.
     * <p>
     * @param search the search indicator
     * @return a list of costumer information in {@link UiCustomer}<code>s</code> used in user interfaces;
     * @see UiCustomer
     */
    List<UiCustomer> asUiCustomers(String search);

    /**
     * Method that collects/aggregates customer information in a wrapper class used in user interfaces;
     * <p>
     * Data is trimmed to a simple form.
     * <p>
     * @param company        company to search for
     * @param firstName      customers first name to search for
     * @param lastName       customers last name to search for
     * @param email          customers email to search for
     * @param appendWildcard shall a wildcard be appended to the searched fields
     * @return a list of costumer information in {@link UiCustomer}<code>s</code> used in user interfaces;
     * @see UiCustomer
     */
    List<UiCustomer> asUiCustomers(String company, String firstName, String lastName, String email, boolean appendWildcard);

    /**
     * Method that returns a highly detailed, html formated string with most customer information.
     * <p>
     * @param id customer identifier
     * @return a highly detailed, html formated string with most customer information.
     */
    String asHtmlHighDetailed(long id);

    /**
     * Returns the comment (former Anmerkung) of the Customer or null if not found.
     * <p>
     * @param id the customerId
     * @return the comment of the Customer or null if not found.
     */
    String findComment(long id);

    /**
     * Update the {@link CustomerFlag}<code>s</code> from a customer.
     * <p>
     * @param customerId the customers identifier
     * @param flags      flags that may have changed
     */
    void updateCustomerFlags(long customerId, Set<CustomerFlag> flags);

    /**
     * Returns all Customer Ids, which have the Flag SystemCustomer.
     * <p>
     * @return all Customer Ids, which have the Flag SystemCustomer.
     */
    List<Long> allSystemCustomerIds();
}
