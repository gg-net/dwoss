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

import lombok.*;

/**
 * Simple form of a Response handling.
 * This can be enhanced much more, but lets see how this goes. Keep it small for the start.
 *
 * @author oliver.guenther
 */
@AllArgsConstructor
@ToString
public final class Reply<T> implements Serializable {

    private final boolean success;

    /**
     * The payload, the actual useful result, probally null in the failure case.
     */
    @Getter
    private final T payload;

    /**
     * A short summary of the reply, probally null in the success case.
     */
    @Getter
    private final String summary;

    /**
     * A longer information, probally null in the success case.
     */
    @Getter
    private final String detailDescription;

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
        return new Reply<>(true, payload, "success", null);
    }

    /**
     * Creates a successful reply with payload and messages.
     *
     * @param <T>               type of payload
     * @param payload           the payload
     * @param summary           the summary
     * @param detailDescription the detailDescription
     * @return the reply instance
     */
    public static <T> Reply<T> success(T payload, String summary, String detailDescription) {
        return new Reply<>(true, payload, summary, detailDescription);
    }

    /**
     * Creates a failed replay with an error summary.
     *
     * @param <T>     type of payload not needed
     * @param summary the errormessage
     * @return the reply instance
     */
    public static <T> Reply<T> failure(String summary) {
        return new Reply<>(false, null, summary, null);
    }

    /**
     * Creates a failed replay with an error summary.
     *
     * @param <T>     type of payload not needed
     * @param summary the errormessage
     * @return the reply instance
     */
    public static <T> Reply<T> failure(String summary, String detailDesciption) {
        return new Reply<>(false, null, summary, detailDesciption);
    }
}
