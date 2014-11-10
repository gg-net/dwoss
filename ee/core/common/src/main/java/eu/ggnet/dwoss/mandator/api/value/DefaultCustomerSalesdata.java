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
