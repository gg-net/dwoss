/*
 * Copyright (C) 2014 GG-Net GmbH - Oliver Günther
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
package eu.ggnet.dwoss.redtapext.ui.cao.common;

import java.awt.Color;
import java.awt.Component;
import java.util.Iterator;

import javax.swing.*;
import javax.swing.border.EtchedBorder;

import eu.ggnet.dwoss.configuration.GlobalConfig;
import eu.ggnet.dwoss.redtape.entity.Document;
import eu.ggnet.dwoss.rules.DocumentType;
import eu.ggnet.dwoss.util.DateFormats;

import static eu.ggnet.dwoss.redtape.format.DocumentFormater.toConditions;

/**
 *
 * @author pascal.perau
 */
public class DocumentStringRenderer extends DefaultListCellRenderer {

    public static String toHtmlDetailed(Document doc, boolean showTaxType) {
        String res = toHtmlSimple(doc, showTaxType);
        res += "Anweisung: " + doc.getDirective().getName() + "<br />";
        return res;
    }

    /**
     * This method generates a html formated string.
     * Make sure to add the "<html> - </html>" tags manually.
     * <p/>
     * @param doc the Document
     * @return a html string representation of a document.
     */
    public static String toHtmlSimple(Document doc, boolean showTaxType) {
        String res = (doc.getType() == null) ? "<b>Dokumenttyp leer (null)</b>" : "<b>" + doc.getType().getName() + "</b>";
        res += (doc.getIdentifier() == null) ? "" : " <i>" + doc.getIdentifier() + "</i>";
        res += " von " + ((doc.getActual() != null) ? DateFormats.ISO.format(doc.getActual()) : "Kein Actual gesetzt");
        if ( showTaxType ) res += " (" + doc.getTaxType().getName() + ")";
        res += "<br />";
        res += (doc.getConditions().isEmpty() ? "" : toConditions(doc) + "<br />");
        if ( doc.getFlags().contains(Document.Flag.CUSTOMER_EXACTLY_BRIEFED) ) {
            res += "Aktuelle Version liegt dem Kunden vor.<br />";
        } else if ( doc.getFlags().contains(Document.Flag.CUSTOMER_BRIEFED) ) {
            res += "Eine nicht aktuelle Version liegt dem Kunden vor.<br />";
        } else {
            res += "Dieses Dokument liegt dem Kunden noch nicht vor.<br />";
        }
        res += "Positionen: " + doc.getPositions().size() + "<br />";
        if ( !doc.getSettlements().isEmpty() && doc.getType() == DocumentType.INVOICE ) {
            res += "Gezahlt via ";
            for (Iterator<Document.Settlement> it = doc.getSettlements().iterator(); it.hasNext();) {
                Document.Settlement settlement = it.next();
                res += settlement.getName();
                if ( it.hasNext() ) res += " / ";
            }
            res += "<br />";
        }
        return res;
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel)super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        setBorder(new EtchedBorder(1, new Color(204, 204, 255), Color.BLACK));

        Document doc = ((Document)value);
        switch (doc.getType()) {
            case ORDER:
                label.setText(!doc.getDossier().getActiveDocuments(DocumentType.INVOICE).isEmpty()
                        ? "<html>" + toHtmlSimple(doc, false) + "</html>"
                        : "<html>" + toHtmlDetailed(doc, doc.getTaxType() != GlobalConfig.DEFAULT_TAX) + "</html>");
                break;
            case COMPLAINT:
                label.setText(!doc.getDossier().getActiveDocuments(DocumentType.CREDIT_MEMO).isEmpty()
                        || !doc.getDossier().getActiveDocuments(DocumentType.ANNULATION_INVOICE).isEmpty()
                        ? "<html>" + toHtmlSimple(doc, false) + "</html>"
                        : "<html>" + toHtmlDetailed(doc, doc.getTaxType() != GlobalConfig.DEFAULT_TAX) + "</html>");
                break;
            case INVOICE:
            case BLOCK:
            case CREDIT_MEMO:
            case RETURNS:
            case ANNULATION_INVOICE:
            case CAPITAL_ASSET:
                label.setText("<html>" + toHtmlDetailed(doc, doc.getTaxType() != GlobalConfig.DEFAULT_TAX) + "</html>");
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
