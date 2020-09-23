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

import java.io.Serializable;

/**
 * Server-sided object to allow progress monitoring.
 *
 * @see IMonitor
 * @see SubMonitor
 * @see MonitorFactory
 *
 * @author oliver.guenther
 */
public class HiddenMonitor implements IMonitor, Serializable {

    private String title;

    private String message;

    private boolean started = false;

    private boolean finished = false;

    private int progress = 100;

    private long lastChange;

    @Override
    public IMonitor title(String title) {
        this.title = title;
        return this;
    }

    @Override
    public IMonitor message(String message) {
        this.message = message;
        return this;
    }

    /**
     * Checks if this monitor has changed in the last 5 minutes and returns true if no change is detected.
     *
     * @return boolean - true, if this monitor has not changed during the last 5 minutes
     */
    public boolean isStale() {
        return (System.currentTimeMillis() - lastChange > (1000 * 60 * 5));
    }

    @Override
    public IMonitor start() {
        lastChange = System.currentTimeMillis();
        started = true;
        return this;
    }

    @Override
    public IMonitor worked(int workunits) {
        lastChange = System.currentTimeMillis();
        if ( finished ) return this;
        if ( !started ) start();
        if ( workunits <= 0 ) return this;
        if ( progress < workunits ) progress = 0;
        progress -= workunits;
        if ( progress == 0 ) finish();
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
        lastChange = System.currentTimeMillis();
        finished = true;
        return this;
    }

    //<editor-fold defaultstate="collapsed" desc="getter/setter">
    @Override
    public int getAbsolutRemainingTicks() {
        return progress;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public boolean isStarted() {
        return started;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    //</editor-fold>

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" + "title=" + title + ", message=" + message + ", started=" + started + ", finished=" + finished + ", progress=" + progress + ", lastChange=" + lastChange + '}';
    }

}
