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

import java.net.URL;

import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;

import eu.ggnet.dwoss.redtapext.ui.cao.RedTapeController;
import eu.ggnet.saft.core.Ui;


/**
 * Button to start RedTape form the Toolbar.
 * 
 * @author pascal.perau
 */
public class RedTapeToolbarButton extends Button {

    public RedTapeToolbarButton() {
        super(null, new ImageView(loadLargeIcon().toExternalForm()));
        Tooltip tip = new Tooltip("Öffnet das Kunden und Auftragsmanagement");
        Tooltip.install(this, tip);
        setOnAction(e -> Ui.build().swing().show(() -> RedTapeController.build().getView()));
    }

    static URL loadLargeIcon() {
        return RedTapeToolbarButton.class.getResource("RedTapeActionIcon_Large.png");
    }
}
