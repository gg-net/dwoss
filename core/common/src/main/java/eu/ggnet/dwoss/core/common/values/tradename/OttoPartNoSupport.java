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
package eu.ggnet.dwoss.core.common.values.tradename;

import java.util.regex.Pattern;

/**
 * OttoPartNoSupport.
 * <p>
 * @author oliver.guenther
 */
public class OttoPartNoSupport implements PartNoSupport {

    @Override
    public boolean isValid(String partNo) {
        return violationMessages(partNo) == null;
    }

    @Override
    public String violationMessages(String partNo) {
        if ( partNo == null ) return "PartNo is null";

        if ( !(Pattern.matches("( [0-9]{8} | [0-9]{6} )", partNo)) ) {
            return "PartNo " + partNo
                    + "does not match either one of the Patterns [0-9]{8} and [0-9]{6}";
        }
        return null;
    }

}
