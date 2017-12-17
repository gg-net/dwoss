package eu.ggnet.saft;

import java.awt.*;
import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinPool;

import javax.swing.JPanel;

import javafx.scene.Node;
import javafx.scene.layout.Pane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.saft.core.FxCore;
import eu.ggnet.saft.core.SwingCore;
import eu.ggnet.saft.core.all.UiCreator;
import eu.ggnet.saft.core.all.UiOk;
import eu.ggnet.saft.core.experimental.*;
import eu.ggnet.saft.core.fx.FxCreator;
import eu.ggnet.saft.core.swing.SwingCreator;

/*
 Notes of olli:
- alles was mit ui zuständen des frameworks zu tun hat, startet hier. zb. mainFrame, progress, failure messager, excetion output.
 */
/**
 * The main entry point.
 * <p>
 * <p>
 * import static eu.ggnet.saft.core.UiAlert.Type.ERROR;
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
        return new SwingBuilder().parent(SwingCore.windowAncestor(swingParent).orElse(SwingCore.mainFrame()));
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
     * Returns a new failure Handler.
     *
     * @return a new failure Handler.
     */
    public static Failure failure() {
        return new Failure();
    }

    /**
     * Returns a new progress Handler.
     *
     * @return a new progress Handler.
     */
    public static ProgressBuilder progress() {
        return new ProgressBuilder();
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
     * Wrapper for Desktop.getDesktop().open() with UI Exception handling
     *
     * @param file a file to open via ui.
     * @return true if operation was successful, otherwise false. Can be used if the following operations should happen.
     */
    public static boolean osOpen(File file) {
        try {
            Desktop.getDesktop().open(file);
            return true;
        } catch (Exception e) {
            UiCore.handle(e);
        }
        return false;
    }

    /**
     * Handles an Exception in the Ui, using the registered ExceptionCosumers form {@link UiCore#registerExceptionConsumer(java.lang.Class, java.util.function.Consumer)
     * }.
     *
     * @param b the throwable to be handled.
     */
    public static void handle(Throwable b) {
        UiCore.handle(b);
    }

    // starting from here comes old. will be deprecated in the future
    @Deprecated
    private static <R> UiCreator<R> creator() {
        if ( UiCore.isRunning() && UiCore.isFx() )
            return new FxCreator<>(null, FxCore.mainStage(), null);
        if ( UiCore.isRunning() && UiCore.isSwing() )
            return new SwingCreator<>(null, SwingCore.mainFrame(), null);
        throw new IllegalStateException("UiCore not initalized");
    }

    /**
     * @deprecated use fx() oder dialog()
     */
    @Deprecated
    public static <T, R extends Pane> UiOk<R> choiceFx(Class<R> panelClazz) {
        return Ui.<T>creator().choiceFx(panelClazz);
    }

    /**
     * @deprecated use swing()
     */
    @Deprecated
    public static <T, R extends JPanel> UiOk<R> choiceSwing(Class<R> panelClazz) {
        return Ui.<T>creator().choiceSwing(panelClazz);
    }

}
