package eu.ggnet.dwoss.uniqueunit;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import eu.ggnet.dwoss.uniqueunit.assist.UniqueUnits;
import eu.ggnet.dwoss.uniqueunit.eao.ProductEao;
import eu.ggnet.dwoss.uniqueunit.eao.UniqueUnitEao;
import eu.ggnet.dwoss.uniqueunit.entity.Product;
import eu.ggnet.dwoss.uniqueunit.entity.UniqueUnit;

import eu.ggnet.dwoss.util.persistence.AbstractAgentBean;

/**
 * The Implementation of the UniqueUnitAgent
 * <p/>
 * @author oliver.guenther
 */
@Stateless
public class UniqueUnitAgentBean extends AbstractAgentBean implements UniqueUnitAgent {

    @Inject
    @UniqueUnits
    private EntityManager entityManager;

    @Override
    protected EntityManager getEntityManager() {
        return entityManager;
    }

    /**
     * Finds a Product with the partNo.
     * <p/>
     * @param partNo the partNo as search
     * @return the found product or null.
     */
    @Override
    public Product findProductByPartNo(String partNo) {
        return new ProductEao(entityManager).findByPartNo(partNo);
    }

    /**
     * Finds a Product with the partNo, eager loading all resources.
     * <p/>
     * @param partNo the partNo as search
     * @return the found product or null.
     */
    @Override
    public Product findProductByPartNoEager(String partNo) {
        return optionalFetchEager(new ProductEao(entityManager).findByPartNo(partNo));
    }

    /**
     * Finds a UniqueUnit by the Identifier.
     * <p/>
     * @param type       the identifierType
     * @param identifier the identifier
     * @return the uniqueUnit or null.
     */
    @Override
    public UniqueUnit findUnitByIdentifierEager(UniqueUnit.Identifier type, String identifier) {
        return optionalFetchEager(new UniqueUnitEao(entityManager).findByIdentifier(type, identifier));
    }
}
