package eu.ggnet.dwoss.redtape;

import java.util.List;

import javax.ejb.Remote;

import eu.ggnet.dwoss.rules.DocumentType;
import eu.ggnet.dwoss.util.Tuple2;

/**
 *
 * @author pascal.perau
 */
//TODO: Total screwup, see http://overload.ahrensburg.gg-net.de/jira/browse/DW-1215
@Remote
public interface UniversalSearcher {

    /**
     * Find a Customer and return a html formated String representing it.
     * Ensure to add the html start/end tags manually
     * <p/>
     * @param id CustomerId to search for
     * @return a html formated String representing a Customer.
     */
    // Used in Misc. Unversal Search
    String findCustomer(int id);

    /**
     * Returns a List of short descriptions of Customers in Html.
     * Ensure to add the html start/end tags manually
     * <p/>
     * @param firma    the firma or null
     * @param vorname  the vorname or null
     * @param nachname the nachname or null
     * @param email    the email or null
     * @return a List of short descriptions of Customers in Html.
     */
    // RedTape createCustomer Contorller.
    List<Tuple2<Long, String>> findCustomers(String firma, String vorname, String nachname, String email);

    /**
     * Generates a html formated string containing information about the Document.
     * <p/>
     * @param id the dossier id
     * @return a html formated string containing information about the Document
     */
    // Used in Misc. Unversal Search
    String findDocument(long id);

    /**
     * Generates a html formated string containing information about the Dossier.
     * <p/>
     * @param id the dossier id
     * @return a html formated string containing information about the Dossier
     */
    // Used in Misc. Unversal Search
    String findDossier(long id);

    /**
     * Returns a List of short descriptions of Customers in Html.
     * Ensure to add the html start/end tags manually
     * <p/>
     * @param search a search string.
     * @return a List of short descriptions of Customers in Html.
     */
    // misc and redTape.
    List<Tuple2<Long, String>> searchCustomers(String search);

    /**
     * Search for Documents where the search matches the identifier.
     * This method will search for any partial matches from the beginning of a identifier if a wildcard is used.
     * <p/>
     * @param identifier the identifier to search for
     * @param type
     * @return a List of Tuple2 containing document.id and string representation
     */
    // Used in Misc. Unversal Search
    List<Tuple2<Long, String>> searchDocuments(String identifier, DocumentType type);

    /**
     * Search for Dossiers where the search matches the identifier.
     * This method will search for any partial matches from the beginning of a identifier if a wildcard is used.
     * <p/>
     * @param identifier the identifier to search for
     * @return a List of Tuple2 containing dossier.id and string representation
     */
    // Used in Misc. Unversal Search
    List<Tuple2<Long, String>> searchDossiers(String identifier);

    /**
     * Returns a List of short descriptions of Units in Html.
     * Ensure to add the html start/end tags manually
     * <p/>
     * @param search a search string.
     * @return a List of short descriptions of Units in Html.
     */
    // Used in Misc. Unversal Search
    List<Tuple2<Long, String>> searchUnits(String search);
}
