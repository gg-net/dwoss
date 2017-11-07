package eu.ggnet.saft.core;

import java.awt.Component;
import java.awt.Window;
import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinPool;

import javax.swing.JPanel;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.saft.api.ui.FxController;
import eu.ggnet.saft.core.all.UiCreator;
import eu.ggnet.saft.core.all.UiOk;
import eu.ggnet.saft.core.experimental.DialogFx;
import eu.ggnet.saft.core.experimental.Swing;
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
     * @return a new swing handling.
     */
    public static Swing swing() {
        return new Swing();
    }

    /**
     * Initializes a new swing component handling with a parent.
     * The mode: swing is relevant for the component to be wrapped. The Wrapping Ui is set in the UiCore.
     *
     * @param swingParent the parent of the swing element.
     * @return a new swing handling.
     */
    public static Swing swing(Window swingParent) {
        return new Swing().parent(swingParent);
    }

    /**
     * Initializes a new fx dialog component handling.
     * The mode: the fx dialog is relevant for the component to be wrapped. The Wrapping Ui is set in the UiCore.
     *
     * @return
     */
    public static DialogFx dialog() {
        return new DialogFx();
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

    // starting from here comes old. will be deprecated in the future
    public static <R> UiCreator<R> parent(Component parent) {
        if ( UiCore.isRunning() && UiCore.isFx() ) {
            L.warn("Using a swing component as parent in JavaFx Mode is not yet implemented");
            return new FxCreator<>(null, FxCore.mainStage(), null); // TODO: Find a way to get a Stage from a Swing embedded component.
        }
        if ( UiCore.isRunning() && UiCore.isSwing() )
            return new SwingCreator<>(null, SwingCore.windowAncestor(parent).orElse(SwingCore.mainFrame()), null);
        throw new IllegalStateException("UiCore not initalized");
    }

    public static <R> UiCreator<R> parent(Parent parent) {
        if ( UiCore.isRunning() && UiCore.isFx() )
            return new FxCreator<>(null, FxSaft.windowAncestor(parent), null);
        if ( UiCore.isRunning() && UiCore.isSwing() )
            return new SwingCreator<>(null, SwingCore.windowAncestor(parent).orElse(SwingCore.mainFrame()), null);
        throw new IllegalStateException("UiCore not initalized");
    }

    private static <R> UiCreator<R> creator() {
        if ( UiCore.isRunning() && UiCore.isFx() )
            return new FxCreator<>(null, FxCore.mainStage(), null);
        if ( UiCore.isRunning() && UiCore.isSwing() )
            return new SwingCreator<>(null, SwingCore.mainFrame(), null);
        throw new IllegalStateException("UiCore not initalized");
    }

    public static <R> UiCreator<R> call(Callable<R> callable) {
        return creator().call(callable);
    }

    public static <T, R extends Pane> UiOk<R> choiceFx(Class<R> panelClazz) {
        return Ui.<T>creator().choiceFx(panelClazz);
    }

    public static <T, R extends FxController> UiOk<R> choiceFxml(Class<R> controllerClass) {
        return Ui.<T>creator().choiceFxml(controllerClass);
    }

    public static <T, R extends JPanel> UiOk<R> choiceSwing(Class<R> panelClazz) {
        return Ui.<T>creator().choiceSwing(panelClazz);
    }

    public static UiOk<File> openFileChooser(String title) {
        return Ui.<File>creator().openFileChooser(title);

    }

    public static UiOk<File> openFileChooser() {
        return Ui.<File>creator().openFileChooser();
    }

    public static <T, R extends JPanel> SwingOpenPanel<T, R> openSwing(Class<R> panelClass) {
        return openSwing(panelClass, null);
    }

    public static <T, R extends JPanel> SwingOpenPanel<T, R> openSwing(Class<R> panelClass, String key) {
        return new SwingCreator<T>(null, SwingCore.mainFrame(), null).openSwing(panelClass, key);
    }

    public static <T, R extends JPanel> SwingOpenPanel<T, R> showSwing(Class<R> panelClass) {
        return showSwing(panelClass, null);
    }

    public static <T, R extends JPanel> SwingOpenPanel<T, R> showSwing(Class<R> panelClass, String key) {
        return new SwingCreator<T>(null, SwingCore.mainFrame(), null).openSwing(panelClass, key);
    }

    public static <T, R extends Pane> SwingOpenPane<T, R> openFx(Class<R> panelClass) {
        return openFx(panelClass, null);
    }

    public static <T, R extends Pane> SwingOpenPane<T, R> openFx(Class<R> panelClass, String id) {
        return new SwingCreator<T>(null, SwingCore.mainFrame(), null).openFx(panelClass, id);
    }

    public static <T, R extends FxController> SwingOpenFxml<T, R> openFxml(Class<R> controllerClass) {
        return openFxml(controllerClass, null);
    }

    public static <T, R extends FxController> SwingOpenFxml<T, R> openFxml(Class<R> controllerClass, String id) {
        return new SwingCreator<T>(null, SwingCore.mainFrame(), null).openFxml(controllerClass, id);
    }

}
