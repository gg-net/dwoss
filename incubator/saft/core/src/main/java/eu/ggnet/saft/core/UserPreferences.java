package eu.ggnet.saft.core;

import java.awt.Component;

/**
 * Service for UserPreferences.
 * <p/>
 * @author oliver.guenther
 */
public interface UserPreferences {

    /**
     * Returns true if UserPreferences are in reset mode.
     * <p/>
     * @return true if UserPreferences are in reset mode.
     */
    boolean isReset();

    /**
     * Loads the location of a component from the user preferences.
     * <p/>
     * @param c the component.
     */
    void loadLocation(Component c);

    /**
     * Sets the reset mode.
     * In this mode, a load location will not change the component, but clear the node in the preferences tree.
     * <p/>
     * @param reset true for reset mode.
     */
    void setReset(boolean reset);

    /**
     * Stores the location of a component in the user preferences using the class as reference.
     * <p/>
     * @param c the component.
     */
    void storeLocation(Component c);

    /**
     * Loads the className of the LAF from the Preferences Store.
     * <p/>
     * @return the className
     */
    String loadLaf();

    /**
     * Stores the LAF className in the Preferences Store
     * <p/>
     * @param className the className to store.
     */
    void storeLaf(String className);
}
