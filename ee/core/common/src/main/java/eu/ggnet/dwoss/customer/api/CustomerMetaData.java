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
import java.util.Set;

import eu.ggnet.dwoss.rules.*;

import lombok.*;

/**
 * Wrapper class for purchase information from a customer.
 * <p>
 * @author pascal.perau
 */
// HINT: This Name is still open for debate.
@Value
public class CustomerMetaData implements Serializable {

    /**
     * Customer identifier.
     */
    private final long id;

    /**
     * Email information from the customer.
     */
    private final String email;

    /**
     * {@link PaymentCondition} on wich the customer buys.
     */
    private final PaymentCondition paymentCondition;

    /**
     * {@link PaymentMethod} on wich the customer buys.
     */
    private final PaymentMethod paymentMethod;

    /**
     * {@link ShippingCondition} on wich the customer buys.
     */
    private final ShippingCondition shippingCondition;

    /**
     * {@link CustomerFlag}<code>s</code> of the customer.
     */
    private final Set<CustomerFlag> flags;

    /**
     * Allowed {@link SalesChannel} for the customer.
     */
    private final Set<SalesChannel> allowedSalesChannel;

}
