/*
 * Copyright (C) 2019 GG-Net GmbH
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
package eu.ggnet.saft.core.ui;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletionException;

/**
 * Helper class to wrap exeption into in a {@link CompletionException}.
 *
 * @author oliver.guenther
 */
public class ExceptionWrapper {

    /**
     * A Runable implementation, that allows throwing an Exception.
     *
     * @author oliver.guenther
     */
    public static interface RunableWithException {

        /**
         * run method.
         *
         * @throws Exception
         */
        void run() throws Exception;

    }

    private static ExceptionWrapper instance;

    /**
     * Returns a singleton instance of this class.
     * The util character of the class, makes the singleton pattern superfluous. But to use the class in the Ui via a method, we need an instance.
     *
     * @return the single instance of this class.
     */
    public static ExceptionWrapper getInstance() {
        if ( instance == null ) instance = new ExceptionWrapper();
        return instance;
    }

    /**
     * Wrappes a possible throw exception of the callable in a {@link CompletionException}.
     *
     * @param <T>      the type
     * @param callable the callable
     * @return the result of the callable
     * @throws CompletionException the exception thrown by the callable wrapped.
     */
    public <T> T wrap(Callable<T> callable) throws CompletionException {
        try {
            return callable.call();
        } catch (Exception ex) {
            throw new CompletionException(ex);
        }
    }

    public void wrap(RunableWithException runable) {
        wrap(() -> {
            runable.run();
            return null;
        });
    }
}
