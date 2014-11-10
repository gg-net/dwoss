package eu.ggnet.dwoss.redtape.state;

import java.io.Serializable;
import java.util.*;

import eu.ggnet.dwoss.redtape.entity.Document;

import eu.ggnet.dwoss.rules.CustomerFlag;
import eu.ggnet.dwoss.rules.PaymentMethod;
import eu.ggnet.dwoss.rules.ShippingCondition;

import lombok.Data;

/**
 *
 * @author oliver.guenther
 */
@Data
public class CustomerDocument implements Serializable {

    private final Set<CustomerFlag> customerFlags;

    private final Document document;

    private ShippingCondition shippingCondition;

    private PaymentMethod paymentMethod;

    public CustomerDocument(Set<CustomerFlag> customerFlags, Document document, ShippingCondition shippingCondition, PaymentMethod paymentMethod) {
        this.customerFlags = Objects.requireNonNull(customerFlags, "CustomerFlags must not be null");
        this.document = Objects.requireNonNull(document, "Document must not be null");
        this.shippingCondition = Objects.requireNonNull(shippingCondition, "ShippingCondition must not be null");
        this.paymentMethod = Objects.requireNonNull(paymentMethod, "PaymentMethod must not be null");
    }

    @Override
    public String toString() {
        return "CustomerDocument with " + new RedTapeStateCharacteristic(document.getType(), document.getDossier().getPaymentMethod(), document.getConditions(), document.getDirective(), customerFlags, document.getDossier().isDispatch()).toString();
    }
}
