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

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import eu.ggnet.dwoss.redtapext.ee.DocumentSupporter;
import eu.ggnet.dwoss.util.FileJacket;
import eu.ggnet.saft.*;
import eu.ggnet.saft.core.auth.AccessableAction;

import static eu.ggnet.dwoss.rights.api.AtomicRight.EXPORT_DOSSIER_TO_XLS;

/**
 *
 * @author oliver.guenther
 */
public class ExportDossierToXlsAction extends AccessableAction {

    public ExportDossierToXlsAction() {
        super(EXPORT_DOSSIER_TO_XLS);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final String dossierId = JOptionPane.showInputDialog(UiCore.getMainFrame(), "Bitte DossierId eingeben:").trim();
        new SwingWorker<FileJacket, Object>() {
            @Override
            protected FileJacket doInBackground() throws Exception {
                return Dl.remote().lookup(DocumentSupporter.class).toXls(dossierId);
            }

            @Override
            protected void done() {
                try {
                    FileJacket fj = get();
                    if ( fj == null ) Ui.build().alert("Keine Rückgabewerte");
                    else Desktop.getDesktop().open(fj.toTemporaryFile());
                } catch (InterruptedException | ExecutionException | IOException ex) {
                    Ui.handle(ex);
                }
            }
        }.execute();
    }
}
