package eu.ggnet.dwoss.util.persistence.eao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;

/**
 * An abstract Entity Access Object for the default actions.
 * T class of the Entity
 */
public abstract class AbstractEao<T> {

    private Class<T> entityClass;

    public AbstractEao(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public abstract EntityManager getEntityManager();

    public T findById(Object id) {
        if ( id == null ) return null;
        return getEntityManager().find(entityClass, id);
    }

    public T findById(Object id, LockModeType lockModeType) {
        if ( id == null ) return null;
        return getEntityManager().find(entityClass, id, lockModeType);
    }

    public List<T> findAll() {
        javax.persistence.criteria.CriteriaQuery<T> cq = getEntityManager().getCriteriaBuilder().createQuery(entityClass);
        cq.select(cq.from(entityClass));
        return getEntityManager().createQuery(cq).getResultList();
    }

    public List<T> findAll(int start, int amount) {
        javax.persistence.criteria.CriteriaQuery<T> cq = getEntityManager().getCriteriaBuilder().createQuery(entityClass);
        cq.select(cq.from(entityClass));
        return getEntityManager().createQuery(cq).setFirstResult(start).setMaxResults(amount).getResultList();
    }

    public int count() {
        javax.persistence.criteria.CriteriaQuery<Long> cq = getEntityManager().getCriteriaBuilder().createQuery(Long.class);
        javax.persistence.criteria.Root<T> rt = cq.from(entityClass);
        cq.select(getEntityManager().getCriteriaBuilder().count(rt));
        javax.persistence.TypedQuery<Long> q = getEntityManager().createQuery(cq);
        return q.getSingleResult().intValue();
    }
}
