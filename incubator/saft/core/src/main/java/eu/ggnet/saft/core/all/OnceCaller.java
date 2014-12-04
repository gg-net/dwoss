package eu.ggnet.saft.core.all;

import java.util.concurrent.Callable;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 *
 * @author oliver.guenther
 * @param <T>
 */
@EqualsAndHashCode
@ToString
public class OnceCaller<T> {

    private final Callable<T> callable;

    private T onceResult = null;

    private boolean evaluated;

    public OnceCaller(Callable<T> before) {
        this.callable = before;
    }

    /**
     * Returns true if and only if the supplied callable in constructor was not null, but the result of call is null. Implies the evaluation of callable.call().
     *
     * @return true if and only if the supplied callable in constructor was not null, but the result of call is null.
     * @throws Exception
     */
    public boolean ifPresentIsNull() throws Exception {
        evalOnce();
        if (callable == null) return false;
        return onceResult == null;
    }

    public T get() throws Exception {
        evalOnce();
        return onceResult;
    }

    private void evalOnce() throws Exception {
        if (evaluated) return;
        evaluated = true;
        if (callable != null) onceResult = callable.call();
    }

    public Callable<T> getCallable() {
        return callable;
    }

}
