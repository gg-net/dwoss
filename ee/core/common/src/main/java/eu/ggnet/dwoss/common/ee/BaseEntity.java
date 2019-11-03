/*
 * Copyright (C) 2019 GG-Net GmbH
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
package eu.ggnet.dwoss.common.ee;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Abtract Superclass for Entity classes.
 * Includes equals and hashCode based on Id.
 *
 * @author oliver.guenther
 */
public abstract class BaseEntity implements Serializable {

    public abstract long getId();

    @Override
    public int hashCode() {
        int hash = this.getClass().getSimpleName().length();
        hash = this.getClass().getPackage().getName().length() * hash + (int)(this.getId() ^ (this.getId() >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object that) {
        if ( this == that ) return true;
        if ( that == null ) return false;
        if ( getClass() != that.getClass() ) return false;
        final BaseEntity other = (BaseEntity)that;
        return this.getId() == other.getId();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    /*
    // Alternative implementation: If Persisted uses id otherwise object identity.

    @Override
    public final int hashCode() {
        if (getId() == 0) return super.hashCode(); // Not persisted.
        return this.getClass().hashCode() * 7 + (int)(this.getId() ^ (this.getId() >>> 32));
    }

    @Deprecated
    public boolean equals(Object two) {
        Object one = this;
        if ( two == null ) return false;
        if ( one.getClass() != two.getClass() ) return false;
        final BaseEntity other = (BaseEntity)two;
        if ( one.getId() == 0 && other.getId() == 0 ) return one == other; // Not persisted use object identity
        return one.getId() == other.getId();
    }

     */
}
