package eu.ggnet.dwoss.mandator.api.value;

import java.io.Serializable;
import java.util.*;

import eu.ggnet.dwoss.rules.TradeName;

import lombok.Value;

/**
 * Contains SystemCustomers which are used for the scrap operation based on the Contractor.
 * <p>
 * @author oliver.guenther
 */
@Value
public class ScrapCustomers  implements Serializable{

    private final Map<TradeName, Long> contractorCustomers;

    public Optional<Long> get(TradeName contractor) {
        return Optional.ofNullable(contractorCustomers.get(contractor));
    }

}
