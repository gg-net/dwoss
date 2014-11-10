package eu.ggnet.dwoss.spec;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import eu.ggnet.dwoss.spec.assist.Specs;
import eu.ggnet.dwoss.spec.eao.ProductSpecEao;
import eu.ggnet.dwoss.spec.entity.ProductSpec;
import eu.ggnet.dwoss.util.persistence.AbstractAgentBean;

/**
 * The Implementation of the SpecAgent
 * <p>
 * @author oliver.guenther
 */
@Stateless
public class SpecAgentBean extends AbstractAgentBean implements SpecAgent {

    @Inject
    @Specs
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    /**
     * Finds a ProductSpec by part no and fetches the object tree eager.
     * <p>
     * @param partNo the part no to search for
     * @return a ProductSpec or null if non found.
     */
    @Override
    public ProductSpec findProductSpecByPartNoEager(String partNo) {
        if ( partNo == null ) return null;
        return optionalFetchEager(new ProductSpecEao(em).findByPartNo(partNo));
    }
}
