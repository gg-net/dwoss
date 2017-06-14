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

import javax.ejb.Remote;

import eu.ggnet.dwoss.customer.entity.Customer;

/**
 * Agent for the Old Customer implementation.
 * <p>
 * @author oliver.guenther
 */
@Remote
public interface OldCustomerAgent {

    /**
     * Returns a {@link Customer} wrapped into an old one by id.
     * <p>
     * @param id the id
     * @return a old customer.
     */
    OldCustomer findById(long id);

    /**
     * Unwraps the old customer and store it as a {@link Customer} either persisting ore merging him.
     * <p>
     * @param old the old customer
     * @return the old customer after storage, if persisted, containing the new id.
     */
    OldCustomer store(OldCustomer old);

}
