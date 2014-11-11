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
package eu.ggnet.dwoss.rules;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Warranty.
 * <p/>
 * @author oliver.guenther
 */
@RequiredArgsConstructor
public enum Warranty {

    ONE_YEAR_CARRY_IN("1 Jahr Bring-In Garantie"), ONE_YEAR_CARRY_IN_ADVANCED("1 Jahr Bring-In, Verlängerung mögl."),
    FOURTEEN_DAYS_FUNTION_WARRANTY("14 Tage Funktionsgarantie"), TWO_YEARS_CARRY_IN("2 Jahre Bring-In Garantie"), NO_WARRANTY("Keine Garantie"),
    WARRANTY_TILL_DATE("Garantie bis Datum");

    @Getter
    private final String name;

    public static Warranty getWarrantyById(int id) {
        if (id < 0 || id >= Warranty.values().length) return null;
        return Warranty.values()[id];
    }
}
