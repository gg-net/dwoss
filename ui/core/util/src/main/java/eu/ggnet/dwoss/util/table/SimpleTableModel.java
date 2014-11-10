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
