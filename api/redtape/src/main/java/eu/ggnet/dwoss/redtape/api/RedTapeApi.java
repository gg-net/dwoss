/*
 * Copyright (C) 2020 GG-Net GmbH
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
package eu.ggnet.dwoss.redtape.api;

import jakarta.ejb.Remote;

/**
 * Redtape Api.
 *
 * @author oliver.guenther
 */
@Remote
public interface RedTapeApi {

    /**
     * Tries to find a Unit based on the refurbishId and verifies, if its aviable for sale.
     * Uses {@link UniqueUnitApi} and {@link StockApi}
     *
     * @param refurbishId
     * @return
     */
    UnitAvailability findUnitByRefurbishIdAndVerifyAviability(String refurbishId);

}
