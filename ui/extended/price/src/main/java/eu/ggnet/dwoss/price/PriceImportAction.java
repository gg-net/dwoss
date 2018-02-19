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
import eu.ggnet.dwoss.util.*;
import eu.ggnet.saft.Dl;
import eu.ggnet.saft.Ui;
import eu.ggnet.saft.api.Reply;
import eu.ggnet.saft.core.auth.AccessableAction;
import eu.ggnet.saft.core.auth.Guardian;
import eu.ggnet.saft.core.ui.AlertType;

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
            Ui.fileChooser().open().opt()
                    .ifPresent(f -> {
                        Ui.build().dialog().eval(() -> new Alert(CONFIRMATION, "PriceManagment: " + f.getPath() + " importieren ?"))
                                .opt()
                                .filter(b -> b == OK)
                                .map(b -> TikaUtil.isExcel(f))
                                .filter(Ui.failure()::handle)
                                .map(Reply::getPayload)
                                .map(ff -> {
                                    try {
                                        Dl.remote().lookup(Importer.class).fromXls(new FileJacket("in", ".xls", ff), Dl.local().lookup(Guardian.class).getUsername());
                                        return false;
                                    } catch (UserInfoException ex) {
                                        Ui.exec(() -> {
                                            Ui.build().alert().message("Lesefehler: " + ex.getMessage()).show(AlertType.WARNING);
                                        });
                                        return false;
                                    }
                                });
                    });
        });
    }
}
