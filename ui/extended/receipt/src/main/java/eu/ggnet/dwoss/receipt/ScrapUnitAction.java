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

import org.apache.commons.lang3.StringUtils;

import eu.ggnet.dwoss.common.ReplyUtil;
import eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnit;
import eu.ggnet.saft.*;
import eu.ggnet.saft.core.auth.AccessableAction;
import eu.ggnet.saft.core.auth.Guardian;
import eu.ggnet.saft.core.ui.AlertType;

import static eu.ggnet.dwoss.rights.api.AtomicRight.UPDATE_UNIQUE_UNIT_TO_SCRAP_UNIT;
import static javafx.scene.control.Alert.AlertType.CONFIRMATION;
import static javafx.scene.control.ButtonType.OK;
import static javax.swing.JOptionPane.showInputDialog;

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
        UnitDestroyer destroyer = Dl.remote().lookup(UnitDestroyer.class);
        String refurbishedId = showInputDialog(UiCore.getMainFrame(), "SopoNr die verschrottet werden soll:");
        if ( StringUtils.isBlank(refurbishedId) ) return;
        try {
            UniqueUnit uniqueUnit = ReplyUtil.wrap(() -> destroyer.verifyScarpOrDeleteAble(refurbishedId)).getPayload();
            Ui.exec(() -> {
                Ui.build().dialog().eval(() -> new Alert(CONFIRMATION, "SopoNr " + refurbishedId + " wirklich verschrotten ?"))
                        .opt()
                        .filter(b -> b == OK)
                        .ifPresent(r -> {
                            String reason = showInputDialog(UiCore.getMainFrame(), "Bitte Grund angeben");
                            if ( reason == null || reason.isEmpty() ) return;
                            destroyer.scrap(uniqueUnit, reason, Dl.remote().lookup(Guardian.class).getUsername());
                            Ui.build().alert().message("SopoNr " + refurbishedId + " ist verschrottet.").show(AlertType.INFO);
                        });
            });
        } catch (NullPointerException ex) {
            Ui.exec(() -> {
                Ui.build().alert().message("Kein Ergebins für SopoNr: " + refurbishedId).show(AlertType.WARNING);
            });
        }

    }
}
