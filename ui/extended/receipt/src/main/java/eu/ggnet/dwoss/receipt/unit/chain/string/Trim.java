package eu.ggnet.dwoss.receipt.unit.chain.string;

import java.util.Objects;

import eu.ggnet.dwoss.receipt.unit.chain.ChainLink;

/**
 * Trims the supplied string.
 * <p/>
 * @author oliver.guenther
 */
public class Trim implements ChainLink<String> {

    @Override
    public Result<String> execute(String value) {
        return new Result<>(Objects.requireNonNull(value).trim());
    }
}
