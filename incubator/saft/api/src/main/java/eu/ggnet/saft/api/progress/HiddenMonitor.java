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
package eu.ggnet.saft.api.progress;

import java.io.Serializable;

import lombok.*;

/**
 *
 * @author oliver.guenther
 */
@ToString
public class HiddenMonitor implements IMonitor, Serializable {

    @Setter
    @Getter
    private String title;

    @Setter
    @Getter
    private String message;

    @Getter
    private boolean started = false;

    @Getter
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
     * Returns true if this monitor has not changed the last 5 min.
     * <p/>
     * @return true if this monitor has not changed the last 5 min.
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

    @Override
    public int getAbsolutRemainingTicks() {
        return progress;
    }
}
