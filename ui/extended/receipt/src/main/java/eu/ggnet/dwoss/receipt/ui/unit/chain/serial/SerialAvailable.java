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
package eu.ggnet.dwoss.receipt.ui.unit.chain.serial;

import java.util.Objects;

import eu.ggnet.dwoss.receipt.ee.UnitSupporter;
import eu.ggnet.dwoss.receipt.ui.unit.ValidationStatus;
import eu.ggnet.dwoss.receipt.ui.unit.chain.ChainLink;

/**
 * Tries to lookup the refurbishId in the Database, continues if it doesn't exist.
 * <p/>
 * @author oliver.guenther
 */
public class SerialAvailable implements ChainLink<String> {

    // Add the RefurbishId to allow one allready take Serial.
    private final UnitSupporter unitSupporter;

    private final String editRefurbihsId;

    public SerialAvailable(UnitSupporter refurbishIdSupporter, String editRefurbishId) {
        this.unitSupporter = Objects.requireNonNull(refurbishIdSupporter, UnitSupporter.class.getSimpleName() + " is requiered");
        this.editRefurbihsId = editRefurbishId;
    }

    @Override
    public ChainLink.Result<String> execute(String value) {
        if ( !unitSupporter.isSerialAvailable(value) ) {
            if ( editRefurbihsId != null && editRefurbihsId.equals(unitSupporter.findRefurbishIdBySerial(value)) ) {
                // Edit Mode and this is the same unit.
                return new ChainLink.Result<>(value);
            } else { // Both modes, but in edit not the same Unit.
                String refrubhishId = unitSupporter.findRefurbishIdBySerial(value);
                return new ChainLink.Result<>(value, ValidationStatus.ERROR, "Seriennummer ist nicht verfügbar, noch im Lager mit SopoNr " + refrubhishId);
            }
        }
        return new ChainLink.Result<>(value);
    }
}
