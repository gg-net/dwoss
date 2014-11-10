package eu.ggnet.saft.runtime;

import java.util.SortedSet;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ScheduledExecutorService;

import org.slf4j.LoggerFactory;

import eu.ggnet.saft.api.progress.ProgressObserver;
import lombok.Data;

import static eu.ggnet.saft.core.Client.lookup;

/**
 * HiddenMonitorDisplayer, considered for usage in a {@link ScheduledExecutorService#scheduleAtFixedRate(java.lang.Runnable, long, long, java.util.concurrent.TimeUnit)
 * } with periodic call.
 * <p/>
 * @author oliver.guenther
 */
@Data
public class HiddenMonitorDisplayer implements Runnable {

    private final SortedSet<Integer> localKeys = new ConcurrentSkipListSet<>();

    private final ClientView view;

    @Override
    public void run() {
        try {
            ProgressObserver po = lookup(ProgressObserver.class);
            if ( !po.hasProgress() ) return;
            SortedSet<Integer> remoteKeys = po.getActiveProgressKeys();
            if ( remoteKeys.equals(localKeys) ) return; // no new progress, all is tracked.
            remoteKeys.removeAll(localKeys);
            for (Integer key : remoteKeys) {
                new HiddenMonitorDisplayTask(key, localKeys, view.progressBar, view.messageLabel).execute();
                localKeys.add(key);
            }
        } catch (IllegalArgumentException | NullPointerException ex) {
            LoggerFactory.getLogger(this.getClass()).warn("Exception during progress {}", ex.getMessage());
            ex.printStackTrace(); // We know, but sometimes you want to se it explode somethere.
        }
    }
}
