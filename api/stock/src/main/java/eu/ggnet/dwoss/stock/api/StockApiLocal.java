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
package eu.ggnet.dwoss.stock.api;

import jakarta.ejb.Local;

/**
 * Local Stock Api.
 *
 * @author oliver.guenther
 */
@Local
public interface StockApiLocal {

    /**
     * Returns a html representation of a stockunit with the referencing unique unit id.
     *
     * @param uniqueUnitId the unique unit id.
     * @return a html representation.
     */
    String findByUniqueUnitIdAsHtml(long uniqueUnitId);

    /**
     * Returns a stock unit based on the unique unit id.
     *
     * @param uniqueUnitId the referencing unique unit id.
     * @return a stock unit or null if none.
     */
    SimpleStockUnit findByUniqueUnitId(long uniqueUnitId);
}
