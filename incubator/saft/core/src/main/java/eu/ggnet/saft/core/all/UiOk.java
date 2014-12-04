package eu.ggnet.saft.core.all;

import eu.ggnet.saft.api.CallableA1;

import java.util.concurrent.Callable;

/**
 *
 * @author oliver.guenther
 * @param <V>
 */
public interface UiOk<V> extends Callable<OkCancelResult<V>> {

    <R> UiCreator<R> onOk(CallableA1<V, R> function);

    void exec();
}
