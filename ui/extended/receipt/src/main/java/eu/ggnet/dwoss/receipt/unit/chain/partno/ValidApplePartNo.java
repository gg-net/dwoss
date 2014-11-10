package eu.ggnet.dwoss.receipt.unit.chain.partno;

import eu.ggnet.dwoss.receipt.unit.ValidationStatus;
import eu.ggnet.dwoss.receipt.unit.chain.ChainLink;

import eu.ggnet.dwoss.rules.AppleRules;

/**
 * Validates the PartNo by Bean Validations of the Property.
 * <p/>
 * @author oliver.guenther
 */
public class ValidApplePartNo implements ChainLink<String> {

    @Override
    public ChainLink.Result<String> execute(String value) {
        String violations = AppleRules.validatePartNo(value);
        if ( violations == null ) return new ChainLink.Result<>(value);
        return new ChainLink.Result<>(value, ValidationStatus.ERROR, violations);
    }
}
