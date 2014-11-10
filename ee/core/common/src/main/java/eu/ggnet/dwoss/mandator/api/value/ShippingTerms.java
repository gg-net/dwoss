package eu.ggnet.dwoss.mandator.api.value;

import java.io.Serializable;
import java.util.*;

import eu.ggnet.dwoss.rules.ShippingCondition;

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
