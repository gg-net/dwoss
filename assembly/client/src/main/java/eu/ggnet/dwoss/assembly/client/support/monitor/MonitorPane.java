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

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import eu.ggnet.dwoss.assembly.client.support.executor.Executor;

import static java.lang.Double.MAX_VALUE;

/**
 * Pane containing a ListView to display instances of {@link Task}.
 *
 * @author oliver.guenther
 */
public class MonitorPane extends BorderPane {

    private final ListView<Task<?>> taskListView;

    private final ObservableList<Task<?>> taskList;

    @Inject
    @Executor
    private ScheduledExecutorService ses;

    /**
     * Workarround for the case, that the saftbackgrountask is created but the end has allready happend.
     */
    private final AtomicBoolean runningSaftBackgroundTask = new AtomicBoolean(false);

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
                    status.textProperty().bind(item.messageProperty());
                    VBox b = new VBox(bar, status);
                    b.setFillWidth(true);
                    setGraphic(b);
                }

            }

        });

        setCenter(taskListView);
    }

    /**
     * Helpermethod for the stupid saft background construct.
     *
     * @param running state of the required background activity.
     */
    void saftBackground(boolean running) {
        if ( !runningSaftBackgroundTask.compareAndSet(!running, running) ) return; // Allready in the right state
        if ( running ) submit(new SaftNaiveProgressTask(this));
    }

    /**
     * Allows submisson of task to be displayed and run on the global executor.
     *
     * @param t the task to display
     */
    void submit(Task<?> t) {
        Platform.runLater(() -> {
            // Eviction handler.
            EventHandler<WorkerStateEvent> e = (WorkerStateEvent event) -> Platform.runLater(() -> taskList.remove(t));

            t.setOnSucceeded(e);
            t.setOnFailed(e);
            t.setOnCancelled(e);

            taskList.add(t);
        });
        ses.execute(t);
    }

    /**
     * Returns a status of the saft background activity.
     *
     * @return a status of the saft background activity.
     */
    AtomicBoolean runningSaftBackgroundTask() {
        return runningSaftBackgroundTask;
    }

}
