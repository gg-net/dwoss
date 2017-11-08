package eu.ggnet.saft.core;

import java.awt.*;
import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Function;

import javax.swing.JPanel;

import javafx.concurrent.Worker;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.saft.api.ui.FxController;
import eu.ggnet.saft.core.all.UiCreator;
import eu.ggnet.saft.core.all.UiOk;
import eu.ggnet.saft.core.experimental.*;
import eu.ggnet.saft.core.fx.FxCreator;
import eu.ggnet.saft.core.fx.FxSaft;
import eu.ggnet.saft.core.swing.*;

/**
 * The main entry point.
 * <p>
 * Some rules which I invented on the way:
 * <ul>
 * <li>Result of null is indicator to break the chain</li>
 * <li></li>
 * <li></li>
 * <li></li>
 * </ul>
 *
 * @author oliver.guenther
 */
public class Ui {

    private final static Logger L = LoggerFactory.getLogger(Ui.class);

    /**
     * Initializes a new swing component handling.
     * The mode: swing is relevant for the component to be wrapped. The Wrapping Ui is set in the UiCore.
     *
     * @return a new swing builder
     */
    public static SwingBuilder swing() {
        return new SwingBuilder().parent(SwingCore.mainFrame());
    }

    /**
     * Initializes a new swing component handling with a parent.
     * The mode: swing is relevant for the component to be wrapped. The Wrapping Ui is set in the UiCore.
     *
     * @param swingParent the parent of the swing element.
     * @return a new swing builder.
     */
    public static SwingBuilder swing(Window swingParent) {
        return new SwingBuilder().parent(swingParent);
    }

    /**
     * Initializes a new fx dialog component handling.
     * The mode: the fx dialog is relevant for the component to be wrapped. The Wrapping Ui is set in the UiCore.
     *
     * @return a new dialog builder
     */
    public static DialogBuilder dialog() {
        return new DialogBuilder().parent(SwingCore.mainFrame());
    }

    /**
     * Initializes a new fx component handling.
     * The mode: the fx pane is relevant for the component to be wrapped. The Wrapping Ui is set in the UiCore.
     *
     * @return a new fxbuilder
     */
    public static FxBuilder fx() {
        return new FxBuilder().parent(SwingCore.mainFrame());
    }

    /**
     * Initializes a new fx component handling.
     * The mode: the fx pane is relevant for the component to be wrapped. The Wrapping Ui is set in the UiCore.
     *
     * @return a new fxbuilder
     */
    public static FxmlBuilder fxml() {
        return new FxmlBuilder().parent(SwingCore.mainFrame());
    }

    /**
     * Shortcut to a file chooser.
     *
     * @return a file chooser builder.
     */
    public static FileChooserBuilder fileChooser() {
        return new FileChooserBuilder();
    }

    /**
     * Wrapper around {@link ForkJoinPool#commonPool() } with Ui Exception handling.
     * This is the default way to build a ui chain/stream with some background activity
     * <pre>
     * {@code
     * Ui.exec(Ui
     *   .call(() -> HardWorker.work2s("per", "Eine leere Adresse"))
     *   .choiceSwing(DocumentAdressUpdateView.class)
     *   .onOk((t) -> HardWorker.work2s("middle", t.getAddress()))
     *   .choiceSwing(DocumentAdressUpdateView.class)
     *   .onOk((t) -> HardWorker.work2s("post", t.getAddress()))
     *   );
     * }
     * </pre>
     *
     * @param <V>
     * @param callable a callable for the background.
     */
    // TODO: Runable version
    public static <V> void exec(Callable<V> callable) {
        ForkJoinPool.commonPool().execute(() -> {
            try {
                callable.call();
            } catch (Exception e) {
                UiCore.handle(e);
            }
        });
    }

    public static void exec(Runnable runnable) {
        ForkJoinPool.commonPool().execute(() -> {
            try {
                runnable.run();
            } catch (RuntimeException e) {
                UiCore.handle(e);
            }
        });
    }

