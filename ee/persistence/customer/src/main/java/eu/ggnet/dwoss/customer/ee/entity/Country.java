/*
 * Copyright (C) 2018 GG-Net GmbH
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
package eu.ggnet.dwoss.customer.ee.entity;

import java.util.Arrays;

/**
 * Countries as Enum
 *
 * @author oliver.guenther
 */
public enum Country {

    GERMANY("Deutschland","DE"),AUSTRIA("Östreich","AT");
    
    public final String countryName;
    
    public final String isoCode;

    private Country(String countryName, String isoCode) {
        this.countryName = countryName;
        this.isoCode = isoCode;
    }
    
    /**
     * Returns a country by iso code or Germany as default.
     * 
     * @param code the iso code
     * @return a country by iso code or Germany as default.
     */
    public static Country ofIsoCode(String code) {
        return Arrays.asList(values()).stream().filter(c -> c.isoCode.equals(code)).findAny().orElse(GERMANY);
    }
   
    
    
}
