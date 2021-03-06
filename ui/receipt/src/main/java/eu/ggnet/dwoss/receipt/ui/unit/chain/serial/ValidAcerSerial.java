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

import eu.ggnet.dwoss.receipt.ui.unit.ValidationStatus;
import eu.ggnet.dwoss.receipt.ui.unit.chain.ChainLink;

import eu.ggnet.dwoss.core.common.values.AcerRules;

/**
 * Validates the Serial by Acer norms, may produce Errors.
 * <p/>
 * @author oliver.guenther
 */
public class ValidAcerSerial implements ChainLink<String> {

    @Override
    public Result<String> execute(String value) {
        String violations = AcerRules.validateSerialError(value);
        if ( violations != null ) return new Result<>(value, ValidationStatus.ERROR, violations);
        return new Result<>(value);
    }
}
