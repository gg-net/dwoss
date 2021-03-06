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
package eu.ggnet.dwoss.mandator.api.value;

import java.io.Serializable;
import java.util.Set;

import eu.ggnet.dwoss.core.common.values.tradename.TradeName;

/**
 * Contains TradeNames, which we do business with.
 * <p>
 * @author oliver.guenther
 */
public class Contractors implements Serializable {

    private final Set<TradeName> all;

    private final Set<TradeName> allowedBrands;

    public Set<TradeName> all() {
        return all;
    }

    public Set<TradeName> allowedBrands() {
        return allowedBrands;
    }

    public Contractors(Set<TradeName> contractors, Set<TradeName> allowedBrands) {
        this.all = contractors;
        this.allowedBrands = allowedBrands;
    }

}
