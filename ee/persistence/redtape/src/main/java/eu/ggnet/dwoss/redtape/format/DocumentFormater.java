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
package eu.ggnet.dwoss.redtape.format;

import java.util.*;

import eu.ggnet.dwoss.redtape.entity.Document;
import eu.ggnet.dwoss.redtape.entity.Position;

import eu.ggnet.dwoss.rules.DocumentType;

import eu.ggnet.dwoss.util.DateFormats;

/**
 *
 * @author oliver.guenther
 */
public class DocumentFormater {

    public static String toHtmlDetailed(Document doc) {
        String res = toHtmlSimple(doc);
        res += "Anweisung: " + doc.getDirective().getName() + "<br />";
        return res;
    }

    public static String toHtmlDetailedWithPositions(Document doc) {
        String res = toHtmlDetailed(doc) + "<br />";
        res += "<b>" + (doc.isClosed() ? "<font color=\"#666600\">Dokument geschlossen" : "<font color=\"#009900\">Dokument offen") + "</font></b><br />";
        res += "Document.id=" + doc.getId() + "<br />";
        res += "<br />";
        if ( !doc.getPositions().values().isEmpty() ) {
            res += "<ul>";
            for (Position position : doc.getPositions().values()) {
                res += "<li>" + PositionFormater.toHtmlDetailed(position) + "</li>";
            }
            res += "</ul>";
        }
        return res;
    }

    /**
     * This method generates a html formated string.
     * Make sure to add the "<html> - </html>" tags manually.
     * <p/>
     * @param doc the Document
     * @return a html string representation of a document.
     */
    public static String toHtmlSimple(Document doc) {
        String res = (doc.getType() == null) ? "<b>Dokumenttyp unbekannt</b>" : "<b>" + doc.getType().getName() + "</b>";
        res += (doc.getIdentifier() == null) ? "" : " <i>" + doc.getIdentifier() + "</i>";
        res += " von " + ((doc.getActual() != null) ? DateFormats.ISO.format(doc.getActual()) : "Kein Actual gesetzt") + "<br />";
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

    public static String toSimpleLine(Document doc) {
        if ( doc == null ) return "doc==null";
        return "Document("
                + "id=" + doc.getId() + ",idenifier=" + doc.getIdentifier()
                + ",dossierId=" + doc.getDossier().getId() + ",dossierIdentifier=" + doc.getDossier().getIdentifier()
                + ",positions=" + toShortPositions(doc);
    }

    public static String toConditions(Document doc) {
        String res = "";
        if ( doc.getConditions().isEmpty() ) return res;
        for (Iterator<Document.Condition> it = doc.getConditions().iterator(); it.hasNext();) {
            Document.Condition con = it.next();
            res += con.getName();
            if ( it.hasNext() ) res += ",";
        }
        return res;
    }

    private static List<String> toShortPositions(Document doc) {
        List<String> pos = new ArrayList<>();
        for (Position position : doc.getPositions().values()) {
            pos.add((position.getAmount() == 1 ? "" : position.getAmount() + "*") + "(" + position.getType() + ")" + position.getName());
        }
        return pos;
    }
}
