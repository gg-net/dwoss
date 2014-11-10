package eu.ggnet.dwoss.uniqueunit;

import javax.ejb.Local;
import javax.ejb.Remote;

import eu.ggnet.dwoss.uniqueunit.entity.Product;
import eu.ggnet.dwoss.uniqueunit.entity.UniqueUnit;

import eu.ggnet.dwoss.util.persistence.RemoteAgent;

/**
 * The UniqueUnitAgent.
 * <p/>
 * @author oliver.guenther
 */
@Remote
@Local
public interface UniqueUnitAgent extends RemoteAgent {

    /**
     * Finds a Product with the partNo.
     * <p/>
     * @param partNo the partNo as search
     * @return the found product or null.
     */
    Product findProductByPartNo(String partNo);

    /**
     * Finds a UniqueUnit by the Identifier.
     * <p/>
     * @param type       the identifierType
     * @param identifier the identifier
     * @return the uniqueUnit or null.
     */
    UniqueUnit findUnitByIdentifierEager(UniqueUnit.Identifier type, String identifier);

    /**
     * Finds a Product with the partNo, eager loading all resources.
     * <p/>
     * @param partNo the partNo as search
     * @return the found product or null.
     */
    Product findProductByPartNoEager(String partNo);
}
