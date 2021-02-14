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
package eu.ggnet.dwoss.receipt.ui;

import javafx.stage.Modality;

import eu.ggnet.dwoss.core.widget.Dl;
import eu.ggnet.dwoss.core.widget.saft.OkCancelWrap;
import eu.ggnet.dwoss.receipt.ee.ProductProcessor;
import eu.ggnet.dwoss.receipt.ee.ProductProcessor.SpecAndModel;
import eu.ggnet.dwoss.receipt.ui.product.AbstractView;
import eu.ggnet.dwoss.receipt.ui.product.SimpleView;
import eu.ggnet.saft.core.Ui;
import eu.ggnet.saft.core.ui.UiParent;

/**
 * Support Class for creation or edit of Products.
 * Not perfect, but a simple step to cleanup the ReceiptController.
 * <p/>
 * @author oliver.guenther
 */
public class UiProductSupport {

    public static void createOrEditPart(SimpleView.CreateOrEdit in, UiParent parent) {
        Ui.build().parent(parent).modality(Modality.WINDOW_MODAL)
                .swing().eval(() -> in, SimpleView.class).cf()
                .thenCompose((SpecAndModel sam) -> Ui.build().parent(parent).title("Artikeldetailconfiguration").modality(Modality.WINDOW_MODAL)
                .swing().eval(() -> sam, () -> OkCancelWrap.consumerVetoResult(AbstractView.newView(sam))).cf())
                .thenAccept((SpecAndModel sam) -> {
                    if ( sam.spec().getId() > 0 ) Dl.remote().lookup(ProductProcessor.class).update(sam);
                    else Dl.remote().lookup(ProductProcessor.class).create(sam);
                });
    }

    public static void createOrEditPart(SimpleView.CreateOrEdit in) {
        Ui.build().modality(Modality.WINDOW_MODAL)
                .swing().eval(() -> in, SimpleView.class).cf()
                .thenCompose((SpecAndModel sam) -> Ui.build().title("Artikeldetailconfiguration").modality(Modality.WINDOW_MODAL)
                .swing().eval(() -> sam, () -> OkCancelWrap.consumerVetoResult(AbstractView.newView(sam))).cf())
                .thenAccept((SpecAndModel sam) -> {
                    if ( sam.spec().getId() > 0 ) Dl.remote().lookup(ProductProcessor.class).update(sam);
                    else Dl.remote().lookup(ProductProcessor.class).create(sam);
                });
    }

}
