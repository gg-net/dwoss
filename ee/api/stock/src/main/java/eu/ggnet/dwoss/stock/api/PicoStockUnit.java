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
package eu.ggnet.dwoss.stock.api;

import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author oliver.guenther
 */
public class PicoStockUnit implements Serializable {

    public final long id;

    public final long uniqueUnitId;

    public final String shortDescription;

    public PicoStockUnit(long id, long uniqueUnitId, String shortDescription) {
        this.id = id;
        this.uniqueUnitId = uniqueUnitId;
        this.shortDescription = Objects.requireNonNull(shortDescription,"shortDescription must not be null");
        if (shortDescription.trim().isEmpty()) throw new IllegalArgumentException("shortDescription must not be empty");
    }

    @Override
    public String toString() {
        return "PicoStockUnit{" + "id=" + id + ", uniqueUnitId=" + uniqueUnitId + ", shortDescription=" + shortDescription + '}';
    }
    
}
