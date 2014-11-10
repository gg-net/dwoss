package eu.ggnet.dwoss.redtape.renderer;

import java.awt.Color;
import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.border.EtchedBorder;

import eu.ggnet.dwoss.redtape.entity.Document;
import eu.ggnet.dwoss.redtape.format.DocumentFormater;

import eu.ggnet.dwoss.rules.DocumentType;

/**
 *
 * @author pascal.perau
 */
public class DocumentStringRenderer extends DefaultListCellRenderer {

    public DocumentStringRenderer() {
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel)super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        setBorder(new EtchedBorder(1, new Color(204, 204, 255), Color.BLACK));

        Document doc = ((Document)value);
        switch(doc.getType()){
            case ORDER:
                label.setText(!doc.getDossier().getActiveDocuments(DocumentType.INVOICE).isEmpty()
                        ? "<html>" + DocumentFormater.toHtmlSimple(((Document)value)) + "</html>"
                        : "<html>" + DocumentFormater.toHtmlDetailed(((Document)value)) + "</html>");
                break;
            case COMPLAINT:
                label.setText(!doc.getDossier().getActiveDocuments(DocumentType.CREDIT_MEMO).isEmpty()
                        || !doc.getDossier().getActiveDocuments(DocumentType.ANNULATION_INVOICE).isEmpty()
                        ? "<html>" + DocumentFormater.toHtmlSimple(((Document)value)) + "</html>"
                        : "<html>" + DocumentFormater.toHtmlDetailed(((Document)value)) + "</html>");
                break;
            case INVOICE:
            case BLOCK:
            case CREDIT_MEMO:
            case RETURNS:
            case ANNULATION_INVOICE:
            case CAPITAL_ASSET:
                label.setText("<html>" + DocumentFormater.toHtmlDetailed(((Document)value)) + "</html>");
        }
        if ( !isSelected ) {
            if ( ((Document)value).getFlags().contains(Document.Flag.CUSTOMER_EXACTLY_BRIEFED) ) {
                label.setBackground(Color.GREEN);
                label.setForeground(Color.BLACK);
            } else if ( ((Document)value).getFlags().contains(Document.Flag.CUSTOMER_BRIEFED) ) {
                label.setBackground(Color.YELLOW);
                label.setForeground(Color.BLACK);
            } else {
                label.setBackground(Color.white);
                label.setForeground(Color.BLACK);
            }
        }

        return label;
    }
}
