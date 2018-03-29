package eu.ggnet.saft.core.ui;

import java.awt.Component;
import java.awt.Window;
import java.lang.ref.WeakReference;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import eu.ggnet.saft.UiCore;

/**
 * Core which is used if Saft is in Swing mode.
 *
 * @author oliver.guenther
 */
public class SwingCore {

    private static JFXPanel startHelper = null;

    private static boolean started = false;

    /**
     * Active Windows Collection. Is used to get a window by a key if its already active.
     */
    public final static Map<String, WeakReference<Window>> ACTIVE_WINDOWS = new ConcurrentHashMap<>();

    /**
     * Holds a mapping of all Scenes in JFXPanels. Used to discover parent windows if in a wrapped JFXPanel.
     */
    private static final Map<Scene, JFXPanel> SWING_PARENT_HELPER = new WeakHashMap<>();

    public static JFrame mainFrame() {
        return UiCore.getMainFrame();
    }

    public static JFXPanel wrap(Pane p) throws InterruptedException {
        final JFXPanel fxp = jfxPanel();
        final CountDownLatch cdl = new CountDownLatch(1);
        if ( Platform.isFxApplicationThread() ) {
            fxp.setScene(new Scene(p, Color.TRANSPARENT));
            SWING_PARENT_HELPER.put(fxp.getScene(), fxp);
            cdl.countDown();
        } else {
            Platform.runLater(() -> {
                fxp.setScene(new Scene(p));
                SWING_PARENT_HELPER.put(fxp.getScene(), fxp);
                cdl.countDown();
            });
        }
        cdl.await();
        return fxp;
    }

    /**
     * Wrap a pane into a JFXPanel on the actual thread, the user must be aware, that this must be run on the JavaFx Thread.
     *
     * @param pane a Pane to be wrapped.
     * @return a CompletableFuture creating the JFXPanel
     */
    public static JFXPanel wrapDirect(Pane pane) {
        JFXPanel fxp = jfxPanel();
        fxp.setScene(new Scene(pane, Color.TRANSPARENT));
        SWING_PARENT_HELPER.put(fxp.getScene(), fxp);
        return fxp;
    }

    private static JFXPanel jfxPanel() { // HINT: Be sure, that this can be run on any thread.
        JFXPanel result;
        if ( startHelper != null ) {
            result = startHelper;
            startHelper = null;
        } else {
            result = new JFXPanel();
        }
        return result;
    }

    public static void ensurePlatformIsRunning() {
        if ( !started ) {
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
        if ( c == null ) return Optional.empty();
        if ( c instanceof Window ) return Optional.of((Window)c);
        return Optional.ofNullable(SwingUtilities.getWindowAncestor(c));
    }

    /**
     * Returns the Swing Window in Swing Mode from a wrapped JavaFx Node.
     *
     * @param p the node
     * @return a window
     */
    public static Optional<Window> windowAncestor(Node p) {
        if ( p == null ) return Optional.empty();
        return windowAncestor(SWING_PARENT_HELPER.get(p.getScene()));
    }
}
