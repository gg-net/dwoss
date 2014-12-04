package eu.ggnet.saft.core.swing;

import eu.ggnet.saft.core.SwingCore;
import eu.ggnet.saft.core.fx.FxSaft;
import java.awt.Window;
import java.util.concurrent.Callable;
import javafx.embed.swing.JFXPanel;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;

/**
 *
 * @author oliver.guenther
 * @param <T>
 * @param <R>
 */
public class SwingOpenPane<T, R extends Pane> extends AbstractSwingOpen<T, R> {

    public SwingOpenPane(Callable<T> before, Window parent, Modality modality, String id, Class<R> paneClass) {
        super(before, parent, modality, id, paneClass);
    }

    @Override
    protected T2<R> build(T parameter, Class<R> paneClass) throws Exception {
        SwingCore.ensurePlatformIsRunning();
        final R pane = FxSaft.construct(paneClass, parameter);
        JFXPanel p = SwingCore.wrap(pane);
        return new T2<>(p, pane);
    }

}
