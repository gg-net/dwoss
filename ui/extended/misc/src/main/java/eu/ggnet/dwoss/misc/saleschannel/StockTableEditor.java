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
