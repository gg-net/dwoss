/* 
 * Copyright (C) 2014 pascal.perau
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
package eu.ggnet.dwoss.stock.emo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.TreeSet;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import eu.ggnet.dwoss.stock.eao.LogicTransactionEao;
import eu.ggnet.dwoss.stock.eao.StockUnitEao;
import eu.ggnet.dwoss.stock.entity.LogicTransaction;
import eu.ggnet.dwoss.stock.entity.StockUnit;

/**
 *
 * @author pascal.perau
 */
public class LogicTransactionEmo {

    private EntityManager em;

    public LogicTransactionEmo(EntityManager em) {
        this.em = em;
    }

    public LogicTransaction request(long dossierId, LockModeType lockModeType) {
        try {
            return em.createNamedQuery("LogicTransaction.findByDossierId", LogicTransaction.class).setParameter(1, dossierId).setLockMode(lockModeType).getSingleResult();
        } catch (NoResultException e) {
            LogicTransaction lt = new LogicTransaction();
            lt.setDossierId(dossierId);
            em.persist(lt);
            em.refresh(lt, lockModeType);
            return lt;
        }
    }

    /**
     * Request a LogicTransaction by its DossierId. <br />
     * If no LogicTransaction is found, a new is created and the DossierId will be set.
     * <p/>
     * @param dossierId The Dossier Id
     * @return the found or a new LogicTransaction
     */
    public LogicTransaction request(long dossierId) {
        Query query = em.createNamedQuery("LogicTransaction.findByDossierId");
        query.setParameter(1, dossierId);
        try {
            return (LogicTransaction)query.getSingleResult();
        } catch (NoResultException e) {
            LogicTransaction lt = new LogicTransaction();
            lt.setDossierId(dossierId);
            em.persist(lt);
            return lt;
        }
    }

    /**
     * Removes StockUnits identified by UniqueUnitIds from a LogicTransaction, if it exists.
     *
     * @param dossierId     the dossierId of the LogicTransaction
     * @param uniqueUnitIds the uniqueUnitIds
     * @return the LogicTransaction after the removal, always returns a list.
     */
    // TODO: Test
    public List<StockUnit> optionalRemoveUnits(long dossierId, Collection<Integer> uniqueUnitIds) {
        if ( uniqueUnitIds == null ) throw new NullPointerException("uniqueUnitIds must not be null");
        LogicTransaction logicTransaction = new LogicTransactionEao(em).findByDossierId(dossierId); // Null is possible
        if ( logicTransaction == null ) return new ArrayList<>();

        List<StockUnit> stockUnits = new ArrayList<>();
        for (Integer uniqueUnitId : uniqueUnitIds) {
            // check stockunit if not in LogicTransaction.
            StockUnit unit = new StockUnitEao(em).findByUniqueUnitId(uniqueUnitId);
            if ( unit == null ) continue;
            if ( unit.getLogicTransaction() == null ) throw new IllegalStateException(unit + " is not on a LogicTransaction, shoud be on " + logicTransaction);
            if ( !logicTransaction.equals(unit.getLogicTransaction()) )
                throw new IllegalStateException(unit + " is on a different LogicTransaction than " + logicTransaction);
            logicTransaction.remove(unit);
            stockUnits.add(unit);
        }
        return stockUnits;
    }

    /**
     * Brings the LogicTransaction identified by the dossierId and the supplied uniqueUnitIds in equilibrium.
     * <p/>
     * Handles the following states:
     * <ul>
     * <li>If uniqueUnitIds is empty a possible LogicTransaction is emptied and removed</li>
     * <li>Otherwise the LogicTransaction is synchronised with the uniqueUnitIds. possibly creating the LogicTransaction</li>
     * </ul>
     * TODO: Test (is Tested in RedTapeOperationTest)
     * <p/>
     * @param dossierId        the id of the dossier
     * @param newUniqueUnitIds List of uniqueUnitIds may not be null
     * @return a Result containing uniqueUnitIds which have been added an removed, or null if nothing happend.
     * @throws NullPointerException     if uniqueUnitIds is null
     * @throws IllegalArgumentException if a uniqueUnitId has no StockUnit
     * @throws IllegalStateException    if a StockUnit, that should be free, is already on a LogicTransaction.
     */
    // TODO: Return removed, added.
    public EquilibrationResult equilibrate(long dossierId, final Collection<Integer> newUniqueUnitIds) throws IllegalArgumentException, IllegalStateException, NullPointerException {
        if ( newUniqueUnitIds == null ) throw new NullPointerException("uniqueUnitIds must not be null");
        LogicTransaction logicTransaction = new LogicTransactionEao(em).findByDossierId(dossierId, LockModeType.PESSIMISTIC_WRITE); // Null is possible
        if ( logicTransaction == null && newUniqueUnitIds.isEmpty() ) return null;

        NavigableSet<Integer> adding = new TreeSet<>(newUniqueUnitIds);
        Map<Integer, StockUnit> oldStockUnits = new HashMap<>();
        NavigableSet<Integer> removal = new TreeSet<>();

        if ( logicTransaction != null ) {

            for (StockUnit stockUnit : logicTransaction.getUnits()) {
                oldStockUnits.put(stockUnit.getUniqueUnitId(), stockUnit);
            }

            removal.addAll(oldStockUnits.keySet());
            removal.removeAll(newUniqueUnitIds);

            adding.removeAll(oldStockUnits.keySet());

            for (Integer uniqueUnitId : removal) {
                logicTransaction.remove(oldStockUnits.get(uniqueUnitId));
            }

            if ( newUniqueUnitIds.isEmpty() ) {
                em.remove(logicTransaction);
                return null;
            }
        }

        if ( logicTransaction == null ) logicTransaction = request(dossierId, LockModeType.PESSIMISTIC_WRITE);

        for (Integer uniqueUnitId : adding) {
            // check stockunit if not in LogicTransaction.
            StockUnit unit = new StockUnitEao(em).findByUniqueUnitId(uniqueUnitId);
            if ( unit == null ) throw new IllegalArgumentException("The supplied uniqueUnitId=" + uniqueUnitId + " has no StockUnit");
            if ( unit.getLogicTransaction() != null ) throw new IllegalStateException(unit + " is already in a LogicTransaction");
            else logicTransaction.add(unit);
        }
        return new EquilibrationResult(adding, removal, logicTransaction);
    }
}
