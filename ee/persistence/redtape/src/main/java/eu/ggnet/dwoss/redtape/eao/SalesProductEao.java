package eu.ggnet.dwoss.redtape.eao;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import eu.ggnet.dwoss.redtape.entity.SalesProduct;

import eu.ggnet.dwoss.util.persistence.eao.AbstractEao;

/**
 * @author bastian.venz
 * @author oliver.guenther
 * @author pascal.perau
 */
public class SalesProductEao extends AbstractEao<SalesProduct> {

    private EntityManager em;

    public SalesProductEao(EntityManager em) {
        super(SalesProduct.class);
        this.em = em;
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public SalesProduct findByUniqueUnitProductId(long uniqueUnitProductId) {
        Query createNamedQuery = em.createNamedQuery("byUniqueUnitProductId");
        createNamedQuery.setParameter(1, uniqueUnitProductId);
        try {
            return (SalesProduct)createNamedQuery.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }
}
