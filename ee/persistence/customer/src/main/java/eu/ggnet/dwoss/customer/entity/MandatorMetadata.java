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
package eu.ggnet.dwoss.customer.entity;

import eu.ggnet.dwoss.rules.SalesChannel;
import eu.ggnet.dwoss.rules.PaymentMethod;
import eu.ggnet.dwoss.rules.ShippingCondition;
import eu.ggnet.dwoss.rules.PaymentCondition;

import java.io.Serializable;
import java.util.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import eu.ggnet.dwoss.mandator.api.value.DefaultCustomerSalesdata;

import lombok.*;

/**
 * Mandator Specific Metadata.
 * <p>
 * @author oliver.guenther
 */
@Entity
@ToString
@EqualsAndHashCode(of = "id")
public class MandatorMetadata implements Serializable {

    /**
     * Helper class to get e View of the metadata, filling all null values with the supplied defaults.
     */
    @Value
    public static class MergedView {

        private final MandatorMetadata metadata;

        private final DefaultCustomerSalesdata defaults;

        public ShippingCondition getShippingCondition() {
            if ( metadata == null || metadata.getShippingCondition() == null ) return defaults.getShippingCondition();
            return metadata.getShippingCondition();
        }

        public PaymentCondition getPaymentCondition() {
            if ( metadata == null || metadata.getPaymentCondition() == null ) return defaults.getPaymentCondition();
            return metadata.getPaymentCondition();
        }

        public PaymentMethod getPaymentMethod() {
            if ( metadata == null || metadata.getPaymentMethod() == null ) return defaults.getPaymentMethod();
            return metadata.getPaymentMethod();
        }

        public Set<SalesChannel> getAllowedSalesChannels() {
            if ( metadata == null || metadata.getAllowedSalesChannels().isEmpty() ) return defaults.getAllowedSalesChannels();
            return metadata.getAllowedSalesChannels();
        }

    }

    @Id
    @Getter
    @GeneratedValue
    private long id;

    @Version
    private short optLock;

    @NotNull
    @Getter
    @Setter
    private String mandatorMatchcode;

    /**
     * The default {@link ShippingCondition} of the customer.
     */
    @Setter
    @Getter
    @Enumerated
    private ShippingCondition shippingCondition;

    /**
     * The default {@link PaymentCondition} of the customer.
     */
    @Setter
    @Getter
    @Enumerated
    private PaymentCondition paymentCondition;

    /**
     * The default {@link PaymentMethod} of the customer.
     */
    @Setter
    @Getter
    @Enumerated
    private PaymentMethod paymentMethod;

    @NotNull
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<SalesChannel> allowedSalesChannels = new HashSet<>();

    public Set<SalesChannel> getAllowedSalesChannels() {
        return new HashSet<>(allowedSalesChannels);
    }

    public void clearSalesChannels() {
        allowedSalesChannels.clear();
    }

    public void add(SalesChannel s) {
        allowedSalesChannels.add(s);
    }

    /**
     * Returns true if at least on element is set.
     * <p>
     * @return true if at least on element is set.
     */
    public boolean isSet() {
        return shippingCondition != null || paymentCondition != null || paymentMethod != null || !allowedSalesChannels.isEmpty();
    }

}
