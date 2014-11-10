package eu.ggnet.dwoss.redtape.position;

import java.awt.Color;
import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.border.EtchedBorder;

import eu.ggnet.dwoss.redtape.entity.Position;

/**
 *
 * @author pascal.perau
 */
public class Tuple2PositionRenderer extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel)super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if ( value instanceof Position ) {
            label.setBorder(new EtchedBorder(1, Color.lightGray, Color.DARK_GRAY));
            label.setText("<html>" + ((Position)value).getName() + "<br />" + ((Position)value).getDescription() + "</html>");
        }
        return label;
    }
}
