package eu.ggnet.saft.core.all;

import eu.ggnet.saft.api.ui.FxController;
import eu.ggnet.saft.core.swing.*;

import java.io.File;
import java.util.concurrent.Callable;

import javafx.scene.layout.Pane;

import javax.swing.JPanel;

/**
 * Interface to all Ui's.
 *
 * @author oliver.guenther
 * @param <T>
 */
public interface UiCreator<T> extends Callable<T> {

    <R extends JPanel> UiOk<R> choiceSwing(Class<R> panelClazz);

    <R extends Pane> UiOk<R> choiceFx(Class<R> paneClazz);

    <R extends FxController> UiOk<R> choiceFxml(Class<R> controllerClass);

    <D> UiCreator<D> call(Callable<D> callable);

    Callable<Void> osOpen();

    <R extends JPanel> SwingOpenPanel<T, R> openSwing(Class<R> builder);

    <R extends JPanel> SwingOpenPanel<T, R> openSwing(Class<R> builder, String id);

    <R extends Pane> SwingOpenPane<T, R> openFx(Class<R> panelClass);

    <R extends Pane> SwingOpenPane<T, R> openFx(Class<R> panelClass, String id);

    <R extends FxController> SwingOpenFxml<T, R> openFxml(Class<R> controllerClass);

    <R extends FxController> SwingOpenFxml<T, R> openFxml(Class<R> controllerClass, String id);

    UiOk<File> openFileChooser();

    UiOk<File> openFileChooser(String title);

    void exec();
}
