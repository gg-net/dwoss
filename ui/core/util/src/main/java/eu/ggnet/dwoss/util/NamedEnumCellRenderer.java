package eu.ggnet.dwoss.util;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

import eu.ggnet.dwoss.util.PojoUtil;

/**
 *
 * @author pascal.perau
 */
public class NamedEnumCellRenderer extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel)super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (value == null) return label;        
        try {
            label.setText((String)PojoUtil.getValue("note", value));
        } catch (RuntimeException e) {
            try {
                label.setText((String)PojoUtil.getValue("name", value));
            } catch (RuntimeException ex) {}
        }
        return label;
    }
}
