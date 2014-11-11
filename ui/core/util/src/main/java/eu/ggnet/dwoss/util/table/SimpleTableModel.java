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
package eu.ggnet.dwoss.util.table;

import java.util.*;

import javax.swing.table.AbstractTableModel;

/**
 * Makeing a stable an compile safe model
 */
public class SimpleTableModel<T> extends AbstractTableModel {

    private final List<T> dataModel;

    private final List<Column<T>> columns;

    public SimpleTableModel(List<T> dataModel, Column<T>... columns) {
        this.dataModel = dataModel;
        this.columns = new ArrayList<>();
        if ( columns != null && columns.length > 0 ) this.columns.addAll(Arrays.asList(columns));
    }

    public void addColumn(Column<T> column) {
        columns.add(column);
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return columns.get(columnIndex).getColumnClass();
    }

    @Override
    public String getColumnName(int column) {
        return columns.get(column).getHeadline();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columns.get(columnIndex).isEditable();
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        columns.get(columnIndex).setValue(rowIndex, aValue);
        fireTableRowsUpdated(rowIndex, rowIndex);
    }

    @Override
    public int getRowCount() {
        return dataModel.size();
    }

    @Override
    public int getColumnCount() {
        return columns.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return columns.get(columnIndex).getValue(rowIndex);
    }

    public int getPreferredWidth(int columnIndex) {
        return columns.get(columnIndex).getPreferredWidth();
    }

    public List<T> getDataModel() {
        return dataModel;
    }

    public void removeValue(T t) {
        dataModel.remove(t);
        fireTableDataChanged();
    }

    public void addValue(T t) {
        dataModel.add(t);
        fireTableDataChanged();
    }

}
