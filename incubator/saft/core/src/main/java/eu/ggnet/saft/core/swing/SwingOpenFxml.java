package eu.ggnet.saft.core.swing;

import eu.ggnet.saft.core.SwingCore;
import eu.ggnet.saft.api.ui.FxController;
import eu.ggnet.saft.core.fx.FxSaft;

import java.awt.Window;
import java.util.concurrent.Callable;

import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.stage.Modality;

/**
 *
 * @author oliver.guenther
 * @param <T>
 * @param <R>
 */
public class SwingOpenFxml<T, R extends FxController> extends AbstractSwingOpen<T, R> {

    public SwingOpenFxml(Callable<T> before, Window parent, Modality modality, String id, Class<R> controllerClass) {
        super(before, parent, modality, id, controllerClass);
    }

    @Override
    protected T2<R> build(T parameter, Class<R> controllerClass) throws Exception {
        SwingCore.ensurePlatformIsRunning();
        FXMLLoader loader = FxSaft.constructFxml(controllerClass, parameter);
        JFXPanel p = SwingCore.wrap(loader.getRoot());
        return new T2<>(p, loader.getController());
    }

}
