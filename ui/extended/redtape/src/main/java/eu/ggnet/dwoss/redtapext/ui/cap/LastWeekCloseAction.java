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

import javafx.scene.control.Alert;

import org.openide.util.Lookup;

import eu.ggnet.dwoss.redtapext.ee.reporting.RedTapeCloser;
import eu.ggnet.saft.core.Dl;
import eu.ggnet.saft.core.Ui;
import eu.ggnet.dwoss.common.ui.AccessableAction;
import eu.ggnet.saft.experimental.auth.Guardian;

import static eu.ggnet.dwoss.rights.api.AtomicRight.EXECUTE_MANUAL_CLOSING;
import static javafx.scene.control.Alert.AlertType.CONFIRMATION;
import static javafx.scene.control.ButtonType.OK;

/**
 * Closes the last Week.
 *
 * @author oliver.guenther
 */
public class LastWeekCloseAction extends AccessableAction {

    public LastWeekCloseAction() {
        super(EXECUTE_MANUAL_CLOSING);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Ui.exec(() -> {
            Ui.build().dialog().eval(() -> new Alert(CONFIRMATION, "Möchten Sie den manuellen Wochen/Tagesabschluss durchführen ?"))
                    .opt().filter(b -> b == OK)
                    .map(f -> Ui.progress().wrap(() -> Dl.remote().lookup(RedTapeCloser.class).executeManual(Lookup.getDefault().lookup(Guardian.class).getUsername())));

        });

    }
}
