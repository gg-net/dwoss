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
import java.util.Optional;
import java.util.concurrent.CompletionException;

import jakarta.inject.Inject;

import javafx.scene.control.TextInputDialog;

import eu.ggnet.dwoss.core.common.UserInfoException;
import eu.ggnet.dwoss.core.widget.AccessableAction;
import eu.ggnet.dwoss.core.widget.dl.RemoteDl;
import eu.ggnet.dwoss.receipt.ui.ProductUiBuilder;
import eu.ggnet.dwoss.receipt.ui.product.SimpleView;
import eu.ggnet.dwoss.uniqueunit.ee.UniqueUnitAgent;
import eu.ggnet.saft.core.Saft;

import jakarta.enterprise.context.Dependent;

import static eu.ggnet.dwoss.rights.api.AtomicRight.UPDATE_PRODUCT;

/**
 * Allow the modification of a Product/Part.
 * <p/>
 * @author oliver.guenther
 */
@Dependent
public class UpdateProductAction extends AccessableAction {

    @Inject
    private Saft saft;

    @Inject
    private ProductUiBuilder productUiBuilder;

    @Inject
    private RemoteDl remote;

    public UpdateProductAction() {
        super(UPDATE_PRODUCT);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO: in a complete correct case, the manufacturer should be selectable (In Case of a two manufactures have overlapping partnos)
        saft.build().title("Bitte Artikelnummer des Herstellers eingeben").dialog()
                .eval(() -> {
                    TextInputDialog dialog = new TextInputDialog();
                    dialog.setContentText("Bitte Artikelnummer des Herstellers eingeben:");
                    return dialog;
                }).cf()
                .thenApply(partNo -> remote.lookup(UniqueUnitAgent.class).findProductByPartNo(partNo))
                .thenApply(p -> Optional.ofNullable(p).orElseThrow(() -> new CompletionException(new UserInfoException("Kein Produkt gefunden"))))
                .thenCompose(p -> productUiBuilder.createOrEditPart(() -> new SimpleView.CreateOrEdit(p.getTradeName().getManufacturer(), p.getPartNo())))
                .handle(saft.handler());
    }

}
