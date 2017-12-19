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

import eu.ggnet.dwoss.redtape.reporting.RedTapeCloser;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import org.openide.util.Lookup;

import eu.ggnet.saft.Ui;
import eu.ggnet.saft.core.ui.Workspace;
import eu.ggnet.saft.core.auth.AccessableAction;
import eu.ggnet.saft.core.auth.Guardian;

import static eu.ggnet.dwoss.rights.api.AtomicRight.EXECUTE_MANUAL_CLOSING;
import static eu.ggnet.saft.Client.lookup;

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
        final RedTapeCloser closer = lookup(RedTapeCloser.class);
        if ( JOptionPane.YES_OPTION != JOptionPane.showConfirmDialog(lookup(Workspace.class).getMainFrame(), "Möchten Sie den manuellen Wochen/Tagesabschluss durchführen ?",
                "Wochen-/Tagesabschluss", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) ) return;

        Ui.exec(Ui.progress().wrap(() -> closer.executeManual(Lookup.getDefault().lookup(Guardian.class).getUsername())));

    }
}
