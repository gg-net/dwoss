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
package eu.ggnet.dwoss.redtapext.ui.cap;

import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;

import eu.ggnet.dwoss.redtapext.ui.cao.RedTapeView;
import eu.ggnet.saft.core.UiCore;

import jakarta.enterprise.context.Dependent;

/**
 *
 * @author pascal.perau
 */
@Dependent
public class RedTapeMenuItem extends MenuItem {

    public RedTapeMenuItem() {
        super("Kunden und Aufträge verwalten", new ImageView(CapRes.smallIcon().toExternalForm()));
        setOnAction(e -> UiCore.global().showOnce(RedTapeView.ONCE_KEY));
    }

}
