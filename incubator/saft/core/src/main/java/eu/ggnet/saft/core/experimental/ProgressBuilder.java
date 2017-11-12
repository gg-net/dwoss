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
package eu.ggnet.saft.core.experimental;

import java.util.concurrent.Callable;
import java.util.function.Function;

import javafx.concurrent.Worker;

import eu.ggnet.saft.UiCore;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Builder for progress observer.
 * Connects long running code to different ways for displaying progress.
 * Also, very simple wrapper methods are here.
 *
 * @author oliver.guenther
 */
@Accessors(fluent = true)
public class ProgressBuilder {

    // TODO: Unused for now.
    @Setter
    @Getter
    private String title;

    /**
     * Wrapes a function with progress connectivity. Enables the progress information in the main ui while the supplied function is run.
     * Starts a progress display then the returned function is called and stops it, then its complete.
     * Uses the class name of the supplied function as monitor title.
     *
     * @param <U>
     * @param <T>
     * @param function the function to be wrapped into progress information.
     * @return a enhanced function
     */
    public <U, T> Function<T, U> wrap(Function<T, U> function) {
        // TODO: Progresshandling sucks, but its only internal, so we can live with it for now.
        return (T t) -> {
            UiCore.backgroundActivityProperty().set(true);
            try {
                return function.apply(t);
            } finally {
                UiCore.backgroundActivityProperty().set(false);
            }
        };
    }

    /**
     * Wrapes a runnalbe with progress connectivity. Enables the progress information in the main ui while the supplied function is run.
     * Starts a progress display then the returned function is called and stops it, then its complete.
     * Uses the class name of the supplied function as monitor title.
     *
     * @param runnable the runnable
     * @return a enhanced runnable
     */
    public Runnable wrap(Runnable runnable) {
        return () -> {
            UiCore.backgroundActivityProperty().set(true);
            try {
                runnable.run();
            } finally {
                UiCore.backgroundActivityProperty().set(false);
            }
        };
    }

    /**
     * Wrapes a callable with progress connectivity. Enables the progress information in the main ui while the supplied function is run.
     * Starts a progress display then the returned function is called and stops it, then its complete.
     * Uses the class name of the supplied function as monitor title.
     *
     * @param <V>      type parameter of the callable
     * @param callable the callable
     * @return a enhanced callable
     */
    public <V> Callable<V> wrap(Callable<V> callable) {
        return () -> {
            UiCore.backgroundActivityProperty().set(true);
            try {
                return callable.call();
            } finally {
                UiCore.backgroundActivityProperty().set(false);
            }
        };
    }

    /**
     * Calls the callable with progress.
     *
     * @param <V>      type of the callable parameter
     * @param callable the callable
     * @return the result of the callable.
     * @throws RuntimeException wrapped exception of the .call() method.
     */
    public <V> V call(Callable<V> callable) throws RuntimeException {
        try {
            return wrap(callable).call();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Observers the progress on any javaFx worker.
     * If there is some form of central managed and displayed progress and status message system registered with saft, this can be used to show a worker
     * progress.
     *
     * @param <T>
     * @param worker the worker to be observed.
     * @return the parameter worker, for fluent usage.
     */
    public <T extends Worker> T observe(T worker) {
        if ( worker != null ) worker.runningProperty().addListener((ob, o, n) -> UiCore.backgroundActivityProperty().setValue(n));
        return worker;
    }

}
