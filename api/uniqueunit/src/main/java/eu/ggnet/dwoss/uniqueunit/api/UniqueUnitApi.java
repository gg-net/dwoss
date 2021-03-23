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

import javax.ejb.Remote;

import eu.ggnet.dwoss.core.common.FileJacket;
import eu.ggnet.dwoss.core.common.UserInfoException;

/**
 * Main Api entrence point.
 *
 * @author oliver.guenther
 */
@Remote
public interface UniqueUnitApi {

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
}
