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
package eu.ggnet.dwoss.core.widget;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionException;

import javafx.concurrent.Task;
import javafx.concurrent.Worker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.saft.core.UiUtil;

/**
 * Direct connection to the progress display in the ui.
 * <p>
 * This class contains methods to track and display the progress of background activities.
 *
 * @author mirko.schulze
 */
public class Progressor {

    public static interface Displayer {

        public void submit(Task<?> task);

    }

    private final static Logger L = LoggerFactory.getLogger(Progressor.class);

    private static Progressor instance;

    private final Map<String, Task<?>> tasks = new HashMap<>();

    private Displayer displayer;

    /**
     * Returns the global instance of Progressor.
     *
     * @return the global Progressor instance
     */
    public static Progressor global() {
        if ( instance == null ) instance = new Progressor();
        return instance;
    }

    /**
     * Executes the submitted callable.
     *
     * @param callable task to execute
     */
    public <V> V run(Callable<V> callable) {
        return run("Hintergrundaktivität", callable);
    }

    /**
     * Executes the submitted Runnable.
     *
     * @param runnable task to execute
     */
    public void run(UiUtil.ExceptionRunnable runnable) {
        run("Hintergrundaktivität", runnable);
    }

    /**
     * Executes the submitted Runnable.
     *
     * @param title    title of the task
     * @param runnable task to execute
     */
    public void run(String title, UiUtil.ExceptionRunnable runnable) {
        run(title, () -> {
            runnable.run();
            return null;
        });
    }

    public void setDisplayer(Displayer displayer) {
        this.displayer = displayer;
    }

    /**
     * Executes the sumitted Callable.
     *
     * @param <V>      type of the callable and the return value
     * @param title    title of the task
     * @param callable task to execute
     * @return the result of the submitted callablr
     * @throws NullPointerException if title or callable are null
     * @throws CompletionException  if the callable can not be executed
     */
    public <V> V run(String title, Callable<V> callable) throws NullPointerException, CompletionException {
        Objects.requireNonNull(title, "title must not be null");
        Objects.requireNonNull(callable, "callable/runable must not be null");
        start(title);
        try {
            return callable.call();
        } catch (Exception e) {
            throw new CompletionException(e);
        } finally {
            stop(title);
        }
    }

    /**
     * Tracks the progress of the submitted Worker based on the running property using a title.
     *
     * @param <T>    type of worker
     * @param worker the submitted worker
     * @return the submitted worker
     */
    public <T extends Worker> T observe(T worker) {
        if ( worker != null ) worker.runningProperty().addListener((ob, o, n) -> {
                String title = worker.getTitle();
                if ( title == null || title.isBlank() ) title = worker.getClass().getSimpleName() + worker.hashCode();
                if ( n ) start(title);
                else stop(title);
            });
        return worker;
    }

    /**
     * Submits the delivered task to a thread for execution.
     *
     * @param task the delivered task
     */
    public void submit(Task<?> task) {
        if ( displayer == null ) throw new IllegalStateException("Displayer not yet set");
        displayer.submit(task);
    }

    /**
     * Starts a new task with the submitted title and stores it within a Map of Strings and Tasks with the title as key.
     *
     * @param title title fot the task
     */
    private void start(final String title) {
        L.debug("start({})", title);
        if ( displayer == null ) return;
        Task<Void> b = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                updateTitle(title);
                updateMessage("Allgemeine Hintergrundaktivität");
                while (true) {
                    if ( isCancelled() ) return null;
                    Thread.sleep(500);
                }
            }
        };
        tasks.put(title, b);
        displayer.submit(b);
    }

    /**
     *
     * @param title
     */
    private void stop(String title) {
        L.debug("stop({})", title);
        if ( !tasks.containsKey(title) ) return;
        tasks.get(title).cancel(false);
        tasks.remove(title);
    }
}
