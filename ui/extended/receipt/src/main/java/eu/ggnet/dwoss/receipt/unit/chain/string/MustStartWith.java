package eu.ggnet.dwoss.receipt.unit.chain.string;

import eu.ggnet.dwoss.receipt.unit.ValidationStatus;
import eu.ggnet.dwoss.receipt.unit.chain.ChainLink;

import lombok.RequiredArgsConstructor;

/**
 * A Link that ensures that the value starts with start.
 * <p/>
 * @author oliver.guenther
 */
@RequiredArgsConstructor
public class MustStartWith implements ChainLink<String> {

    private final String start;

    @Override
    public Result<String> execute(String value) {
        if ( !value.startsWith(start) ) return new Result<>(value, ValidationStatus.ERROR, value + " beginnt nicht mit " + start);
        return new Result<>(value);
    }
}
