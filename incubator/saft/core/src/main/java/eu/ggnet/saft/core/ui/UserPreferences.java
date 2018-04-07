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

import javafx.stage.Window;

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
     *
     * @param key
     * @param c   the component.
     */
    void loadLocation(Class<?> key, Component c);

    /**
     * Sets the reset mode.
     * In this mode, a load location will not change the component, but clear the node in the preferences tree.
     *
     * @param reset true for reset mode.
     */
    void setReset(boolean reset);

    /**
     * Stores the location of a component in the user preferences using the class as reference.
     *
     * @param key
     * @param c   the component.
     */
    void storeLocation(Class<?> key, Component c);

    /**
     * Loads the className of the LAF from the Preferences Store.
     *
     * @return the className
     */
    String loadLaf();

    /**
     * Stores the LAF className in the Preferences Store
     *
     * @param className the className to store.
     */
    void storeLaf(String className);

    /**
     * Loads and sets the location and size on the component if existing in the store.
     *
     * @param key the key
     * @param c   the window
     */
    void loadLocation(Class<?> key, Window c);

    /**
     * Stores the location of a component in the user preferences using the class as reference.
     *
     * @param key the key
     * @param c   the component.
     */
    void storeLocation(Class<?> key, Window c);

}
