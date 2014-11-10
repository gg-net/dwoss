package eu.ggnet.dwoss.redtape.renderer;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.EtchedBorder;

import eu.ggnet.dwoss.redtape.entity.Position;
import eu.ggnet.dwoss.redtape.format.PositionFormater;

/**
 *
 * @author pascal.perau
 */
public class PositionStringRenderer extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        String content;
        if ( value instanceof Position ) {
            content = "<html>" + PositionFormater.toHtmlShort(((Position)value), ((int)(list.getWidth() - (list.getWidth() * 0.23))) + "px") + "</html>";
        } else {
            content = value.toString();
        }
        JLabel label = (JLabel)super.getListCellRendererComponent(list, content, index, isSelected, cellHasFocus);
        setBorder(new EtchedBorder(1, new Color(204, 204, 255), Color.BLACK));
        return label;
    }
}
