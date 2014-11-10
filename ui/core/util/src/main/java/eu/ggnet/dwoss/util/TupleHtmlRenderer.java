package eu.ggnet.dwoss.util;

import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.border.EtchedBorder;

import eu.ggnet.dwoss.util.Tuple2;


/**
 *
 * @author pascal.perau
 */
public class TupleHtmlRenderer extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel)super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if ( value instanceof Tuple2 ) {
            label.setText("<html>" + ((Tuple2<Long, String>)value)._2 + "</html>");
            label.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        }
        return label;
    }
}
