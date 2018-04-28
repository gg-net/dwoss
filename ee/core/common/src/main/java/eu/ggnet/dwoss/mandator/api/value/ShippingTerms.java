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
package eu.ggnet.dwoss.mandator.api.value;

import java.io.Serializable;
import java.util.*;

import eu.ggnet.dwoss.common.api.values.ShippingCondition;

import lombok.Value;

/**
 * Shipping terms define values associated to a shipping condition.
 * <p>
 * @author pascal.perau
 */
@Value
public class ShippingTerms  implements Serializable{

    @Value
    public static class ConditionValue implements Serializable{

        /**
         * Description of the condition
         */
        private final String note;

        /**
         * Price of a single unit shipping
         */
        private final double priceOfOne;

        /**
         * Multiplicator for shippings of multiple unit
         */
        private final double multiplicator;
    }

    private final Map<ShippingCondition, ConditionValue> terms;

    public Optional<ConditionValue> get(ShippingCondition type) {
        return Optional.ofNullable(terms.get(type));
    }
}
