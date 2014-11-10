package eu.ggnet.dwoss.receipt.unit.chain.serial;

import eu.ggnet.dwoss.receipt.unit.chain.ChainLink;

import eu.ggnet.dwoss.rules.AppleRules;

/**
 * Trims the supplied string.
 * <p/>
 * @author bastian.venz
 */
public class AppleSerialToPartNoAndMfgDate implements ChainLink<String> {

    @Override
    public Result<String> execute(String value) {
        return new Result<>(value, new Optional(null, AppleRules.mfgDateFromSerial(value)));
    }
}