    /**
     * Allows the closing of a window from within a Pane or Panel
     * <pre>
     * {@code
     * JFrame f = new JFrame();
     * JPanel p = new JPanel();
     * JButton b = new Button("Close");
     * p.add(b);
     * f.getContentPane().get(p);
     * b.addActionListener(() -> Ui.cloesWindowOf(p);
     * f.setVisible(true);
     * }
     * </pre>.
     *
     * @param c the component which is the closest to the window.
     */
    public static void closeWindowOf(Component c) {
        if ( !UiCore.isRunning() ) return;
        if ( UiCore.isFx() ) throw new RuntimeException("Closing of embedded Swing in JavaFx not yet implemented");
        SwingCore.windowAncestor(c).ifPresent((w) -> {
            w.setVisible(false);
            w.dispose();
        });
    }

    public static void closeWindowOf(Node n) {
        if ( !UiCore.isRunning() ) return;
        if ( UiCore.isFx() ) throw new RuntimeException("Closing of JavaFx not yet implemented");
        SwingCore.windowAncestor(n).ifPresent((w) -> {
            w.setVisible(false);
            w.dispose();
        });
    }

    /**
     * Enables the progress information in the main ui while the supplied function is run.
     * Starts a progress display then the returned function is called and stops it, then its complete.
     * Uses the class name of the supplied function as monitor title.
     *
     * @param <U>
     * @param <T>
     * @param function the function to be wrapped into progress information.
     * @return a new function, which if called,shows progress.
     */
    public static <U, T> Function<T, U> progress(Function<T, U> function) {
        // TODO: Progresshandling sucks, but its only internal, so we can live with it for now.
        return (T t) -> {
            UiCore.backgroundActivityProperty().set(true);
            try {
                return function.apply(t);
            } finally {
                UiCore.backgroundActivityProperty().set(false);
            }
        };
    }

    /**
     * Observes progress of a supplied function.
     * Starts a progress display then the returned function is called and stops it, then its complete.
     *
     * @param <U>
     * @param <T>
     * @param function the function to be wrapped into progress information.
     * @param title    the title of the progress monitor.
     * @return a new function, which if called,shows progress.
     */
    public static <U, T> Function<? super T, ? extends U> progress(String title, Function<? super T, ? extends U> function) {
        // TODO: Title is ignored for now.
        return progress(function);
    }

    /**
     * Observers the progress on any javaFx worker.
     * If there is some form of central managed and displayed progress and status message system registered with saft, this can be used to show a worker
     * progress.
     *
     * @param <T>
     * @param worker the worker to be observed.
     * @return the parameter worker, for fluent usage.
     */
    public static <T> Worker<T> progress(Worker<T> worker) {
        if ( worker != null ) worker.runningProperty().addListener((ob, o, n) -> UiCore.backgroundActivityProperty().setValue(n));
        return worker;
    }

    /**
     * Wrapper for Desktop.getDesktop().open() with UI Exception handling
     *
     * @param file a file to open via ui.
     */
    public static void osOpen(File file) {
        try {
            Desktop.getDesktop().open(file);
        } catch (Exception e) {
            UiCore.handle(e);
        }
    }

    // starting from here comes old. will be deprecated in the future
    @Deprecated
    public static <R> UiCreator<R> parent(Component parent) {
        if ( UiCore.isRunning() && UiCore.isFx() ) {
            L.warn("Using a swing component as parent in JavaFx Mode is not yet implemented");
            return new FxCreator<>(null, FxCore.mainStage(), null); // TODO: Find a way to get a Stage from a Swing embedded component.
        }
        if ( UiCore.isRunning() && UiCore.isSwing() )
            return new SwingCreator<>(null, SwingCore.windowAncestor(parent).orElse(SwingCore.mainFrame()), null);
        throw new IllegalStateException("UiCore not initalized");
    }

    @Deprecated
    public static <R> UiCreator<R> parent(Parent parent) {
        if ( UiCore.isRunning() && UiCore.isFx() )
            return new FxCreator<>(null, FxSaft.windowAncestor(parent), null);
        if ( UiCore.isRunning() && UiCore.isSwing() )
            return new SwingCreator<>(null, SwingCore.windowAncestor(parent).orElse(SwingCore.mainFrame()), null);
        throw new IllegalStateException("UiCore not initalized");
    }

