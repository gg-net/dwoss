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
package eu.ggnet.dwoss.customer.opi;

import eu.ggnet.dwoss.rules.AddressType;

import javax.ejb.Remote;

import eu.ggnet.dwoss.event.AddressChange;

/**
 *
 * @author pascal.perau
 */
@Remote
public interface AddressService {

    /**
     * Returns the default address label of customer and type.
     * <p>
     * @param customerId the customerId
     * @param type       the type of label
     * @return the default address label of customer and type.
     */
    String defaultAddressLabel(long customerId, AddressType type);

    /**
     * Sends a Server based notification about a addresschange of the customer.
     * <p>
     * @param changeEvent object containing all adress changeing information
     */
    void notifyAddressChange(AddressChange changeEvent);

}
