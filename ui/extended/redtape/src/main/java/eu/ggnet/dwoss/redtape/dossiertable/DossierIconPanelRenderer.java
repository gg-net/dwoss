package eu.ggnet.dwoss.redtape.dossiertable;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import eu.ggnet.dwoss.redtape.dossiertable.DossierTableController.IMAGE_NAME;
import eu.ggnet.dwoss.redtape.entity.Document;
import eu.ggnet.dwoss.redtape.entity.Dossier;

import eu.ggnet.dwoss.redtape.entity.Document.Condition;

import eu.ggnet.dwoss.rules.DocumentType;


/**
 *
 * @author pascal.perau
 */
public class DossierIconPanelRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        List<JLabel> labels = new ArrayList<>();
        //Get the Dossier status dependant icons
        Dossier dos = (Dossier)value;

        if ( dos.isClosed() ) {
            JLabel closedLabel = new JLabel(new ImageIcon(DossierTableController.load(IMAGE_NAME.CLOSED_ICON)));
            closedLabel.setToolTipText("Vorgang ist abgeschlossen");
            labels.add(closedLabel);
        }

        JLabel complaintLabel = new JLabel();
        if ( !dos.getActiveDocuments(DocumentType.COMPLAINT).isEmpty() ) {
            complaintLabel.setIcon(new ImageIcon(DossierTableController.load(IMAGE_NAME.COMPLAINT_ICON)));
            complaintLabel.setToolTipText("Eine Reklamation liegt vor");
        }

        boolean canceledExist = false;
        for (Document document : dos.getDocuments()) {
            if ( !canceledExist && document.getConditions().contains(Condition.CANCELED) ) {
                JLabel canceledLabel = new JLabel(new ImageIcon(DossierTableController.load(IMAGE_NAME.CANCELED_ICON)));
                canceledLabel.setToolTipText("Vorgang wurde storniert");
                labels.add(canceledLabel);
                canceledExist = true;
            }
            if ( document.getConditions().contains(Condition.REJECTED) ) {
                complaintLabel.setIcon(new ImageIcon(DossierTableController.load(IMAGE_NAME.COMPLAINT_REJECTED_ICON)));
                complaintLabel.setToolTipText("Eine Reklamation liegt vor");
            } else if ( document.getConditions().contains(Condition.WITHDRAWN) ) {
                complaintLabel.setIcon(new ImageIcon(DossierTableController.load(IMAGE_NAME.COMPLAINT_WITHDRAWN_ICON)));
                complaintLabel.setToolTipText("Eine Reklamation wurde zurückgezogen");
            } else if ( document.getConditions().contains(Condition.ACCEPTED) ) {
                complaintLabel.setIcon(new ImageIcon(DossierTableController.load(IMAGE_NAME.COMPLAINT_ACCEPTED_ICON)));
                complaintLabel.setToolTipText("Eine Reklamation wurde angenommen");
            }
        }
        if ( complaintLabel.getIcon() != null ) labels.add(complaintLabel);
        if ( !dos.getActiveDocuments(DocumentType.ANNULATION_INVOICE).isEmpty() ) {
            JLabel annulationLabel = new JLabel(new ImageIcon(DossierTableController.load(IMAGE_NAME.ANNULATION_INVOICE_ICON)));
            annulationLabel.setToolTipText("Eine Stornorechnung liegt vor");
            labels.add(annulationLabel);
        }
        if ( !dos.getActiveDocuments(DocumentType.CREDIT_MEMO).isEmpty() ) {
            JLabel creditMemoLabel = new JLabel(new ImageIcon(DossierTableController.load(IMAGE_NAME.CREDIT_MEMO_ICON)));
            creditMemoLabel.setToolTipText("Eine Gutschrift liegt vor");
            labels.add(creditMemoLabel);
        }

        //create and fill a panel with all icons
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        String tooltip = "";
        for (Iterator<JLabel> it = labels.iterator(); it.hasNext();) {
            JLabel jLabel = it.next();
            panel.add(jLabel);
            if ( it.hasNext() ) tooltip += jLabel.getToolTipText() + " | ";
            else tooltip += jLabel.getToolTipText();
        }
        if ( isSelected ) panel.setBackground(table.getSelectionBackground());
        else panel.setBackground(Color.white);
        panel.setToolTipText(tooltip);
        return panel;
    }
}
