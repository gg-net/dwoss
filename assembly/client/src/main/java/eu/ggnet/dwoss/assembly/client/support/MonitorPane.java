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

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import eu.ggnet.saft.core.UiCore;

import static java.lang.Double.MAX_VALUE;

/**
 *
 * @author oliver.guenther
 */
public class MonitorPane extends BorderPane {

    private final ListView<Task<Void>> taskListView;

    private final ObservableList<Task<Void>> taskList;

    public MonitorPane() {
        taskList = FXCollections.observableArrayList();
        taskListView = new ListView<>(taskList);

        taskListView.setCellFactory((ListView<Task<Void>> view) -> new ListCell<Task<Void>>() {
            @Override
            protected void updateItem(Task<Void> item, boolean empty) {
                super.updateItem(item, empty);
                System.out.println(item);
                if ( empty ) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(null);
                    ProgressBar bar = new ProgressBar();
                    bar.setMaxWidth(MAX_VALUE);
                    bar.progressProperty().bind(item.progressProperty());
                    Label status = new Label();
                    status.textProperty().bind(item.messageProperty());
                    VBox b = new VBox(bar, status);
                    b.setFillWidth(true);
                    setGraphic(b);
                }

            }

        });

        setCenter(taskListView);
    }

    public void submit(Task<Void> t) {
        Platform.runLater(() -> {
            // Eviction handler.
            EventHandler<WorkerStateEvent> e = (WorkerStateEvent event) -> Platform.runLater(() -> taskList.remove(t));

            t.setOnSucceeded(e);
            t.setOnFailed(e);
            t.setOnCancelled(e);
            taskList.add(t);
        });
        UiCore.getExecutor().execute(t);
    }
}
