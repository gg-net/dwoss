package eu.ggnet.saft;

import java.awt.Component;
import java.awt.Desktop;
import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinPool;

import javafx.scene.Node;
import javafx.scene.Parent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.saft.core.ui.Failure;
import eu.ggnet.saft.core.ui.SwingCore;
import eu.ggnet.saft.core.ui.builder.*;

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
     * Returns a new Ui builder.
     *
     * @return a new Ui builder.
     */
    public static PreBuilder build() {
        return new PreBuilder().parent(SwingCore.mainFrame());
    }

    /**
     * Returns a new Ui builder.
     *
     * @param swingParent optional swing parrent
     * @return a new Ui builder.
     */
    public static PreBuilder build(Component swingParent) {
        return new PreBuilder().parent(swingParent);
    }

    /**
     * Returns a new Ui builder.
     *
     * @param javaFxParent optional javafx parrent
     * @return a new Ui builder.
     */
    public static PreBuilder build(Parent javaFxParent) {
        return new PreBuilder().parent(javaFxParent);
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
        UiCore.EXECUTOR_SERVICE.execute(() -> {
            try {
                callable.call();
            } catch (Exception e) {
                UiCore.handle(e);
            }
        });
    }

    public static void exec(Runnable runnable) {
        UiCore.EXECUTOR_SERVICE.execute(() -> {
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

}
