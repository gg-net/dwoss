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
package eu.ggnet.dwoss.redtapext.ee.state;

import eu.ggnet.dwoss.core.common.values.CustomerFlag;
import eu.ggnet.dwoss.core.common.values.ShippingCondition;
import eu.ggnet.dwoss.core.common.values.PaymentMethod;

import java.io.Serializable;
import java.util.*;

import eu.ggnet.dwoss.redtape.ee.entity.Document;

/**
 * Basic input Object for the RedTapeStateCharacteristikFactory, uses some filters.
 * Only the following CustomerFlags are used:
 * <ul>
 * <li>{@link CustomerFlag#CONFIRMS_DOSSIER</li>
 * <li>{@link CustomerFlag#CONFIRMED_CASH_ON_DELIVERY}</li>
 * <li>{@link CustomerFlag#SYSTEM_CUSTOMER</li>
 * <p>
 * @author oliver.guenther
 */
// TODO: Freebuilder me please.
public class CustomerDocument implements Serializable {

    private final Set<CustomerFlag> customerFlags;

    private final Document document;

    private final ShippingCondition shippingCondition;

    private final PaymentMethod paymentMethod;

    public CustomerDocument(Set<CustomerFlag> customerFlags, Document document, ShippingCondition shippingCondition, PaymentMethod paymentMethod) {
        this.customerFlags = EnumSet.noneOf(CustomerFlag.class);
        this.customerFlags.addAll(Objects.requireNonNull(customerFlags, "CustomerFlags must not be null"));
        // Filter of flags.
        this.customerFlags.retainAll(EnumSet.of(CustomerFlag.CONFIRMS_DOSSIER, CustomerFlag.CONFIRMED_CASH_ON_DELIVERY, CustomerFlag.SYSTEM_CUSTOMER));
        this.document = Objects.requireNonNull(document, "Document must not be null");
        this.shippingCondition = Objects.requireNonNull(shippingCondition, "ShippingCondition must not be null");
        this.paymentMethod = Objects.requireNonNull(paymentMethod, "PaymentMethod must not be null");
    }

    public Set<CustomerFlag> getCustomerFlags() {
        return customerFlags;
    }

    public Document getDocument() {
        return document;
    }

    public ShippingCondition getShippingCondition() {
        return shippingCondition;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    //<editor-fold defaultstate="collapsed" desc="equals and hashCode of all">
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 19 * hash + Objects.hashCode(this.customerFlags);
        hash = 19 * hash + Objects.hashCode(this.document);
        hash = 19 * hash + Objects.hashCode(this.shippingCondition);
        hash = 19 * hash + Objects.hashCode(this.paymentMethod);
        return hash;
    }
    
    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        final CustomerDocument other = (CustomerDocument)obj;
        if ( !Objects.equals(this.customerFlags, other.customerFlags) ) return false;
        if ( !Objects.equals(this.document, other.document) ) return false;
        if ( this.shippingCondition != other.shippingCondition ) return false;
        if ( this.paymentMethod != other.paymentMethod ) return false;
        return true;
    }
    //</editor-fold>
    
    @Override
    public String toString() {
        return "CustomerDocument with " + new RedTapeStateCharacteristic(document.getType(), document.getDossier().getPaymentMethod(), document.getConditions(), document.getDirective(), customerFlags, document.getDossier().isDispatch()).toString();
    }
}
