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
package eu.ggnet.dwoss.receipt;

import java.awt.event.ActionEvent;

import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;

import eu.ggnet.dwoss.common.ReplyUtil;
import eu.ggnet.dwoss.receipt.ee.UnitDestroyer;
import eu.ggnet.saft.Dl;
import eu.ggnet.saft.Ui;
import eu.ggnet.saft.api.Reply;
import eu.ggnet.saft.core.auth.AccessableAction;
import eu.ggnet.saft.core.auth.Guardian;
import eu.ggnet.saft.core.ui.AlertType;

import static eu.ggnet.dwoss.rights.api.AtomicRight.UPDATE_UNIQUE_UNIT_TO_SCRAP_UNIT;
import static javafx.scene.control.Alert.AlertType.CONFIRMATION;

/**
 *
 * @author oliver.guenther
 */
public class ScrapUnitAction extends AccessableAction {

    public ScrapUnitAction() {
        super(UPDATE_UNIQUE_UNIT_TO_SCRAP_UNIT);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Ui.exec(() -> {
            Ui.build().title("SopoNr die verschrottet werden soll").dialog().eval(() -> {
                TextInputDialog dialog = new TextInputDialog();
                dialog.setContentText("SopoNr die verschrottet werden soll:");
                return dialog;
            }).opt().ifPresent(r -> {
                Ui.build().dialog().eval(() -> new Alert(CONFIRMATION, "SopoNr " + r + " wirklich verschrotten ?"))
                        .opt()
                        .ifPresent(rr -> {
                            Ui.build().title("Bitte Grund angeben").dialog().eval(() -> {
                                TextInputDialog dialog = new TextInputDialog();
                                dialog.setContentText("Bitte Grund angeben");
                                return dialog;
                            })
                                    .opt()
                                    .map(s -> ReplyUtil.wrap(() -> Dl.remote().lookup(UnitDestroyer.class).verifyScarpOrDeleteAble(r)))
                                    .filter(Ui.failure()::handle)
                                    .map(Reply::getPayload)
                                    .ifPresent(u -> {
                                        Dl.remote().lookup(UnitDestroyer.class).scrap(u, r, Dl.local().lookup(Guardian.class).getUsername());
                                        Ui.build().alert().message("SopoNr " + r + " ist verschrottet.").show(AlertType.INFO);
                                    });

                        });

            });
        });
    }
}
