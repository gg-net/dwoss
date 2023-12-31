/*
 * Copyright (C) 2020 GG-Net GmbH
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

import jakarta.ejb.Local;

/**
 * Local Customer API.
 *
 * @author oliver.guenther
 */
@Local
public interface CustomerApiLocal {

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

}
