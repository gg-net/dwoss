package eu.ggnet.dwoss.progress;

import eu.ggnet.saft.api.progress.HiddenMonitor;
import eu.ggnet.saft.api.progress.ProgressObserver;

import java.util.SortedSet;

import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 * Delegator for the oberervalbe part of the {@link MonitorFactory}.
 * <p/>
 * @author oliver.guenther
 */
@Stateless
public class ProgressObserverOperation implements ProgressObserver {

    @Inject
    private MonitorFactory monitorFactory;

    /**
     * See {@link MonitorFactory#hasProgress() }.
     * <p/>
     * @return See {@link MonitorFactory#hasProgress() }.
     */
    @Override
    public boolean hasProgress() {
        return monitorFactory.hasProgress();
    }

    /**
     * See {@link MonitorFactory#getActiveProgressKeys() }.
     * <p/>
     * @return See {@link MonitorFactory#getActiveProgressKeys() }.
     */
    @Override
    public SortedSet<Integer> getActiveProgressKeys() {
        return monitorFactory.getActiveProgressKeys();
    }

    /**
     * See {@link MonitorFactory#getMonitor(int) }.
     * <p/>
     * @return See {@link MonitorFactory#getMonitor(int) }.
     */
    @Override
    public HiddenMonitor getMonitor(int key) {
        return monitorFactory.getMonitor(key);
    }
}
