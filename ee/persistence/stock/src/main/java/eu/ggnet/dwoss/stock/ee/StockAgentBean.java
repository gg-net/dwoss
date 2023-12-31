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
package eu.ggnet.dwoss.stock.ee;

import java.util.List;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import eu.ggnet.dwoss.core.system.persistence.AbstractAgentBean;
import eu.ggnet.dwoss.core.system.persistence.BaseEntity;
import eu.ggnet.dwoss.stock.ee.assist.Stocks;
import eu.ggnet.dwoss.stock.ee.eao.StockTransactionEao;
import eu.ggnet.dwoss.stock.ee.eao.StockUnitEao;
import eu.ggnet.dwoss.stock.ee.emo.StockTransactionEmo;
import eu.ggnet.dwoss.stock.ee.entity.*;

/**
 * The StockAgent Implementation.
 * <p/>
 * @author oliver.guenther
 */
@Stateless
public class StockAgentBean extends AbstractAgentBean implements StockAgent {

    @Inject
    @Stocks
    private EntityManager em;

    @Inject
    private StockTransactionEmo stockTransactionEmo;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    /**
     * Returns a StockUnit identified by the uniqueUnitId, or null if not existent.
     * <p/>
     * @param uniqueUnitId the uniqueUnitId.
     * @return a StockUnit identified by the uniqueUnitId, or null if not existent.
     */
    @Override
    public StockUnit findStockUnitByUniqueUnitIdEager(Integer uniqueUnitId) {
        return optionalFetchEager(new StockUnitEao(em).findByUniqueUnitId(uniqueUnitId));
    }

    /**
     * Returns a StockUnit identified by the refurbishId, or null if not existent.
     * <p/>
     * @param refurbishId the refubishId.
     * @return a StockUnit identified by the refurbishId, or null if not existent.
     */
    @Override
    public StockUnit findStockUnitByRefurbishIdEager(String refurbishId) {
        return optionalFetchEager(new StockUnitEao(em).findByRefurbishId(refurbishId));
    }

    /**
     * Returns a List of StockTransactions, which match the supplied parameters.
     * <p/>
     * @param type       the type of the transaction
     * @param statusType the statusType of the transaction.
     * @return a List of StockTransactions, which match the supplied parameters.
     */
    @Override
    public List<StockTransaction> findStockTransactionEager(StockTransactionType type, StockTransactionStatusType statusType) {
        return optionalFetchEager(new StockTransactionEao(em).findByTypeAndStatus(type, statusType));
    }

    /**
     * Returns a List of StockTransactions, which match the supplied parameters.
     * <p/>
     * @param type       the type of the transaction
     * @param statusType the statusType of the transaction.
     * @param start      the start of the database result.
     * @param amount     the amount of the database result.
     * @return a List of StockTransactions, which match the supplied parameters.
     */
    @Override
    public List<StockTransaction> findStockTransactionEager(StockTransactionType type, StockTransactionStatusType statusType, int start, int amount) {
        return optionalFetchEager(new StockTransactionEao(em).findByTypeAndStatus(type, statusType, start, amount));
    }

    /**
     * Finds a List of StockUnits identified by the refurbishIds, which are able to be transfered.
     * <p/>
     * @param refurbishIds
     * @return the list of stockUnits.
     */
    @Override
    public List<StockUnit> findStockUnitsByRefurbishIdEager(List<String> refurbishIds) {
        return optionalFetchEager(new StockUnitEao(em).findByRefurbishIds(refurbishIds));
    }

    @Override
    public <T> T persist(T t) {
        if ( t == null ) throw new RuntimeException("T was Null in delete.");
        em.persist(t);
        return t;
    }

    @Override
    public <T> T merge(T t) {
        if ( t == null ) throw new RuntimeException("T was Null in delete.");
        em.merge(t);
        return t;
    }

    @Override
    public <T> void delete(T t) {
        if ( t == null ) throw new RuntimeException("T was Null in delete.");
        if ( t instanceof BaseEntity ) {
            BaseEntity id = (BaseEntity)t;
            em.remove(em.find(id.getClass(), id.getId()));
        } else {
            em.remove(t);
        }
    }
}
