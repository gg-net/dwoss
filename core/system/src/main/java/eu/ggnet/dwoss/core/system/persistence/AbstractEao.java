/* 
 * Copyright (C) 2014 GG-Net GmbH - Oliver Günther
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.ggnet.dwoss.core.system.persistence;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An abstract Entity Access Object for the default actions.
 * T class of the Entity
 */
public abstract class AbstractEao<T> {

    private final Logger L = LoggerFactory.getLogger(this.getClass()); // logger of subclass.
    
    private Class<T> entityClass;

    public AbstractEao(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public abstract EntityManager getEntityManager();

    public T findById(Object id) {
        L.debug("findbyId({}) of entityClass={} called",id,entityClass);
        if ( id == null ) return null;
        return getEntityManager().find(entityClass, id);
    }

    public T findById(Object id, LockModeType lockModeType) {
        L.debug("findbyId({}) of entityClass={} called",id,entityClass);
        if ( id == null ) return null;
        return getEntityManager().find(entityClass, id, lockModeType);
    }

    public List<T> findAll() {
        jakarta.persistence.criteria.CriteriaQuery<T> cq = getEntityManager().getCriteriaBuilder().createQuery(entityClass);
        cq.select(cq.from(entityClass));
        return getEntityManager().createQuery(cq).getResultList();
    }

    public List<T> findAll(int start, int amount) {
        jakarta.persistence.criteria.CriteriaQuery<T> cq = getEntityManager().getCriteriaBuilder().createQuery(entityClass);
        cq.select(cq.from(entityClass));
        return getEntityManager().createQuery(cq).setFirstResult(start).setMaxResults(amount).getResultList();
    }

    public int count() {
        jakarta.persistence.criteria.CriteriaQuery<Long> cq = getEntityManager().getCriteriaBuilder().createQuery(Long.class);
        jakarta.persistence.criteria.Root<T> rt = cq.from(entityClass);
        cq.select(getEntityManager().getCriteriaBuilder().count(rt));
        jakarta.persistence.TypedQuery<Long> q = getEntityManager().createQuery(cq);
        return q.getSingleResult().intValue();
    }
}
