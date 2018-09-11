package eu.ggnet.saft.core.ui;

import java.net.URL;
import java.util.Objects;
import java.util.concurrent.*;

import javafx.application.Platform;

/**
 *
 * @author oliver.guenther
 */
public class FxSaft {

    // Internal Api
    /**
     * Returns a url of the FXML file based on the controllerClazz.
     *
     * @param <R>             the type of the contorller class
     * @param controllerClazz the controller class
     * @return a url of the FXML file, ready to be used in the FXMLLoader.
     * @throws IllegalArgumentException if the controller class does conform to the nameing convetion, it must end with Controller or Presenter
     * @throws NullPointerException     if no resource can be found, hence there is no file in the same package trewe with the ending View.fxml
     */
    public static <R extends FxController> URL loadView(Class<R> controllerClazz) throws IllegalArgumentException, NullPointerException {
        String head = null;
        if ( controllerClazz.getSimpleName().endsWith("Controller") ) {
            head = controllerClazz.getSimpleName().substring(0, controllerClazz.getSimpleName().length() - "Controller".length());
        } else if ( controllerClazz.getSimpleName().endsWith("Presenter") ) {
            head = controllerClazz.getSimpleName().substring(0, controllerClazz.getSimpleName().length() - "Presenter".length());
        }
        if ( head == null ) throw new IllegalArgumentException(controllerClazz + " does not end with Controller or Presenter");
        return Objects.requireNonNull(controllerClazz.getResource(head + "View.fxml"), "No View for " + controllerClazz);
    }

    // TODO: Move to UiUtil.
    /**
     * Dispatches the Callable to the Platform Ui Thread. If this method is called on the javafx ui thread, the supplied callable is called,
     * otherwise the exection on Platform.runLater ist synchrnized via a latch.
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

    // Internal api
    /**
     * Run on the application thread, but looking into if we are on it already.
     *
     * @param r a runnable.
     */
    public static void run(Runnable r) {
        if ( Platform.isFxApplicationThread() ) r.run();
        else Platform.runLater(r);
    }

}
