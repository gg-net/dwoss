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
package eu.ggnet.dwoss.misc.ee;

import javax.ejb.Remote;

import eu.ggnet.dwoss.core.common.FileJacket;
import eu.ggnet.dwoss.core.common.UserInfoException;
import eu.ggnet.dwoss.core.common.values.SalesChannel;

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

    /**
     * Import missing image ids.
     *
     * @throws UserInfoException if something in the import fails.
     * @param inFile an Excel-97 (xls) file.
     */
    void importMissing(FileJacket inFile) throws UserInfoException;
}
