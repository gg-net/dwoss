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
package eu.ggnet.dwoss.price.ui.cap;

import java.awt.event.ActionEvent;

import javafx.scene.control.Alert;

import eu.ggnet.dwoss.core.common.FileJacket;
import eu.ggnet.dwoss.core.common.UserInfoException;
import eu.ggnet.dwoss.core.widget.*;
import eu.ggnet.dwoss.core.widget.auth.Guardian;
import eu.ggnet.dwoss.core.widget.saft.Failure;
import eu.ggnet.dwoss.price.ee.Importer;
import eu.ggnet.dwoss.core.widget.saft.Reply;
import eu.ggnet.saft.core.Ui;

import jakarta.enterprise.context.Dependent;

import static eu.ggnet.dwoss.rights.api.AtomicRight.IMPORT_PRICEMANGMENT;
import static javafx.scene.control.Alert.AlertType.CONFIRMATION;
import static javafx.scene.control.ButtonType.OK;

/**
 *
 * @author pascal.perau
 */
@Dependent
public class PriceImportAction extends AccessableAction {

    public PriceImportAction() {
        super(IMPORT_PRICEMANGMENT);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Ui.exec(() -> {
            FileUtil.open(null).opt()
                    .ifPresent(f -> {
                        Ui.build().dialog().eval(() -> new Alert(CONFIRMATION, "PriceManagment: " + f.getPath() + " importieren ?"))
                                .opt()
                                .filter(b -> b == OK)
                                .map(b -> TikaUtil.isExcel(f))
                                .filter(Failure::handle)
                                .map(Reply::getPayload)
                                .ifPresent(f2 -> {
                                    try {
                                        Dl.remote().lookup(Importer.class).fromXls(new FileJacket("in", ".xls", f2), Dl.local().lookup(Guardian.class).getUsername());
                                    } catch (UserInfoException ex) {
                                        Ui.handle(ex);
                                    }
                                });
                    });
        });
    }
}
