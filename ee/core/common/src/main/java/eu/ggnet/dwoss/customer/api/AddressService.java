package eu.ggnet.dwoss.customer.api;

import eu.ggnet.dwoss.rules.AddressType;

import javax.ejb.Local;
import javax.ejb.Remote;

import eu.ggnet.dwoss.event.AddressChange;

/**
 *
 * @author pascal.perau
 */
@Remote
@Local
public interface AddressService {


    /**
     * Returns the default address label of customer and type.
     * <p>
     * @param customerId the customerId
     * @param type       the type of label
     * @return the default address label of customer and type.
     */
    String defaultAddressLabel(long customerId, AddressType type);

    /**
     * Sends a Server based notification about a addresschange of the customer.
     * <p>
     * @param changeEvent object containing all adress changeing information
     */
    void notifyAddressChange(AddressChange changeEvent);

}