    @Deprecated
    private static <R> UiCreator<R> creator() {
        if ( UiCore.isRunning() && UiCore.isFx() )
            return new FxCreator<>(null, FxCore.mainStage(), null);
        if ( UiCore.isRunning() && UiCore.isSwing() )
            return new SwingCreator<>(null, SwingCore.mainFrame(), null);
        throw new IllegalStateException("UiCore not initalized");
    }

    /**
     * @deprecated use show or eval with preProcesor
     */
    @Deprecated
    public static <R> UiCreator<R> call(Callable<R> callable) {
        return creator().call(callable);
    }

    /**
     * @deprecated use fx() oder dialog()
     */
    @Deprecated
    public static <T, R extends Pane> UiOk<R> choiceFx(Class<R> panelClazz) {
        return Ui.<T>creator().choiceFx(panelClazz);
    }

    /**
     * @deprecated use fxml()
     */
    @Deprecated
    public static <T, R extends FxController> UiOk<R> choiceFxml(Class<R> controllerClass) {
        return Ui.<T>creator().choiceFxml(controllerClass);
    }

    /**
     * @deprecated use swing()
     */
    @Deprecated
    public static <T, R extends JPanel> UiOk<R> choiceSwing(Class<R> panelClazz) {
        return Ui.<T>creator().choiceSwing(panelClazz);
    }

    /**
     * @deprecated use filechooser().title().open()
     */
    @Deprecated
    public static UiOk<File> openFileChooser(String title) {
        return Ui.<File>creator().openFileChooser(title);

    }

    /**
     * @deprecated use filechooser().open()
     */
    @Deprecated
    public static UiOk<File> openFileChooser() {
        return Ui.<File>creator().openFileChooser();
    }

    /**
     * @deprecated use swing().show()
     */
    @Deprecated
    public static <T, R extends JPanel> SwingOpenPanel<T, R> openSwing(Class<R> panelClass) {
        return openSwing(panelClass, null);
    }

    /**
     * @deprecated use swing().id(key).show()
     */
    @Deprecated
    public static <T, R extends JPanel> SwingOpenPanel<T, R> openSwing(Class<R> panelClass, String key) {
        return new SwingCreator<T>(null, SwingCore.mainFrame(), null).openSwing(panelClass, key);
    }

    /**
     * @deprecated use swing().show()
     */
    @Deprecated
    public static <T, R extends JPanel> SwingOpenPanel<T, R> showSwing(Class<R> panelClass) {
        return showSwing(panelClass, null);
    }

    /**
     * @deprecated use swing().id(key).show()
     */
    @Deprecated
    public static <T, R extends JPanel> SwingOpenPanel<T, R> showSwing(Class<R> panelClass, String key) {
        return new SwingCreator<T>(null, SwingCore.mainFrame(), null).openSwing(panelClass, key);
    }

    /**
     * @deprecated use fx() oder dialog()
     */
    @Deprecated
    public static <T, R extends Pane> SwingOpenPane<T, R> openFx(Class<R> panelClass) {
        return openFx(panelClass, null);
    }

    /**
     * @deprecated use fx() oder dialog()
     */
    @Deprecated
    public static <T, R extends Pane> SwingOpenPane<T, R> openFx(Class<R> panelClass, String id) {
        return new SwingCreator<T>(null, SwingCore.mainFrame(), null).openFx(panelClass, id);
    }

    /**
     * @deprecated use fxml()
     */
    @Deprecated
    public static <T, R extends FxController> SwingOpenFxml<T, R> openFxml(Class<R> controllerClass) {
        return openFxml(controllerClass, null);
    }

    /**
     * @deprecated use fxml()
     */
    @Deprecated
    public static <T, R extends FxController> SwingOpenFxml<T, R> openFxml(Class<R> controllerClass, String id) {
        return new SwingCreator<T>(null, SwingCore.mainFrame(), null).openFxml(controllerClass, id);
    }

}
