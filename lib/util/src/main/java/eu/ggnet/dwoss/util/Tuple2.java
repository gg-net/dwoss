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
package eu.ggnet.dwoss.util;

import java.io.Serializable;

/**
 *
 * @author oliver.guenther
 * @param <O>
 * @param <T>
 */
public class Tuple2<O, T> implements Serializable {

    public final O _1;

    public final T _2;

    /**
     * @param _1
     * @param _2
     */
    public Tuple2(O _1, T _2) {
        super();
        this._1 = _1;
        this._2 = _2;
    }

    public O _1() {
        return _1;
    }

    public T _2() {
        return _2;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((_1 == null) ? 0 : _1.hashCode());
        result = prime * result + ((_2 == null) ? 0 : _2.hashCode());
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        Tuple2 other = (Tuple2)obj;
        if ( _1 == null ) {
            if ( other._1 != null ) return false;
        } else if ( !_1.equals(other._1) ) return false;
        if ( _2 == null ) {
            if ( other._2 != null ) return false;
        } else if ( !_2.equals(other._2) ) return false;
        return true;
    }

    @Override
    public String toString() {
        return "Tuple2{" + "_1=" + _1 + ", _2=" + _2 + '}';
    }
}
