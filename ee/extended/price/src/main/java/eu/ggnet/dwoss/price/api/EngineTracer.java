package eu.ggnet.dwoss.price.api;

import java.io.Serializable;
import java.util.*;

import static eu.ggnet.dwoss.price.api.EngineTracer.Status.ERROR;

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

    public String collectMessages() {
        StringBuilder sb = new StringBuilder(clazz);
        if ( method != null ) sb.append(".").append(method);
        sb.append("(").append(messages.toString()).append(")");
        if ( result != null ) sb.append("=").append(result);
        return sb.toString();
    }

}
