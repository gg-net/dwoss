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

import eu.ggnet.dwoss.common.api.values.*;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Wrapper class for purchase information from a customer.
 * <p>
 * @author pascal.perau
 */
// HINT: This Name is still open for debate.
@Data // Wildfly 15 cannot deserialize final collection or @Value.
@AllArgsConstructor
public class CustomerMetaData implements Serializable {

    /**
     * Customer identifier.
     */
    private long id;

    /**
     * {@link PaymentCondition} on wich the customer buys.
     */
    private PaymentCondition paymentCondition;

    /**
     * {@link PaymentMethod} on wich the customer buys.
     */
    private PaymentMethod paymentMethod;

    /**
     * {@link ShippingCondition} on wich the customer buys.
     */
    private ShippingCondition shippingCondition;

    /**
     * {@link CustomerFlag}<code>s</code> of the customer.
     */
    private Set<CustomerFlag> flags;

    /**
     * Allowed {@link SalesChannel} for the customer.
     */
    private Set<SalesChannel> allowedSalesChannel;

    /**
     * Contains a violation info, if the customer is not valid based on the entity model (Caused by a migration in 2018).
     */
    private String violationMessage;
}
