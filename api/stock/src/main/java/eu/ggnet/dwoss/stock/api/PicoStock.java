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
 * Pico implementation of Stock.
 *
 * @author oliver.guenther
 */
public class PicoStock implements Serializable {

    public final int id;

    public final String shortDescription;

    public PicoStock(int id, String shortDescription) {
        this.id = id;
        this.shortDescription = Objects.requireNonNull(shortDescription, "shortDescription must not be null");
        if ( shortDescription.trim().isEmpty() ) throw new IllegalArgumentException("shortDescription must not be empty");
    }

    //<editor-fold defaultstate="collapsed" desc="equals and hashcode of id">
    // Used in Ui ActiveStock
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 43 * hash + this.id;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        final PicoStock other = (PicoStock)obj;
        if ( this.id != other.id ) return false;
        return true;
    }
//</editor-fold>

    @Override
    public String toString() {
        return "PicoStock{" + "id=" + id + ", shortDescription=" + shortDescription + '}';
    }

}
