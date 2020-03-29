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

import eu.ggnet.saft.core.UiCore;
import eu.ggnet.saft.core.Ui;
import eu.ggnet.dwoss.core.widget.Dl;

import java.awt.event.ActionEvent;

import javafx.scene.control.TextInputDialog;

import eu.ggnet.dwoss.core.widget.saft.ReplyUtil;
import eu.ggnet.dwoss.receipt.ui.UiProductSupport;
import eu.ggnet.dwoss.uniqueunit.ee.UniqueUnitAgent;
import eu.ggnet.saft.api.Reply;
import eu.ggnet.dwoss.core.widget.AccessableAction;

import static eu.ggnet.dwoss.rights.api.AtomicRight.UPDATE_PRODUCT;

/**
 * Allow the modification of a Product/Part.
 * <p/>
 * @author oliver.guenther
 */
public class UpdateProductAction extends AccessableAction {

    public UpdateProductAction() {
        super(UPDATE_PRODUCT);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Ui.exec(() -> {
            Ui.build().title("Bitte Artikelnummer des Herstellers eingeben").dialog().eval(() -> {
                TextInputDialog dialog = new TextInputDialog();
                dialog.setContentText("Bitte Artikelnummer des Herstellers eingeben:");
                return dialog;
            }).opt()
                    .map(s -> ReplyUtil.wrap(() -> Dl.remote().lookup(UniqueUnitAgent.class).findProductByPartNo(s)))
                    .filter(Ui.failure()::handle)
                    .map(Reply::getPayload)
                    .map(p -> ReplyUtil.wrap(() -> UiProductSupport.createOrEditPart(p.getTradeName().getManufacturer(), p.getPartNo(), UiCore.getMainFrame())))
                    .filter(Ui.failure()::handle);
        });
    }
}
