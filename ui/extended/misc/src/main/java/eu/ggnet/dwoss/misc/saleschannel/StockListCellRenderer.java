package eu.ggnet.dwoss.misc.saleschannel;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import eu.ggnet.dwoss.stock.entity.Stock;

/**
 * Cell Renderer for the Stock Class.
 * <p>
 * @author oliver.guenther
 */
public class StockListCellRenderer extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if ( value instanceof Stock ) setText(((Stock)value).getName());
        if ( value == null ) setText("Kein Transfer");
        return this;
    }
}
