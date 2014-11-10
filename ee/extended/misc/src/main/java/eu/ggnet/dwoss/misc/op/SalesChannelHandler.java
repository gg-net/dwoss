package eu.ggnet.dwoss.misc.op;

import java.util.List;

import javax.ejb.Remote;

import eu.ggnet.dwoss.stock.model.SalesChannelLine;

import eu.ggnet.dwoss.util.UserInfoException;

/**
 *
 * @author oliver.guenther
 */
@Remote
public interface SalesChannelHandler {

    /**
     * Returns all units, which are in a stock. Units which are on a transaction, are not displayed.
     * <p/>
     * @return all units, which are in a stock
     */
    List<SalesChannelLine> findAvailableUnits();

    /**
     * Updates the salesChanel of all supplied units
     * <p/>
     * @param lines              a list of salesChannelLines, must not be null.
     * @param arranger
     * @param transactionComment
     * @return true if something was changed.
     * @throws de.dw.util.UserInfoException
     */
    boolean update(final List<SalesChannelLine> lines, String arranger, String transactionComment) throws UserInfoException;
}
