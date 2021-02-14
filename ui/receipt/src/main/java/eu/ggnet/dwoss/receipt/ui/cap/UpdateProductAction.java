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
import java.util.concurrent.CompletionException;

import javafx.scene.control.TextInputDialog;

import eu.ggnet.dwoss.core.common.values.tradename.TradeName;
import eu.ggnet.dwoss.core.widget.AccessableAction;
import eu.ggnet.dwoss.core.widget.Dl;
import eu.ggnet.dwoss.core.widget.saft.*;
import eu.ggnet.dwoss.receipt.ui.UiProductSupport;
import eu.ggnet.dwoss.receipt.ui.product.SimpleView;
import eu.ggnet.dwoss.uniqueunit.ee.UniqueUnitAgent;
import eu.ggnet.saft.core.Ui;
import eu.ggnet.saft.core.UiUtil;

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
            // TODO: in a complete correct case, the manufacturer should be selectable (In Case of a two manufactures have overlapping partnos)
            Ui.build().title("Bitte Artikelnummer des Herstellers eingeben").dialog().eval(() -> {
                TextInputDialog dialog = new TextInputDialog();
                dialog.setContentText("Bitte Artikelnummer des Herstellers eingeben:");
                return dialog;
            }).opt()
                    .map(s -> ReplyUtil.wrap(() -> Dl.remote().lookup(UniqueUnitAgent.class).findProductByPartNo(s)))
                    .filter(Failure::handle)
                    .map(Reply::getPayload)
                    .ifPresent(p -> editPart(p.getTradeName().getManufacturer(), p.getPartNo()));
        });
    }

    // Only here to be used in the tryout
    public static void editPart(TradeName manufacturer, String partNo) throws CompletionException {
        UiUtil.exceptionRun(() -> UiProductSupport.createOrEditPart(new SimpleView.CreateOrEdit(manufacturer, partNo)));
    }
}
