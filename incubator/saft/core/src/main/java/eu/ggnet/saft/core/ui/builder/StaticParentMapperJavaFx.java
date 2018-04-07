/*
 * Copyright (C) 2018 GG-Net GmbH
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
package eu.ggnet.saft.core.ui.builder;

import java.awt.Component;
import java.util.*;

import javafx.embed.swing.SwingNode;
import javafx.stage.Stage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class, that hold references
 *
 * @author oliver.guenther
 */
public class StaticParentMapperJavaFx {

    private final static Logger L = LoggerFactory.getLogger(StaticParentMapperJavaFx.class);

    private final static Map<Component, SwingNode> JAVAFX_PARENT_HELPER = new HashMap<>();

    public static void map(Component c, SwingNode n) {
        JAVAFX_PARENT_HELPER.put(c, n);
    }

    /**
     * Returns the Stage containing the swingnode with the component or null if not found.
     *
     * @param c the component
     * @return the stage or null
     */
    // TODO: Look into the future if we need the stage or can use the window.
    public static Stage find(Component c) {
        L.debug("find({})", c);
        SwingNode sn = deepfind(Objects.requireNonNull(c, "Component for find is null"));
        if ( sn == null ) return null;
        javafx.stage.Window window = sn.getScene().getWindow();
        if ( window instanceof Stage ) return (Stage)window;
        return null;
    }

    private static SwingNode deepfind(Component c) {
        L.debug("deep({})", c);
        if ( c == null ) return null;
        if ( JAVAFX_PARENT_HELPER.containsKey(c) ) return JAVAFX_PARENT_HELPER.get(c);
        return deepfind(c.getParent());
    }

}
