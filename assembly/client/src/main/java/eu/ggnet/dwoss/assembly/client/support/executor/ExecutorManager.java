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
package eu.ggnet.dwoss.assembly.client.support.executor;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PreDestroy;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;

import eu.ggnet.dwoss.assembly.client.support.executor.Executor;

/**
 * First throw with an alternative to static UiCore executors.
 *
 * @author oliver.guenther
 */
@Singleton
public class ExecutorManager {

    @Inject
    private Logger log;

    @Produces
    @Executor
    private final ScheduledExecutorService ses = Executors.newScheduledThreadPool(1, new ThreadFactory() {

        private final ThreadGroup group = new ThreadGroup("dwoss-global-scheduled-pool");

        private final AtomicInteger counter = new AtomicInteger(0);

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(group, r, "ScheduledThread-" + counter.incrementAndGet() + "-" + r.toString());
        }
    });

    @PreDestroy
    private void shutdown() {
        log.debug("shutdown()");
        ses.shutdown();
    }
}
