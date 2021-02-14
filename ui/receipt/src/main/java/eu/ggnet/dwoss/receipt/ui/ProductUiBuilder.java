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

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import javafx.stage.Modality;

import eu.ggnet.dwoss.core.widget.dl.RemoteDl;
import eu.ggnet.dwoss.core.widget.saft.OkCancelWrap;
import eu.ggnet.dwoss.receipt.ee.ProductProcessor;
import eu.ggnet.dwoss.receipt.ee.ProductProcessor.SpecAndModel;
import eu.ggnet.dwoss.receipt.ui.product.AbstractView;
import eu.ggnet.dwoss.receipt.ui.product.SimpleView;
import eu.ggnet.dwoss.spec.ee.entity.ProductSpec;
import eu.ggnet.saft.core.Saft;
import eu.ggnet.saft.core.ui.UiParent;

/**
 * Support Class for creation or edit of Products.
 * Not perfect, but a simple step to cleanup the ReceiptController.
 * <p/>
 * @author oliver.guenther
 */
public class ProductUiBuilder {

    @Inject
    private Instance<Object> instance;

    @Inject
    private Saft saft;

    @Inject
    private RemoteDl remote;

    public CompletableFuture<ProductSpec> createOrEditPart(Callable<SimpleView.CreateOrEdit> inCall, UiParent parent) {
        return saft.build().parent(parent).modality(Modality.WINDOW_MODAL)
                .swing().eval(inCall, SimpleView.class).cf()
                .thenCompose((SpecAndModel sam) -> saft.build().parent(parent).title("Artikeldetailconfiguration").modality(Modality.WINDOW_MODAL)
                .swing().eval(() -> sam, () -> OkCancelWrap.consumerVetoResult(instance.select(AbstractView.selectView(sam)).get())).cf())
                .thenApply((SpecAndModel sam) -> {
                    if ( sam.spec().getId() > 0 ) return remote.lookup(ProductProcessor.class).update(sam);
                    else return remote.lookup(ProductProcessor.class).create(sam);
                });
    }

    public CompletableFuture<ProductSpec> createOrEditPart(Callable<SimpleView.CreateOrEdit> inCall) {
        return saft.build().modality(Modality.WINDOW_MODAL)
                .swing().eval(inCall, SimpleView.class).cf()
                .thenCompose((SpecAndModel sam) -> saft.build().title("Artikeldetailconfiguration").modality(Modality.WINDOW_MODAL)
                .swing().eval(() -> sam, () -> OkCancelWrap.consumerVetoResult(instance.select(AbstractView.selectView(sam)).get())).cf())
                .thenApply((SpecAndModel sam) -> {
                    if ( sam.spec().getId() > 0 ) return remote.lookup(ProductProcessor.class).update(sam);
                    else return remote.lookup(ProductProcessor.class).create(sam);
                });
    }

}
