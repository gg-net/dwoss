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
package eu.ggnet.dwoss.mandator.api.value;

import java.io.Serializable;
import java.util.*;

import eu.ggnet.dwoss.rules.TradeName;

import lombok.AllArgsConstructor;

/**
 * Contains TradeNames, which we do business with.
 * <p>
 * @author oliver.guenther
 */
@AllArgsConstructor
public class Contractors implements Serializable {

    private final Set<TradeName> _contractors;

    private final Set<TradeName> _allowedBrands;

    public Set<TradeName> all() {
        return _contractors;
    }

    public Set<TradeName> allowedBrands() {
        return _allowedBrands;
    }

}
