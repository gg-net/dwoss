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
package eu.ggnet.saft.core.ui;

import java.awt.Component;
import java.awt.Window;

import javafx.scene.Parent;
import javafx.stage.Stage;

/**
 * Parent, that can hold either a swing or a javafx parent.
 *
 * @author oliver.guenther
 */
public class UiParent {

    /**
     * Returns a new wrapped parrent of a swing component.
     *
     * @param swingParent the swingparent to be wrapped.
     * @return a new wrapped parrent of a swing component.
     */
    public static UiParent of(Component swingParent) {
        return new UiParent(swingParent);
    }

    /**
     * Returns a new wrapped parrent of a javafx parent.
     *
     * @param javafxParent a javafxparent
     * @return a new wrapped parrent of a javafx parent
     */
    public static UiParent of(Parent javafxParent) {
        return new UiParent(javafxParent);
    }

    public static UiParent defaults() {
        // Todo: Implemnt javaFx
        return new UiParent(SwingCore.mainFrame());
    }

    private Window swingParent = null;

    private Stage javafxParent = null;

    private UiParent(Component swingParent) {
        //TODO: Implement JavaFx
        if ( swingParent instanceof Window ) this.swingParent = (Window)this.swingParent;
        else this.swingParent = SwingCore.windowAncestor(swingParent).orElse(SwingCore.mainFrame());
    }

    private UiParent(Parent javafxParent) {
        // TODO: Implement JavaFx Way
        this.swingParent = SwingCore.windowAncestor(javafxParent).orElse(SwingCore.mainFrame());
    }

    public Window getSwingParent() {
        return swingParent;
    }

    public Stage getJavafxParent() {
        throw new IllegalArgumentException("Not jet implemented");
    }

}
