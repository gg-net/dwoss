package eu.ggnet.dwoss.event;

import java.io.Serializable;

import eu.ggnet.dwoss.rules.AddressType;

import lombok.Value;

/**
 * Adress change event.
 * <p>
 * @author pascal.perau
 */
@Value
public class AddressChange implements Serializable{

    /**
     * Identifier of the customer.
     */
    private final long customerId;

    /**
     * The arranger of the change.
     */
    private final String arranger;

    /**
     * Type of the address.
     */
    private final AddressType type;

    /**
     * The old address.
     */
    private final String oldAdress;

    /**
     * The new address.
     */
    private final String newAdress;
}
