package eu.ggnet.dwoss.receipt.unit.chain.serial;

import eu.ggnet.dwoss.receipt.unit.ValidationStatus;
import eu.ggnet.dwoss.receipt.unit.chain.ChainLink;

import eu.ggnet.dwoss.rules.AcerRules;

/**
 * Validates the Serial by Acer norms, may produce Warnings.
 * <p/>
 * @author oliver.guenther
 */
public class ValidAcerSerialNice implements ChainLink<String> {

    @Override
    public Result<String> execute(String value) {
        String violations = AcerRules.validateSerialWarning(value);
        if ( violations != null ) return new Result<>(value, ValidationStatus.WARNING, violations);
        return new Result<>(value);
    }
}
