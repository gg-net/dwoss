package eu.ggnet.saft.api;

/**
 * Like {@link java.util.concurrent.Callable } , but with a parameter.
 *
 * @author oliver.guenther
 * @param <T> type of the argument
 * @param <V> type of the result.
 */
public interface CallableA1<T, V> {

    public V call(T t) throws Exception;

}
