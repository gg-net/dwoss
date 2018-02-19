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
package eu.ggnet.dwoss.receipt.product;

import java.awt.event.ActionEvent;

import javafx.scene.control.TextInputDialog;

import eu.ggnet.dwoss.common.ReplyUtil;
import eu.ggnet.dwoss.receipt.UiProductSupport;
import eu.ggnet.dwoss.uniqueunit.ee.UniqueUnitAgent;
import eu.ggnet.dwoss.uniqueunit.ee.entity.Product;
import eu.ggnet.saft.*;
import eu.ggnet.saft.core.auth.AccessableAction;
import eu.ggnet.saft.core.ui.AlertType;

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
            }).opt().filter(r -> {
                Product product = Dl.remote().lookup(UniqueUnitAgent.class).findProductByPartNo(r);
                if(product == null){
                    Ui.build().alert().message("Artikel " + r + " existiert nicht, bitte über Aufnahme erfassen").show(AlertType.WARNING);
                    return false;
                }
                ReplyUtil.wrap(() -> new UiProductSupport().createOrEditPart(product.getTradeName().getManufacturer(), r, UiCore.getMainFrame()));
                return false;
            });
        });
    }
}
