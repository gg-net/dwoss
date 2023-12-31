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
package eu.ggnet.dwoss.stock.ee.eao;

import java.util.*;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.*;

import eu.ggnet.dwoss.stock.ee.assist.Stocks;
import eu.ggnet.dwoss.stock.ee.entity.*;
import eu.ggnet.dwoss.core.system.persistence.AbstractEao;

/**
 * JPA Service for {@link StockUnit}
 */
@Stateless
public class StockUnitEao extends AbstractEao<StockUnit> {

    @Inject
    @Stocks
    private EntityManager em;

    public StockUnitEao() {
        super(StockUnit.class);
    }

    public StockUnitEao(EntityManager em) {
        super(StockUnit.class);
        this.em = em;
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    /**
     * Returns a StockUnit which is identified by the unitId or null if non existend
     * <p/>
     * @param unitId the unitId
     * @return a StockUnit which is identified by the unitId or null if non existend
     */
    public StockUnit findByRefurbishId(String unitId) {
        try {
            return em.createNamedQuery("StockUnit.byRefurbishId", StockUnit.class).setParameter(1, unitId).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Returns a StockUnit which is identified by the unitId or null if non existend
     * <p/>
     * @param refurbishIds the refurbishIds
     * @return a StockUnit which is identified by the unitId or null if non existend
     */
    public List<StockUnit> findByRefurbishIds(Collection<String> refurbishIds) {
        if ( refurbishIds == null || refurbishIds.isEmpty() ) return Collections.EMPTY_LIST;
        return em.createNamedQuery("StockUnit.byRefurbishIds", StockUnit.class).setParameter(1, refurbishIds).getResultList();
    }

    /**
     * Returns all units in a stock
     * <p/>
     * @param stock the stock, must not be null
     * @return all units in a stock
     */
    public List<StockUnit> findByStockId(int stock) {
        return em.createNamedQuery("StockUnit.byStockId", StockUnit.class).setParameter(1, stock).getResultList();
    }

    /**
     * Returns all StockUnits, which are not on a LogicTransaction and not on a StockTransaction.
     * <p/>
     * @return all StockUnits, which are not on a LogicTransaction and not on a StockTransaction.
     */
    public List<StockUnit> findByNoTransaction() {
        List<StockUnit> result = findByNoLogicTransaction();
        for (Iterator<StockUnit> it = result.iterator(); it.hasNext();) {
            StockUnit stockUnit = it.next();
            if ( stockUnit.isInTransaction() ) it.remove();
        }
        // TODO: Not working, don't know why.
        // return em.createNamedQuery("StockUnit.byNoTransaciton", StockUnit.class).getResultList();
        return result;
    }

    /**
     * Returns all StockUnits, which are not on a LogicTransaction.
     * <p/>
     * @return all StockUnits, which are not on a LogicTransaction.
     */
    public List<StockUnit> findByNoLogicTransaction() {
        return em.createNamedQuery("StockUnit.byNoLogicTransaciton", StockUnit.class).getResultList();
    }

    /**
     * Returns all StockUnits, which are not on a LogicTransaction.
     * <p/>
     * @return all StockUnits, which are not on a LogicTransaction.
     */
    public List<StockUnit> findByNoLogicTransactionAndPresentStock() {
        return em.createNamedQuery("StockUnit.byNoLogicTransacitonAndPresentStock", StockUnit.class).getResultList();
    }

    public List<Integer> findByNoLogicTransactionAsUniqueUnitId() {
        return em.createNamedQuery("StockUnit.byNoLogicTransacitonAsUniqueUnitId", Integer.class).getResultList();
    }

    /**
     * Returns the amount of units, which are in transaction of the specific types
     * <p/>
     * @param stockId         the source of the transaction
     * @param transactionType the type of transaction
     * @param statusType      the actual status type of the transaction
     * @return the amount of units, which are in transaciton
     */
    public int countByTransaction(int stockId, StockTransactionType transactionType, StockTransactionStatusType statusType) {
        TypedQuery<Long> query = em.createNamedQuery("StockUnit.countByTypeStatusSource", Long.class);
        query.setParameter(1, transactionType);
        query.setParameter(2, statusType);
        query.setParameter(3, stockId);
        return query.getSingleResult().intValue();
    }

    /**
     * Returns the amount of units, which are not on a logic transaction.
     *
     * @param stockId the stockId
     * @return the amount of units, which are not on a logic transaction.
     */
    public int countByStockNoLogicTransaction(int stockId) {
        return em.createNamedQuery("StockUnit.countByStockNoLogicTransaciton", Long.class).setParameter(1, stockId).getSingleResult().intValue();
    }

    /**
     * Returns the amount of units, which are on a logic transaction.
     *
     * @param stockId the stockId
     * @return the amount of units, which are on a logic transaction.
     */
    public int countByStockOnLogicTransaction(int stockId) {
        return em.createNamedQuery("StockUnit.countByStockOnLogicTransaciton", Long.class).setParameter(1, stockId).getSingleResult().intValue();
    }

    /**
     * Returns a StockUnit by the referencing UniqueUnitId or null if not exists.
     *
     * @param uniqueUnitId the UnqiueUnit.id
     * @return a StockUnit by the referencing UniqueUnitId or null if not exists.
     */
    public StockUnit findByUniqueUnitId(Integer uniqueUnitId) {
        if ( uniqueUnitId == null ) return null;
        TypedQuery<StockUnit> query = em.createNamedQuery("StockUnit.byUniqueUnitId", StockUnit.class);
        query.setParameter(1, uniqueUnitId);
        List<StockUnit> result = query.getResultList();
        if ( result.isEmpty() ) return null;
        return result.get(0);
    }

    /**
     * Returns a List of StockUnits mapped by the UniqueUnits.
     *
     * @param uniqueUnitIds the uniqueUnitIds
     * @return a List of StockUnits mapped by the UniqueUnits.
     */
    public List<StockUnit> findByUniqueUnitIds(Collection<Integer> uniqueUnitIds) {
        if ( uniqueUnitIds == null || uniqueUnitIds.isEmpty() ) return Collections.emptyList();
        return em.createNamedQuery("StockUnit.findByUniqueUnitIds", StockUnit.class).setParameter(1, uniqueUnitIds).getResultList();
    }
}
