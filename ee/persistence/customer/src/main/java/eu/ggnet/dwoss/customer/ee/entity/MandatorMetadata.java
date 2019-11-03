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

import eu.ggnet.dwoss.core.common.values.SalesChannel;
import eu.ggnet.dwoss.core.common.values.ShippingCondition;
import eu.ggnet.dwoss.core.common.values.PaymentCondition;
import eu.ggnet.dwoss.core.common.values.PaymentMethod;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ToStringBuilder;

import eu.ggnet.dwoss.core.system.persistence.BaseEntity;
import eu.ggnet.dwoss.mandator.api.value.DefaultCustomerSalesdata;

/**
 * Mandator Specific Metadata.
 * <p>
 * @author oliver.guenther
 */
@Entity
@SuppressWarnings("PersistenceUnitPresent")
public class MandatorMetadata extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue
    private long id;

    @Version
    private short optLock;

    @NotNull
    private String mandatorMatchcode;

    /**
     * The default {@link ShippingCondition} of the customer.
     */
    @Enumerated
    private ShippingCondition shippingCondition;

    /**
     * The default {@link PaymentCondition} of the customer.
     */
    @Enumerated
    private PaymentCondition paymentCondition;

    /**
     * The default {@link PaymentMethod} of the customer.
     */
    @Enumerated
    private PaymentMethod paymentMethod;

    @NotNull
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<SalesChannel> allowedSalesChannels = new HashSet<>();

    public MandatorMetadata(String mandatorMatchcode) {
        this.mandatorMatchcode = mandatorMatchcode;
    }

    public MandatorMetadata() {
    }

    //<editor-fold defaultstate="collapsed" desc="getter/setter">
    public String getMandatorMatchcode() {
        return mandatorMatchcode;
    }
    
    public void setMandatorMatchcode(String mandatorMatchcode) {
        this.mandatorMatchcode = mandatorMatchcode;
    }
    
    public ShippingCondition getShippingCondition() {
        return shippingCondition;
    }
    
    public void setShippingCondition(ShippingCondition shippingCondition) {
        this.shippingCondition = shippingCondition;
    }
    
    public PaymentCondition getPaymentCondition() {
        return paymentCondition;
    }
    
    public void setPaymentCondition(PaymentCondition paymentCondition) {
        this.paymentCondition = paymentCondition;
    }
    
    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }
    
    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    @Override
    public long getId() {
        return id;
    }
    
    public short getOptLock() {
        return optLock;
    }
    
    public Set<SalesChannel> getAllowedSalesChannels() {
        return allowedSalesChannels;
    }
    //</editor-fold>

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
    public boolean isSameAs(DefaultCustomerSalesdata defaultCsd) {
        Objects.requireNonNull(defaultCsd,"DefaultCustomerSalesdata must not be null");
        if ( !defaultCsd.allowedSalesChannels().equals(getAllowedSalesChannels()) ) return false;
        if ( getPaymentCondition() != null && defaultCsd.paymentCondition() != getPaymentCondition() ) return false;
        if ( getPaymentMethod() != null && defaultCsd.paymentMethod() != getPaymentMethod() ) return false;
        if ( getShippingCondition() != null && defaultCsd.shippingCondition() != getShippingCondition() ) return false;
        return true;
    }

    /**
     * Normalize the MandatorMetadata, setting all defaults to null.
     *
     * @param defaultCsd
     */
    public void normalize(DefaultCustomerSalesdata defaultCsd) {
        Objects.requireNonNull(defaultCsd,"DefaultCustomerSalesdata must not be null");
        if ( defaultCsd.allowedSalesChannels().equals(getAllowedSalesChannels()) ) getAllowedSalesChannels().clear();
        if ( defaultCsd.paymentCondition() == getPaymentCondition() ) setPaymentCondition(null);
        if ( defaultCsd.paymentMethod() == getPaymentMethod() ) setPaymentMethod(null);
        if ( defaultCsd.shippingCondition() == getShippingCondition() ) setShippingCondition(null);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
