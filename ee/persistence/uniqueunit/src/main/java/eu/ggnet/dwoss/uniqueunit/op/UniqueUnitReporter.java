package eu.ggnet.dwoss.uniqueunit.op;

import java.util.Date;

import javax.ejb.Remote;

import eu.ggnet.dwoss.rules.TradeName;
import eu.ggnet.dwoss.util.FileJacket;

/**
 *
 * @author oliver.guenther
 */
@Remote
public interface UniqueUnitReporter {

    // TODO: Document Me
    FileJacket quality(Date start, Date end, TradeName contractor);
}
