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

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;
import javax.inject.Singleton;

import javafx.concurrent.Task;

import org.slf4j.Logger;

import eu.ggnet.dwoss.assembly.client.support.executor.Executor;
import eu.ggnet.saft.core.UiCore;

/**
 * Client side monitor manager.
 * <ul>
 * <li>supplies a listview for all active progress</li>
 * <li>polls progress from the server</li>
 * <li>conntects to the saft progress property</li>
 * <li>can handle services, task, runables and callables for client side progress view</li>
 * </ul>
 *
 * @author oliver.guenther
 */
//TODO: This class is a great candidate to replace Ui.progress() of Saft. Needs some stripping and a movement to core.widget.
@Singleton
public class MonitorManager {

    @Inject
    private Logger log;

    @Inject
    private MonitorPane monitorPane;

    @Inject
    @Executor
    private ScheduledExecutorService ses;

    @Inject
    private ServerAllProgressPoller poller;

    private final AtomicBoolean paneCreated = new AtomicBoolean(false);

    private final AtomicBoolean running = new AtomicBoolean(false);

    /**
     * One time method to create the connected monitor pane.
     *
     * @return the monitorpane
     * @throws IllegalStateException if called a second time.
     */
    public MonitorPane createPane() throws IllegalStateException {
        if ( !paneCreated.compareAndSet(false, true) ) throw new IllegalStateException("MonitorPane has been created already!");
        return monitorPane;
    }

    /**
     * Start the background polling of server progress.
     * Also binds to the saft naive property.
     * Multiple calls are ignored
     */
    // TODO: Consider an autostarting version, but make sure, that the poller works with everything missing.
    public void startPolling() {
        if ( !running.compareAndSet(false, true) ) {
            log.info("startPolling() called more than once, ignoring");
            return;
        }
        log.debug("startPolling() setting view on the poller and schedule it at a 2 sec rate.");
        poller.view(monitorPane);
        ses.scheduleAtFixedRate(poller, 2, 2, TimeUnit.SECONDS);

        log.debug("startPolling() setting the listener for saft naiv progress.");
        UiCore.backgroundActivityProperty().addListener((ob, o, n) -> monitorPane.saftBackground(n));
    }

    /**
     * Supply a task for background execution and display in the ui.
     *
     * @param task the task
     */
    public void submit(Task<?> task) {
        monitorPane.submit(task);
    }

}
