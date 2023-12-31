/*
 * Copyright (C) 2019 GG-Net GmbH
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
package eu.ggnet.dwoss.misc.api;

import java.util.List;

import jakarta.ejb.Local;

import eu.ggnet.dwoss.core.common.values.SalesChannel;
import eu.ggnet.dwoss.core.common.FileJacket;

/**
 * Generates predefined Sales Listings.
 * 
 * @author oliver.guenther
 */
@Local
public interface SalesListingService {
    
    /**
     * Generates pdf listings of the supplied channel.
     * 
     * @param channel the channel
     * @return Filejackets containing the pdfs.
     */
    List<FileJacket> generatePdfs(SalesChannel channel);
    
    /**
     * Generates xls listings of the supplied channel.
     * 
     * @param channel the channel
     * @return Filejackets containing the xlses.
     */
    List<FileJacket> generateXlses(SalesChannel channel);
    
}
