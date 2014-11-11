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
package eu.ggnet.dwoss.uniqueunit.op;

import javax.ejb.Stateless;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.event.SalesChannelChange;
import eu.ggnet.dwoss.event.UnitHistory;
import eu.ggnet.dwoss.uniqueunit.assist.UniqueUnits;
import eu.ggnet.dwoss.uniqueunit.entity.UniqueUnit;

/**
 * Listener for Events targeting UniqueUnits.
 *
 * @author oliver.guenther
 */
@Stateless
public class UnitListenerOperation {

    private final static Logger L = LoggerFactory.getLogger(UnitListenerOperation.class);

    @Inject
    @UniqueUnits
    private EntityManager em;

    /**
     * Listens for UnitHistoies.
     *
     * @param history
     */
    public void addHistory(@Observes UnitHistory history) {
        L.debug("Observed: " + history);
        UniqueUnit uu = em.find(UniqueUnit.class, history.getUniqueUnitId());
        if ( uu != null ) uu.addHistory(history.getComment() + " - " + history.getArranger());
        else L.warn("No UniqueUnit for Event " + history);
    }

    /**
     * Listens for SalesChannelChanges.
     *
     * @param change the change
     */
    public void changeChannel(@Observes SalesChannelChange change) {
        L.debug("Observed: " + change);
        UniqueUnit uu = em.find(UniqueUnit.class, change.getUniqueUnitId());
        if ( uu != null ) uu.setSalesChannel(change.getNewChannel());
        else L.warn("No UniqueUnit for Event " + change);
    }
}
