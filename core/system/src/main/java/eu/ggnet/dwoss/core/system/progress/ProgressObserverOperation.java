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


import java.util.SortedSet;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.inject.Named;


/**
 * Delegator for the oberervalbe part of the {@link MonitorFactory}.
 * <p/>
 * @author oliver.guenther
 */
@Named
@Stateless
public class ProgressObserverOperation implements ProgressObserver {

    @Inject
    private MonitorFactory monitorFactory;

    /**
     * See {@link MonitorFactory#hasProgress() }.
     * <p/>
     * @return See {@link MonitorFactory#hasProgress() }.
     */
    @Override
    public boolean hasProgress() {
        return monitorFactory.hasProgress();
    }

    /**
     * See {@link MonitorFactory#getActiveProgressKeys() }.
     * <p/>
     * @return See {@link MonitorFactory#getActiveProgressKeys() }.
     */
    @Override
    public SortedSet<Integer> getActiveProgressKeys() {
        return monitorFactory.getActiveProgressKeys();
    }

    /**
     * See {@link MonitorFactory#getMonitor(int) }.
     * <p/>
     * @return See {@link MonitorFactory#getMonitor(int) }.
     */
    @Override
    public HiddenMonitor getMonitor(int key) {
        return monitorFactory.getMonitor(key);
    }
}
