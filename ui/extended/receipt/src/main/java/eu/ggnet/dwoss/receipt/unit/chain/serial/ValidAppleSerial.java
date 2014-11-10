package eu.ggnet.dwoss.receipt.unit.chain.serial;

import eu.ggnet.dwoss.receipt.unit.ValidationStatus;
import eu.ggnet.dwoss.receipt.unit.chain.ChainLink;

import eu.ggnet.dwoss.rules.AppleRules;

/**
 * Validates the Serial by Acer norms, may produce Errors.
 * <p/>
 * @author oliver.guenther
 */
public class ValidAppleSerial implements ChainLink<String> {

    @Override
    public Result<String> execute(String value) {
        String violations = AppleRules.validateSerial(value);
        if ( violations != null ) return new Result<>(value, ValidationStatus.ERROR, violations);
        return new Result<>(value);
    }
}
