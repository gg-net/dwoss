/* 
 * Copyright (C) 2014 GG-Net GmbH - Oliver Günther
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.ggnet.dwoss.misc.op.listings;

import java.io.Serializable;
import java.util.*;

import javax.ejb.Local;
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
@Local
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
