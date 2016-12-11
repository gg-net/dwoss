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
 *
 */
public interface SAction<T> {
    /**
     * Returns a possible modified value
     *
     * @param relativeColumnIndex the relative column index (start counting at the parent object)
     * @param relativeRowIndex the relative row index (start counting at the parent object)
     * @param absoluteColumnIndex the absolute column index (start counting at the top of the resulting sheet)
     * @param absoluteRowIndex the absolute row index (start counting at the top of the resulting sheet)
     * @param lineModel the lineModel
     * @return a value for the sheet used in a CCell
     */
    Object getValue(int relativeColumnIndex, int relativeRowIndex, int absoluteColumnIndex, int absoluteRowIndex, T lineModel);

    /**
     * Returns a possible null Format for the a CCell
     *
     * @param relativeColumnIndex the relative column index (start counting at the parent object)
     * @param relativeRowIndex the relative row index (start counting at the parent object)
     * @param absoluteColumnIndex the absolute column index (start counting at the top of the resulting sheet)
     * @param absoluteRowIndex the absolute row index (start counting at the top of the resulting sheet)
     * @param lineModel the lineModel
     * @return a possible null Format for the a CCell
     */
    CFormat getFormat(int relativeColumnIndex, int relativeRowIndex, int absoluteColumnIndex, int absoluteRowIndex, T lineModel);

}
