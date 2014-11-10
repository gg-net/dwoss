package eu.ggnet.dwoss.mandator.api.value;

import java.io.Serializable;
import java.util.*;

import eu.ggnet.dwoss.rules.TradeName;

import lombok.AllArgsConstructor;

/**
 * Contains TradeNames, which we do business with.
 * <p>
 * @author oliver.guenther
 */
@AllArgsConstructor
public class Contractors implements Serializable {

    private final Set<TradeName> _contractors;

    private final Set<TradeName> _allowedBrands;

    public Set<TradeName> all() {
        return _contractors;
    }

    public Set<TradeName> allowedBrands() {
        return _allowedBrands;
    }

}
