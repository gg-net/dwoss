package eu.ggnet.dwoss.receipt.unit.chain.partno;

import eu.ggnet.dwoss.receipt.unit.ValidationStatus;
import eu.ggnet.dwoss.receipt.unit.chain.ChainLink;

import static eu.ggnet.dwoss.rules.TradeName.LENOVO;

/**
 *
 * @author bastian.venz
 */
public class ValidLenovoPartNo implements ChainLink<String> {

    @Override
    public ChainLink.Result<String> execute(String value) {
        String violations = LENOVO.getPartNoSupport().violationMessages(value);
        if ( violations == null ) return new ChainLink.Result<>(value);
        return new ChainLink.Result<>(value, ValidationStatus.ERROR, violations);
    }

}
