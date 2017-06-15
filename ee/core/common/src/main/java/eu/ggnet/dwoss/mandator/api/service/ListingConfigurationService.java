/*
 * Copyright (C) 2017 GG-Net GmbH
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
package eu.ggnet.dwoss.mandator.api.service;

import java.util.*;

import javax.ejb.Local;

import eu.ggnet.dwoss.mandator.api.value.partial.ListingMailConfiguration;
import eu.ggnet.dwoss.rules.TradeName;
import eu.ggnet.dwoss.util.FileJacket;

/**
 * Local Configurations for Listings.
 *
 * @author oliver.guenther
 */
@Local
public interface ListingConfigurationService {

    List<ListingConfiguration> listingConfigurations();

    FtpConfiguration listingFtpConfiguration(Map<TradeName, Collection<FileJacket>> files);

    ListingMailConfiguration listingMailConfiguration();

}
