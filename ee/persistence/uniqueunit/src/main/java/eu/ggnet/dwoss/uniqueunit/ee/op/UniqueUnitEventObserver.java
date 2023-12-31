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
package eu.ggnet.dwoss.uniqueunit.ee.op;

import jakarta.ejb.Stateless;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.stock.api.event.DeleteEvent;
import eu.ggnet.dwoss.stock.api.event.ScrapEvent;
import eu.ggnet.dwoss.uniqueunit.api.event.SalesChannelChange;
import eu.ggnet.dwoss.uniqueunit.api.event.UnitHistory;
import eu.ggnet.dwoss.uniqueunit.ee.assist.UniqueUnits;
import eu.ggnet.dwoss.uniqueunit.ee.eao.UniqueUnitEao;
import eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnit;

import static eu.ggnet.dwoss.core.common.values.SalesChannel.UNKNOWN;

/**
 * Listener for Events targeting UniqueUnits.
 *
 * @author oliver.guenther
 */
@Stateless
public class UniqueUnitEventObserver {

    private final static Logger L = LoggerFactory.getLogger(UniqueUnitEventObserver.class);

    @Inject
    @UniqueUnits
    private EntityManager em;

    @Inject
    private UniqueUnitEao uueao;

    /**
     * Listens for UnitHistoies.
     *
     * @param history
     */
    public void addHistory(@Observes UnitHistory history) {
        L.debug("Observed: " + history);
        UniqueUnit uu = em.find(UniqueUnit.class, history.uniqueUnitId());
        if ( uu != null ) uu.addHistory(history.comment() + " - " + history.arranger());
        else L.warn("No UniqueUnit for Event " + history);
    }

    /**
     * Listens for SalesChannelChanges.
     *
     * @param change the change
     */
    public void changeChannel(@Observes SalesChannelChange change) {
        L.debug("Observed: " + change);
        UniqueUnit uu = em.find(UniqueUnit.class, change.uniqueUnitId());
        if ( uu != null ) uu.setSalesChannel(change.newChannel());
        else L.warn("No UniqueUnit for Event " + change);
    }

    public void onScrap(@Observes ScrapEvent event) {
        L.debug("onScrap({})", event);
        for (long uniqueUnitId : event.uniqueUnitIds()) {
            UniqueUnit uu = uueao.findById((int)uniqueUnitId);
            if ( uu != null ) {
                uu.addHistory("Verschrottung auf Grund " + event.comment() + " durch " + event.arranger());
                uu.setInternalComment(uu.getInternalComment() + ", verschrottet");
                uu.setSalesChannel(UNKNOWN);
            }
        }
    }

    public void onDelete(@Observes DeleteEvent event) {
        L.debug("onDelete({})", event);
        for (long uniqueUnitId : event.uniqueUnitIds()) {
            UniqueUnit uu = uueao.findById((int)uniqueUnitId);
            if ( uu != null ) {
                uu.addHistory("Löschung auf Grund " + event.comment() + " durch " + event.arranger());
                uu.setInternalComment(uu.getInternalComment() + ", geloscht");
                uu.setSalesChannel(UNKNOWN);
            }
        }
    }

}
