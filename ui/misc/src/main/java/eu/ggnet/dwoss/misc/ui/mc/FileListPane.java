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
package eu.ggnet.dwoss.misc.ui.mc;

import java.io.File;

import jakarta.inject.Inject;

import jakarta.annotation.PostConstruct;

import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;

import eu.ggnet.dwoss.core.widget.FileUtil;
import eu.ggnet.saft.core.Ui;

import jakarta.enterprise.context.Dependent;

/**
 *
 * @author oliver.guenther
 */
@Dependent
public class FileListPane extends BorderPane {

    @Inject
    private DirectoryMonitor dm;

    private final ListView<File> fileListView;

    public FileListPane() {
        fileListView = new ListView<>();
        fileListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        fileListView.setCellFactory((ListView<File> view) -> new ListCell<File>() {
            @Override
            protected void updateItem(File item, boolean empty) {
                super.updateItem(item, empty);
                if ( empty ) setText("");
                else setText(item.getName());
            }

        });

        fileListView.setOnMouseClicked((e) -> {
            if ( !fileListView.getSelectionModel().isEmpty() && e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2 ) {
                Ui.exec(() -> FileUtil.osOpen(fileListView.getSelectionModel().getSelectedItem()));
            }
        });

        setCenter(fileListView);

    }

    @PostConstruct
    public void postInit() {
        fileListView.setItems(dm.fileFxList);
        dm.start();
    }

}
