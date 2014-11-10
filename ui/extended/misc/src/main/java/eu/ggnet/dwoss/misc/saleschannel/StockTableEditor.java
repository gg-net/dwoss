package eu.ggnet.dwoss.misc.saleschannel;

import java.awt.Component;

import javax.swing.*;
import javax.swing.table.TableCellEditor;

/**
 *
 * @author oliver.guenther
 */
public class StockTableEditor extends AbstractCellEditor implements TableCellEditor {

    private JComboBox box = null;

    public StockTableEditor(Object[] values) {
        box = new JComboBox(values);
        box.setRenderer(new StockListCellRenderer());
    }

    @Override
    public Object getCellEditorValue() {
        return box.getSelectedItem();
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        box.setSelectedItem(value);
        return box;
    }

}
