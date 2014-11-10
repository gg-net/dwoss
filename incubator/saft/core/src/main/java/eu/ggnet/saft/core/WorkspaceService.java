package eu.ggnet.saft.core;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;

import javafx.application.Platform;

/**
 * This is the Workspace Lookup.
 *
 * All relevant information, which are set at runtime in the Client can be discovert through this.
 *
 * @author oliver.guenther
 */
/* Hint: This is implemened via a Singleton pattern, which is known to be not a good solution.
 * But it can be very simply transformed to a Lookup implementation, which is the Ui standard by now.
 * With this knowledge, the singleton is ok for now.
 */
public class WorkspaceService implements Workspace {

    private Window mainFrame;

    private Set<ActionListener> shutdownListeners = new HashSet<>();

    private Map<Class<?>, Object> values = new HashMap<>();

    private boolean shutdown = false;

    @Override
    public Window getMainFrame() {
        return mainFrame;
    }

    @Override
    public void setMainFrame(JFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    @Override
    public void shutdown() {
        if ( shutdown ) return; // enure no loops.
        shutdown = true;
        for (ActionListener listener : shutdownListeners) {
            listener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "shutdown"));
        }

        for (Window window : Frame.getWindows()) {
            window.setVisible(false);
            window.dispose();
        }
        Platform.exit();
    }

    @Override
    public void addShutdownListener(ActionListener l) {
        shutdownListeners.add(l);
    }

    @Override
    public boolean removeShutdownListener(ActionListener l) {
        return shutdownListeners.remove(l);
    }

    @Override
    public <T> T getValue(Class<T> clazz) {
        return (T)values.get(clazz);
    }

    @Override
    public <T> void setValue(T t) {
        values.put(t.getClass(), t);
    }
}
