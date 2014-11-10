package eu.ggnet.dwoss.misc.files;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

import javax.swing.AbstractListModel;

import eu.ggnet.saft.core.Client;
import eu.ggnet.saft.core.Workspace;

import eu.ggnet.dwoss.common.ExceptionUtil;

import static java.nio.file.StandardWatchEventKinds.*;

/**
 * Listens to changes in the FileSystem and adds it to the files, considered to be used with {@link ScheduledExecutorService#scheduleWithFixedDelay(java.lang.Runnable, long, long, java.util.concurrent.TimeUnit)
 * }.
 * <p/>
 * @author oliver.guenther
 */
public class DirectoryMonitor extends AbstractListModel<File> implements Runnable {

    private File directory;

    private List<File> fileList;

    private WatchService watchService;

    public DirectoryMonitor(File directory) {
        try {
            if ( directory == null || !directory.isDirectory() ) throw new IllegalArgumentException("Dir:" + directory + " is not a Directory");
            this.directory = directory;
            this.fileList = new ArrayList<>();
            watchService = FileSystems.getDefault().newWatchService();
            directory.toPath().register(watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
            reloadView();
        } catch (IOException | RuntimeException ex) {
            ExceptionUtil.show(Client.lookup(Workspace.class).getMainFrame(), ex);
            throw new RuntimeException(ex);
        }
    }

    @Override
    public int getSize() {
        return fileList.size();
    }

    @Override
    public File getElementAt(int index) {
        if ( index >= getSize() || index < 0 ) {
            return null;
        }
        return fileList.get(index);
    }

    private void reloadView() {
        fileList.clear();
        for (String fileName : directory.list()) {
            fileList.add(new File(directory, fileName));
        }
        Collections.sort(fileList);
        fireContentsChanged(this, 0, fileList.size() - 1);
    }

    @Override
    public void run() {
        try {
            WatchKey key = watchService.take();
            key.pollEvents(); // Need to poll events before reset.
            key.reset();
            reloadView();
        } catch (InterruptedException ex) {
        }
    }
}
