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
package eu.ggnet.dwoss.mandator.api.value.partial;

import java.io.Serializable;
import java.net.URL;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Wither;

/**
 * Masterdata for Mandator.
 * <p/>
 * @author oliver.guenther
 */
@Wither
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Company implements Serializable {

    private String name;

    private String street;

    private String city;

    private String zip;

    private URL logo;

    private String email;

    private String emailName;

    public String toSingleLine() {
        return name + " - " + street + " - " + zip + " " + city;
    }
}
