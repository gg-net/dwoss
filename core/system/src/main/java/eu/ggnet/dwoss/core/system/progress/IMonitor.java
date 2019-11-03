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

/**
 * Wrapper for progross with a minimal api
 * Documentation may follow if in heavy usage
 *
 * @author oliver.guenther
 */
public interface IMonitor {

    /**
     * Starts the process of monitor.
     * <p>
     * @return itself.
     */
    IMonitor start();

    /**
     * Name of monitor.
     * <p/>
     * @param name
     * @return itself.
     */
    IMonitor title(String name);

    /**
     * Amount of tasks, which are done.
     * <p/>
     * @param workunits
     * @return itself.
     */
    IMonitor worked(int workunits);

    /**
     * Name of tasks, which are done.
     * <p/>
     * @param subMessage
     * @return itself.
     */
    IMonitor message(String subMessage);

    /**
     * Amount and name of the tasks, which are done.
     * <p/>
     * @param workunits
     * @param subMessage
     * @return itself.
     */
    IMonitor worked(int workunits, String subMessage);

    /**
     * Monitor at end.
     * <p>
     * @return itself.
     */
    IMonitor finish();

    /**
     * Returns the remaining ticks for internal work. This method must return an absolut or relative value of how many ticks are avaiable.
     * It must be ensured, that these ticks are only becoming less, not more. (Or the resultig presentation will be useless)
     *
     * @return the remaining ticks for internal work
     */
    int getAbsolutRemainingTicks();
}
