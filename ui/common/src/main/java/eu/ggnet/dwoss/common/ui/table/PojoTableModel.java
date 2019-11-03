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
package eu.ggnet.dwoss.common.ui.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author oliver.guenther
 */
public class PojoTableModel<T> extends AbstractTableModel {

    private List<T> dataModel;

    private List<PojoColumn<T>> columns;

    private JTable table;

    private PojoFilter<T> filter;

    public PojoTableModel(List<T> dataModel, PojoColumn<T>... columns) {
        this.dataModel = dataModel;
        this.columns = new ArrayList<PojoColumn<T>>();
        if ( columns != null && columns.length > 0 ) this.columns.addAll(Arrays.asList(columns));
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if ( table == null ) return String.class;
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
//        columns.get(columnIndex).setValue(rowIndex, aValue);
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
        if ( table == null ) return "View @ Model is null";
        return columns.get(columnIndex).getValue(dataModel.get(rowIndex));
    }

    public List<T> getLines() {
        return dataModel;
    }

    public void remove(T t) {
        dataModel.remove(t);
        fireTableDataChanged();
    }

    public void add(T t) {
        dataModel.add(t);
        fireTableRowsInserted(dataModel.size() - 1, dataModel.size() - 1);
    }

    public T getSelected() {
        if ( table.getSelectedRow() == -1 ) return null;
        return dataModel.get(table.convertRowIndexToModel(table.getSelectedRow()));
    }

    public PojoTableModel<T> add(PojoColumn<T> column) {
        columns.add(column);
        return this;
    }

    public JTable getTable() {
        return table;
    }

    public void setTable(JTable table) {
        if ( table == null ) return;
        this.table = table;
        for (int i = 0; i < columns.size(); i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(columns.get(i).getPreferredWidth());
        }
        TableRowSorter<PojoTableModel<T>> rowSorter = new TableRowSorter<PojoTableModel<T>>(this);
        rowSorter.setRowFilter(new RowFilter<PojoTableModel<T>, Integer>() {
            @Override
            public boolean include(RowFilter.Entry<? extends PojoTableModel<T>, ? extends Integer> entry) {
                if ( filter == null ) return true;
                return filter.filter(entry.getModel().getLines().get(entry.getIdentifier()));
            }
        });
        table.setRowSorter(rowSorter);
    }

    public PojoFilter<T> getFilter() {
        return filter;
    }

    public void setFilter(PojoFilter<T> filter) {
        this.filter = filter;
    }
}
