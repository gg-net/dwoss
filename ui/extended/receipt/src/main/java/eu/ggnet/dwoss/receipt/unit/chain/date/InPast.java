package eu.ggnet.dwoss.receipt.unit.chain.date;

import java.util.Date;

import eu.ggnet.dwoss.receipt.unit.ValidationStatus;
import eu.ggnet.dwoss.receipt.unit.chain.ChainLink;

/**
 *
 * @author oliver.guenther
 */
public class InPast implements ChainLink<Date> {

    @Override
    public Result<Date> execute(Date value) {
        if ( value.after(new Date()) ) return new Result<>(value, ValidationStatus.ERROR, "Datum ist in der Zukunft.");
        return new Result<>(value);
    }
}
