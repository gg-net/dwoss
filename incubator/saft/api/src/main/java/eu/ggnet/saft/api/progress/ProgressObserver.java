/* 
 * Copyright (C) 2014 pascal.perau
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
package eu.ggnet.saft.api.progress;

import java.util.SortedSet;

import javax.ejb.Remote;

/*
 Having the Remote Progress concept so deep in the API is just solution for now.
 It would be much better that the solution would be something like:
 remote-progress -> client-progress <- saft-core -> saft-runtime
 So in the client progess the information is haded over, but for now we live with this solution.
 */
/**
 * Delegator for the observable part of the {@link MonitorFactory}.
 * <p/>
 * @author oliver.guenther
 */
@Remote
public interface ProgressObserver {

    /**
     * See {@link MonitorFactory#getActiveProgressKeys() }.
     * <p/>
     * @return See {@link MonitorFactory#getActiveProgressKeys() }.
     */
    SortedSet<Integer> getActiveProgressKeys();

    /**
     * See {@link MonitorFactory#getMonitor(int) }.
     * <p/>
     * @param key the key of the monitor.
     * @return See {@link MonitorFactory#getMonitor(int) }.
     */
    HiddenMonitor getMonitor(int key);

    /**
     * See {@link MonitorFactory#hasProgress() }.
     * <p/>
     * @return See {@link MonitorFactory#hasProgress() }.
     */
    boolean hasProgress();
}
