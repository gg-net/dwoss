package eu.ggnet.dwoss.redtape.renderer;

import java.awt.Color;
import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.border.EtchedBorder;

import eu.ggnet.dwoss.redtape.entity.Dossier;
import eu.ggnet.dwoss.redtape.format.DossierFormater;

/**
 *
 * @author pascal.perau
 */
public class DossierStringRenderer extends DefaultListCellRenderer {

    public DossierStringRenderer() {
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel)super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        setBorder(new EtchedBorder(1, new Color(204, 204, 255), Color.BLACK));

        if ( value instanceof Dossier ) {
            label.setText("<html>" + DossierFormater.toHtmlSimple(((Dossier)value)) + "</html>");
        }
        return label;
    }
}
