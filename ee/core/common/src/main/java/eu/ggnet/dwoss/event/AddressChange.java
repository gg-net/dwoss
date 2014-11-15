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
package eu.ggnet.dwoss.event;

import java.io.Serializable;

import eu.ggnet.dwoss.rules.AddressType;

import lombok.Value;

/**
 * Adress change event.
 * <p>
 * @author pascal.perau
 */
@Value
public class AddressChange implements Serializable{

    /**
     * Identifier of the customer.
     */
    private final long customerId;

    /**
     * The arranger of the change.
     */
    private final String arranger;

    /**
     * Type of the address.
     */
    private final AddressType type;

    /**
     * The old address.
     */
    private final String oldAdress;

    /**
     * The new address.
     */
    private final String newAdress;
}
