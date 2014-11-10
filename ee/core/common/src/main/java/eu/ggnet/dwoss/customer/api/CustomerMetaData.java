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
