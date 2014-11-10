package eu.ggnet.saft.api.progress;

import java.util.SortedSet;

import javax.ejb.Remote;

/*
 Having the Remote Progress concept so deep in the API is just solution for now.
 It would be much better that the solution would be something like:
 remote-progress -> client-progress <- saft-core -> saft-runtime
 So in the client progess the information is haded over, but for now we live with this solution.
 */
/**
 * Delegator for the observable part of the {@link MonitorFactory}.
 * <p/>
 * @author oliver.guenther
 */
@Remote
public interface ProgressObserver {

    /**
     * See {@link MonitorFactory#getActiveProgressKeys() }.
     * <p/>
     * @return See {@link MonitorFactory#getActiveProgressKeys() }.
     */
    SortedSet<Integer> getActiveProgressKeys();

    /**
     * See {@link MonitorFactory#getMonitor(int) }.
     * <p/>
     * @param key the key of the monitor.
     * @return See {@link MonitorFactory#getMonitor(int) }.
     */
    HiddenMonitor getMonitor(int key);

    /**
     * See {@link MonitorFactory#hasProgress() }.
     * <p/>
     * @return See {@link MonitorFactory#hasProgress() }.
     */
    boolean hasProgress();
}
