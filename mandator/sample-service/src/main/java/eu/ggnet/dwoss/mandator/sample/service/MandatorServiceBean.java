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
package eu.ggnet.dwoss.mandator.sample.service;

import eu.ggnet.dwoss.core.common.values.tradename.TradeName;
import eu.ggnet.dwoss.mandator.api.service.ClientLocation;
import eu.ggnet.dwoss.mandator.api.service.MandatorService;

import jakarta.ejb.Stateless;


/**
 * Sample Service Implementation.
 */
@Stateless
public class MandatorServiceBean implements MandatorService {

    @Override
    public boolean isAllowedRefurbishId(TradeName contractor, String refurbishId) {
        return true;

    }

    @Override
    public int getLocationStockId(ClientLocation location) {
        return 0;
    }

}
