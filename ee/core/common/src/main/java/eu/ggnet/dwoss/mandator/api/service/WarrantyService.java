package eu.ggnet.dwoss.mandator.api.service;

import eu.ggnet.dwoss.rules.TradeName;

/**
 * A optional Service to allow the mandator to handle warranties with units.
 * <p>
 * @author oliver.guenther
 */
public interface WarrantyService {

    /**
     * Returns true, if the partNo is a Warranty otherwise false
     * <p>
     * @param partNo the partNo
     * @return true, if the partNo is a Warranty otherwise false
     */
    boolean isWarranty(String partNo);

    /**
     * Returns the contractor for this warranty type.
     * <p>
     * @param partNo the partNo
     * @return the contractor, may be null.
     */
    TradeName warrantyContractor(String partNo);
}
