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
package eu.ggnet.dwoss.receipt.ui.cap;

import java.awt.event.ActionEvent;

import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;

import org.apache.commons.lang3.StringUtils;

import eu.ggnet.dwoss.common.ee.ReplyUtil;
import eu.ggnet.dwoss.receipt.ee.UnitDestroyer;
import eu.ggnet.saft.core.Dl;
import eu.ggnet.saft.core.Ui;
import eu.ggnet.saft.api.Reply;
import eu.ggnet.dwoss.common.ui.AccessableAction;
import eu.ggnet.saft.experimental.auth.Guardian;
import eu.ggnet.saft.core.ui.AlertType;

import static eu.ggnet.dwoss.rights.api.AtomicRight.DELETE_UNIQUE_UNIT;
import static javafx.scene.control.Alert.AlertType.CONFIRMATION;

/**
 * Delete a Unit from the Database.
 * <p/>
 * @author oliver.guenther
 */
public class DeleteUnitAction extends AccessableAction {

    public DeleteUnitAction() {
        super(DELETE_UNIQUE_UNIT);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Ui.exec(() -> {
            Ui.build().title("SopoNr die gelöscht werden soll").dialog().eval(() -> {
                TextInputDialog dialog = new TextInputDialog();
                dialog.setContentText("SopoNr die gelöscht werden soll:");
                return dialog;
            }).opt()
                    .filter(s -> !StringUtils.isBlank(s))
                    .ifPresent(r -> {
                        Ui.build().dialog().eval(() -> new Alert(CONFIRMATION, "SopoNr " + r + " wirklich gelöschen ?"))
                                .opt()
                                .map(s -> ReplyUtil.wrap(() -> Dl.remote().lookup(UnitDestroyer.class).verifyScarpOrDeleteAble(r)))
                                .filter(Ui.failure()::handle)
                                .map(Reply::getPayload)
                                .ifPresent(u -> {
                                    Dl.remote().lookup(UnitDestroyer.class).delete(u, "Löschung aus UI", Dl.local().lookup(Guardian.class).getUsername());
                                    Ui.build().alert().message("SopoNr " + r + " ist gelöscht.").show(AlertType.INFO);
                                });
                    });
        });
    }
}
