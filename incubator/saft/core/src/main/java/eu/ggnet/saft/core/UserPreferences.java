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
