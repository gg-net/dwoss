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
package eu.ggnet.dwoss.assembly.client.support;

import java.util.concurrent.locks.ReentrantLock;

import javafx.beans.value.ChangeListener;
import javafx.concurrent.Task;

import eu.ggnet.saft.core.UiCore;

/**
 * Veriy simple task for minimal local progress.
 *
 * @author oliver.guenther
 */
public class MonitorClientTask extends Task<Void> {

    @Override
    protected Void call() throws Exception {
        updateTitle("Hintergrundaktivität");
        updateMessage("Hintergrundaktivität");
        ReentrantLock lock = new ReentrantLock();
        ChangeListener<Boolean> l = (ob, o, n) -> {
            if ( !n ) {
                lock.unlock();
            }
        };
        UiCore.backgroundActivityProperty().addListener(l);
        lock.lock();
        UiCore.backgroundActivityProperty().removeListener(l);
        return null;
    }

}
