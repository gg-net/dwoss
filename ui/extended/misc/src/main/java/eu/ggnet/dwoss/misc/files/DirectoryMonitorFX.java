/*
 * Copyright (C) 2017 GG-Net GmbH
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
package eu.ggnet.dwoss.misc.files;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import eu.ggnet.saft.Ui;

import lombok.Getter;

import static java.nio.file.StandardWatchEventKinds.*;

/**
 * Listens to changes in the FileSystem and adds it to the files, considered to be used with {@link ScheduledExecutorService#scheduleWithFixedDelay(java.lang.Runnable, long, long, java.util.concurrent.TimeUnit)
 * }.
 * <p/>
 * @author jens.papenhagen
 */
public class DirectoryMonitorFX implements Runnable {

    @Getter
    private File directory;

    @Getter
    private ObservableList<TreeItem<File>> files;

    private WatchService watchService;

    @Getter
    private TreeView<Object> treeView = new TreeView<>();

    public DirectoryMonitorFX(File directory) {
        files = FXCollections.observableArrayList();
        this.directory = directory;
        createNodeTree(this.directory.getAbsolutePath());

        try {
            watchService = FileSystems.getDefault().newWatchService();
            directory.toPath().register(watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
            updateList();
        } catch (IOException ex) {
            Ui.handle(ex);
        }
    }

    private void updateList() {
        Platform.runLater(() -> {
            //reset the treeview after every change
            createNodeTree(this.directory.getAbsolutePath());

        });
    }

    /**
     * Fill the Treeview with TreeItem<Object>
     *
     * @param path
     */
    private void createNodeTree(String path) {
        TreeItem<Object> tree = new TreeItem<>(path.substring(path.lastIndexOf(File.separator)));
        //build a list for folder and for files
        List<TreeItem<Object>> folders = new ArrayList<>();
        List<TreeItem<Object>> files = new ArrayList<>();

        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(path))) {
            for (Path p : directoryStream) {
                if ( Files.isDirectory(p) ) {
                    //getting only the Foldername
                    TreeItem<Object> subDirectory = new TreeItem<>(p.getName(p.getNameCount() - 1));
                    getSubLeafs(p, subDirectory);
                    folders.add(subDirectory);
                } else {
                    files.add(getLeafs(p));
                }
            }

            tree.getChildren().addAll(folders);
            tree.getChildren().addAll(files);
        } catch (IOException ex) {
            Ui.handle(ex);
        }
        tree.setExpanded(true);

        //expane the root and hide it than
        treeView.setRoot(tree);
        treeView.setShowRoot(false);
    }

    /**
     * make a single leaf for Files
     *
     * @param subPath
     * @return
     */
    private TreeItem<Object> getLeafs(Path subPath) {
        String strPath = subPath.toString();
        TreeItem<Object> leafs = new TreeItem<>(strPath.substring(1 + strPath.lastIndexOf(File.separator)));
        return leafs;
    }

    /**
     * make a own SubLeaft for Folder in the main Folder
     *
     * @param subPath
     * @param parent
     */
    private void getSubLeafs(Path subPath, TreeItem<Object> parent) {
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(subPath.toString()))) {
            for (Path subDir : directoryStream) {
                // explicit search for files because we dont want to get sub-sub-directories
                if ( !Files.isDirectory(subDir) ) {
                    String subTree = subDir.getFileName().toString();
                    TreeItem<Object> subLeafs = new TreeItem<>(subTree.substring(1 + subTree.lastIndexOf(File.separator)));
                    parent.getChildren().add(subLeafs);
                }
            }
        } catch (IOException ex) {
            Ui.handle(ex);
        }
    }

    /**
     * build up the watchService
     */
    @Override
    public void run() {
        try {
            WatchKey take = watchService.take();
            take.pollEvents();
            take.reset();
            updateList();
        } catch (InterruptedException ex) {
        }

    }

}
