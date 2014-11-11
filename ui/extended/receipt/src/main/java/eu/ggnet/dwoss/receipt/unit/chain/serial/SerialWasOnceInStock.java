/* 
 * Copyright (C) 2014 pascal.perau
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
package eu.ggnet.dwoss.receipt.unit.chain.serial;

import java.util.Objects;

import eu.ggnet.dwoss.receipt.UnitSupporter;
import eu.ggnet.dwoss.receipt.unit.ValidationStatus;
import eu.ggnet.dwoss.receipt.unit.chain.ChainLink;

/**
 * Tries to lookup the refurbishId in the Database, continues if it doesn't exist.
 * <p/>
 * @author oliver.guenther
 */
public class SerialWasOnceInStock implements ChainLink<String> {

    private final UnitSupporter unitSupporter;

    private final String editRefurbishId;
    // Add the RefurbihsId on edit, to not tell something known.

    public SerialWasOnceInStock(UnitSupporter unitSupporter, String editRefurbishId) {
        this.unitSupporter = Objects.requireNonNull(unitSupporter, UnitSupporter.class.getSimpleName() + " is requiered");
        this.editRefurbishId = editRefurbishId;
    }

    @Override
    public ChainLink.Result<String> execute(String value) {
        String oldRefurbishId = unitSupporter.findRefurbishIdBySerial(value);
        if ( oldRefurbishId == null || oldRefurbishId.equals(editRefurbishId) ) return new ChainLink.Result<>(value);
        return new ChainLink.Result<>(value, ValidationStatus.WARNING, "Seriennummer war schon mal da, letzte SopoNr: " + oldRefurbishId);
    }
}
