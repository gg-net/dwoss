/* 
 * Copyright (C) 2014 pascal.perau
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
package eu.ggnet.dwoss.mandator.api.value;

import java.io.Serializable;
import java.util.*;

import javax.validation.constraints.NotNull;

import eu.ggnet.dwoss.rules.*;
import eu.ggnet.dwoss.util.validation.ValidationUtil;

import lombok.*;
import lombok.experimental.Builder;

/**
 * Mandator Specific Metadata.
 * <p>
 * @author oliver.guenther
 */
@Value
public class DefaultCustomerSalesdata implements Serializable {

    @Builder
    public DefaultCustomerSalesdata(ShippingCondition shippingCondition, PaymentCondition paymentCondition, PaymentMethod paymentMethod, Collection<SalesChannel> allowedSalesChannels, Collection<Long> viewOnlyCustomerIds) {
        this.shippingCondition = shippingCondition;
        this.paymentCondition = paymentCondition;
        this.paymentMethod = paymentMethod;
        this.allowedSalesChannels = Collections.unmodifiableNavigableSet(allowedSalesChannels == null ? new TreeSet<>() : new TreeSet<>(allowedSalesChannels));
        this.viewOnlyCustomerIds = Collections.unmodifiableNavigableSet(viewOnlyCustomerIds == null ? new TreeSet<>() : new TreeSet<>(viewOnlyCustomerIds));
        ValidationUtil.validate(this);
    }

    /**
     * The default {@link ShippingCondition} of the Mandator.
     */
    @NotNull
    private final ShippingCondition shippingCondition;

    /**
     * The default {@link PaymentCondition} of the Mandator.
     */
    @NotNull
    private final PaymentCondition paymentCondition;

    /**
     * The default {@link PaymentMethod} of the Mandator.
     */
    @NotNull
    private final PaymentMethod paymentMethod;

    @NotNull
    private final NavigableSet<SalesChannel> allowedSalesChannels;

    @NotNull
    private final NavigableSet<Long> viewOnlyCustomerIds;
}
