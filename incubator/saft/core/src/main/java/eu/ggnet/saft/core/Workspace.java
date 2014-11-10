package eu.ggnet.saft.core;

import java.awt.Window;
import java.awt.event.ActionListener;

import javax.swing.JFrame;

/**
 * Workspace service.
 * <p/>
 * @author oliver.guenther
 */
public interface Workspace {

    /**
     * Returns a stored instance of the supplied class, use not encouraged.
     * <p/>
     * @param <T>   the type
     * @param clazz the class
     * @return the value or null.
     */
    <T> T getValue(Class<T> clazz);

    /**
     * Set a value to be stored in the workspace and used by another component, use <u>not</u> encouraged.
     * This method allows to store a instance of anything in the workspace. the instance will be stored by t.getClass().
     * So the workspace can have one instance by class type.
     * <p/>
     * The usage of this method is not encouraged, because it binds components to the workspace itself. It should only be used
     * as a last resort. (e.g. worse alternatives are to bind components together, which only what to exchange on value, or creating
     * an extra project)
     * <p/>
     * <h3>Known usages:</h3>
     * <ul>
     * <li>Stock.Id</li>
     * </ul>
     * <p/>
     * @param <T> the type
     * @param t   the instance
     */
    <T> void setValue(T t);

    Window getMainFrame();

    void setMainFrame(JFrame mainFrame);

    /**
     * Shuts the workspace down. Should be called from all closing components.
     */
    void shutdown();

    /**
     * The possibility to add a Listener which gets informed in case of a shutdown.
     * <p/>
     * @param l the listener
     */
    void addShutdownListener(ActionListener l);

    /**
     * Remove a listener which was listening for a shutdown.
     * <p/>
     * @param l the listener
     * @return true if removal was successful
     */
    boolean removeShutdownListener(ActionListener l);
}
