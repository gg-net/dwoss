package eu.ggnet.dwoss.progress;

import eu.ggnet.saft.api.progress.HiddenMonitor;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.Schedule;
import javax.ejb.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Factory for ProgressMonitors to be used in a EJB environment.
 * <p/>
 * @author oliver.guenther
 */
@Singleton
public class MonitorFactory {

    private final static Logger L = LoggerFactory.getLogger(MonitorFactory.class);

    private final Map<Integer, HiddenMonitor> monitors = new ConcurrentHashMap<>();

    private final Map<Integer, Integer> lastProgress = new ConcurrentHashMap<>();

    /**
     * Creates a new intermediate Submonitor.
     * <p>
     * @param title
     * @return
     */
    public SubMonitor newSubMonitor(String title) {
        return newSubMonitor(title, 100);
    }

    /**
     * Creates a new Submonitor, with initial work remaing.
     * <p>
     * @param title
     * @param workRemaining
     * @return
     */
    public SubMonitor newSubMonitor(String title, int workRemaining) {
        HiddenMonitor monitor = new HiddenMonitor();
        monitor.title(title);
        monitors.put(monitor.hashCode(), monitor);
        return SubMonitor.convert(monitor, workRemaining);
    }

    /**
     * Returns true if some task/operation with progress is running and not yet finished.
     * <p/>
     * @return true if some task/operation with progress is running and not yet finished.
     */
    public boolean hasProgress() {
        for (HiddenMonitor m : monitors.values()) {
            if ( !m.isFinished() ) return true;
        }
        return false;
    }

    /**
     * Returns the keys for all hidden monitors which are not yet finished.
     * <p/>
     * @return the keys for all hidden monitors which are not yet finished.
     */
    public SortedSet<Integer> getActiveProgressKeys() {
        SortedSet<Integer> result = new TreeSet<>();
        for (Map.Entry<Integer, HiddenMonitor> entry : monitors.entrySet()) {
            if ( !entry.getValue().isFinished() ) result.add(entry.getKey());
        }
        return result;
    }

    /**
     * Returns a snapshot of the selected Monitor.
     * <p/>
     * @param key the key
     * @return a snapshot of the selected Monitor.
     */
    public HiddenMonitor getMonitor(int key) {
        return monitors.get(key);
    }

    @Schedule(second = "0", minute = "*/5", hour = "*", dayOfMonth = "*", month = "*", year = "*")
    private void cleanUp() {
        L.debug("cleanUp called by Timer @ {}", new Date());
        for (Integer key : new HashSet<>(monitors.keySet())) {
            if ( monitors.get(key).isFinished() ) {
                HiddenMonitor m = monitors.remove(key);
                L.info("Evicting finished Monitor {}", m);
                lastProgress.remove(key);
            } else if ( monitors.get(key).isStale() ) {
                HiddenMonitor m = monitors.remove(key);
                L.warn("Evicting stale Monitor {}", m);
                lastProgress.remove(key);
            }
        }
    }
}
