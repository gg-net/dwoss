package eu.ggnet.dwoss.redtape.eao;

import java.util.List;

import javax.persistence.EntityManager;

import eu.ggnet.dwoss.redtape.entity.Position;

import eu.ggnet.dwoss.util.persistence.eao.AbstractEao;

/**
 *
 * @author oliver.guenther
 */
public class PositionEao extends AbstractEao<Position> {

    private final EntityManager em;

    public PositionEao(EntityManager em) {
        super(Position.class);
        this.em = em;
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public List<Position> findByDocumentId(long documentId) {
        return em.createNamedQuery("Position.findByDocumentId", Position.class).setParameter(1, documentId).getResultList();
    }

    public long countByDocumentId(long documentId) {
        return em.createNamedQuery("Position.countByDocumentId", Long.class).setParameter(1, documentId).getSingleResult();
    }

    public List<Position> findByUniqueUnitId(int unitId) {
        return em.createNamedQuery("Position.findByUniqueUnitId", Position.class).setParameter(1, unitId).getResultList();
    }
}
