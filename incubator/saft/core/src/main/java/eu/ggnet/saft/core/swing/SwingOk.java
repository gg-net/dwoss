package eu.ggnet.saft.core.swing;

import eu.ggnet.saft.UiUtil;
import eu.ggnet.saft.Ui;
import eu.ggnet.saft.core.all.*;
import eu.ggnet.saft.api.CallableA1;

import java.awt.Window;
import java.util.concurrent.Callable;

import javafx.stage.Modality;

/**
 *
 * @author oliver.guenther
 * @param <V>
 */
public class SwingOk<V> implements UiOk<V> {

    private final OnceCaller<OkCancelResult<V>> before;

    private final Window parent;

    private final Modality modality;

    public SwingOk(Callable<OkCancelResult<V>> before, Window parent, Modality modality) {
        this.before = new OnceCaller<>(before);
        this.parent = parent;
        this.modality = modality;
    }

    @Override
    public <R> SwingCreator<R> onOk(CallableA1<V, R> function) {
        return new SwingCreator<>(UiUtil.onOk(function, before), parent, modality);
    }

    @Override
    public OkCancelResult<V> call() throws Exception {
        return before.get();
    }

    @Override
    public void exec() {
        Ui.exec(this);
    }

}
