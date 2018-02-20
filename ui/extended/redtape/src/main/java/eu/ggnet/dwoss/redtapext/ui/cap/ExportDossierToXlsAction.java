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
import java.io.File;

import javafx.scene.control.TextInputDialog;

import eu.ggnet.dwoss.redtapext.ee.DocumentSupporter;
import eu.ggnet.saft.Dl;
import eu.ggnet.saft.Ui;
import eu.ggnet.saft.core.auth.AccessableAction;
import eu.ggnet.saft.core.ui.AlertType;

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
        Ui.exec(() -> {
            Ui.build().title("Bitte DossierId eingeben").dialog().eval(() -> {
                TextInputDialog dialog = new TextInputDialog();
                dialog.setContentText("Bitte DossierId eingeben:");
                return dialog;
            }).opt().filter(r -> {
                try {
                    File toTemporaryFile = Ui.progress().title("Dossier: " + r).call(Dl.remote().lookup(DocumentSupporter.class).toXls(r)::toTemporaryFile);
                    if ( toTemporaryFile == null ) {
                        Ui.build().alert().message("Dossier " + r + " existiert nicht").show(AlertType.WARNING);
                        return false;
                    }
                    Ui.osOpen(toTemporaryFile);
                    return false;
                } catch (NullPointerException ex) {
                    Ui.handle(ex);
                    return false;
                }
            });
        });
    }
}
