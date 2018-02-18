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

import eu.ggnet.dwoss.common.ReplyUtil;
import eu.ggnet.dwoss.util.FileJacket;
import eu.ggnet.dwoss.util.TikaUtil;
import eu.ggnet.saft.Dl;
import eu.ggnet.saft.Ui;
import eu.ggnet.saft.api.Reply;
import eu.ggnet.saft.core.auth.AccessableAction;
import eu.ggnet.saft.core.auth.Guardian;

import static eu.ggnet.dwoss.rights.api.AtomicRight.IMPORT_PRICEMANGMENT;
import static javafx.scene.control.Alert.AlertType.CONFIRMATION;
import static javafx.scene.control.ButtonType.OK;

/**
 *
 * @author pascal.perau
 */
public class PriceImportAction extends AccessableAction {

    public PriceImportAction() {
        super(IMPORT_PRICEMANGMENT);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Ui.exec(() -> {
            Optional<File> inFile = Ui.fileChooser().open().opt();
            if ( !inFile.isPresent() ) return;
            Ui.build().dialog().eval(() -> new Alert(CONFIRMATION, "PriceManagment: " + inFile.get().getPath() + " importieren ?"))
                    .opt()
                    .filter(b -> b == OK)
                    .map(b -> TikaUtil.isExcel(inFile.get()))
                    .filter(Ui.failure()::handle)
                    .map(Reply::getPayload)
                    .map(f -> ReplyUtil.wrap(() -> Dl.remote().lookup(Importer.class).fromXls(new FileJacket("in", ".xls", f), Dl.local().lookup(Guardian.class).getUsername())
            )).filter(Ui.failure()::handle)
                    .isPresent();

        });

    }
}
