package eu.ggnet.dwoss.customer.priv;

import javax.ejb.Local;
import javax.ejb.Remote;

import eu.ggnet.dwoss.customer.entity.Customer;

/**
 * Agent for the Old Customer implementation.
 * <p>
 * @author oliver.guenther
 */
@Local
@Remote
public interface OldCustomerAgent {

    /**
     * Returns a {@link Customer} wrapped into an old one by id.
     * <p>
     * @param id the id
     * @return a old customer.
     */
    OldCustomer findById(long id);

    /**
     * Unwraps the old customer and store it as a {@link Customer} either persisting ore merging him.
     * <p>
     * @param old the old customer
     * @return the old customer after storage, if persisted, containing the new id.
     */
    OldCustomer store(OldCustomer old);

}
