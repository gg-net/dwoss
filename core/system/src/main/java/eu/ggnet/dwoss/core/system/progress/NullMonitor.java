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

import jakarta.enterprise.inject.Alternative;

/**
 * Implementation of {@link IMonitor} to prevent null pointers.
 * <p/>
 * Overriden methods implement no further actions and only allow usage of an "empty" monitor for testing purposes.
 *
 * @see SubMonitor#convert(IMonitor)
 *
 * @author oliver.guenther
 */
@Alternative
public class NullMonitor implements IMonitor {

    /**
     * Returns this {@link NullMonitor}.
     *
     * @return IMonitor - this object
     */
    @Override
    public IMonitor start() {
        return this;
    }

    /**
     * Returns this {@link NullMonitor}.
     *
     * @return IMonitor - this object
     */
    @Override
    public IMonitor finish() {
        return this;
    }

    /**
     * Returns this {@link NullMonitor}.
     *
     * @return IMonitor - this object
     */
    @Override
    public IMonitor title(String name) {
        return this;
    }

    /**
     * Returns this {@link NullMonitor}.
     *
     * @return IMonitor - this object
     */
    @Override
    public IMonitor worked(int workunits) {
        return this;
    }

    /**
     * Returns this {@link NullMonitor}.
     *
     * @return IMonitor - this object
     */
    @Override
    public IMonitor message(String subMessage) {
        return this;
    }

    /**
     * Returns this {@link NullMonitor}.
     *
     * @return IMonitor - this object
     */
    @Override
    public IMonitor worked(int workunits, String subMessage) {
        return this;
    }

    /**
     * Returns 0 (zero).
     * 
     * @return int - 0 (zero)
     */
    @Override
    public int getAbsolutRemainingTicks() {
        return 0;
    }

}




