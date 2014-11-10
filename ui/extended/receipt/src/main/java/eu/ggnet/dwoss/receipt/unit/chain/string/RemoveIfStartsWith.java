package eu.ggnet.dwoss.receipt.unit.chain.string;

import eu.ggnet.dwoss.receipt.unit.chain.ChainLink;

import lombok.RequiredArgsConstructor;

/**
 * A Link that removes the "start" from "value" if the "value" has it, else does nothing.
 * <p/>
 * @author oliver.guenther
 */
@RequiredArgsConstructor
public class RemoveIfStartsWith implements ChainLink<String> {

    private final String start;

    @Override
    public Result<String> execute(String value) {
        if ( value.startsWith(start) ) return new Result<>(value.substring(start.length(), value.length()));
        return new Result<>(value);
    }
}
