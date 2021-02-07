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
import java.util.Map;

import javax.ejb.Remote;

import org.inferred.freebuilder.FreeBuilder;

import eu.ggnet.dwoss.core.common.UserInfoException;

/**
 * Stock Api.
 *
 * @author oliver.guenther
 */
@Remote
public interface StockApi {

    @FreeBuilder
    public static interface Scraped {

        class Builder extends StockApi_Scraped_Builder {
        }

        /**
         * Retruns a description of the scraped unit.
         *
         * @return a description of the scraped unit.
         */
        String description();

        /**
         * Returns true if scraped was successful.
         *
         * @return true if scraped was successful.
         */
        boolean successful();

        /**
         * Returns a comment, may be blank but never null.
         *
         * @return a comment, may be blank but never null.
         */
        String comment();
    }

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
     * Returns a stock unit based on the refurbish id.
     *
     * @param refurbishId the refurbish id
     * @return a stockunit or null if none.
     */
    SimpleStockUnit findByRefurbishId(String refurbishId);

    /**
     * Returns a map with the assosiated simple stock units to the supplied refurbishids.
     * All errors or missmatches are handled:
     * <ul>
     * <li>if the supplied param is null, an empty map is returned
     * <li>if the list contains a null element, the map will contain a null elemet as key
     * <li>if a refurbishid does not match a unit, the value in the map will be null
     * </ul>
     *
     * @param refurbishIds the refurbishids.
     * @return a map with the assosiated simple stock units to the supplied refurbishids, never null.
     */
    Map<String, SimpleStockUnit> findByRefurbishIds(List<String> refurbishIds);

    /**
     * Scrapping units identifiered by the supplied stockUnitIds.
     * The units wont be available for sale after that action. There will be a comment in the unique unit and a dossier representing the scrap.
     * A unit can only be scrapped if:
     * <ul>
     * <li>StockUnit exists
     * <li>StockUnit is not on a StockTransaction
     * <li>StockUnit is not on a LogicTransaction
     * </ul>
     *
     * @param stockUnitIds a list of stockUnitids, must not be null.
     * @param reason       a reason for the scrap, must not be blank.
     * @param arranger     a reason for the scrap, must not be blank.
     * @return result of the scrapping, containings successful or unsuccessful scrapes.
     * @throws UserInfoException    if reason or arranger is blank, list is empty.
     * @throws NullPointerException if list is null.
     */
    List<Scraped> scrap(List<Long> stockUnitIds, String reason, String arranger) throws NullPointerException, UserInfoException;

    /**
     * Deleting units identifiered by the supplied stockUnitIds.
     * Same rules as in {@link StockApi#scrap(java.util.List, java.lang.String, java.lang.String) }, but no Dossier will be created.
     *
     * @param stockUnitIds a list stockids, must not be null.
     * @param reason       a reason for the scrap, must not be blank.
     * @param arranger     a reason for the scrap, must not be blank.
     * @return result of the deletion, containings successful or unsuccessful deletes.
     * @throws UserInfoException    if reason or arranger is blank, list is empty.
     * @throws NullPointerException if list is null.
     */
    List<Scraped> delete(List<Long> stockUnitIds, String reason, String arranger) throws NullPointerException, UserInfoException;

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
