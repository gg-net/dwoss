/*
 * Copyright (C) 2014 GG-Net GmbH
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
package eu.ggnet.dwoss.uniqueunit.api;

import java.io.Serializable;
import java.util.Objects;

import eu.ggnet.saft.api.IdSupplier;

/**
 * The smallest representation of a unique unit.
 * <p>
 * @author oliver.guenther
 */
public class PicoUnit implements IdSupplier, Serializable {

    public static final String MIME_TYPE = "dw-api/picounit";

    public final int uniqueUnitId;

    public final String shortDescription;

    public PicoUnit(int uniqueUnitId, String shortDescription) {
        this.uniqueUnitId = uniqueUnitId;
        this.shortDescription = Objects.requireNonNull(shortDescription,"shortDescription must not be null");
        if (shortDescription.trim().isEmpty()) throw new IllegalArgumentException("shortDescription must not be empty");
    }

    @Override
    public String id() {
        return "" + uniqueUnitId;
    }

    //<editor-fold defaultstate="collapsed" desc="hashCode and Equals of uniqueUnitId">
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + this.uniqueUnitId;
        return hash;
    }
    
    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        final PicoUnit other = (PicoUnit)obj;
        if ( this.uniqueUnitId != other.uniqueUnitId ) return false;
        return true;
    }
//</editor-fold>

    @Override
    public String toString() {
        return "PicoUnit{" + "uniqueUnitId=" + uniqueUnitId + ", shortDescription=" + shortDescription + '}';
    }
    
}
