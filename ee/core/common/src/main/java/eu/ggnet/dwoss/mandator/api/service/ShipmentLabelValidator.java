package eu.ggnet.dwoss.mandator.api.service;

import javax.ejb.Remote;

import eu.ggnet.dwoss.rules.TradeName;

/**
 * Optional Service to validate a Shipment Label.
 * <p>
 * @author oliver.guenther
 */
@Remote
public interface ShipmentLabelValidator {

    /**
     * Validates if the shipmentLabel is ok.
     * <p>
     * @param shipmentLabel
     * @param contractor
     * @return null if valid, otherwise a error message.
     */
    String validate(String shipmentLabel, TradeName contractor);
}
