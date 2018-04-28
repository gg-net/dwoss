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

import eu.ggnet.dwoss.common.api.values.PaymentMethod;
import eu.ggnet.dwoss.common.api.values.ShippingCondition;
import eu.ggnet.dwoss.common.api.values.CustomerFlag;

import java.io.Serializable;
import java.util.*;

import eu.ggnet.dwoss.redtape.ee.entity.Document;

import lombok.Data;

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
@Data
public class CustomerDocument implements Serializable {

    private final Set<CustomerFlag> customerFlags;

    private final Document document;

    private ShippingCondition shippingCondition;

    private PaymentMethod paymentMethod;

    public CustomerDocument(Set<CustomerFlag> customerFlags, Document document, ShippingCondition shippingCondition, PaymentMethod paymentMethod) {
        this.customerFlags = EnumSet.noneOf(CustomerFlag.class);
        this.customerFlags.addAll(Objects.requireNonNull(customerFlags, "CustomerFlags must not be null"));
        // Filter of flags.
        this.customerFlags.retainAll(EnumSet.of(CustomerFlag.CONFIRMS_DOSSIER, CustomerFlag.CONFIRMED_CASH_ON_DELIVERY, CustomerFlag.SYSTEM_CUSTOMER));
        this.document = Objects.requireNonNull(document, "Document must not be null");
        this.shippingCondition = Objects.requireNonNull(shippingCondition, "ShippingCondition must not be null");
        this.paymentMethod = Objects.requireNonNull(paymentMethod, "PaymentMethod must not be null");
    }

    @Override
    public String toString() {
        return "CustomerDocument with " + new RedTapeStateCharacteristic(document.getType(), document.getDossier().getPaymentMethod(), document.getConditions(), document.getDirective(), customerFlags, document.getDossier().isDispatch()).toString();
    }
}
