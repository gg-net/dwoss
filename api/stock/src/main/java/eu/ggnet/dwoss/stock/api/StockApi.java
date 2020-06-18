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

import java.util.List;

import javax.ejb.Remote;

import eu.ggnet.dwoss.core.common.UserInfoException;

/**
 * Stock Api.
 *
 * @author oliver.guenther
 */
@Remote
public interface StockApi {

    /**
     * Returns all available stocks.
     *
     * @return all available stocks, never null.
     */
    List<PicoStock> findAllStocks();

    /**
     * Returns a stock unit based on the unique unit id.
     *
     * @param uniqueUnitId the referencing unique unit id.
     * @return a stock unit or null if none.
     */
    SimpleStockUnit findByUniqueUnitId(long uniqueUnitId);

    /**
     * Prepares the transfer of multiple units.
     * Creates an amount of needed transactions in the form,
     * - that the transactions are correct (all units of a transaction have the same source as the transaciton)
     * - that no transaction has more units than maxUnitSize.
     *
     * @param uniqueUnitIds      the stockUnits to transfer identified by unqiueUnitId.
     * @param destinationStockId the destination stockId
     * @param arranger           the arranger
     * @param comment            a optional comment
     * @throws UserInfoException stock does not exist, unit not in a stock or unit not on source.
     */
    void perpareTransferByUniqueUnitIds(List<Long> uniqueUnitIds, int destinationStockId, String arranger, String comment) throws UserInfoException;

}
