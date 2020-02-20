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
package eu.ggnet.dwoss.assembly.client.support;


import java.util.Objects;
import java.util.SortedSet;
import java.util.concurrent.*;

import javax.inject.Inject;

import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.core.system.progress.ProgressObserver;
import eu.ggnet.saft.core.Dl;

/**
 * MonitorServerManager, considered for usage in a 
 * {@link ScheduledExecutorService#scheduleAtFixedRate(java.lang.Runnable, long, long, java.util.concurrent.TimeUnit)} with periodic call.
 * <p>
 * @author oliver.guenther
 */
public class MonitorServerManager implements Runnable {

    private final SortedSet<Integer> localKeys = new ConcurrentSkipListSet<>();

    private MonitorPane view;

    @Inject @Executor
    private ScheduledExecutorService ses;
    
    public void start(MonitorPane view) {
        this.view = Objects.requireNonNull(view,"view must not be null");
        ses.scheduleAtFixedRate(this, 2, 2, TimeUnit.SECONDS);
    }
    
    @Override
    public void run() {
        try {
            ProgressObserver po = Dl.remote().lookup(ProgressObserver.class);
            if (po == null || !po.hasProgress() ) return;
            SortedSet<Integer> remoteKeys = po.getActiveProgressKeys();
            if ( remoteKeys.equals(localKeys) ) return; // no new progress, all is tracked.
            remoteKeys.removeAll(localKeys);
            for (Integer key : remoteKeys) {
                view.submit(new MonitorServerTask(key, localKeys));
                localKeys.add(key);
            }
        } catch (IllegalArgumentException | NullPointerException ex) {
            LoggerFactory.getLogger(this.getClass()).warn("run(): Exception during progress {}", ex.getMessage());
        }
    }
}