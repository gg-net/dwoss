package eu.ggnet.dwoss.mandator.api.service;

import javax.ejb.Local;
import javax.ejb.Remote;

import eu.ggnet.dwoss.rules.PaymentMethod;
import eu.ggnet.dwoss.rules.ShippingCondition;


/**
 * Service for Shipping Costs, optional.
 * A Mandator may implement the service. If not, no automatic calculation of shipping costs is happening.
 * <p>
 * @author oliver.guenther
 */
@Local
@Remote
public interface ShippingCostService {

    /**
     * Calculates the shipping cost based on the parameters.
     * <p>
     * @param positionCount     the amount of relevant positions (units and product batches)
     * @param paymentMethod     the payment method
     * @param shippingCondition the shippingCondition
     * @return the shippingCost without tax
     */
    double calculate(int positionCount, PaymentMethod paymentMethod, ShippingCondition shippingCondition);
}
