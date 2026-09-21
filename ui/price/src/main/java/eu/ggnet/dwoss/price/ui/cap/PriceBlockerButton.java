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
package eu.ggnet.dwoss.price.ui.cap;

import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.Optional;

import javafx.scene.control.*;
import javafx.scene.image.ImageView;

import eu.ggnet.dwoss.core.widget.Dl;
import eu.ggnet.dwoss.core.widget.auth.Guardian;
import eu.ggnet.dwoss.core.widget.saft.*;
import eu.ggnet.dwoss.price.ee.Exporter;
import eu.ggnet.dwoss.price.ee.Importer;
import eu.ggnet.dwoss.price.ee.engine.PriceEngineResult.Change;
import eu.ggnet.dwoss.price.ui.PriceBlockerViewCask;
import eu.ggnet.saft.core.Ui;

import jakarta.enterprise.context.Dependent;

/**
 *
 * @author oliver.guenther
 */
@Dependent
public class PriceBlockerButton extends Button {

    public PriceBlockerButton() {
        super(null, new ImageView(PriceBlockerButton.class.getResource("PriceBlockerButton_Large.png").toExternalForm()));
        Tooltip tip = new Tooltip("Setzen eines einzelnen Preises");
        Tooltip.install(this, tip);
        setOnAction(e -> {
            Ui.exec(() -> {

                Optional<String> sopoOptional = Ui.build().dialog().eval(
                        () -> {
                            TextInputDialog dialog = new TextInputDialog();
                            dialog.setTitle("SopoNr Eingabe");
                            dialog.setHeaderText("SopoNr:");
                            dialog.setContentText("Bitte SopoNr zur Fixierung eines Preises eingeben:");
                            return dialog;
                        })
                        .opt();

                sopoOptional.map(sopoNr -> ReplyUtil.wrap(() -> Dl.remote().lookup(Exporter.class).load(sopoNr)))
                        .filter(Failure::handle)
                        .map(Reply::getPayload)
                        .ifPresent(priceEngineResult -> {
                            Ui.build().swing().eval(() -> OkCancelWrap.vetoResult(new PriceBlockerViewCask(sopoOptional.get(), priceEngineResult.getProductDescription(), priceEngineResult.getCustomerPrice(), priceEngineResult.getRetailerPrice()))).opt()
                                    .ifPresent(payload -> {
                                        priceEngineResult.setCustomerPrice(payload.customerPrice);
                                        priceEngineResult.setRetailerPrice(payload.retailerPrice);
                                        priceEngineResult.setUnitPriceFixed(Change.SET);
                                        Dl.remote().lookup(Importer.class).store(priceEngineResult, "Set directly via PriceBlocker", Dl.local().lookup(Guardian.class).getUsername());

                                    });
                        });

            }
            );
        });

    }

}
