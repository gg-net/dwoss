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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;

import jakarta.inject.Inject;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import eu.ggnet.dwoss.assembly.client.support.executor.Executor;
import eu.ggnet.saft.core.Saft;

import jakarta.enterprise.context.Dependent;

import static java.lang.Double.MAX_VALUE;

/**
 * Pane containing a ListView to display instances of {@link Task}.
 *
 * @author oliver.guenther
 */
@Dependent
public class MonitorPane extends BorderPane {

    private final ListView<Task<?>> taskListView;

    private final ObservableList<Task<?>> taskList;

    @Inject
    @Executor
    private ScheduledExecutorService ses;

    @Inject
    private Saft saft;

    public MonitorPane() {
        taskList = FXCollections.observableArrayList();
        taskListView = new ListView<>(taskList);

        taskListView.setCellFactory((ListView<Task<?>> view) -> new ListCell<Task<?>>() {
            @Override
            protected void updateItem(Task<?> item, boolean empty) {
                super.updateItem(item, empty);
                if ( empty ) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(null);
                    ProgressBar bar = new ProgressBar();
                    bar.setMaxWidth(MAX_VALUE);
                    bar.progressProperty().bind(item.progressProperty());
                    Label status = new Label();
                    status.textProperty().bind(Bindings.concat(item.titleProperty(), ":", item.messageProperty()));
                    VBox b = new VBox(bar, status);
                    b.setFillWidth(true);
                    setGraphic(b);
                }

            }

        });

        setCenter(taskListView);
    }

    /**
     * Allows submisson of task to be displayed and run on the global executor.
     *
     * @param task the task to display
     */
    void submit(Task<?> task) {
        // Remove Task from Tasklistview, if task is complete
        EventHandler<WorkerStateEvent> e = (WorkerStateEvent event) -> Platform.runLater(() -> taskList.remove(task));
        task.setOnSucceeded(e);
        task.setOnFailed(e);
        task.setOnCancelled(e);

        // Add Task to the UI
        Platform.runLater(() -> taskList.add(task));
        // Start task
        ses.execute(task);
        ses.submit(() -> {  // Exceptionrouter for task.
            try {
                task.get();
            } catch (InterruptedException | ExecutionException ex) {
                saft.handle(ex);
            }
        });
    }

}
