/*
 * Copyright (C) 2021 GG-Net GmbH
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
package eu.ggnet.dwoss.core.widget;

import java.util.Objects;

import javafx.scene.control.MenuItem;

import eu.ggnet.dwoss.core.widget.auth.Accessable;
import eu.ggnet.dwoss.rights.api.AtomicRight;

/**
 * JavaFx Menuitem with right set.
 *
 * @author oliver.guenther
 */
public class AccessableMenuItem extends MenuItem implements Accessable {

    private final AtomicRight right;

    public AccessableMenuItem(AtomicRight right) {
        this.right = Objects.requireNonNull(right, "right must not be null");
        setText(right.description());
    }

    @Override
    public void setEnabled(boolean enable) {
        setDisable(!enable);
    }

    @Override
    public AtomicRight getNeededRight() {
        return right;
    }

    @Override
    public String toString() {
        return "AccessableMenuItem{" + "right=" + right + '}';
    }

}
