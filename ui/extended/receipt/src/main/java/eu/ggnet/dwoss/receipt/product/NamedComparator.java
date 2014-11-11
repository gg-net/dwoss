/* 
 * Copyright (C) 2014 pascal.perau
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
package eu.ggnet.dwoss.receipt.product;

import java.util.Comparator;

import eu.ggnet.dwoss.spec.entity.INamed;

/**
 *
 * @author oliver.guenther
 */
public class NamedComparator implements Comparator<INamed> {

    @Override
    public int compare(INamed o1, INamed o2) {
        if ( o1 == o2 ) return 0;
        if ( o1 == null ) return -1;
        if ( o2 == null ) return +1;
        if ( o1.getName() == o2.getName() ) return 0;
        if ( o1.getName() == null ) return -1;
        return o1.getName().compareTo(o2.getName());
    }

}
