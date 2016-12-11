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
 * The RowView
 */
public class CRowView {

    private final int rowIndex;
    private final int size;

    public CRowView(int rowIndex, int size) {
        this.rowIndex = rowIndex;
        this.size = size;
    }

    /**
     * Get the value of rowSize
     *
     * @return the value of rowSize
     */
    public int getSize() {
        return size;
    }


    /**
     * Get the value of rowIndex
     *
     * @return the value of rowIndex
     */
    public int getRowIndex() {
        return rowIndex;
    }


}
