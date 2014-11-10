package eu.ggnet.dwoss.redtape.api;

import java.io.Serializable;
import java.util.List;

import javax.ejb.*;

import eu.ggnet.dwoss.redtape.entity.Dossier;

import lombok.Value;

/**
 * Bridge Interface if someone wants to supply a legacy system.
 * It is assumed, that the legacy system and the dw use the same customer service, so ids are shared.
 * <p/>
 * @author oliver.guenther
 */
@Remote
@Local
public interface LegacyBridge {

    @Value
    public static class Unit implements Serializable {

        private final String unitIdentifier;

        private final String serial;

        private final String manufacturerPartNo;

        private final String name;

    }

    /**
     * Returns true if the supplied identifier does not collied with some unit identifier in the legacy system.
     * For Sopo this was the SopoNr.
     * This also implies, if this method returns false, the {@link LegacyBridge#toDetailedHtmlUnit(java.lang.String) } will return a useful result.
     * <p>
     * @param identifier the identifier to verify
     * @return true if not used.
     */
    boolean isUnitIdentifierAvailable(String identifier);

    /**
     * Returns all Auftrags with no dossier id wrapped as Dossiers which are assigned to the supplied customer.
     * <p/>
     * @param customerId the customer
     * @param start      the start of the database result.
     * @param amount     the amount of the database result.
     * @return all Auftrags with no dossier id wrapped as Dossiers which are assigned to the supplied customer.
     */
    List<Dossier> findByCustomerId(long customerId, int start, int amount);

    /**
     * A full Text Search for Units.
     * <p>
     * @param search the full text search string.
     * @return a unit representation.
     */
    List<Unit> findUnit(String search);

    /**
     * Returns a detailed HTML representation of existing instance like a dossier.
     * <p>
     * @param dossierIdentifier the identifier use in the legacy system
     * @return a detailed html.
     */
    String toDetailedHtmlDossier(String dossierIdentifier);

    /**
     * Returns a detailed HTLM representation of an existing instance like a unit.
     * <p>
     * @param unitIdentifier the unit identifier
     * @return detailed html
     */
    String toDetailedHtmlUnit(String unitIdentifier);

    /**
     * Should return a Human readable and useful name of the implementing system.
     * <p>
     * @return the name.
     */
    String name();
}
