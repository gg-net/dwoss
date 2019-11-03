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


import org.slf4j.Logger;

public final class SubMonitor implements IMonitor {

    /**
     * <p>
     * Converts an unknown (possibly null) IProgressMonitor into a SubMonitor. It is
     * not necessary to call done() on the result, but the caller is responsible for calling
     * done() on the argument. Calls beginTask on the argument.</p>
     *
     * <p>
     * This method should generally be called at the beginning of a method that accepts
     * an IProgressMonitor in order to convert the IProgressMonitor into a SubMonitor.</p>
     *
     * @param monitor monitor to convert to a SubMonitor instance or null. Treats null
     *                as a new instance of <code>NullProgressMonitor</code>.
     * @return a SubMonitor instance that adapts the argument
     */
    public static SubMonitor convert(IMonitor monitor) {
        if ( monitor == null ) {
            monitor = new NullMonitor();
        }
        if ( monitor instanceof SubMonitor ) {
            return (SubMonitor)monitor;
        }
        return new SubMonitor(monitor);
    }

    /**
     * <p>
     * Converts an unknown (possibly null) IProgressMonitor into a SubMonitor allocated
     * with the given number of ticks. It is not necessary to call done() on the result,
     * but the caller is responsible for calling done() on the argument. Calls beginTask
     * on the argument.</p>
     *
     * <p>
     * This method should generally be called at the beginning of a method that accepts
     * an IProgressMonitor in order to convert the IProgressMonitor into a SubMonitor.</p>
     *
     * @param monitor       monitor to convert to a SubMonitor instance or null. Treats null
     *                      as a new instance of <code>NullProgressMonitor</code>.
     * @param workRemaining number of ticks that will be available in the resulting monitor
     * @return a SubMonitor instance that adapts the argument
     */
    public static SubMonitor convert(IMonitor monitor, int workRemaining) {
        SubMonitor result = convert(monitor);
        result.setWorkRemaining(workRemaining);
        return result;
    }

    /**
     * <p>
     * Converts an unknown (possibly null) IProgressMonitor into a SubMonitor allocated
     * with the given number of ticks. It is not necessary to call done() on the result,
     * but the caller is responsible for calling done() on the argument. Calls beginTask
     * on the argument.</p>
     *
     * <p>
     * This method should generally be called at the beginning of a method that accepts
     * an IProgressMonitor in order to convert the IProgressMonitor into a SubMonitor.</p>
     *
     * @param monitor to convert into a SubMonitor instance or null. If given a null argument,
     *                the resulting SubMonitor will not report its progress anywhere.
     * @param title   user readable name to pass to monitor.beginTask. Never null.
     * @param work    calls {@link SubMonitor#setWorkRemaining(int) }
     * @return a new SubMonitor instance that is a child of the given monitor
     */
    public static SubMonitor convert(IMonitor monitor, String title, int work) {
        SubMonitor result = convert(monitor, work);
        result.title(title);
        return result;
    }

    private SubMonitor(IMonitor monitor) {
        this.monitor = monitor;
        this.start();
    }

    /**
     * Sets an instance of an slf4j Logger. If this is supplied, all messages are send to the debug
     * level of the Logger.
     *
     * @param logger
     */
    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    @Override
    public IMonitor start() {
        if ( !started ) {
            monitor.start();
            started = true;
        }
        return this;
    }

    @Override
    public IMonitor title(String name) {
        monitor.title(name);
        return this;
    }

    @Override
    public IMonitor message(String subMessage) {
        monitor.message(subMessage);
        if ( logger != null ) logger.debug(subMessage);
        return this;
    }

    @Override
    public IMonitor worked(int workunits, String subMessage) {
        message(subMessage);
        worked(workunits);
        return this;
    }

    @Override
    public IMonitor finish() {
        monitor.finish();
        return this;
    }

    @Override
    public int getAbsolutRemainingTicks() {
        return workRemaining;
    }

    /**
     * <p>
     * Sets the work remaining for this SubMonitor instance. This is the total number
     * of ticks that may be reported by all subsequent calls to message(int), newChild(int), etc.
     * This may be called many times for the same SubMonitor instance. When this method
     * is called, the remaining space on the progress monitor is redistributed into the given
     * number of ticks.</p>
     *
     * <p>
     * It doesn't matter how much progress has already been reported with this SubMonitor
     * instance. If you call setWorkRemaining(100), you will be able to report 100 more ticks of
     * work before the progress meter reaches 100%.</p>
     *
     * @param workRemaining total number of remaining ticks
     * @return the receiver
     */
    public IMonitor setWorkRemaining(int workRemaining) {
        this.workRemaining = workRemaining;
        if ( workRemaining > 0 ) {
            barPerTick = (double)monitor.getAbsolutRemainingTicks() / workRemaining;
        }
        return this;
    }


    /* (non-Javadoc)
     * @see org.eclipse.core.runtime.IProgressMonitor#message(int)
     */
    @Override
    public IMonitor worked(int work) {
        if ( work <= 0 ) {
            return this;
        }
        workRemaining = workRemaining - work;
        tempSmallWork = tempSmallWork + (barPerTick * work);
        monitor.worked((int)tempSmallWork);
        tempSmallWork -= (int)tempSmallWork;
        return this;
    }

    public SubMonitor newChild(final int work) {

        return SubMonitor.convert(new IMonitor() {

            private int totalWork = work;

            @Override
            public IMonitor start() {
                return this;
            }

            @Override
            public IMonitor title(String name) {
                monitor.title(name);
                return this;
            }

            @Override
            public IMonitor worked(int workunits) {
                if ( workunits < 1 || totalWork < 1 ) {
                    return this;
                }
                if ( workunits > totalWork ) {
                    workunits = totalWork;
                }
                totalWork -= workunits;
                SubMonitor.this.worked(workunits);
                return this;
            }

            @Override
            public IMonitor message(String subMessage) {
                monitor.message(subMessage);
                return this;
            }

            @Override
            public IMonitor worked(int workunits, String subMessage) {
                worked(workunits);
                message(subMessage);
                return this;
            }

            @Override
            public IMonitor finish() {
                worked(totalWork);
                return this;
            }

            @Override
            public int getAbsolutRemainingTicks() {
                return totalWork;
            }
        });
    }

    /**
     * Used to communicate with the monitor of this progress monitor tree
     */
    private final IMonitor monitor;

    private boolean started = false;

    /**
     * Bar length for one tick
     */
    private double barPerTick;

    private int workRemaining;

    private double tempSmallWork = 0.0;

    private Logger logger;

}
