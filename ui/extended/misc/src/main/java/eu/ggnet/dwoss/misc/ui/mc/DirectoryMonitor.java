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
package eu.ggnet.dwoss.misc.ui.mc;

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

import eu.ggnet.saft.core.Ui;

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
            Ui.handle(ex);
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
