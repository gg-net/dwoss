package eu.ggnet.saft.core;

import java.awt.Component;
import java.awt.Window;
import java.lang.ref.WeakReference;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * Core which is used if Saft is in Swing mode.
 *
 * @author oliver.guenther
 */
public class SwingCore {

    private static JFXPanel startHelper = null;

    private static boolean started = false;

    /**
     * The Main Application Frame. If null not initialised.
     */
    static JFrame mainFrame = null;

    /**
     * Active Windows Collection. Is used to get a window by a key if its already active.
     */
    public final static Map<String, WeakReference<Window>> ACTIVE_WINDOWS = new HashMap<>();

    /**
     * Holds a mapping of all Scenes in JFXPanels. Used to discover parent windows if in a wrapped JFXPanel.
     */
    private static Map<Scene, JFXPanel> swingParentHelper = new WeakHashMap<>();

    public static JFrame mainFrame() {
        return mainFrame;
    }

    public static JFXPanel wrap(Pane p) throws InterruptedException {
        final JFXPanel fxp = jfxPanel();
        final CountDownLatch cdl = new CountDownLatch(1);
        if (Platform.isFxApplicationThread()) {
            fxp.setScene(new Scene(p));
            swingParentHelper.put(fxp.getScene(), fxp);
            cdl.countDown();
        } else {
            Platform.runLater(() -> {
                fxp.setScene(new Scene(p));
                swingParentHelper.put(fxp.getScene(), fxp);
                cdl.countDown();
            });
        }
        cdl.await();
        return fxp;
    }

    private static JFXPanel jfxPanel() {
        JFXPanel result;
        if (startHelper != null) {
            result = startHelper;
            startHelper = null;
        } else {
            result = new JFXPanel();
        }
        return result;
    }

    public static void ensurePlatformIsRunning() {
        if (!started) {
            startHelper = new JFXPanel();
            started = true;
        }
    }

    /**
     * Special form of {@link SwingUtilities#getWindowAncestor(java.awt.Component) }, as it also verifies if the supplied parameter is of type Window and if
     * true returns it.
     *
     * @param c the component
     * @return a window.
     */
    public static Optional<Window> windowAncestor(Component c) {
        if (c == null) return Optional.empty();
        if (c instanceof Window) return Optional.of((Window) c);
        return Optional.ofNullable(SwingUtilities.getWindowAncestor(c));
    }

    /**
     * Returns the Swing Window in Swing Mode from a wrapped JavaFx Node.
     *
     * @param p the node
     * @return a window
     */
    public static Optional<Window> windowAncestor(Node p) {
        if (p == null) return Optional.empty();
        return windowAncestor(swingParentHelper.get(p.getScene()));
    }
}
