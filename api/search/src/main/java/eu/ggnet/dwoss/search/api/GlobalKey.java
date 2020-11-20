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
 * Global Key to identify an entity uniquely.
 *
 * @author oliver.guenther
 */
public class GlobalKey implements Serializable {

    /**
     * Identifies an application component. For now, we know which components exist in every final depolyment.
     * If this changes in the future, we must convert the componets to some string representation.
     */
    public static enum Component {
        CUSTOMER, UNIQUE_UNIT, UNIQUE_PRODUCT, REDTAPE_DOCUMENT_INVOICE
    }

    public final Component component;

    /**
     * A unique database identifier.
     */
    public final long id;

    public GlobalKey(Component component, long id) {
        this.component = component;
        this.id = id;
    }

    //<editor-fold defaultstate="collapsed" desc="hashcode and equals of all">
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + Objects.hashCode(this.component);
        hash = 41 * hash + (int)(this.id ^ (this.id >>> 32));
        return hash;
    }
    
    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        final GlobalKey other = (GlobalKey)obj;
        if ( this.id != other.id ) return false;
        if ( this.component != other.component ) return false;
        return true;
    }
    //</editor-fold>
    
    @Override
    public String toString() {
        return "GlobalKey{" + "component=" + component + ", id=" + id + '}';
    }
    
}
