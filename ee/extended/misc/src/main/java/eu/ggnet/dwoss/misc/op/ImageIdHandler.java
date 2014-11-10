package eu.ggnet.dwoss.misc.op;

import javax.ejb.Remote;

import eu.ggnet.dwoss.rules.SalesChannel;
import eu.ggnet.dwoss.util.FileJacket;
import eu.ggnet.dwoss.util.UserInfoException;

/**
 *
 * @author oliver.guenther
 */
@Remote
public interface ImageIdHandler {

    /**
     * Returns a FileJacket of XLS, which contains all Products with missing ImageIds.
     * <p/>
     * Acitve Filters are:
     * <ul>
     * <li>If SalesChannel is supplied, only Products, which have units in the SalesChannel.</li>
     * <li>Only Products, which don't have an image id.</li>
     * <li>Only Products, which have Unit in stock.</li>
     * </ul>
     * <p/>
     * @param salesChannel
     * @return
     */
    FileJacket exportMissing(SalesChannel salesChannel);

    void importMissing(FileJacket inFile) throws UserInfoException;
}
