package eu.ggnet.saft.core.all;

import java.util.concurrent.Callable;

/**
 *
 * @author oliver.guenther
 * @param <T>
 */
public abstract class AbstractCreator<T> implements UiCreator<T> {

    protected OnceCaller<T> before;

    public AbstractCreator(Callable<T> callable) {
        before = new OnceCaller<>(callable);
    }

    @Override
    public Callable<Void> osOpen() {
        return UiUtil.osOpen(before);
    }

    /**
     * It this is the terminal instance, execute call or submit to an Executor, for exmple Ui.exec().
     *
     * @return result.
     * @throws Exception might throw exception.
     */
    @Override
    public T call() throws Exception {
        return before.get();
    }

}
