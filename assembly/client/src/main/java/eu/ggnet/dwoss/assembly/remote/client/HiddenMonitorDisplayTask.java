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
package eu.ggnet.dwoss.assembly.remote.client;

import java.util.List;
import java.util.SortedSet;
import java.util.concurrent.ExecutionException;

import javax.swing.*;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.assembly.remote.client.HiddenMonitorDisplayTask.Progress;
import eu.ggnet.dwoss.core.system.progress.HiddenMonitor;
import eu.ggnet.dwoss.core.system.progress.ProgressObserver;
import eu.ggnet.dwoss.core.widget.Dl;

public class HiddenMonitorDisplayTask extends SwingWorker<Void, HiddenMonitorDisplayTask.Progress> {

    public static class Progress {

        public final int progress;

        public final String message;

        public Progress(int progress, String message) {
            this.progress = progress;
            this.message = message;
        }

        @Override
        public String toString() {
            return "Progress{" + "progress=" + progress + ", message=" + message + '}';
        }
        
    }

    private final int key;

    private final SortedSet<Integer> keys;

    private final JProgressBar progressBar;

    private final JLabel messageBar;

    /**
     * Constructor.
     * <p/>
     * @param key         the key identifing this progress
     * @param progressBar the progress bar
     * @param messageBar  the message bar
     * @param keys        a concurent safe set used by all monitors to inform, that one has finished.
     */
    public HiddenMonitorDisplayTask(int key, SortedSet<Integer> keys, final JProgressBar progressBar, final JLabel messageBar) {
        this.key = key;
        this.keys = keys;
        this.progressBar = progressBar;
        this.messageBar = messageBar;
        progressBar.setIndeterminate(true);
    }

    @Override
    @SuppressWarnings("SleepWhileInLoop")
    protected Void doInBackground() throws Exception {
        // Hint: the supplied Monitor has a length of 100;
        HiddenMonitor hm = Dl.remote().lookup(ProgressObserver.class).getMonitor(key);
        while (hm != null && !hm.isFinished()) {
            int progress = 100 - hm.getAbsolutRemainingTicks();
            if ( progress < 0 ) progress = 0;
            if ( progress > 100 ) progress = 100;
            publish(new Progress(progress, hm.getTitle() + ":" + StringUtils.defaultIfBlank(hm.getMessage(), "")));
            Thread.sleep(250);
            hm = Dl.remote().lookup(ProgressObserver.class).getMonitor(key);
        }
        publish(new Progress(100, ""));
        keys.remove(key);
        return null;
    }

    @Override
    protected void process(List<Progress> chunks) {
        Progress last = chunks.get(chunks.size() - 1);
        if ( !progressBar.isVisible() ) progressBar.setVisible(true);
        if ( last.progress > 0 && progressBar.isIndeterminate() ) progressBar.setIndeterminate(false);
        messageBar.setText(last.message);
        progressBar.setValue(last.progress);
    }

    @Override
    protected void done() {
        try {
            get();
        } catch (InterruptedException | ExecutionException ex) {
            // Hint: If this dies, a Log is ok, as it only impacts the progress bar.
            LoggerFactory.getLogger(this.getClass()).warn("Exception during progress {}", ex.getMessage());
        } finally {
            messageBar.setText("");
            progressBar.setValue(0);
            progressBar.setIndeterminate(false);
            progressBar.setVisible(false);
        }
    }
}
