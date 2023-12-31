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
package eu.ggnet.dwoss.receipt.ee;

import jakarta.ejb.Remote;

/**
 *
 * @author oliver.guenther
 */
@Remote
public interface UnitSupporter {

    /**
     * Returns true if supplied refurbishId is available.
     *
     * @param refurbishId the refubishedId
     * @return true if available.
     */
    boolean isRefurbishIdAvailable(String refurbishId);

    /**
     * Returns true if supplied serial is available.
     * <p/>
     * @param serial the serial
     * @return true if available.
     */
    boolean isSerialAvailable(String serial);

    /**
     * Returns a refurbishId if a unit with the serial was in stock, otherwise null.
     * <p/>
     * @param serial the serial
     * @return a refurbishId if a unit with the serial was in stock, otherwise null.
     */
    String findRefurbishIdBySerial(String serial);
}
