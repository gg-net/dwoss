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

import java.io.Serializable;
import java.util.Objects;

import eu.ggnet.dwoss.core.common.values.AddressType;

/**
 * Adress change event.
 * <p>
 * @author pascal.perau
 */
public class AddressChange implements Serializable {

    /**
     * Identifier of the customer.
     */
    public final long customerId;

    /**
     * The arranger of the change.
     */
    public final String arranger;

    /**
     * Type of the address.
     */
    public final AddressType type;

    /**
     * The old address.
     */
    public final String oldAddress;

    /**
     * The new address.
     */
    public final String newAddress;

    /**
     * All Args Constructor. All values must not be null.
     * 
     * @param customerId the customerid
     * @param arranger the arranger
     * @param type the type
     * @param oldAddress the old address
     * @param newAddress the new address
     */
    public AddressChange(long customerId, String arranger, AddressType type, String oldAddress, String newAddress) {
        this.customerId = customerId;
        this.arranger = Objects.requireNonNull(arranger,"arranger must not be null");
        this.type = Objects.requireNonNull(type,"arranger must not be null");
        this.oldAddress = Objects.requireNonNull(oldAddress,"oldAddress must not be null");
        this.newAddress = Objects.requireNonNull(newAddress,"newAddress must not be null");
    }

    @Override
    public String toString() {
        return "AddressChange{" + "customerId=" + customerId + ", arranger=" + arranger + ", type=" + type + ", oldAdress=" + oldAddress + ", newAdress=" + newAddress + '}';
    }
    
}
