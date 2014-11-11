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
package eu.ggnet.dwoss.progress;

import eu.ggnet.saft.api.progress.IMonitor;

import javax.enterprise.inject.Alternative;

/**
 *
 * @author oliver.guenther
 */
@Alternative
public class NullMonitor implements IMonitor {

    @Override
    public IMonitor start() {
        return this;
    }

    @Override
    public IMonitor finish() {
        return this;
    }

    @Override
    public IMonitor title(String name) {
        return this;
    }

    @Override
    public IMonitor worked(int workunits) {
        return this;
    }

    @Override
    public IMonitor message(String subMessage) {
        return this;
    }

    @Override
    public IMonitor worked(int workunits, String subMessage) {
        return this;
    }

    @Override
    public int getAbsolutRemainingTicks() {
        return 0;
    }

}
