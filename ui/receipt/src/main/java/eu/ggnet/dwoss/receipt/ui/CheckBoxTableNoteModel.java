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
package eu.ggnet.dwoss.receipt.ui;

import eu.ggnet.dwoss.core.widget.swing.Column;
import eu.ggnet.dwoss.core.widget.swing.IColumnGetSetAction;
import eu.ggnet.dwoss.core.widget.swing.SimpleTableModel;

import java.util.*;

import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.table.TableRowSorter;

import eu.ggnet.dwoss.core.common.INoteModel;

/**
 * A generic model for a multiselect of enums.
 *
 * @author pascal.perau
 * @param <T> the type to be shown.
 */
public class CheckBoxTableNoteModel<T extends INoteModel> extends SimpleTableModel<T>{

    private Set<T> marked;

    private JTable table;

    private TableRowSorter<CheckBoxTableNoteModel<T>> rowSorter;

    public CheckBoxTableNoteModel(final List<T> lines, String columnTitle) {
        super(lines);
        marked = new HashSet<>();
        addColumn(new Column<T>("", true, 25, Boolean.class, new IColumnGetSetAction() {

            @Override
            public void setValue(int row, Object selected) {
                if ( (Boolean)selected ) marked.add(lines.get(row));
                else marked.remove(lines.get(row));
            }

            @Override
            public Object getValue(int row) {
                return marked.contains(lines.get(row));
            }
        }));
        addColumn(new Column<>(columnTitle, false, 200, String.class, (row) -> lines.get(row).getNote()));
    }

    public void setFiltered(final Collection<T> filtered) {
        if ( filtered == null ) rowSorter.setRowFilter(null);
        else rowSorter.setRowFilter(new RowFilter<CheckBoxTableNoteModel<T>, Integer>() {
                @Override
                public boolean include(RowFilter.Entry<? extends CheckBoxTableNoteModel<T>, ? extends Integer> entry) {
                    return filtered.contains(entry.getModel().getDataModel().get(entry.getIdentifier()));
                }
            });
        fireTableDataChanged();
    }

    public void setMarked(Collection<T> marks) {
        marked.clear();
        marked.addAll(marks);
        fireTableDataChanged();
    }

    public Set<T> getMarked() {
        return Collections.unmodifiableSet(marked);
    }

    public void setTable(JTable table) {
        if ( table == null ) return;
        this.table = table;
        for (int i = 0; i < getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(getPreferredWidth(i));
        }
        rowSorter = new TableRowSorter<>(this);
        table.setRowSorter(rowSorter);
    }
}
