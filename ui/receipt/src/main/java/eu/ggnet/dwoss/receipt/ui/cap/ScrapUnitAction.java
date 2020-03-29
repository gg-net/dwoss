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

import javafx.beans.binding.Bindings;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;

import org.apache.commons.lang3.StringUtils;

import eu.ggnet.dwoss.core.widget.saft.ReplyUtil;
import eu.ggnet.dwoss.receipt.ee.UnitDestroyer;
import eu.ggnet.dwoss.core.widget.Dl;
import eu.ggnet.saft.core.Ui;
import eu.ggnet.saft.api.Reply;
import eu.ggnet.dwoss.core.widget.AccessableAction;
import eu.ggnet.dwoss.core.widget.auth.Guardian;
import eu.ggnet.saft.core.ui.AlertType;

import static eu.ggnet.dwoss.rights.api.AtomicRight.UPDATE_UNIQUE_UNIT_TO_SCRAP_UNIT;
import static javafx.scene.control.Alert.AlertType.CONFIRMATION;
import static javafx.scene.control.ButtonType.OK;

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
            }).opt()
                    .filter(s -> !StringUtils.isBlank(s))
                    .ifPresent(r -> {
                        Ui.build().dialog().eval(() -> new Alert(CONFIRMATION, "SopoNr " + r + " wirklich verschrotten ?"))
                                .opt()
                                .filter(b -> b == OK)
                                .map(u -> ReplyUtil.wrap(() -> Dl.remote().lookup(UnitDestroyer.class).verifyScarpOrDeleteAble(r)))
                                .filter(Ui.failure()::handle)
                                .map(Reply::getPayload)
                                .ifPresent(u -> {
                                    Ui.build().title("Bitte Grund angeben").dialog().eval(() -> {
                                        TextInputDialog dialog = new TextInputDialog();
                                        dialog.setContentText("Bitte Grund angeben");
                                        dialog.getDialogPane()
                                                .lookupButton(OK)
                                                .disableProperty()
                                                .bind(Bindings.createBooleanBinding(() -> dialog.getEditor().getText().trim().isEmpty(), dialog.getEditor().textProperty()));
                                        return dialog;
                                    }).opt()
                                            .filter(s -> !StringUtils.isBlank(s))
                                            .ifPresent(c -> {
                                                Dl.remote().lookup(UnitDestroyer.class).scrap(u, c, Dl.local().lookup(Guardian.class).getUsername());
                                                Ui.build().alert().message("SopoNr " + r + " ist verschrottet.").show(AlertType.INFO);
                                            });
                                });
                    });
        });
    }
}
