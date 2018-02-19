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

import eu.ggnet.dwoss.receipt.ee.UnitDestroyer;

import java.awt.event.ActionEvent;
import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;

import eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnit;
import eu.ggnet.dwoss.util.UserInfoException;
import eu.ggnet.saft.*;
import eu.ggnet.saft.core.auth.AccessableAction;
import eu.ggnet.saft.core.auth.Guardian;
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
            }).opt().filter(r -> {
                try {
                    UniqueUnit uniqueUnit = Dl.remote().lookup(UnitDestroyer.class).verifyScarpOrDeleteAble(r);
                    if ( uniqueUnit == null ) {
                        Ui.build().alert().message("Kein Ergebins für SopoNr: " + r).show(AlertType.WARNING);
                        return false;
                    }
                    Ui.build().dialog().eval(() -> new Alert(CONFIRMATION, "SopoNr " + r + " wirklich verschrotten ?"))
                            .opt()
                            .filter(b -> b == OK)
                            .ifPresent(rr -> {
                                Ui.build().title("Bitte Grund angeben").dialog().eval(() -> {
                                    TextInputDialog dialog = new TextInputDialog();
                                    dialog.setContentText("Bitte Grund angeben");
                                    return dialog;
                                }).opt().filter(s -> {
                                    Dl.remote().lookup(UnitDestroyer.class).scrap(uniqueUnit, s, Dl.local().lookup(Guardian.class).getUsername());
                                    Ui.build().alert().message("SopoNr " + r + " ist verschrottet.").show(AlertType.INFO);
                                    return false;
                                });
                            });
                    return false;
                } catch (UserInfoException ex) {
                    Ui.exec(() -> {
                        Ui.build().alert().message("Kein Ergebins für SopoNr: " + ex.getMessage()).show(AlertType.WARNING);
                    });
                    return false;
                }
            });
        });
    }
}
