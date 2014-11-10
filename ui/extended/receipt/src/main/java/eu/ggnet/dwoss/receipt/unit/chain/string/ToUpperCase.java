package eu.ggnet.dwoss.receipt.unit.chain.string;

import java.util.Objects;

import eu.ggnet.dwoss.receipt.unit.chain.ChainLink;

/**
 * Makes a String uppercase.
 * <p/>
 * @author oliver.guenther
 */
public class ToUpperCase implements ChainLink<String> {

    @Override
    public Result<String> execute(String value) {
        return new Result<>(Objects.requireNonNull(value).toUpperCase());
    }
}
