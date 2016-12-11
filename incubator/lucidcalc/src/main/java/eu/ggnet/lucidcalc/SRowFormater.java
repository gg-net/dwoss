/* 
 * Copyright (C) 2014 GG-Net GmbH - Oliver Günther.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; If not, see <http://www.gnu.org/licenses/>.
 */
package eu.ggnet.lucidcalc;

/**
 * Allows to set the Format for a row
 *
 * @author oliver.guenther
 */
public interface SRowFormater<T> {

    /**
     * Returns a possible null Format for the a CCell
     *
     * @param relativeRowIndex the relative row index (start counting at the parent object)
     * @param lineModel the lineModel
     * @return a possible null Format for the a CCell
     */
    CFormat getFormat(int relativeRowIndex, T lineModel);

}
