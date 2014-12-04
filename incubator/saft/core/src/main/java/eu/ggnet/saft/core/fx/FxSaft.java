package eu.ggnet.saft.core.fx;

import eu.ggnet.saft.api.ui.FxController;
import eu.ggnet.saft.api.ui.Initialiser;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.stage.Window;

import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.function.Consumer;

/**
 *
 * @author oliver.guenther
 */
public class FxSaft {

    public static <R extends FxController> URL loadView(Class<R> controllerClazz) {
        if (!controllerClazz.getSimpleName().endsWith("Controller"))
            throw new IllegalArgumentException(controllerClazz + " does not end with Controller");
        String head = controllerClazz.getSimpleName().substring(0, controllerClazz.getSimpleName().length() - "Controller".length());
        return controllerClazz.getResource(head + "View.fxml");
    }

    public static <T, R extends FxController> FXMLLoader constructFxml(Class<R> controllerClazz, T parameter) throws Exception {
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(loadView(controllerClazz), "No View for " + controllerClazz));
        loader.load();
        R controller = Objects.requireNonNull(loader.getController(), "No controller based on " + controllerClazz + ". Controller set in Fxml ?");
        if (parameter != null && controller instanceof Consumer) {
            try {
                ((Consumer<T>) controller).accept(parameter);
            } catch (ClassCastException e) {
                LoggerFactory.getLogger(FxSaft.class).warn(controller.getClass() + " implements Consumer, but not of type " + parameter.getClass());
            }
        }
        return loader;
    }

    public static <T, R extends Pane> R construct(Class<R> panelClazz, T parameter) throws Exception {
        R panel = panelClazz.getConstructor().newInstance();
        if (panel instanceof Initialiser) {
            ((Initialiser) panel).initialise();
        }
        if (parameter != null && panel instanceof Consumer) {
            try {
                ((Consumer<T>) panel).accept(parameter);
            } catch (ClassCastException e) {
                LoggerFactory.getLogger(FxSaft.class).warn(panel.getClass() + " implements Consumer, but not of type " + parameter.getClass());
            }
        }
        return panel;

    }

    /**
     * Dispatches the Callable to the Platform Ui Thread.
     *
     * @param <T> Return type of callable
     * @param callable the callable to dispatch
     * @return the result of the callable
     * @throws InterruptedException see {@link CountDownLatch#await() }
     * @throws ExecutionException see {@link FutureTask#get() }
     */
    public static <T> T dispatch(Callable<T> callable) throws InterruptedException, ExecutionException {
        FutureTask<T> futureTask = new FutureTask<>(callable);
        final CountDownLatch cdl = new CountDownLatch(1);
        if (Platform.isFxApplicationThread()) {
            futureTask.run();
            cdl.countDown();
        } else {
            Platform.runLater(() -> {
                futureTask.run();
                cdl.countDown();
            });
        }
        cdl.await();
        return futureTask.get();
    }

    public static Window windowAncestor(Node c) {
        if (c == null) return null;
        return c.getScene().getWindow();
    }

}
