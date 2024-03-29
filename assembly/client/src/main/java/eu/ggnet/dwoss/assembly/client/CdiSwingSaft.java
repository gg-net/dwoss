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
package eu.ggnet.dwoss.assembly.client;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

import eu.ggnet.saft.core.Saft;

import jakarta.annotation.PostConstruct;
import eu.ggnet.saft.core.impl.Swing;
import eu.ggnet.saft.core.ui.LocationStorage;

/**
 * CDI Saft with Swing.
 *
 * @author mirko.schulze
 */
@ApplicationScoped
// @Specializes
public class CdiSwingSaft extends Saft {

    @Inject
    private Instance<Object> instance;

    public CdiSwingSaft() {
        super(new LocationStorage(), Executors.newCachedThreadPool(new ThreadFactory() {

            private final ThreadGroup group = new ThreadGroup("saft-uicore-pool");

            private final AtomicInteger counter = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(group, r, "Thread-" + counter.incrementAndGet() + "-" + r.toString());
            }
        }));
    }

    @PostConstruct
    private void postInit() {
        init(new Swing(this, p -> instance.select(p).get()));
        core().captureMode(true);
    }

}
