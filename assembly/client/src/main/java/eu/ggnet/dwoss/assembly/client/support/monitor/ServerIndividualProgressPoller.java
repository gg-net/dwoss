/*
 * Copyright (C) 2020 GG-Net GmbH
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

import java.util.SortedSet;

import javafx.concurrent.Task;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.core.system.progress.HiddenMonitor;
import eu.ggnet.dwoss.core.system.progress.ProgressObserver;
import eu.ggnet.dwoss.core.widget.Dl;

/**
 * Task to poll the progress of one running activity on the server.
 *
 * @see ServerAllProgressPoller
 * @author oliver.guenther
 */
public class ServerIndividualProgressPoller extends Task<Void> {

    private final int key;

    private final SortedSet<Integer> localKeys;

    public ServerIndividualProgressPoller(int key, SortedSet<Integer> localKeys) {
        this.key = key;
        this.localKeys = localKeys;
    }

    @Override
    protected Void call() throws Exception {
        // Hint: the supplied Monitor has a length of 100;
        HiddenMonitor hm = Dl.remote().lookup(ProgressObserver.class).getMonitor(key);
        LoggerFactory.getLogger(ServerIndividualProgressPoller.class).debug("call() starting to poll progress of {}", hm.getTitle());
        updateTitle(hm.getTitle());
        while (hm != null && !hm.isFinished() && !hm.isStale()) {
            int progress = 100 - hm.getAbsolutRemainingTicks();
            if ( progress < 0 ) progress = 0;
            if ( progress > 100 ) progress = 100;
            updateProgress(progress, 100);
            updateMessage(hm.getTitle() + ":" + StringUtils.defaultIfBlank(hm.getMessage(), ""));
            Thread.sleep(250); // Poll Step Size
            hm = Dl.remote().lookup(ProgressObserver.class).getMonitor(key);
        }
        // Todo: Later, maybe try with events.
        localKeys.remove(key);
        return null;
    }

}
