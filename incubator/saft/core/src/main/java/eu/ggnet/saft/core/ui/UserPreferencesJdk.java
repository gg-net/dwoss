/*
 * Copyright (C) 2014 GG-Net GmbH - Oliver Günther
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.ggnet.saft.core.ui;

import java.awt.Component;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.LookAndFeel;
import javax.swing.UIManager;

import org.openide.util.lookup.ServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Util Class for storing and retrieving complex information in the Preferences.
 * <p/>
 * @author oliver.guenther
 */
@ServiceProvider(service = UserPreferences.class)
public class UserPreferencesJdk implements UserPreferences {

    public final static String WINDOW_X = "window.x";

    public final static String WINDOW_Y = "window.y";

    public final static String WINDOW_HEIGHT = "window.height";

    public final static String WINDOW_WIDTH = "window.width";

    public final static String ACTIVE_LAF = "activeLaf";

    private final static Logger L = LoggerFactory.getLogger(UserPreferencesJdk.class);

    private boolean reset = false;

    /**
     * Returns true if UserPreferences are in reset mode.
     * <p/>
     * @return true if UserPreferences are in reset mode.
     */
    @Override
    public boolean isReset() {
        return reset;
    }

    /**
     * Sets the reset mode.
     * In this mode, a load location will not change the component, but clear the node in the preferences tree.
     * <p/>
     * @param reset true for reset mode.
     */
    @Override
    public void setReset(boolean reset) {
        this.reset = reset;
    }

    /**
     * Stores the LAF className in the Preferences Store
     * <p/>
     * @param className the className to store.
     */
    @Override
    public void storeLaf(String className) {
        if ( className == null ) return;
        Preferences p = Preferences.userNodeForPackage(LookAndFeel.class);
        p.put(ACTIVE_LAF, className);
        try {
            p.flush();
        } catch (BackingStoreException ex) {
            LoggerFactory.getLogger(UserPreferencesJdk.class).error("Cound not store Preferences", ex);
        }
    }

    /**
     * Loads the className of the LAF from the Preferences Store.
     * <p/>
     * @return the className
     */
    @Override
    public String loadLaf() {
        Preferences p = Preferences.userNodeForPackage(LookAndFeel.class);
        return p.get(ACTIVE_LAF, UIManager.getSystemLookAndFeelClassName());
    }

    /**
     * Stores the location of a component in the user preferences using the class as reference.
     * <p/>
     * @param key the key to assoisiate.
     * @param c   the component.
     */
    @Override
    public void storeLocation(Class<?> key, Component c) {
        if ( key == null || c == null ) return;
        Preferences p = Preferences.userNodeForPackage(key).node(key.getSimpleName());
        p.putInt(WINDOW_X, c.getX());
        p.putInt(WINDOW_Y, c.getY());
        p.putInt(WINDOW_HEIGHT, c.getHeight());
        p.putInt(WINDOW_WIDTH, c.getWidth());
        try {
            p.flush();
            L.debug("Stored: {}", p);
        } catch (BackingStoreException ex) {
            L.error("Cound not store Preferences", ex);
        }
    }

    // 2071-12-11 - Changed behavior. For now on the id is ignored.
    @Override
    public void loadLocation(Class<?> key, Component c) {
        if ( key == null || c == null ) return;
        try {
            if ( !Preferences.userNodeForPackage(key).nodeExists(key.getSimpleName()) ) return;
            Preferences p = Preferences.userNodeForPackage(key).node(key.getSimpleName());
            if ( reset ) {
                p.clear();
                L.info("Reset on load {} reseted", p);
            } else {
                c.setLocation(p.getInt(WINDOW_X, 100), p.getInt(WINDOW_Y, 100));
                c.setSize(p.getInt(WINDOW_WIDTH, 200), p.getInt(WINDOW_HEIGHT, 200));
                L.debug("Loaded: {}", p);
            }
        } catch (BackingStoreException ex) {
            L.error("Cound not load Preferences", ex);
        }
    }

}
