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
package eu.ggnet.dwoss.customer.ee.entity;

import eu.ggnet.dwoss.common.api.values.PaymentCondition;
import eu.ggnet.dwoss.common.api.values.PaymentMethod;
import eu.ggnet.dwoss.common.api.values.ShippingCondition;
import eu.ggnet.dwoss.common.api.values.SalesChannel;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

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

    @Getter
    @NotNull
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<SalesChannel> allowedSalesChannels = new HashSet<>();

    public MandatorMetadata(String mandatorMatchcode) {
        this.mandatorMatchcode = mandatorMatchcode;
    }

    public MandatorMetadata() {
    }

    /**
     * Returns true if at least on element is set.
     * <p>
     * @return true if at least on element is set.
     */
    public boolean isSet() {
        return shippingCondition != null || paymentCondition != null || paymentMethod != null || !allowedSalesChannels.isEmpty();
    }

    /**
     * Html representation of the class.
     *
     * @return a html string
     */
    public String toHtml() {
        if ( !isSet() ) return "No Mandator Metadata";
        return "Mandant: " + mandatorMatchcode
                + "<ul>"
                + (shippingCondition == null ? "" : "<li>Versandkonditionen:" + shippingCondition.getName() + "</li>")
                + (paymentCondition == null ? "" : "<li>Zahlungskonditionen:" + paymentCondition.getNote() + "</li>")
                + (paymentMethod == null ? "" : "<li>Zahlungsmodalität:" + paymentMethod.getNote() + "</li>")
                + (allowedSalesChannels.isEmpty() ? "" : "<li>Erlaubte Verkaufskanäle:" + allowedSalesChannels.stream().map(SalesChannel::getName).collect(Collectors.toList()) + "</li>")
                + "</ul>";
    }

    /**
     * Content equals test which imply null is true.
     *
     * @param defaultCsd the defaults
     * @return ture if contents equal or cmd is null.
     */
    public boolean isSameAs(@NonNull DefaultCustomerSalesdata defaultCsd) {
        if ( !defaultCsd.getAllowedSalesChannels().equals(getAllowedSalesChannels()) ) return false;
        if ( getPaymentCondition() != null && defaultCsd.getPaymentCondition() != getPaymentCondition() ) return false;
        if ( getPaymentMethod() != null && defaultCsd.getPaymentMethod() != getPaymentMethod() ) return false;
        if ( getShippingCondition() != null && defaultCsd.getShippingCondition() != getShippingCondition() ) return false;
        return true;
    }

    /**
     * Normalize the MandatorMetadata, setting all defaults to null.
     *
     * @param defaultCsd
     */
    public void normalize(@NonNull DefaultCustomerSalesdata defaultCsd) {
        if ( defaultCsd.getAllowedSalesChannels().equals(getAllowedSalesChannels()) ) getAllowedSalesChannels().clear();
        if ( defaultCsd.getPaymentCondition() == getPaymentCondition() ) setPaymentCondition(null);
        if ( defaultCsd.getPaymentMethod() == getPaymentMethod() ) setPaymentMethod(null);
        if ( defaultCsd.getShippingCondition() == getShippingCondition() ) setShippingCondition(null);
    }

}
