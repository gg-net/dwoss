package eu.ggnet.dwoss.misc.op.listings;

import java.io.Serializable;
import java.util.*;

import javax.ejb.Remote;

import eu.ggnet.dwoss.mandator.api.service.ListingActionConfiguration;
import eu.ggnet.dwoss.rules.TradeName;
import eu.ggnet.dwoss.util.FileJacket;
import eu.ggnet.dwoss.util.UserInfoException;

import lombok.Data;

/**
 * Remote Connection to all SalesListing Operations.
 * <p/>
 * @author oliver.guenther
 */
@Remote
public interface SalesListingProducer {

    /**
     * Returns the next Image Id.
     * <p/>
     * @return the next Image Id.
     */
    public int nextImageId();

    /**
     * Result for a Listing Operation.
     */
    @Data
    public static class ListingResult implements Serializable {

        /**
         * Collection of Files mapped by TradeName.
         */
        private final Map<TradeName, Collection<FileJacket>> listings;

        /**
         * Collection of Warnings while processing.
         */
        private final SortedSet<String> warnings;
    }

    FileJacket generateAllSalesListing();

    List<FileJacket> generateListings(ListingActionConfiguration config) throws UserInfoException;
}
