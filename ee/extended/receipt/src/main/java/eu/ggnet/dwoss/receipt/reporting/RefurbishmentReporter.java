package eu.ggnet.dwoss.receipt.reporting;

import java.util.Date;

import javax.ejb.Remote;

import eu.ggnet.dwoss.rules.TradeName;
import eu.ggnet.dwoss.util.FileJacket;

/**
 *
 * @author oliver.guenther
 */
@Remote
public interface RefurbishmentReporter {

    /**
     * Generates the report between two dates for the contractor.
     *
     * @param contractor the contractor to report about.
     * @param start      the starting date
     * @param end        the end date
     * @return an XLS document as FileJacket
     */
    FileJacket toXls(TradeName contractor, Date start, Date end);
}
