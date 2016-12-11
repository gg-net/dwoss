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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SBlock implements IDynamicCellContainer {

    private CFormat format;
    // List(r,c) 1st index is row, second column

    private List<List<SCell>> cells;

    public SBlock() {
        cells = new ArrayList<>();
    }

    /**
     * Simple Value
     *
     * @param value
     * @param format
     * @param newline
     */
    public SBlock(String value, CFormat format, boolean newline) {
        this();
        this.format = format;
        add(value);
        if ( newline ) add();
    }

    public SBlock(SBlock old) {
        this();
        if ( old == null ) return;
        this.format = old.format;
        for (List<SCell> oldRow : old.cells) {
            List<SCell> row = new ArrayList<>(oldRow.size());
            for (SCell cell : oldRow) {
                row.add(cell);
            }
            this.cells.add(row);
        }
    }

    /**
     * Sets a Format to be used in the whole block.
     * <p/>
     * @param format the fromat.
     */
    public void setFormat(CFormat format) {
        this.format = format;
    }

    public CFormat getFormat() {
        return format;
    }

    public List<List<SCell>> getContent() {
        return cells;
    }

    /**
     * Adds a line of elements to the block.
     * The following logic is used:
     * <ul>
     * <li>If an element is of type {@link SCell} it is added directly </li>
     * <li>If an element is of anything else as {@link SCell} or {@link CFormat}
     * but followed by {@link CFormat} a new {@link SCell} out of both is created</li>
     * <li>Everything else is only treated as an Object of data without Format information</li>
     * </ul>
     *
     * @param elems
     */
    public final void add(Object... elems) {
        List<SCell> row = new ArrayList<>();
        cells.add(row);
        if ( elems == null ) return;
        if ( elems.length == 1 ) {
            if ( elems[0] instanceof SCell ) row.add((SCell)elems[0]);
            else row.add(new SCell(elems[0]));
            return;
        }
        for (int i = 0; i < elems.length; i++) {
            if ( elems[i] instanceof SCell ) row.add((SCell)elems[i]);
            else if ( ((i + 1) < elems.length) && (elems[i + 1] instanceof CFormat) ) {
                row.add(new SCell(elems[i], (CFormat)elems[i + 1]));
                i++;
            } else row.add(new SCell(elems[i]));
        }
    }

    @Override
    public CCellComposite shiftTo(int columnIndex, int rowIndex) {
        return new CCellComposite(getCellsShiftedTo(columnIndex, rowIndex));
    }

    @Override
    public Set<CCell> getCellsShiftedTo(int toColumnIndex, int toRowIndex) {
        Set<CCell> ccells = new HashSet<>();
        for (int rowIndex = 0; rowIndex < cells.size(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < cells.get(rowIndex).size(); columnIndex++) {
                SCell scell = cells.get(rowIndex).get(columnIndex);
                CCell ccell = new CCell(toColumnIndex + columnIndex, toRowIndex + rowIndex,
                        scell.getValue(), CFormat.combine(scell.getFormat(), format));
                scell.setReference(ccell);
                ccells.add(ccell);
            }
        }
        return ccells;
    }

    @Override
    public int getRowCount() {
        return cells.size();
    }

    @Override
    public int getColumnCount() {
        int count = 0;
        for (List<SCell> row : cells) {
            count = Math.max(row.size(), count);
        }
        return count;
    }
}
