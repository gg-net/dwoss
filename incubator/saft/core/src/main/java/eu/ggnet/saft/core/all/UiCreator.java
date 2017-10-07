package eu.ggnet.saft.core.all;

import java.io.File;
import java.util.concurrent.Callable;

import javax.swing.JPanel;

import javafx.scene.layout.Pane;

import eu.ggnet.saft.api.ui.FxController;
import eu.ggnet.saft.core.swing.*;

/**
 * Interface to all Ui components.
 *
 * @author oliver.guenther
 * @param <T>
 */
public interface UiCreator<T> extends Callable<T> {

    /**
     * Wraps a swing JPanel into an Ok/Cancel dialog.
     *
     * @param <R>
     * @param panelClazz the clazz of the panel implementation
     * @return a fluent choice.
     */
    <R extends JPanel> UiOk<R> choiceSwing(Class<R> panelClazz);

    /**
     * Wraps a javafx pane into an Ok/Cancel dialog.
     *
     * @param <R>
     * @param paneClazz the clazz of the pane implementation
     * @return a fluent choice.
     */
    <R extends Pane> UiOk<R> choiceFx(Class<R> paneClazz);

    /**
     * Wraps a javafx fxml pane into an Ok/Cancel dialog.
     * The fxml must have a pane a the root object. The Controller class must be of type {@link FxController}
     *
     * @param <R>
     * @param controllerClazz the controller class.
     * @return a fluent choice.
     */
    <R extends FxController> UiOk<R> choiceFxml(Class<R> controllerClazz);

    /**
     * Wraps the callable in an other thread, not the ui thread.
     *
     * @param <D>
     * @param callable the task to be done the background
     * @return fluent UiCreator
     */
    <D> UiCreator<D> call(Callable<D> callable);

    Callable<Void> osOpen();

    /**
     * Wraps a swing JPanel into a simple Dialog, without any decorations.
     *
     * @param <R>
     * @param panelClazz the clazz of the JPanel
     * @return a fluent Dialog
     */
    <R extends JPanel> SwingOpenPanel<T, R> openSwing(Class<R> panelClazz);

    /**
     * Wraps a swing JPanel into a simple Dialog, without any decorations.
     *
     * @param <R>
     * @param id         an optional id used in the title
     * @param panelClazz the clazz of the JPanel
     * @return a fluent Dialog
     */
    <R extends JPanel> SwingOpenPanel<T, R> openSwing(Class<R> panelClazz, String id);

    /**
     * Wraps a javafx pane into a simple Dialog, without any decorations.
     *
     * @param <R>
     * @param paneClazz the clazz of the javafx pane
     * @return a fluent Dialog
     */
    <R extends Pane> SwingOpenPane<T, R> openFx(Class<R> paneClazz);

    /**
     * Wraps a javafx pane into a simple Dialog, without any decorations.
     *
     * @param <R>
     * @param id        an optional id used in the title
     * @param paneClazz the clazz of the javafx pane
     * @return a fluent Dialog
     */
    <R extends Pane> SwingOpenPane<T, R> openFx(Class<R> paneClazz, String id);

    /**
     * Wraps a javafx fxml pane into a simple Dialog, without any decorations.
     * The fxml must have a pane a the root object. The Controller class must be of type {@link FxController}
     *
     * @param <R>
     * @param controllerClazz the controller class
     * @return a fluent Dialog
     */
    <R extends FxController> SwingOpenFxml<T, R> openFxml(Class<R> controllerClazz);

    /**
     * Wraps a javafx fxml pane into a simple Dialog, without any decorations.
     * The fxml must have a pane a the root object. The Controller class must be of type {@link FxController}
     *
     * @param <R>
     * @param id              an optional id used in the title
     * @param controllerClazz the controller class
     * @return a fluent Dialog
     */
    <R extends FxController> SwingOpenFxml<T, R> openFxml(Class<R> controllerClazz, String id);

    /**
     * Opens a file chooser.
     *
     * @return a fluent filechooser in the Ok/Cancel Dialog.
     */
    UiOk<File> openFileChooser();

    /**
     * Opens a file chooser.
     *
     * @param title an optional title
     * @return a fluent filechooser in the Ok/Cancel Dialog.
     */
    UiOk<File> openFileChooser(String title);

    /**
     * Executes the full fluent stream.
     */
    void exec();
}
