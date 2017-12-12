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
package eu.ggnet.saft.api;

import java.io.Serializable;

import lombok.Getter;

/**
 * Simple form of a Response handling.
 * This can be enhanced much more, but lets see how this goes. Keep it small for the start.
 *
 * @author oliver.guenther
 */
public final class Reply<T> implements Serializable {

    private Reply(boolean success, T payload, String errorMessage) {
        this.success = success;
        this.payload = payload;
        this.errorMessage = errorMessage;
    }

    private final boolean success;

    @Getter
    private final T payload;

    @Getter
    private final String errorMessage;

    public boolean hasSucceded() {
        return success;
    }

    /**
     * Creates a successful reply with payload.
     *
     * @param <T>     type of payload
     * @param payload the payload
     * @return the reply instance
     */
    public static <T> Reply<T> success(T payload) {
        return new Reply<>(true, payload, null);
    }

    /**
     * Creates a failed replay with an error message.
     *
     * @param <T>     type of payload not needed
     * @param message the errormessage
     * @return the reply instance
     */
    public static <T> Reply<T> failure(String message) {
        return new Reply<>(false, null, message);
    }

}