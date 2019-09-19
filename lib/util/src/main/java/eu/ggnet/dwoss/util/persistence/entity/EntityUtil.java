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

@Deprecated
public class EntityUtil {

    @Deprecated
    public static boolean equals(Identifiable one, Object two) {
        if ( one == null ) throw new NullPointerException("This Util expects the first element to be not null");
        if ( two == null ) return false;
        if ( one.getClass() != two.getClass() ) return false;
        final Identifiable other = (Identifiable)two;
        if ( one.getId() == 0 && other.getId() == 0 ) return one == other;
        return one.getId() == other.getId();
    }

    @Deprecated
    public static int hashCode(Identifiable one, int seed) {
        if ( one == null ) throw new NullPointerException("This Util expects the first element to be not null");
        if ( one.getId() == 0 ) throw new IllegalArgumentException("If the Identifieable is not persisted, it must call super.hashCode by himself");
        return seed * 7 + (int)(one.getId() ^ (one.getId() >>> 32));
    }
}
