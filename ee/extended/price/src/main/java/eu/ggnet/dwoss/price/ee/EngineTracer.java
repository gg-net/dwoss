/*
 * Copyright (C) 2014 GG-Net GmbH - Oliver Günther
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
package eu.ggnet.dwoss.price.ee;

import java.io.Serializable;
import java.util.*;

import static eu.ggnet.dwoss.price.ee.EngineTracer.Status.ERROR;

/**
 * This is simple support class to allow a form of partial logging.
 *
 * @author oliver.guenther
 */
public class EngineTracer implements Serializable {

    public static enum Status {

        INFO, WARNING, ERROR
    }

    private Status status = Status.INFO;

    private String clazz;

    private String method;

    private String result;

    private final List<String> messages = new ArrayList<>();

    public EngineTracer(String clazz, String method) {
        if ( clazz == null ) throw new NullPointerException("Param clazz may not be null");
        this.clazz = clazz;
        this.method = method;
    }

    public EngineTracer setResult(Object result) {
        if ( result == null ) this.result = "null";
        else this.result = result.toString();
        return this;
    }

    public EngineTracer info(String msg) {
        messages.add(msg);
        return this;
    }

    public EngineTracer warn(String msg) {
        messages.add(msg);
        if ( status != ERROR ) status = Status.WARNING;
        return this;
    }

    public EngineTracer error(String msg) {
        messages.add(msg);
        status = Status.ERROR;
        return this;
    }

    public void change(String clazz, String method) {
        this.clazz = clazz;
        this.method = method;
    }

    public void merge(EngineTracer et) {
        if ( status.ordinal() < et.status.ordinal() ) status = et.status;
        messages.addAll(et.messages);
    }

    public Status getStatus() {
        return status;
    }

    /**
     * Return all messages as on result.
     * <p>
     * @return all messages as on result.
     */
    public String collectMessages() {
        StringBuilder sb = new StringBuilder(clazz);
        if ( method != null ) sb.append(".").append(method);
        sb.append("(").append(messages.toString()).append(")");
        if ( result != null ) sb.append("=").append(result);
        return sb.toString();
    }

}
