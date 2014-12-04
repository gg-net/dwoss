package eu.ggnet.saft.core;

import eu.ggnet.saft.core.all.UiUtil;
import eu.ggnet.saft.core.exception.ExceptionUtil;
import eu.ggnet.saft.core.exception.SwingExceptionDialog;
import eu.ggnet.saft.core.fx.FxSaft;
import eu.ggnet.saft.core.swing.SwingSaft;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.*;
import javafx.stage.Stage;
import javax.swing.JFrame;

/**
 *
 * @author oliver.guenther
 */
public class UiCore {

    public final static java.util.List<String> CLASS_SUFFIXES_FOR_ICONS = Arrays.asList("Controller", "View", "ViewCask");

    private final static BooleanProperty backgroundActivity = new SimpleBooleanProperty();

    // We need the raw type here. Otherwise we cannot get different typs of cosumers in and out.
    @SuppressWarnings("unchecked")
    private static final Map<Class, Consumer> exceptionConsumer = new HashMap<>();

    private static Consumer<Throwable> finalConsumer = (b) -> {
        Runnable r = () -> {
            SwingExceptionDialog.show(SwingCore.mainFrame(), "Systemfehler", ExceptionUtil.extractDeepestMessage(b),
                    ExceptionUtil.toMultilineStacktraceMessages(b), ExceptionUtil.toStackStrace(b));
        };

        if (EventQueue.isDispatchThread()) r.run();
        else {
            try {
                EventQueue.invokeAndWait(r);
            } catch (InterruptedException | InvocationTargetException e) {
                // This will never happen.
            }
        }

    };

    public static BooleanProperty backgroundActivityProperty() {
        return backgroundActivity;
    }

    /**
     * interim Mode, Saft connects to a running environment.
     *
     * @param mainView
     */
    public static void continueSwing(JFrame mainView) {
        if (isRunning()) throw new IllegalStateException("UiCore is already initialised and running");
        Platform.setImplicitExit(false); // Need this, as we asume many javafx elements opening and closing.
        SwingCore.mainFrame = mainView;
        mainView.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                for (WeakReference<Window> windowRef : SwingCore.ACTIVE_WINDOWS.values()) {
                    if (windowRef.get() == null) continue;
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
     * @param builder
     */
    public static <T extends Component> void startSwing(final Callable<T> builder) {
        if (isRunning()) throw new IllegalStateException("UiCore is already initialised and running");

        try {
            JFrame panel = SwingSaft.dispatch(() -> {
                JFrame p = new JFrame();
                p.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                p.getContentPane().add(builder.call());
                p.pack();
                p.setLocationByPlatform(true);
                p.setVisible(true);
                return p;
            });
            continueSwing(panel);
        } catch (InterruptedException | InvocationTargetException | ExecutionException ex) {
            catchException(ex);
        }
    }

    /**
     * Starts the Ui in JavaFx variant.
     *
     * This also assumes two things:
     * <ul>
     * <li>The JavaFX Platfrom is already running (as a Stage already exists), most likely created through default lifecycle of javaFx</li>
     * <li>This Stage will always be open or the final to be closed, so implicitExit is ok</li>
     * </ul>
     *
     * @param <T> type restriction.
     * @param primaryStage the primaryStage for the application, not yet visible.
     * @param builder the build for the main ui.
     */
    public static <T extends Parent> void startJavaFx(final Stage primaryStage, final Callable<T> builder) {
        if (isRunning()) throw new IllegalStateException("UiCore is already initialised and running");
        FxCore.mainStage = primaryStage;
        try {
            FxSaft.dispatch(() -> {
                T node = builder.call();
                primaryStage.setTitle(UiUtil.title(node.getClass()));
                primaryStage.setScene(new Scene(node));
                primaryStage.centerOnScreen();
                primaryStage.sizeToScene();
                primaryStage.show();
                return null;
            });
        } catch (ExecutionException | InterruptedException e) {
            catchException(e);
        }
    }

    /**
     * Registers an extra renderer for an Exception in any stacktrace. HINT: There is no order or hierachy in the engine. So if you register duplicates or have
     * more than one match in a StackTrace, no one knows what might happen.
     *
     * @param <T> type of the Exception
     * @param clazz the class of the Exception
     * @param consumer the consumer to handle it.
     */
    public static <T> void registerExceptionConsumer(Class<T> clazz, Consumer<T> consumer) {
        exceptionConsumer.put(clazz, consumer);
    }

    /**
     * Allows to overwrite the default final consumer of all exceptions.
     *
     * @param <T> type of consumer
     * @param consumer the consumer
     */
    public static <T> void overwriteFinalExceptionConsumer(Consumer<Throwable> consumer) {
        if (consumer != null) finalConsumer = consumer;
    }

    static void catchException(Throwable b) {
        for (Class<?> clazz : exceptionConsumer.keySet()) {
            if (ExceptionUtil.containsInStacktrace(clazz, b)) {
                exceptionConsumer.get(clazz).accept(ExceptionUtil.extractFromStraktrace(clazz, b));
                return;
            }
        }
        finalConsumer.accept(b);
    }

    public static boolean isRunning() {
        return SwingCore.mainFrame() != null || FxCore.mainStage() != null;
    }

    public static boolean isFx() {
        return (FxCore.mainStage() != null);
    }

    public static boolean isSwing() {
        return (SwingCore.mainFrame() != null);
    }

}
