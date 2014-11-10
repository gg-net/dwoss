package eu.ggnet.dwoss.receipt.unit.chain.date;

import java.util.Date;

import eu.ggnet.dwoss.receipt.unit.ValidationStatus;
import eu.ggnet.dwoss.receipt.unit.chain.ChainLink;

/**
 *
 * @author oliver.guenther
 */
public class DateNotNull implements ChainLink<Date> {

    @Override
    public Result<Date> execute(Date value) {
        if ( value == null ) return new Result<>(ValidationStatus.ERROR, "Eingabe ist leer");
        return new Result<>(value);
    }
}
