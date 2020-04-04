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

import java.util.concurrent.CompletionException;
import java.util.function.Function;

import eu.ggnet.dwoss.core.common.UserInfoException;

/**
 * Special form of the {@link UserInfoException} for usage in the CompletableFuture.
 *
 * @author oliver.guenther
 */
public class UserInfoCompletionException extends CompletionException {

    public UserInfoCompletionException(UserInfoException uie) {
        super(uie);
    }

    /**
     * Functional interface to wrap a Consumer with an {@link UserInfoException}.
     *
     * @param <T> type of parameter
     */
    @FunctionalInterface
    public static interface UserInfoExceptionConsumer<T> {

        void accept(T t) throws UserInfoException;

    }

    /**
     * Functional interface to wrap a Function with an {@link UserInfoException}.
     *
     * @param <T> type of parameter
     * @param <R> type of result
     */
    @FunctionalInterface
    public static interface UserInfoExceptionFunction<T, R> {

        R apply(T t) throws UserInfoException;

    }

    /**
     * Functional interface to wrap a Runnable with an {@link UserInfoException}.
     */
    @FunctionalInterface
    public static interface UserInfoExceptionRunnable {

        void run() throws UserInfoException;

    }

    /**
     * Wrapper for a Consumer with an {@link UserInfoException}.
     *
     * @param <T> type of parameter
     * @param uic the consumer to wrap
     * @return a consumer possibly thorwing an {@link UserInfoException} wrapped into {@link UserInfoCompletionException}.
     * @throws UserInfoCompletionException
     */
    //TODO: If this method exists, the compiler gets confused in the lambda usage. with the function. But.... do I even need that.
//    public static <T> Consumer<T> wrap(UserInfoExceptionConsumer<T> uic) {
//        return (T t) -> {
//            try {
//                uic.accept(t);
//            } catch (UserInfoException ex) {
//                throw new UserInfoCompletionException(ex);
//            }
//        };
//    }
    /**
     * Wrapper for a Function with an {@link UserInfoException}.
     *
     * @param <T> type of parameter
     * @param <R> type of result
     * @param uif the function to wrap
     * @return a function possibly thorwing an {@link UserInfoException} wrapped into {@link UserInfoCompletionException}.
     */
    public static <T, R> Function<T, R> wrap(UserInfoExceptionFunction<T, R> uif) {
        return (T t) -> {
            try {
                return uif.apply(t);
            } catch (UserInfoException ex) {
                throw new UserInfoCompletionException(ex);
            }
        };

    }

    /**
     * Wrapper for a Runnable with an {@link UserInfoException}.
     *
     * @param uir the runable to wrap
     * @return a runnable possibly thorwing an {@link UserInfoException} wrapped into {@link UserInfoCompletionException}.
     */
    public static Runnable wrap(UserInfoExceptionRunnable uir) {
        return () -> {
            try {
                uir.run();
            } catch (UserInfoException ex) {
                throw new UserInfoCompletionException(ex);
            }
        };
    }

}
