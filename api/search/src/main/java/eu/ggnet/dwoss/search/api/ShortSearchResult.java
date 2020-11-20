/*
 * Copyright (C) 2017 GG-Net GmbH
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
package eu.ggnet.dwoss.search.api;

import java.io.Serializable;
import java.util.Objects;

/**
 * The short result of a search.
 *
 * @author oliver.guenther
 */
public class ShortSearchResult implements Serializable {

    /**
     * Global Key of the result.
     */
    public final GlobalKey key;

    /**
     * A short description of the result.
     */
    public final String shortDescription;

    public ShortSearchResult(GlobalKey key, String shortDescription) {
        this.key = Objects.requireNonNull(key, "key must not be null");
        this.shortDescription = Objects.requireNonNull(shortDescription, "shortDescription must not be null");
        if (shortDescription.trim().isEmpty()) throw new IllegalArgumentException("shortDescription must not be blank");
    }

    //<editor-fold defaultstate="collapsed" desc="hashCode and equals of key">
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + Objects.hashCode(this.key);
        return hash;
    }
    
    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        final ShortSearchResult other = (ShortSearchResult)obj;
        if ( !Objects.equals(this.key, other.key) ) return false;
        return true;
    }
    //</editor-fold>

    @Override
    public String toString() {
        return "ShortSearchResult{" + "key=" + key + ", shortDescription=" + shortDescription + '}';
    }

}
