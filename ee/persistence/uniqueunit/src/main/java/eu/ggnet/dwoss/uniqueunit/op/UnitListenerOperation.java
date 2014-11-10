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
