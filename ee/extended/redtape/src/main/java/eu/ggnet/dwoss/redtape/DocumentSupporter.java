package eu.ggnet.dwoss.redtape;

import javax.ejb.Remote;

import net.sf.jasperreports.engine.JasperPrint;

import eu.ggnet.dwoss.mandator.api.DocumentViewType;
import eu.ggnet.dwoss.redtape.entity.Document;
import eu.ggnet.dwoss.redtape.entity.Dossier;

import eu.ggnet.dwoss.util.FileJacket;
import eu.ggnet.dwoss.util.UserInfoException;

/**
 * Supporter for Documents, mainly for mail or printing.
 * <p/>
 * @author oliver.guenther
 */
@Remote
public interface DocumentSupporter {

    /**
     * This method sends a document to the e-Mail address of the customer.
     * <p/>
     * @param document This is the Document that will be send.
     * @param jtype
     * @throws UserInfoException if the sending of the Mail is not successful.
     * @throws RuntimeException  if problems exist in the JasperExporter
     */
    void mail(Document document, DocumentViewType jtype) throws UserInfoException, RuntimeException;

    /**
     * Creates a JasperPrint for the Document.
     *
     * @param document the document
     * @param viewType
     * @return a JasperPrint
     */
    JasperPrint render(Document document, DocumentViewType viewType);

    /**
     * Sets the Flags {@link Flag#CUSTOMER_BRIEFED} and {@link Flag#CUSTOMER_EXACTLY_BRIEFED} at the document.
     * Also appends this change at the DocumentHistory.
     *
     * @param detached the document
     * @param arranger
     * @return the updated document
     */
    Dossier briefed(Document detached, String arranger);

    FileJacket toXls(String identifier);

}
