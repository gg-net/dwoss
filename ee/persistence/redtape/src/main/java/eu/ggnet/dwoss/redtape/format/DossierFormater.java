package eu.ggnet.dwoss.redtape.format;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import eu.ggnet.dwoss.redtape.entity.Document;
import eu.ggnet.dwoss.redtape.entity.Document.Condition;
import eu.ggnet.dwoss.redtape.entity.Document.Settlement;
import eu.ggnet.dwoss.redtape.entity.Dossier;

import eu.ggnet.dwoss.rules.DocumentType;

/**
 * This class gives multiple possibilities to get html formated representation of RedTape entities.
 * No start or end tags are set at any time so every method can be stacked with the other.
 * <p/>
 * @author pascal.perau
 */
public class DossierFormater {

    /**
     * Generates a Html formated String representing the history of a Dossier.
     * <p/>
     * @param dos The Dossier
     * @return a Html formated String representing the history of a Dossier.
     */
    public static String toHtmlHistory(Dossier dos) {
        Comparator.comparing((Document d) -> d.getHistory().getRelease());
        Collection<Document> docs = new TreeSet<>((Document o1, Document o2) -> o1.getHistory().getRelease().compareTo(o2.getHistory().getRelease()));
        docs.addAll(dos.getDocuments());
        String res = "<h2>Verlauf von " + dos.getIdentifier() + ":</h2><br />";
        for (Document doc : docs) {
            String cons = "";
            String settlements = "";
            for (Iterator<Condition> it = doc.getConditions().iterator(); it.hasNext();) {
                Condition con = it.next();
                cons += con.getName();
                if ( it.hasNext() ) cons += ",";
            }
            for (Iterator<Settlement> it = doc.getSettlements().iterator(); it.hasNext();) {
                Settlement set = it.next();
                settlements += set.getName();
                if ( it.hasNext() ) settlements += ",";
            }

            res += "<p>";
            res += "<b>" + doc.getType().getName() + (doc.getIdentifier() != null ? ": " + doc.getIdentifier() : "") + " | " + (doc.isClosed() ? "geschlossen" : "offen")
                    + (doc.getInvoiceAddress() == null ? "" : " | Adress Ids: (RE: " + doc.getShippingAddress().getId() + " | LI: " + doc.getShippingAddress().getId()) + ")</b><br />";
            res += (cons.isEmpty() ? "" : "Zustände: " + cons + " | ") + (doc.getDirective() == null ? "" : "Direktive: " + doc.getDirective().getName()) + "<br />";
            res += "Erstellt am: " + SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.SHORT, Locale.GERMANY).format(doc.getHistory().getRelease()) + " von " + doc.getHistory().getArranger() + "<br />";
            res += "Anmerkung: " + doc.getHistory().getComment() + "<br />";
            res += doc.getPositions().size() != 0 ? "Positionen: " + doc.getPositions().size()
                    + " | Nettosumme: " + NumberFormat.getCurrencyInstance().format(doc.getPrice())
                    + " | Bruttosumme: " + NumberFormat.getCurrencyInstance().format(doc.getAfterTaxPrice())
                    + (settlements.isEmpty() ? "" : " | Gezahlt via " + settlements) + "<br />" : "";
            res += "</p>";
        }
        return res;
    }

    public static String toSimpleLine(Dossier dos) {
        StringBuilder sb = new StringBuilder("Dossier(id=");
        sb.append(dos.getId()).append(",identifier=").append(dos.getIdentifier());
        if ( dos.isClosed() ) sb.append(",closed");
        sb.append(",activeDocuments.size()=").append(dos.getActiveDocuments().size());
        sb.append(",paymentMethod=").append(dos.getPaymentMethod().getNote());
        sb.append(",directive=").append(dos.getCrucialDirective().getName());
        return sb.toString();
    }

    public static String toHtmlSimple(Dossier dos) {
        StringBuilder sb = new StringBuilder();
        for (Iterator<Document> it = dos.getActiveDocuments().iterator(); it.hasNext();) {
            Document document = it.next();
            if ( document.getType() == DocumentType.ORDER ) sb.append(dos.isDispatch() ? "Versandauftrag" : "Abholauftrag");
            else sb.append(document.getType().getName());
            if ( it.hasNext() ) sb.append(", ");
        }
        String res = "";
        res += "Vorgang: " + dos.getIdentifier() + " | <i>" + sb.toString() + "</i><br />";
        res += "Zahlungsmodalität: " + dos.getPaymentMethod().getNote() + " | Anweisung: " + dos.getCrucialDirective().getName();
        return res;
    }

    public static String toHtmlSimpleWithDocument(Dossier dos) {
        String res = "";
        res += "<br /><u>Kundennummer:</u> " + dos.getCustomerId() + " " + toHtmlSimple(dos);
        res += "<br />Dossier.id=" + dos.getId() + "<br />";
        res += "<b>" + (dos.isClosed() ? "<font color=\"#666600\">Vorgang abgeschlossen" : "<font color=\"#009900\">Vorgang offen") + "</font></b><br /><br />";
        res += "<b>Dokumente:</b><br />";
        if ( !dos.getActiveDocuments().isEmpty() ) {
            res += "<ul type=\"disc\">";
            for (Document document : dos.getActiveDocuments()) {
                res += "<li>" + DocumentFormater.toHtmlSimple(document) + "</li>";
            }
            res += "</ul>";
        } else {
            res += "<font color=\"#666600\">Keine Dokumente enthalten</font>";
        }
        return res;
    }

    public static String toHtmlDetailed(Dossier dos) {
        String res = "";
        res += toHtmlSimple(dos);
        res += "Dossier.id=" + dos.getId() + "<br />";
        res += "<b>" + (dos.isClosed() ? "<font color=\"#666600\">Vorgang abgeschlossen" : "<font color=\"#009900\">Vorgang offen") + "</font></b><br /><br />";
        res += "<b>Dokumente:</b><br />";
        if ( !dos.getActiveDocuments().isEmpty() ) {
            res += "<ul type=\"disc\">";
            for (Document document : dos.getActiveDocuments()) {
                res += "<li>" + DocumentFormater.toHtmlDetailedWithPositions(document) + "</li><hr />";
            }
            res += "</ul>";
        } else {
            res += "<font color=\"#666600\">Keine Dokumente enthalten</font>";
        }
        return res;
    }
}
