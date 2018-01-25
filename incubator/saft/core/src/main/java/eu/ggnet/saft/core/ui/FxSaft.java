package eu.ggnet.saft.core.ui;

import java.net.URL;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.stage.Window;

import org.slf4j.LoggerFactory;

import eu.ggnet.saft.api.ui.FxController;

/**
 *
 * @author oliver.guenther
 */
public class FxSaft {

    public static <R extends FxController> URL loadView(Class<R> controllerClazz) {
        String head = null;
        if ( controllerClazz.getSimpleName().endsWith("Controller") ) {
            head = controllerClazz.getSimpleName().substring(0, controllerClazz.getSimpleName().length() - "Controller".length());
        } else if ( controllerClazz.getSimpleName().endsWith("Presenter") ) {
            head = controllerClazz.getSimpleName().substring(0, controllerClazz.getSimpleName().length() - "Presenter".length());
        }
        if ( head == null ) throw new IllegalArgumentException(controllerClazz + " does not end with Controller or Presenter");
        return controllerClazz.getResource(head + "View.fxml");
    }

    public static <T, R extends FxController> FXMLLoader constructFxml(Class<R> controllerClazz, T parameter) throws Exception {
        return FxSaft.dispatch(() -> { // We need to dispatch it, as the webview must be constructed on the fx thread
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(loadView(controllerClazz), "No View for " + controllerClazz));
            loader.load();
            R controller = Objects.requireNonNull(loader.getController(), "No controller based on " + controllerClazz + ". Controller set in Fxml ?");
            if ( parameter != null && controller instanceof Consumer ) {
                try {
                    ((Consumer<T>)controller).accept(parameter);
                } catch (ClassCastException e) {
                    LoggerFactory.getLogger(FxSaft.class).warn(controller.getClass() + " implements Consumer, but not of type " + parameter.getClass());
                }
            }
            return loader;
        });
    }

    public static <T, R extends Pane> R construct(Class<R> paneClass, T parameter) throws Exception {
        return FxSaft.dispatch(() -> { // We need to dispatch it, as the webview must be constructed on the fx thread
            R pane = paneClass.getConstructor().newInstance();
            if ( parameter != null && pane instanceof Consumer ) {
                try {
                    ((Consumer<T>)pane).accept(parameter);
                } catch (ClassCastException e) {
                    LoggerFactory.getLogger(FxSaft.class).warn(pane.getClass() + " implements Consumer, but not of type " + parameter.getClass());
                }
            }
            return pane;
        });
    }

    /**
     * Dispatches the Callable to the Platform Ui Thread.
     *
     * @param <T>      Return type of callable
     * @param callable the callable to dispatch
     * @return the result of the callable
     * @throws RuntimeException wraps InterruptedException of {@link CountDownLatch#await() } and ExecutionException of {@link FutureTask#get() }
     */
    public static <T> T dispatch(Callable<T> callable) throws RuntimeException {
        try {
            FutureTask<T> futureTask = new FutureTask<>(callable);
            final CountDownLatch cdl = new CountDownLatch(1);
            if ( Platform.isFxApplicationThread() ) {
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
        } catch (InterruptedException | ExecutionException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static Window windowAncestor(Node c) {
        if ( c == null ) return null;
        return c.getScene().getWindow();
    }

}
