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
 * Interface to monitor the progress of running tasks at a client system.
 *
 * @see SubMonitor
 * @see HiddenMonitor
 * @see NullMonitor
 * @see MonitorFactory
 *
 *
 * @author oliver.guenther
 */
public interface IMonitor {

    /**
     * Starts the process of the monitor and returns itself.
     *
     * @return IMonitor - the monitor itself
     */
    IMonitor start();

    /**
     * Gives a name/description to the monitor and returns itself.
     *
     * @param name title for the monitor
     * @return IMonitor - the monitor itself
     */
    IMonitor title(String name);

    /**
     * Sets the amount of tasks, which are done and returns itself afterwards.
     *
     * @param workunits amount of tasks to work
     * @return IMonitor - the monitor itself
     */
    IMonitor worked(int workunits);

    /**
     * Sets the naame of tasks, which are done and returns itself afterwards.
     *
     * @param subMessage name of tasks to work
     * @return IMonitor - the monitor itself
     */
    IMonitor message(String subMessage);

    /**
     * Sets the amount and name of the tasks, which are done and returns itself afterwards.
     *
     * @param workunits  amount of tasks to work
     * @param subMessage name of tasks to work
     * @return IMonitor - the monitor itself
     */
    IMonitor worked(int workunits, String subMessage);

    /**
     * Stops the process of the monitor and returns itself.
     *
     * @return IMonitor - the monitor itself
     */
    IMonitor finish();

    /**
     * Returns the remaining ticks for internal work. This method must return an absolut or relative value of how many ticks are avaiable.
     * It must be ensured, that these ticks are only becoming less, not more (or the resulting presentation will be useless).
     *
     * @return int - the remaining ticks for internal work
     */
    int getAbsolutRemainingTicks();
}
