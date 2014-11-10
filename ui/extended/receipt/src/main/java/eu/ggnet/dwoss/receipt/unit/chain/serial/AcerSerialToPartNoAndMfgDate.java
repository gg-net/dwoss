package eu.ggnet.dwoss.receipt.unit.chain.serial;

import eu.ggnet.dwoss.receipt.unit.chain.ChainLink;

import eu.ggnet.dwoss.rules.AcerRules;

/**
 * Trims the supplied string.
 * <p/>
 * @author oliver.guenther
 */
public class AcerSerialToPartNoAndMfgDate implements ChainLink<String> {

    @Override
    public Result<String> execute(String value) {
        return new Result<>(value, new Optional(AcerRules.partNoFromSerial(value), AcerRules.mfgDateFromSerial(value)));
    }
}
