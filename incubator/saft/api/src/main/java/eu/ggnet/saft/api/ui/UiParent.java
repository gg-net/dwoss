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
package eu.ggnet.saft.api.ui;

import java.awt.Component;

import javafx.scene.Parent;

import lombok.Getter;

/**
 * Parent, that can hold either a swing or a javafx parent.
 *
 * @author oliver.guenther
 */
@Getter
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

    private Component swingParent = null;

    private Parent javafxParent = null;

    private UiParent(Component swingParent) {
        this.swingParent = swingParent;
    }

    private UiParent(Parent javafxParent) {
        this.javafxParent = javafxParent;
    }

}
