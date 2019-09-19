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
package eu.ggnet.dwoss.util.persistence.entity;

/**
 * 
 * 
 * @author oliver.guenther
 * @deprecated use BaseEntity
 */
@Deprecated
public abstract class IdentifiableEntity implements Identifiable {

    @Override
    public final boolean equals(Object obj) {
        return EntityUtil.equals(this, obj);
    }

    @Override
    public final int hashCode() {
        if (getId() == 0) return super.hashCode();
        return EntityUtil.hashCode(this, this.getClass().hashCode());
    }

}
