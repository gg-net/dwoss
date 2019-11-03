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

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.*;

import eu.ggnet.dwoss.stock.ee.assist.Stocks;
import eu.ggnet.dwoss.stock.ee.entity.LogicTransaction;
import eu.ggnet.dwoss.core.system.persistence.AbstractEao;

/**
 * The LogicTransaction Entity Access Object.
 *
 * @author pascal.perau
 */
@Stateless
public class LogicTransactionEao extends AbstractEao<LogicTransaction> {

    @Inject
    @Stocks
    private EntityManager em;

    public LogicTransactionEao() {
        super(LogicTransaction.class);
    }

    /**
     * Default Constructor.
     *
     * @param em the Stock Entity Manager.
     */
    public LogicTransactionEao(EntityManager em) {
        super(LogicTransaction.class);
        this.em = em;
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    /**
     * Returns a LogicTransaction which has the supplied uniqueUnitId associated, or null if none.
     *
     * @param uniqueUnitId the uniqueUnitId
     * @return a LogicTransaction which has the supplied uniqueUnitId associated, or null if none.
     */
    public LogicTransaction findByUniqueUnitId(int uniqueUnitId) {
        TypedQuery<LogicTransaction> query = em.createNamedQuery("LogicTransaction.findByUniqueUnitId", LogicTransaction.class);
        query.setParameter(1, uniqueUnitId);
        List<LogicTransaction> lt = query.getResultList();
        if ( lt.isEmpty() ) return null;
        return lt.get(0);
    }

    /**
     * Gets a LogicTransation by its DossierId.
     * <p/>
     * @param dossierId The DossierId.
     * @return the found LogicTransaction or null if nothing is found.
     */
    public LogicTransaction findByDossierId(long dossierId) {
        TypedQuery<LogicTransaction> query = em.createNamedQuery("LogicTransaction.findByDossierId", LogicTransaction.class);
        query.setParameter(1, dossierId);
        List<LogicTransaction> lt = query.getResultList();
        if ( lt.isEmpty() ) return null;
        return lt.get(0);
    }

    /**
     * Gets a LogicTransation by its DossierId.
     * <p/>
     * @param dossierId The DossierId.
     * @return the found LogicTransaction or null if nothing is found.
     */
    public LogicTransaction findByDossierId(long dossierId, LockModeType lockModeType) {
        List<LogicTransaction> lt = em.createNamedQuery("LogicTransaction.findByDossierId", LogicTransaction.class)
                .setParameter(1, dossierId).setLockMode(lockModeType).getResultList();
        if ( lt.isEmpty() ) return null;
        return lt.get(0);
    }

    public List<LogicTransaction> findByDossierIds(Collection<Long> dossierIds) {
        if ( dossierIds == null || dossierIds.isEmpty() ) return new ArrayList<>();
        TypedQuery<LogicTransaction> query = em.createNamedQuery("LogicTransaction.findByDossierIds", LogicTransaction.class);
        query.setParameter(1, dossierIds);
        return query.getResultList();
    }
}
