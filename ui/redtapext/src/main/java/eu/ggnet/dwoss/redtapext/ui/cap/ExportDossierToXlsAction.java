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

import javafx.beans.binding.Bindings;
import javafx.scene.control.TextInputDialog;

import eu.ggnet.dwoss.core.widget.*;
import eu.ggnet.dwoss.redtapext.ee.DocumentSupporter;
import eu.ggnet.saft.core.Ui;

import jakarta.enterprise.context.Dependent;

import static eu.ggnet.dwoss.rights.api.AtomicRight.EXPORT_DOSSIER_TO_XLS;
import static javafx.scene.control.ButtonType.OK;

/**
 *
 * @author oliver.guenther
 */
@Dependent
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
                dialog.getDialogPane()
                        .lookupButton(OK)
                        .disableProperty()
                        .bind(Bindings.createBooleanBinding(() -> dialog.getEditor().getText().trim().isEmpty(), dialog.getEditor().textProperty()));
                return dialog;
            }).opt().ifPresent(r -> FileUtil.osOpen(Dl.remote().lookup(DocumentSupporter.class).toXls(r).toTemporaryFile()));
        });
    }
}
