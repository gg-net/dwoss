package eu.ggnet.dwoss.receipt;

import java.util.*;

import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.table.TableRowSorter;

import eu.ggnet.dwoss.util.INoteModel;
import eu.ggnet.dwoss.util.table.Column;
import eu.ggnet.dwoss.util.table.IColumnGetAction;
import eu.ggnet.dwoss.util.table.IColumnGetSetAction;
import eu.ggnet.dwoss.util.table.SimpleTableModel;

/**
 *
 * @author pascal.perau
 */
public class CheckBoxTableNoteModel<T extends INoteModel> extends SimpleTableModel<T>{

    private Set<T> marked;

    private JTable table;

    private TableRowSorter<CheckBoxTableNoteModel<T>> rowSorter;

    public CheckBoxTableNoteModel(final List<T> lines, String columnTitle) {
        super(lines);
        marked = new HashSet<T>();
        addColumn(new Column<T>("", true, 0, Boolean.class, new IColumnGetSetAction() {

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
        addColumn(new Column<T>(columnTitle, false, 1000, String.class, new IColumnGetAction() {
            @Override
            public String getValue(int row) {
                return lines.get(row).getNote();
            }
        }));
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
