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
package eu.ggnet.dwoss.receipt.ui.unit.chain.date;

import java.util.Date;

import eu.ggnet.dwoss.receipt.ui.unit.ValidationStatus;
import eu.ggnet.dwoss.receipt.ui.unit.chain.ChainLink;

/**
 *
 * @author oliver.guenther
 */
public class DateNotNull implements ChainLink<Date> {

    @Override
    public Result<Date> execute(Date value) {
        if ( value == null ) return new Result<>(ValidationStatus.ERROR, "Eingabe ist leer");
        return new Result<>(value);
    }
}
