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
package eu.ggnet.dwoss.uniqueunit.api;

import java.util.List;

import jakarta.ejb.Remote;

import eu.ggnet.dwoss.core.common.FileJacket;
import eu.ggnet.dwoss.core.common.UserInfoException;

/**
 * Main Api entrence point.
 *
 * @author oliver.guenther
 */
@Remote
public interface UniqueUnitApi {

    /**
     * Return all shop categories.
     * 
     * @return all shop categories. 
     */
    List<ShopCategory> findAllShopCategories();
    
    /**
     * Create a new ShopCategory.
     * 
     * @param shopCategory the shopCategory to create.
     * @throws UserInfoException if a category with an id other the 0 is supplied.
     * @return shopCategory with database id.
     */
    ShopCategory create(ShopCategory shopCategory) throws UserInfoException;
    
    String findBySerialAsHtml(String serial, String username);

    String findAsHtml(long id, String username);

    /**
     * Adds a history to a unique unit.
     *
     * @param uniqueUnitId the id of the unique unit.
     * @param history      the history to add
     * @param arranger     the arranger, which added it.
     * @throws UserInfoException If history, arranger are null or blank, or no unit with the supplied id is found.
     */
    void addHistory(long uniqueUnitId, String history, String arranger) throws UserInfoException;

    /**
     * Adds a history to a unique unit identified by the supplied refurbishId.
     *
     * @param refurbishId the refurbishid of the unique unit.
     * @param history     the history to add, must not be blank or null.
     * @param arranger    the arranger which added it, must not be blank or null.
     * @throws UserInfoException If history, arranger are null or blank, or no unit with the supplied refurbishid is found.
     */
    void addHistoryByRefurbishId(String refurbishId, String history, String arranger) throws UserInfoException;

    /**
     * Returns an XLS File containing Inforation to all units of the supplied partNo.
     *
     * @param partNo the partNo to supplied.
     * @return an XLS File containing Inforation to all units of the supplied partNo.
     * @throws eu.ggnet.dwoss.core.common.UserInfoException If partNo ist null, empty or no product can be found.
     */
    FileJacket toUnitsOfPartNoAsXls(String partNo) throws UserInfoException;
    
    /**
     * Changes the Shipment on a supplied refurbishId
     * 
     * @param refurbishId refurbishId must not be blank
     * @param shipmentId shipmentId must not be zero or negative
     * @param shipmentLabel shipmentLabel must not be blank
     * @param arranger the arranger must not be blank
     * @throws UserInfoException if refurbishId is null, shipmentlabel is null, shipmentId is zero or negativ, refurbishId does not exist.
     */
    void changeShipment(String refurbishId, long shipmentId, String shipmentLabel, String arranger) throws UserInfoException;    
    
}
