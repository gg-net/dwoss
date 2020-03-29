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
package eu.ggnet.dwoss.assembly.client.support.monitor;

import java.util.Objects;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentSkipListSet;

import javax.inject.Inject;

import org.slf4j.Logger;

import eu.ggnet.dwoss.core.system.progress.ProgressObserver;
import eu.ggnet.dwoss.core.widget.Dl;

/**
 * Polls the progress from the {@link ProgressObserver} for all serverside activity.
 * If a new activity is happening on the serverside a {@link ServerIndividualProgressPoller} is submitted to the {@link MonitorManager} observering that
 * individual progress.
 *
 * @author oliver.guenther
 */
public class ServerAllProgressPoller implements Runnable {

    private final SortedSet<Integer> localKeys = new ConcurrentSkipListSet<>();

    private MonitorPane view;

    @Inject
    private Logger log;

    public void view(MonitorPane view) {
        this.view = Objects.requireNonNull(view, "view must not be null");
    }

    @Override
    public void run() {
        if ( view == null ) {
            log.warn("run() view not yet set, doing nothing.");
            return;
        }
        try {
            ProgressObserver po = Dl.remote().lookup(ProgressObserver.class);
            if ( po == null || !po.hasProgress() ) return;
            SortedSet<Integer> remoteKeys = po.getActiveProgressKeys();
            if ( remoteKeys.equals(localKeys) ) return; // no new progress, all is tracked.
            remoteKeys.removeAll(localKeys);
            for (Integer key : remoteKeys) {
                view.submit(new ServerIndividualProgressPoller(key, localKeys));
                localKeys.add(key);
            }
        } catch (IllegalArgumentException | NullPointerException ex) {
            log.warn("run(): Exception during progress {}", ex.getMessage());
        }
    }
}
