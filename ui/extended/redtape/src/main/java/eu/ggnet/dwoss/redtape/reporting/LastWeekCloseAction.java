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
package eu.ggnet.dwoss.redtape.reporting;

import java.awt.event.ActionEvent;
import java.util.concurrent.ExecutionException;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import org.openide.util.Lookup;

import eu.ggnet.saft.core.Workspace;
import eu.ggnet.saft.core.authorisation.Guardian;

import eu.ggnet.dwoss.redtape.reporting.RedTapeCloser;

import eu.ggnet.saft.core.authorisation.AccessableAction;

import eu.ggnet.dwoss.common.ExceptionUtil;

import static eu.ggnet.saft.core.Client.lookup;
import static eu.ggnet.dwoss.rights.api.AtomicRight.EXECUTE_MANUAL_CLOSING;

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
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                closer.executeManual(Lookup.getDefault().lookup(Guardian.class).getUsername());
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                } catch (InterruptedException | ExecutionException e) {
                    ExceptionUtil.show(lookup(Workspace.class).getMainFrame(), e);
                }
            }
        }.execute();

    }
}
