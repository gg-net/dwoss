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
package eu.ggnet.saft.runtime;

import java.util.SortedSet;

import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

import org.apache.commons.lang3.StringUtils;

import eu.ggnet.saft.api.progress.HiddenMonitor;
import eu.ggnet.saft.api.progress.ProgressObserver;
import eu.ggnet.saft.runtime.HiddenMonitorDisplayTask.Progress;

import lombok.Data;

import static eu.ggnet.saft.core.Client.lookup;

public class HiddenMonitorDisplayTask extends Task<Progress> {

    @Data
    public static class Progress {

        private final int progress;

        private final String message;
    }

    private final int key;

    private final SortedSet<Integer> localKeys;

    private final ProgressBar progressBar;

    private final Label messageBar;

    /**
     * Constructor.
     * <p/>
     * @param key         the key identifing this progress
     * @param localKeys    a concurent safe set used by all monitors to inform, that one has finished.
     * @param progressBar the progress bar
     * @param messageBar  the message bar
     */

    public HiddenMonitorDisplayTask(int key, SortedSet<Integer> localKeys, ProgressBar progressBar, Label messageBar) {
        this.key = key;
        this.localKeys = localKeys;
        this.progressBar = progressBar;
        this.messageBar = messageBar;
        progressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
    }

    @SuppressWarnings("SleepWhileInLoop")
    @Override
    protected Progress call() throws Exception {
        // Hint: the supplied Monitor has a length of 100;
        HiddenMonitor hm = lookup(ProgressObserver.class).getMonitor(key);
        while (hm != null && !hm.isFinished()) {
            int progress = 100 - hm.getAbsolutRemainingTicks();
            if ( progress < 0 ) progress = 0;
            if ( progress > 100 ) progress = 100;
            process(new Progress(progress, hm.getTitle() + ":" + StringUtils.defaultIfBlank(hm.getMessage(), "")));
            Thread.sleep(250);
            hm = lookup(ProgressObserver.class).getMonitor(key);
        }
        process(new Progress(100, ""));
        localKeys.remove(key);
        done();
        return null;
    }

    protected void process(Progress chunks) {
        if ( !progressBar.isVisible() ) {
            progressBar.setVisible(true);
        }
        if ( chunks.getProgress() > 0 && progressBar.isIndeterminate() ) {
            progressBar.setProgress(0.0);
        }
        messageBar.setText(chunks.getMessage());
        progressBar.setProgress(chunks.getProgress());
    }

    @Override
    protected void done() {
        messageBar.setText("");
        progressBar.setProgress(0.0);
        progressBar.setVisible(false);
    }
}
