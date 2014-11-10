package eu.ggnet.dwoss.misc.saleschannel;

import java.awt.Component;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;

import eu.ggnet.dwoss.stock.entity.Stock;

/**
 * Cell Renderer for the Stock Class.
 * <p>
 * @author oliver.guenther
 */
public class StockTableCellRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if ( value instanceof Stock ) setText(((Stock)value).getName());
        if ( value == null ) setText("Kein Transfer");
        return this;
    }
}
