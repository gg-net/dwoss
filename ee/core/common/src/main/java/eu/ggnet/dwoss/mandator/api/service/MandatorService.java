package eu.ggnet.dwoss.mandator.api.service;

import javax.ejb.Local;
import javax.ejb.Remote;

import eu.ggnet.dwoss.rules.TradeName;


/**
 *
 * @author pascal.perau
 */
@Local
@Remote
public interface MandatorService {

    /**
     * Returns true if the refurbishId is allowed for the contractor.
     * <p>
     * @param contractor  the contractor
     * @param refurbishId the refurbishId
     * @return true if allowed.
     */
    boolean isAllowedRefurbishId(TradeName contractor, String refurbishId);

    /**
     * Return the id of the stock based on the actual location.
     * <p>
     * @param location location parameter holding object
     * @return the id of the stock based on the actual location.
     */
    int getLocationStockId(ClientLocation location);

}
