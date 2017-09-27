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

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.*;

import eu.ggnet.saft.core.MainComponent;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;

import org.openide.util.lookup.ServiceProvider;

import eu.ggnet.dwoss.configuration.GlobalConfig;
import eu.ggnet.saft.core.UiCore;

/**
 *
 * @author jens.papenhagen
 */
@ServiceProvider(service = MainComponent.class)
public class FileListViewCaskFX extends BorderPane implements MainComponent {

    private TreeView treeview;

    public FileListViewCaskFX() {
        final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor((r) -> {
            Thread t = new Thread(r);
            t.setDaemon(true);
            return t;
        });

        //build a WatchService for the Path and giveback the updated path
        DirectoryMonitorFX dm = new DirectoryMonitorFX(new File(GlobalConfig.APPLICATION_PATH_OUTPUT));
        //adding a small delay for the watchService to find all the folder and index
        scheduledExecutorService.scheduleWithFixedDelay(dm, 2, 1, TimeUnit.SECONDS);

        this.treeview = dm.getTreeView();
        //override the double-click event Handling for Treeview to only open File 
        treeview.addEventHandler(MouseEvent.ANY, event -> {
            if ( event.getEventType().equals(MouseEvent.MOUSE_CLICKED) && event.getClickCount() == 2 && event.getButton().equals(MouseButton.PRIMARY) ) {
                TreeItem<Object> selectedItem = (TreeItem<Object>)treeview.getSelectionModel().getSelectedItem();
                if ( selectedItem != null ) {
                    try {
                        //build the right path for the file to get open. Because the selectedItem.getValue() give back a String
                        File file = new File(GlobalConfig.APPLICATION_PATH_OUTPUT + File.separator + (String)selectedItem.getValue());
                        Desktop.getDesktop().open(file);
                    } catch (IOException ex) {
                        UiCore.handle(ex);
                    }
                }
                event.consume();
            }
        });

        //add postion in the BorderPane
        setCenter(treeview);

    }

    //for the MainComponent
    @Override
    public String getLayoutHint() {
        return "Center";
    }

}
