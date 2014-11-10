package eu.ggnet.dwoss.receipt.unit.chain.string;

import eu.ggnet.dwoss.receipt.unit.ValidationStatus;
import eu.ggnet.dwoss.receipt.unit.chain.ChainLink;

/**
 *
 * @author oliver.guenther
 */
public class NotEmpty implements ChainLink<String> {

    @Override
    public Result<String> execute(String value) {
        if ( value == null || value.trim().equals("") ) return new Result<>(ValidationStatus.ERROR, "Eingabe ist leer");
        return new Result<>(value);
    }
}
