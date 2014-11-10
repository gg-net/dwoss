package eu.ggnet.saft.runtime;

import java.util.List;
import java.util.SortedSet;
import java.util.concurrent.ExecutionException;

import javax.swing.*;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import eu.ggnet.saft.api.progress.HiddenMonitor;
import eu.ggnet.saft.api.progress.ProgressObserver;
import eu.ggnet.saft.runtime.HiddenMonitorDisplayTask.Progress;
import lombok.Data;

import static eu.ggnet.saft.core.Client.lookup;

public class HiddenMonitorDisplayTask extends SwingWorker<Void, HiddenMonitorDisplayTask.Progress> {

    @Data
    public static class Progress {

        private final int progress;

        private final String message;
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
        HiddenMonitor hm = lookup(ProgressObserver.class).getMonitor(key);
        while (hm != null && !hm.isFinished()) {
            int progress = 100 - hm.getAbsolutRemainingTicks();
            if ( progress < 0 ) progress = 0;
            if ( progress > 100 ) progress = 100;
            publish(new Progress(progress, hm.getTitle() + ":" + StringUtils.defaultIfBlank(hm.getMessage(), "")));
            Thread.sleep(250);
            hm = lookup(ProgressObserver.class).getMonitor(key);
        }
        publish(new Progress(100, ""));
        keys.remove(key);
        return null;
    }

    @Override
    protected void process(List<Progress> chunks) {
        Progress last = chunks.get(chunks.size() - 1);
        if ( !progressBar.isVisible() ) progressBar.setVisible(true);
        if ( last.getProgress() > 0 && progressBar.isIndeterminate() ) progressBar.setIndeterminate(false);
        messageBar.setText(last.getMessage());
        progressBar.setValue(last.getProgress());
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
