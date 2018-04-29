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

import java.awt.event.ActionEvent;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import eu.ggnet.dwoss.redtapext.ui.cao.RedTapeController;
import eu.ggnet.saft.core.Ui;

import static javax.swing.Action.SMALL_ICON;

/**
 *
 * @author pascal.perau
 */
public class RedTapeAction extends AbstractAction {

    @SuppressWarnings("OverridableMethodCallInConstructor")
    public RedTapeAction() {
        super("Kunden und Aufträge verwalten");
        putValue(SMALL_ICON, new ImageIcon(loadSmallIcon()));
        putValue(LARGE_ICON_KEY, new ImageIcon(loadLargeIcon()));
        putValue(SHORT_DESCRIPTION, "Öffnet Kunden und Aufträge");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Ui.exec(() -> {
            Ui.build().swing().show(() -> RedTapeController.build().getView());
        });
    }

    static URL loadSmallIcon() {
        return RedTapeAction.class.getResource("RedTapeActionIcon_Small.png");
    }

    static URL loadLargeIcon() {
        return RedTapeAction.class.getResource("RedTapeActionIcon_Large.png");
    }
}
