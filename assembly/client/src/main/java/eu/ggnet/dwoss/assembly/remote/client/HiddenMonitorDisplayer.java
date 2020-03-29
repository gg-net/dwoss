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
package eu.ggnet.dwoss.assembly.remote.client;

import java.util.SortedSet;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ScheduledExecutorService;

import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.core.system.progress.ProgressObserver;
import eu.ggnet.dwoss.core.widget.Dl;

/**
 * HiddenMonitorDisplayer, considered for usage in a 
 * {@link ScheduledExecutorService#scheduleAtFixedRate(java.lang.Runnable, long, long, java.util.concurrent.TimeUnit)} with periodic call.
 * <p/>
 * @author oliver.guenther
 */
public class HiddenMonitorDisplayer implements Runnable {

    private final SortedSet<Integer> localKeys = new ConcurrentSkipListSet<>();

    private final ClientView view;

    public HiddenMonitorDisplayer(ClientView view) {
        this.view = view;
    }
    
    @Override
    public void run() {
        try {
            ProgressObserver po = Dl.remote().lookup(ProgressObserver.class);
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