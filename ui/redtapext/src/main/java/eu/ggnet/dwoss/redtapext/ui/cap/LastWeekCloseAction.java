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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.core.widget.AccessableAction;
import eu.ggnet.saft.core.*;
import eu.ggnet.dwoss.core.widget.auth.Guardian;

import static eu.ggnet.dwoss.rights.api.AtomicRight.EXECUTE_MANUAL_CLOSING;
import static javafx.scene.control.Alert.AlertType.CONFIRMATION;

import eu.ggnet.dwoss.redtapext.ee.reporting.RedTapeCloserManual;

/**
 * Closes the last Week.
 *
 * @author oliver.guenther
 */
public class LastWeekCloseAction extends AccessableAction {

    private final static Logger L = LoggerFactory.getLogger(LastWeekCloseAction.class);

    public LastWeekCloseAction() {
        super(EXECUTE_MANUAL_CLOSING);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Ui.build().dialog().eval(() -> new Alert(CONFIRMATION, "Möchten Sie den manuellen Wochen/Tagesabschluss durchführen ?"))
                .cf().
                thenAcceptAsync(f -> Ui.progress().wrap(() -> Dl.remote().lookup(RedTapeCloserManual.class).executeManual(Dl.local().lookup(Guardian.class).getUsername())).run(), UiCore.getExecutor())
                .handle(Ui.handler());
    }
}
