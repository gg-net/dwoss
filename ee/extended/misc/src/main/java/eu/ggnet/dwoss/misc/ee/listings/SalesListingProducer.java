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
package eu.ggnet.dwoss.misc.ee.listings;

import java.util.List;

import jakarta.ejb.Remote;

import eu.ggnet.dwoss.mandator.api.service.ListingActionConfiguration;
import eu.ggnet.dwoss.core.common.FileJacket;
import eu.ggnet.dwoss.core.common.UserInfoException;

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

    FileJacket generateAllSalesListing();

    /**
     * Returns a list of generated Files as Jackets, never Null.
     *
     * @param config the config to be used.
     * @return a list of generated Files as Jackets, never Null.
     * @throws UserInfoException
     */
    List<FileJacket> generateListings(ListingActionConfiguration config) throws UserInfoException;
}
