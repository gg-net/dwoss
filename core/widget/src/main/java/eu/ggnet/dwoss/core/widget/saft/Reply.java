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

import java.io.Serializable;

/**
 * Simple form of a Response handling. This can be enhanced much more, but lets
 * see how this goes. Keep it small for the start.
 *
 * @author oliver.guenther
 */
public final class Reply<T> implements Serializable {

    private final boolean success;

    /**
     * The payload, the actual useful result, probally null in the failure case.
     */
    private final T payload;

    /**
     * A short summary of the reply, probally null in the success case.
     */
    private final String summary;

    /**
     * A longer information, probally null in the success case.
     */
    private final String detailDescription;

    public Reply(boolean success, T payload, String summary, String detailDescription) {
        this.success = success;
        this.payload = payload;
        this.summary = summary;
        this.detailDescription = detailDescription;
    }

    public boolean hasSucceded() {
        return success;
    }

    public T getPayload() {
        return payload;
    }

    public String getSummary() {
        return summary;
    }

    public String getDetailDescription() {
        return detailDescription;
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
     * @param <T>                 type of payload not needed
     * @param summary             the errormessage
     * @param detailedDescription the detailed description
     * @return the reply instance
     */
    public static <T> Reply<T> failure(String summary, String detailedDescription) {
        return new Reply<>(false, null, summary, detailedDescription);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" + "success=" + success + ", payload=" + payload + ", summary=" + summary + ", detailDescription=" + detailDescription + '}';
    }

}
