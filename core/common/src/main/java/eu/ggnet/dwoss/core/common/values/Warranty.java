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
package eu.ggnet.dwoss.core.common.values;

/**
 * Warranty.
 * <p>
 * @author oliver.guenther
 */
public enum Warranty {

    ONE_YEAR_CARRY_IN("1 Jahr Bring-In Garantie"),
    ONE_YEAR_CARRY_IN_ADVANCED("1 Jahr Bring-In, Verlängerung mögl."),
    FOURTEEN_DAYS_FUNTION_WARRANTY("14 Tage Funktionsgarantie"),
    TWO_YEARS_CARRY_IN("2 Jahre Bring-In Garantie"),
    NO_WARRANTY("Keine Garantie"),
    WARRANTY_TILL_DATE("Garantie bis Datum"),
    ONE_YEAR_STATUTORY_WARRANTY("12 Monate gesetzliche Gewährleistung ab Lieferung der Ware"),
    ONE_YEAR_STATUTORY_PLUS_ONEADO("12 Monate gesetzliche Gewährleistung ab Lieferung der Ware & 13 Monate oneado-Garantie ab Lieferung der Ware gemäß der beiliegenden Bedingungen"),
    NO_B2B_WARRANTY("Händlergeschäft, die Gewährleistung ist ausgeschlossen"),
    ONEADO_2022("oneado-Garantie gemäß der beiliegenden Bedingungen. Gewährleistung: 12 Monate gesetzliche Gewährleistung ab Lieferung der Ware");

    /**
     * A short (german) description.
     */
    public final String description;

    private Warranty(String name) {
        this.description = name;
    }

    public static Warranty getWarrantyById(int id) {
        if ( id < 0 || id >= Warranty.values().length ) return null;
        return Warranty.values()[id];
    }

    /**
     * A short (german) description.
     *
     * @return a short (german) description.
     * @deprecated use field description.
     */
    @Deprecated
    public String getName() {
        return description;
    }

    public String description() {
        return description;
    }

    /**
     * All Values, but in a predefind order.
     *
     * @return all Values, but in a predefind order.
     */
    public static Warranty[] valuesSorted() {
        return new Warranty[]{
            ONEADO_2022,
            ONE_YEAR_CARRY_IN,
            ONE_YEAR_CARRY_IN_ADVANCED,
            FOURTEEN_DAYS_FUNTION_WARRANTY,
            TWO_YEARS_CARRY_IN,
            NO_WARRANTY,
            WARRANTY_TILL_DATE,
            ONE_YEAR_STATUTORY_WARRANTY,
            ONE_YEAR_STATUTORY_PLUS_ONEADO,
            NO_B2B_WARRANTY
        };
    }

}
