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

import java.util.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * A Simple Table which can be place in a Sheet
 */
// TODO: Make Table honor the type, generics
public class STable implements IDynamicCellContainer {

    private class DynamicCCellReference implements CCellReference {

        private int relativeColumnIndex;

        private int relativeRowIndex;

        private int toColumnIndex;

        private int toRowIndex;

        public DynamicCCellReference(int relativeColumnIndex, int relativeRowIndex) {
            this.relativeColumnIndex = relativeColumnIndex;
            this.relativeRowIndex = relativeRowIndex;
        }

        public void setToColumnIndex(int toColumnIndex) {
            this.toColumnIndex = toColumnIndex;
        }

        public void setToRowIndex(int toRowIndex) {
            this.toRowIndex = toRowIndex;
        }

        @Override
        public int getColumnIndex() {
            return relativeColumnIndex + toColumnIndex;
        }

        @Override
        public int getRowIndex() {
            return relativeRowIndex + toRowIndex;
        }
    }

    private class DynamicTableCCellReference extends DynamicCCellReference {

        public DynamicTableCCellReference(int relativeColumnIndex) {
            super(relativeColumnIndex, 0);
        }

        @Override
        public int getRowIndex() {
            return model.getRowCount() + super.toRowIndex;
        }
    }

    private List<STableColumn> columns;

    private List<DynamicCCellReference> cellReferences;

    @NotNull
    @Valid
    private STableModel model;

    private CFormat headlineFormat;

    private CFormat tableFormat;

    private Integer headlineHeight;

    private Integer rowHeight;

    private SRowFormater rowFormater;

    public STable() {
        this.columns = new ArrayList<>();
        this.cellReferences = new ArrayList<>();
    }

    public STable(STable old) {
        this();
        if ( old == null ) {
            return;
        }
        this.headlineFormat = old.headlineFormat;
        this.tableFormat = old.tableFormat;
        this.headlineHeight = old.headlineHeight;
        this.rowHeight = old.rowHeight;
        for (STableColumn oldColumn : old.columns) {
            columns.add(oldColumn);
        }
    }

    public void setModel(STableModel model) {
        this.model = model;
    }

    public void setHeadlineFormat(CFormat format) {
        this.headlineFormat = format;
    }

    public void setTableFormat(CFormat tableFormat) {
        this.tableFormat = tableFormat;
    }

    public Integer getHeadlineHeight() {
        return headlineHeight;
    }

    public void setHeadlineHeight(Integer headlineHeight) {
        this.headlineHeight = headlineHeight;
    }

    public Integer getRowHeight() {
        return rowHeight;
    }

    public void setRowHeight(Integer rowHeight) {
        this.rowHeight = rowHeight;
    }

    public void setRowFormater(SRowFormater rowFormater) {
        this.rowFormater = rowFormater;
    }

    public STable add(STableColumn o) {
        columns.add(o);
        return this;
    }

    public void replace(int index, STableColumn o) {
        columns.set(index, o);
    }

    public STableModel getModel() {
        return model;
    }

    @Override
    public int getRowCount() {
        int count = 1; // Inc. Headline
        if ( model != null ) {
            count += model.getRowCount();
        }
        return count;
    }

    @Override
    public int getColumnCount() {
        return columns.size();
    }

    /**
     * Returns a reference to the cell for the columnIndex and the first row
     *
     * @param columnIndex
     * @return a reference to the cell for the columnIndex and the first row
     */
    public CCellReference getCellFirstRow(int columnIndex) {
        DynamicCCellReference cell = new DynamicCCellReference(columnIndex, 1);
        cellReferences.add(cell);
        return cell;
    }

    /**
     * Returns a reference to the cell for the columnIndex and the last row
     *
     * @param columnIndex
     * @return a reference to the cell for the columnIndex and the last row
     */
    public CCellReference getCellLastRow(int columnIndex) {
        DynamicCCellReference cell = new DynamicTableCCellReference(columnIndex);
        cellReferences.add(cell);
        return cell;
    }

    @Override
    public CCellComposite shiftTo(int toColumnIndex, int toRowIndex) {
        Set<CCell> cells = getCellsShiftedTo(toColumnIndex, toRowIndex);
        Set<CColumnView> columnViews = new HashSet<>();
        Set<CRowView> rowViews = new HashSet<>();
        for (int i = 0; i < columns.size(); i++) {
            if ( columns.get(i).getSize() != null ) columnViews.add(new CColumnView(i + toColumnIndex, columns.get(i).getSize()));
        }
        if ( headlineHeight != null ) rowViews.add(new CRowView(toRowIndex, headlineHeight));
        for (int i = 1; i < getRowCount(); i++) {
            if ( rowHeight != null ) rowViews.add(new CRowView(toRowIndex + i, rowHeight));
        }
        return new CCellComposite(cells, columnViews, rowViews);
    }

    /**
     * Returns the Value at row and column.
     * Determines the value by taking the row of the model at rowIndex and:
     * <ul>
     * <li>If the column has an action, this action is used at the row.</li>
     * <li>If the row is an instance of List, it is casted to List and the element a columnIndex is returned.</li>
     * <li>If the row is an instance of Object[], it is casted to Object[] and the element a columnIndex is returned.</li>
     * <li>The row is returned.</li>
     * </ul>
     * <p>
     * @param toColumnIndex
     * @param toRowIndex
     * @return the Value at row and column.
     */
    @Override
    public Set<CCell> getCellsShiftedTo(int toColumnIndex, int toRowIndex) {
        for (DynamicCCellReference cell : cellReferences) {
            cell.setToColumnIndex(toColumnIndex);
            cell.setToRowIndex(toRowIndex);
        }

        Set<CCell> ccells = new HashSet<>();
        for (int columnIndex = 0; columnIndex < columns.size(); columnIndex++) {
            ccells.add(new CCell(columnIndex + toColumnIndex, 0 + toRowIndex, columns.get(columnIndex).getHead(),
                    CFormat.combine(headlineFormat, tableFormat)));
        }

        for (int columnIndex = 0; columnIndex < columns.size(); columnIndex++) {
            CFormat columnFormat = CFormat.combine(columns.get(columnIndex).getFormat(), tableFormat);
            for (int rowIndex = 1; rowIndex <= model.getRowCount(); rowIndex++) {
                SAction action = columns.get(columnIndex).getAction();
                Object row = model.getRow(rowIndex - 1); // Shift the model
                Object value = row;
                CFormat rowFormat = columnFormat;
                if ( rowFormater != null ) rowFormat = CFormat.combine(rowFormater.getFormat(rowIndex, row), columnFormat);
                if ( action != null ) {
                    value = action.getValue(columnIndex, rowIndex, columnIndex + toColumnIndex, rowIndex + toRowIndex, row);
                    rowFormat = CFormat.combine(action.getFormat(columnIndex, rowIndex, columnIndex + toColumnIndex, rowIndex + toRowIndex, row), rowFormat);
                } // TODO: Remove here and build a default Model on setModel
                else if ( row instanceof List ) value = ((List)row).get(columnIndex);
                else if ( row instanceof Object[] ) value = ((Object[])row)[columnIndex];
                ccells.add(new CCell(columnIndex + toColumnIndex, rowIndex + toRowIndex, value, rowFormat));
            }
        }

        return ccells;
    }
}
