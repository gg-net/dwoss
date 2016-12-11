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
 * CellReference implementation there the row can be changed by an Action
 */
public class SSelfRowReference implements CCellReference {

    private int columnIndex;
    private int rowIndex;

    public SSelfRowReference(int columnIndex) {
        this.columnIndex = columnIndex;
    }

    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    @Override
    public int getColumnIndex() {
        return columnIndex;
    }

    @Override
    public int getRowIndex() {
        return rowIndex;
    }

}
