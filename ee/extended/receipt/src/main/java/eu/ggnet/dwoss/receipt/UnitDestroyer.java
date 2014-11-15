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
package eu.ggnet.dwoss.receipt;

import javax.ejb.Remote;

import eu.ggnet.dwoss.uniqueunit.entity.UniqueUnit;

import eu.ggnet.dwoss.util.UserInfoException;

/**
 * Remote Interface for UnitDestroyerOperation.
 * <p/>
 * @author oliver.guenther
 */
@Remote
public interface UnitDestroyer {

    /**
     * Delete the Unit.
     * Finds the StockUnit, destroys it via a Destroy Transaction.
     * Updates the UniqueUnit and SopoUnit in the internal comments, that it is destroyed.
     * Hint: For simplicity this method assumes, that verifyScarpOrDeleteAble was called, so no extra validation is done.
     * <p/>
     * @param uniqueUnit the unit to scrap
     * @param arranger   the arranger
     * @param reason     the reason
     */
    void delete(UniqueUnit uniqueUnit, String reason, String arranger);

    /**
     * Scraps the Unit.
     * Finds the StockUnit, destroys it via a Destroy Transaction.
     * Updates the UniqueUnit and SopoUnit in the internal comments, that it is destroyed.
     * Hint: For simplicity this method assumes, that verifyScarpOrDeleteAble was called, so no extra validation is done.
     * <p/>
     * @param uniqueUnit the unit to scrap
     * @param arranger   the arranger
     * @param reason     the reason
     */
    void scrap(final UniqueUnit uniqueUnit, final String reason, final String arranger);

    /**
     * Validates if a unit identified by refurbishedId is scrapable.
     * Throws Exception if:
     * <ul>
     * <li>No UniqueUnit,SopoUnit or StockUnit exists.</li>
     * <li>StockUnit is inTransaction</li>
     * <li>SopoUnit is in Auftrag or Balanced.</li>
     * </ul>
     *
     * @param refurbishedId the refurbishedId
     * @return
     * @throws UserInfoException if not scrapable.
     */
    UniqueUnit verifyScarpOrDeleteAble(String refurbishedId) throws UserInfoException;
}
