package eu.ggnet.dwoss.util.persistence;

import java.util.List;

import javax.persistence.LockModeType;

/**
 * Interface for usage of a Agent in the Remote way.
 * <p/>
 * @author oliver.guenther
 */
public interface RemoteAgent {

    <T> long count(Class<T> entityClass);

    /**
     * Returns all entities of the entityClass or an empty list.
     * <p/>
     * @param <T>         the type of the entities
     * @param entityClass the entityClass
     * @return all entities of the entityClass or an empty list.
     */
    <T> List<T> findAll(Class<T> entityClass);

    /**
     * Returns all entities of the entityClass in the supplied interval or an empty list.
     * <p/>
     * @param <T>         the type of the entities
     * @param entityClass the entityClass
     * @param start       the start of the full result to begin the list with.
     * @param amount      the amount of elemets to return at max.
     * @return all entities of the entityClass or an empty list.
     */
    <T> List<T> findAll(Class<T> entityClass, int start, int amount);

    /**
     * Returns all entities of the entityClass or an empty list.
     * This is the eager implementation. If the Entity implements {@link EagerAble}, the method fetchEager is called in the transaction.
     * <p/>
     * @param <T>         the type of the entities
     * @param entityClass the entityClass
     * @return all entities of the entityClass or an empty list.
     */
    <T> List<T> findAllEager(Class<T> entityClass);

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
    <T> List<T> findAllEager(Class<T> entityClass, int start, int amount);

    /**
     * Returns the entity identified by Id or null if non found.
     * <p/>
     * @param <T>         type of the entity
     * @param entityClass the entityClass
     * @param id          the id
     * @return the entity identified by Id or null if non found.
     */
    <T> T findById(Class<T> entityClass, Object id);

    /**
     * Returns the entity identified by Id and locks it by lockMode or null if non found.
     * <p/>
     * @param <T>          type of the entity
     * @param entityClass  the entityClass
     * @param id           the id
     * @param lockModeType the lockMode to use
     * @return the entity identified by Id or null if non found.
     */
    <T> T findById(Class<T> entityClass, Object id, LockModeType lockModeType);

    /**
     * Returns the entity identified by Id or null if non found.
     * This is the eager implementation. If the Entity implements {@link EagerAble}, the method fetchEager is called in the transaction.
     * <p/>
     * @param <T>         type of the entity
     * @param entityClass the entityClass
     * @param id          the id
     * @return the entity identified by Id or null if non found.
     */
    <T> T findByIdEager(Class<T> entityClass, Object id);

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
    <T> T findByIdEager(Class<T> entityClass, Object id, LockModeType lockModeType);
}
