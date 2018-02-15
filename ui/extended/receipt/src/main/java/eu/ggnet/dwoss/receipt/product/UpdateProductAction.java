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

import eu.ggnet.dwoss.common.ReplyUtil;
import eu.ggnet.dwoss.receipt.UiProductSupport;
import eu.ggnet.dwoss.spec.entity.ProductSpec;
import eu.ggnet.dwoss.uniqueunit.UniqueUnitAgent;
import eu.ggnet.dwoss.uniqueunit.entity.Product;
import eu.ggnet.saft.*;
import eu.ggnet.saft.api.Reply;
import eu.ggnet.saft.core.auth.AccessableAction;
import eu.ggnet.saft.core.ui.AlertType;

import static eu.ggnet.dwoss.rights.api.AtomicRight.UPDATE_PRODUCT;
import static javax.swing.JOptionPane.showInputDialog;

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

        String partNo = showInputDialog(UiCore.getMainFrame(), "Bitte Artikelnummer des Herstellers eingeben:");
        if ( partNo == null || partNo.isEmpty() ) return;
        try {
            Product product = Dl.remote().lookup(UniqueUnitAgent.class).findProductByPartNo(partNo);
            ReplyUtil.wrap(() -> new UiProductSupport().createOrEditPart(product.getTradeName().getManufacturer(), partNo, UiCore.getMainFrame() ));           
        } catch (NullPointerException ex) {
            Ui.exec(() -> {
                Ui.build().alert().message("Artikel " + partNo + " existiert nicht, bitte über Aufnahme erfassen").show(AlertType.WARNING);
            });
        }
    }
}
