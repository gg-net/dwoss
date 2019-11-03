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
package eu.ggnet.dwoss.core.system.progress;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PreDestroy;
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
     * @return a new Submonitor.
     */
    public SubMonitor newSubMonitor(String title) {
        return newSubMonitor(title, 100);
    }

    /**
     * Creates a new Submonitor, with initial work remaing.
     * <p>
     * @param title
     * @param workRemaining
     * @return a new Submonitor.
     */
    public SubMonitor newSubMonitor(String title, int workRemaining) {
        L.debug("creating Submonitor {}", title);
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

    @Schedule(second = "0", minute = "*/5", hour = "*", dayOfMonth = "*", month = "*", year = "*", info = "Cleanup of Montiors", persistent = false)
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

    @PreDestroy
    private void shutdown() {
        L.info("Monitorfactory is going down");
    }

}
