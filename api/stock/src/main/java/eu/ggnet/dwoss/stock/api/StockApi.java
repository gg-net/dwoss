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

import javax.ejb.Remote;

/**
 * Main Api entrance point for Stock.
 *
 * @author oliver.guenther
 */
@Remote
public interface StockApi {

    /**
     * Returns a stock unit based on the database id.
     *
     * @param id the database id.
     * @return a stock unit or null if none
     */
    SimpleStockUnit find(long id);

    /**
     * Returns a stock unit based on the unique unit id.
     *
     * @param uniqueUnitId the referencing unique unit id.
     * @return a stock unit or null if none.
     */
    SimpleStockUnit findByUniqueUnitId(long uniqueUnitId);

}
