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
package eu.ggnet.dwoss.mandator.api.value;

import java.io.Serializable;

/**
 *
 * @author oliver.guenther
 */
public class Ledger implements Serializable {

    /**
     * The Ledger Value.
     */
    public final int value;

    public final String description;

    public Ledger(int value, String description) {
        this.value = value;
        this.description = description;
    }

    /**
     * The Ledger Value.
     *
     * @return
     * @deprecated use pulic field value.
     */
    @Deprecated
    public int getValue() {
        return value;
    }

    // TODO: Verifiy if this is needed.
    //<editor-fold defaultstate="collapsed" desc="Hashcode and Equals of Value">
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + this.value;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        final Ledger other = (Ledger)obj;
        if ( this.value != other.value ) return false;
        return true;
    }
    //</editor-fold>

    @Override
    public String toString() {
        return "Ledger{" + "value=" + value + ", description=" + description + '}';
    }

    public String toHtml() {
        return description + " (" + value + ")";
    }
}
