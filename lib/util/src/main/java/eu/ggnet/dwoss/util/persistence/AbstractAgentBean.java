package eu.ggnet.dwoss.util.persistence;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;

/**
 * An Abstract RemoteAgent is used for general implementation of findAll and findById Methods for a full persistence unit.
 * <p/>
 * @author oliver.guenther
 */
public abstract class AbstractAgentBean implements RemoteAgent {

    /**
     * Must return the EntityManager of the persistence unit.
     * <p/>
     * @return the EntityManger
     */
    protected abstract EntityManager getEntityManager();

    /**
     * Returns the entity identified by Id or null if non found.
     * <p/>
     * @param <T>         type of the entity
     * @param entityClass the entityClass
     * @param id          the id
     * @return the entity identified by Id or null if non found.
     */
    @Override
    public <T> T findById(Class<T> entityClass, Object id) {
        validate(entityClass);
        if ( id == null ) return null;
        return getEntityManager().find(entityClass, id);
    }

    /**
     * Returns the entity identified by Id or null if non found.
     * This is the eager implementation. If the Entity implements {@link EagerAble}, the method fetchEager is called in the transaction.
     * <p/>
     * @param <T>         type of the entity
     * @param entityClass the entityClass
     * @param id          the id
     * @return the entity identified by Id or null if non found.
     */
    @Override
    public <T> T findByIdEager(Class<T> entityClass, Object id) {
        return optionalFetchEager(findById(entityClass, id));
    }

    /**
     * Returns the entity identified by Id and locks it by lockMode or null if non found.
     * <p/>
     * @param <T>          type of the entity
     * @param entityClass  the entityClass
     * @param id           the id
     * @param lockModeType the lockMode to use
     * @return the entity identified by Id or null if non found.
     */
    @Override
    public <T> T findById(Class<T> entityClass, Object id, LockModeType lockModeType) {
        validate(entityClass);
        if ( id == null ) return null;
        return getEntityManager().find(entityClass, id, lockModeType);
    }

    /**
     * Returns the entity identified by Id and locks it by lockMode or null if non found.
     * This is the eager implementation. If the Entity implements {@link EagerAble}, the method fetchEager is called in the transaction.
     * <p/>
     * @param <T>          type of the entity
     * @param entityClass  the entityClass
     * @param id           the id
     * @param lockModeType the lockMode to use
     * @return the entity identified by Id or null if non found.
     */
    @Override
    public <T> T findByIdEager(Class<T> entityClass, Object id, LockModeType lockModeType) {
        return optionalFetchEager(findById(entityClass, id, lockModeType));
    }

    /**
     * Returns all entities of the entityClass or an empty list.
     * <p/>
     * @param <T>         the type of the entities
     * @param entityClass the entityClass
     * @return all entities of the entityClass or an empty list.
     */
    @Override
    public <T> List<T> findAll(Class<T> entityClass) {
        validate(entityClass);
        javax.persistence.criteria.CriteriaQuery<T> cq = getEntityManager().getCriteriaBuilder().createQuery(entityClass);
        cq.select(cq.from(entityClass));
        return getEntityManager().createQuery(cq).getResultList();
    }

    /**
     * Returns all entities of the entityClass or an empty list.
     * This is the eager implementation. If the Entity implements {@link EagerAble}, the method fetchEager is called in the transaction.
     * <p/>
     * @param <T>         the type of the entities
     * @param entityClass the entityClass
     * @return all entities of the entityClass or an empty list.
     */
    @Override
    public <T> List<T> findAllEager(Class<T> entityClass) {
        return optionalFetchEager(findAll(entityClass));
    }

    /**
     * Returns all entities of the entityClass in the supplied interval or an empty list.
     * <p/>
     * @param <T>         the type of the entities
     * @param entityClass the entityClass
     * @param start       the start of the full result to begin the list with.
     * @param amount      the amount of elemets to return at max.
     * @return all entities of the entityClass or an empty list.
     */
    @Override
    public <T> List<T> findAll(Class<T> entityClass, int start, int amount) {
        validate(entityClass);
        javax.persistence.criteria.CriteriaQuery<T> cq = getEntityManager().getCriteriaBuilder().createQuery(entityClass);
        cq.select(cq.from(entityClass));
        return getEntityManager().createQuery(cq).setFirstResult(start).setMaxResults(amount).getResultList();
    }

    /**
     * Returns all entities of the entityClass in the supplied interval or an empty list.
     * This is the eager implementation. If the Entity implements {@link EagerAble}, the method fetchEager is called in the transaction.
     * <p/>
     * @param <T>         the type of the entities
     * @param entityClass the entityClass
     * @param start       the start of the full result to begin the list with.
     * @param amount      the amount of elemets to return at max.
     * @return all entities of the entityClass or an empty list.
     */
    @Override
    public <T> List<T> findAllEager(Class<T> entityClass, int start, int amount) {
        return optionalFetchEager(findAll(entityClass, start, amount));
    }

    @Override
    public <T> long count(Class<T> entityClass) {
        validate(entityClass);
        javax.persistence.criteria.CriteriaQuery<Long> cq = getEntityManager().getCriteriaBuilder().createQuery(Long.class);
        javax.persistence.criteria.Root<T> rt = cq.from(entityClass);
        cq.select(getEntityManager().getCriteriaBuilder().count(rt));
        javax.persistence.TypedQuery<Long> q = getEntityManager().createQuery(cq);
        return q.getSingleResult();
    }

    private void validate(Class<?> clazz) {
        Objects.requireNonNull(clazz, "Supplied entityClass is null");
        Objects.requireNonNull(getEntityManager(), "Supplied EntityManager is null");
    }

    protected final <T> T optionalFetchEager(T entity) {
        if ( entity == null ) return null;
        if ( entity instanceof EagerAble ) ((EagerAble)entity).fetchEager();
        return entity;
    }

    protected final <T> List<T> optionalFetchEager(List<T> entities) {
        if ( entities == null ) return null;
        if ( entities.isEmpty() ) return entities;
        if ( !(entities.get(0) instanceof EagerAble) ) return entities;
        for (T entity : entities) {
            ((EagerAble)entity).fetchEager();
        }
        return entities;
    }

    protected final <T> Set<T> optionalFetchEager(Set<T> entities) {
        if ( entities == null ) return null;
        if ( entities.isEmpty() ) return entities;
        if ( !(entities.iterator().next() instanceof EagerAble) ) return entities;
        for (T entity : entities) {
            ((EagerAble)entity).fetchEager();
        }
        return entities;
    }
}
