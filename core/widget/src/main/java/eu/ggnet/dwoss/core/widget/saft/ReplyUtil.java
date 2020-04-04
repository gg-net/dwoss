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
package eu.ggnet.dwoss.core.widget.saft;

import eu.ggnet.dwoss.core.common.UserInfoException;

/**
 * Utility Class to wrap replies.
 *
 * @author oliver.guenther
 */
public class ReplyUtil {

    public static interface UserInfoExceptionCallable<R> {

        R call() throws UserInfoException;
    }

    /**
     * Wraps a result with possible UserInfoException in a reply.
     *
     * @param <R>      the type of payload
     * @param callable the call which might throw an exception.
     * @return the reply.
     */
    public static <R> Reply<R> wrap(UserInfoExceptionCallable<R> callable) {
        try {
            return Reply.success(callable.call());
        } catch (UserInfoException ex) {
            return Reply.failure(ex.getMessage());
        }
    }

}
