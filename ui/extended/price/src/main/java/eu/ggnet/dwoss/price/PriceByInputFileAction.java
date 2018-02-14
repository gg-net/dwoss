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
package eu.ggnet.dwoss.price;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Optional;

import javafx.scene.control.Alert;

import eu.ggnet.dwoss.util.*;
import eu.ggnet.saft.Dl;
import eu.ggnet.saft.Ui;
import eu.ggnet.saft.api.Reply;
import eu.ggnet.saft.core.auth.AccessableAction;

import static eu.ggnet.dwoss.rights.api.AtomicRight.IMPORT_PRICE_BY_XLS;
import static javafx.scene.control.Alert.AlertType.CONFIRMATION;
import static javafx.scene.control.ButtonType.OK;

/**
 * Action to create an XLS Report of possible sales, initiated by an XLS File containing only partNo.
 * <p/>
 * @author pascal.perau
 */
public class PriceByInputFileAction extends AccessableAction {

    public PriceByInputFileAction() {
        super(IMPORT_PRICE_BY_XLS);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Ui.exec(() -> {
            Optional<File> inFile = Ui.fileChooser().open();
            if ( !inFile.isPresent() ) return;
            Ui.build().dialog().eval(() -> new Alert(CONFIRMATION, "Xls Datei " + inFile.get().getPath() + " als Eingabequelle verwenden ? (erste Zeile = Überschrift, erste Spalte enthält Artikelnummern) Preise erzeugen nach Referencedaten"))
                    .filter(b -> b == OK)
                    .map(b -> TikaUtil.isExcel(inFile.get()))
                    .filter(Ui.failure()::handle)
                    .map(Reply::getPayload)
                    .map(f -> Ui.progress().call(() -> Dl.remote().lookup(Exporter.class).toXlsByXls(new FileJacket("in", ".xls", f))))
                    .ifPresent(r -> Ui.osOpen(r.toTemporaryFile()));

        });

    }

}
