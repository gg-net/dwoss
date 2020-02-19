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
package eu.ggnet.dwoss.redtapext.ee;

import java.util.List;

import javax.ejb.Remote;

import eu.ggnet.dwoss.core.common.UserInfoException;
import eu.ggnet.dwoss.redtape.ee.entity.Position;
import eu.ggnet.dwoss.redtape.ee.interactiveresult.Result;

/**
 * UnitOverseer, knows correctly about the status of a unit.
 * <p>
 * @author oliver.guenther
 */
@Remote
public interface UnitOverseer {

    /**
     * Find an available StockUnit and locks it by add to a LogicTransaction via DossierId.
     * <p>
     * If no unit is found a LayerEightException is thrown.
     * <p>
     * @param dossierId     The Dossiers ID
     * @param refurbishedId The refurbished id for the Unique Unit search
     * @throws IllegalStateException if the refurbishId is not available
     */
    void lockStockUnit(long dossierId, String refurbishedId) throws IllegalStateException;

    /**
     * Builds a result object that contains positions build for a available unit.
     * <p>
     * The result will contain:<ul>
     * <li>positions elaborated by the given information</li>
     * <li>possible user interactions</li>
     * </ul>
     * This method will throw a UserInfoException describing, why the unit is not available.
     * <p/>
     * @param refurbishId The refurbished id of the UniqueUnit
     * @param documentId  the document id for taxtype and reference for more
     * @return a result object that contains positions build for a available unit
     * @throws UserInfoException if the refurbishId is not available
     */
    Result<List<Position>> createUnitPosition(String refurbishId, long documentId) throws UserInfoException;

}
