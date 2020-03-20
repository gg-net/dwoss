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
package eu.ggnet.dwoss.assembly.client.support.monitor;

import java.util.Objects;
import java.util.concurrent.CountDownLatch;

import javafx.beans.value.ChangeListener;
import javafx.concurrent.Task;

import eu.ggnet.saft.core.UiCore;

/**
 * Task to display the naive saft progress.
 *
 * @see MonitorManager#startPolling()
 * @see MonitorPane#saftBackground(boolean)
 * @author oliver.guenther
 */
class SaftNaiveProgressTask extends Task<Void> {

    private final MonitorPane monitorPane;

    SaftNaiveProgressTask(MonitorPane monitorPane) {
        this.monitorPane = Objects.requireNonNull(monitorPane, "monitorPane must not be null");
    }

    @Override
    protected Void call() throws Exception {
        updateTitle("Hintergrundaktivität");
        updateMessage("Hintergrundaktivität");
        CountDownLatch latch = new CountDownLatch(1);
        ChangeListener<Boolean> l = (ob, o, n) -> {
            if ( !n ) {
                latch.countDown();
            }
        };
        UiCore.backgroundActivityProperty().addListener(l);
        if ( !monitorPane.runningSaftBackgroundTask().get() ) latch.countDown(); // Doublecheck, if we ended befor creation.
        latch.await();
        UiCore.backgroundActivityProperty().removeListener(l);
        return null;
    }

}
