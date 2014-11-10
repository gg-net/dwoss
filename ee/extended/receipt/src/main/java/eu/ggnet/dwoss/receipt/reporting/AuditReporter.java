package eu.ggnet.dwoss.receipt.reporting;

import java.util.Date;

import javax.ejb.Remote;

import eu.ggnet.dwoss.util.FileJacket;

/**
 * Remote Interface for the Audit Reporter.
 * <p/>
 * @author oliver.guenther
 */
@Remote
public interface AuditReporter {

    /**
     * Returns an audit report of units which are input between the dates.
     * <p/>
     * @param start the starting date
     * @param end   the ending date
     * @return an audit report of units which are input between the dates.
     */
    FileJacket byRange(Date start, Date end);

    /**
     * Returns an audit report of units which are on a roll in transaction, but not yet rolled in.
     * <p/>
     * @return an audit report of units which are on a roll in transaction, but not yet rolled in.
     */
    FileJacket onRollIn();
}
