package eu.ggnet.saft;

import eu.ggnet.saft.core.ui.TitleUtil;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

import javax.swing.JFrame;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import eu.ggnet.saft.core.ui.FxCore;
import eu.ggnet.saft.core.ui.SwingCore;
import eu.ggnet.saft.core.exception.ExceptionUtil;
import eu.ggnet.saft.core.exception.SwingExceptionDialog;
import eu.ggnet.saft.core.ui.FxSaft;
import eu.ggnet.saft.core.ui.SwingSaft;

/**
 * The Core of the Saft UI, containing methods for startup or registering things.
 *
 * @author oliver.guenther
 */
public class UiCore {

    private final static BooleanProperty BACKGROUND_ACTIVITY = new SimpleBooleanProperty();

    // We need the raw type here. Otherwise we cannot get different typs of cosumers in and out.
    @SuppressWarnings("unchecked")
    private static final Map<Class, Consumer> EXCEPTION_CONSUMER = new HashMap<>();

    private static JFrame mainFrame = null; // Frame in Swing Mode

    private static Stage mainStage = null; // Frame in Fx Mode

    private static Consumer<Throwable> finalConsumer = (b) -> {
        Runnable r = () -> {
            SwingExceptionDialog.show(SwingCore.mainFrame(), "Systemfehler", ExceptionUtil.extractDeepestMessage(b),
                    ExceptionUtil.toMultilineStacktraceMessages(b), ExceptionUtil.toStackStrace(b));
        };

        if ( EventQueue.isDispatchThread() ) r.run();
        else {
            try {
                EventQueue.invokeAndWait(r);
            } catch (InterruptedException | InvocationTargetException e) {
                // This will never happen.
            }
        }

    };

    /**
     * Returns the mainFrame in swing mode, otherwise null.
     *
     * @return the mainFrame in swing mode, otherwise null
     */
    public static JFrame getMainFrame() {
        return mainFrame;
    }

    /**
     * Returns the mainStage in fx mode, otherwise null.
     *
     * @return the mainStage in fx mode, otherwise null
     */
    public static Stage getMainStage() {
        return mainStage;
    }

    /**
     * Returns a property that represents background activity.
     *
     * @return a property that represents background activity
     */
    public static BooleanProperty backgroundActivityProperty() {
        return BACKGROUND_ACTIVITY;
    }

    /**
     * interim Mode, Saft connects to a running environment.
     *
     * @param mainView the mainView to continue on.
     */
    public static void continueSwing(JFrame mainView) {
        if ( isRunning() ) throw new IllegalStateException("UiCore is already initialised and running");
        Platform.setImplicitExit(false); // Need this, as we asume many javafx elements opening and closing.
        mainFrame = mainView;
        mainView.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                for (WeakReference<Window> windowRef : SwingCore.ACTIVE_WINDOWS.values()) {
                    if ( windowRef.get() == null ) continue;
                    windowRef.get().setVisible(false); // Close all windows.
                    windowRef.get().dispose();
                }
            }

            @Override
            public void windowClosed(WindowEvent e) {
                Platform.exit();
            }

        });
    }

    /**
     * Starts the Core in Swing mode, may only be called once.
     *
     * @param <T>
     * @param builder the builder for swing.
     */
    public static <T extends Component> void startSwing(final Callable<T> builder) {
        if ( isRunning() ) throw new IllegalStateException("UiCore is already initialised and running");

        try {
            JFrame panel = SwingSaft.dispatch(() -> {
                T node = builder.call();
                JFrame p = new JFrame();
                p.setTitle(TitleUtil.title(node.getClass()));
                p.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                p.getContentPane().add(builder.call());
                p.pack();
                p.setLocationByPlatform(true);
                p.setVisible(true);
                return p;
            });
            continueSwing(panel);
        } catch (InterruptedException | InvocationTargetException | ExecutionException ex) {
            handle(ex);
        }
    }

    /**
     * Starts the Ui in JavaFx variant.
     * <p>
     * This also assumes two things:
     * <ul>
     * <li>The JavaFX Platfrom is already running (as a Stage already exists), most likely created through default lifecycle of javaFx</li>
     * <li>This Stage will always be open or the final to be closed, so implicitExit is ok</li>
     * </ul>
     *
     * @param <T>          type restriction.
     * @param primaryStage the primaryStage for the application, not yet visible.
     * @param builder      the build for the main ui.
     */
    public static <T extends Parent> void startJavaFx(final Stage primaryStage, final Callable<T> builder) {
        if ( isRunning() ) throw new IllegalStateException("UiCore is already initialised and running");
        mainStage = primaryStage;
        FxSaft.dispatch(() -> {
            T node = builder.call();
            primaryStage.setTitle(TitleUtil.title(node.getClass()));
            primaryStage.setScene(new Scene(node));
            primaryStage.centerOnScreen();
            primaryStage.sizeToScene();
            primaryStage.show();
            return null;
        });
    }

    /**
     * Registers an extra renderer for an Exception in any stacktrace. HINT: There is no order or hierachy in the engine. So if you register duplicates or have
     * more than one match in a StackTrace, no one knows what might happen.
     *
     * @param <T>      type of the Exception
     * @param clazz    the class of the Exception
     * @param consumer the consumer to handle it.
     */
    public static <T> void registerExceptionConsumer(Class<T> clazz, Consumer<T> consumer) {
        EXCEPTION_CONSUMER.put(clazz, consumer);
    }

    /**
     * Allows to overwrite the default final consumer of all exceptions.
     *
     * @param <T>      type of consumer
     * @param consumer the consumer
     */
    public static <T> void overwriteFinalExceptionConsumer(Consumer<Throwable> consumer) {
        if ( consumer != null ) finalConsumer = consumer;
    }

    public static boolean isRunning() {
        return mainFrame != null || FxCore.mainStage() != null;
    }

    public static boolean isFx() {
        return (mainStage != null);
    }

    public static boolean isSwing() {
        return (mainFrame != null);
    }

    /**
     * Intake from Ui.
     * <p>
     * @param b
     */
    static void handle(Throwable b) {
        BACKGROUND_ACTIVITY.set(false); // Cleanup
        for (Class<?> clazz : EXCEPTION_CONSUMER.keySet()) {
            if ( ExceptionUtil.containsInStacktrace(clazz, b) ) {
                EXCEPTION_CONSUMER.get(clazz).accept(ExceptionUtil.extractFromStraktrace(clazz, b));
                return;
            }
        }
        finalConsumer.accept(b);
    }

}
