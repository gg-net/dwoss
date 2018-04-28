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
package eu.ggnet.dwoss.price;

import eu.ggnet.dwoss.price.ee.Exporter;
import eu.ggnet.dwoss.price.ee.Importer;

import java.awt.event.ActionEvent;
import java.util.Optional;

import javafx.scene.control.TextInputDialog;

import eu.ggnet.dwoss.common.ee.ReplyUtil;
import eu.ggnet.dwoss.price.ee.engine.PriceEngineResult.Change;
import eu.ggnet.saft.Dl;
import eu.ggnet.saft.Ui;
import eu.ggnet.saft.api.Reply;
import eu.ggnet.saft.core.auth.AccessableAction;
import eu.ggnet.saft.core.auth.Guardian;
import eu.ggnet.saft.core.swing.OkCancelWrap;

import static eu.ggnet.dwoss.rights.api.AtomicRight.UPDATE_SET_UNIT_PRICE;

/**
 *
 * @author oliver.guenther
 */
public class PriceBlockerAction extends AccessableAction {

    public PriceBlockerAction() {
        super(UPDATE_SET_UNIT_PRICE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
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
                    .filter(Ui.failure()::handle)
                    .map(Reply::getPayload)
                    .ifPresent(priceEngineResult -> {
                        PriceBlockerViewCask pbp = new PriceBlockerViewCask(sopoOptional.get(), priceEngineResult.getProductDescription(), priceEngineResult.getCustomerPrice(), priceEngineResult.getRetailerPrice());

                        Ui.build().swing().eval(() -> OkCancelWrap.vetoResult(pbp)).opt().ifPresent(reply -> {

                            priceEngineResult.setCustomerPrice(pbp.getCustomerPrice());
                            priceEngineResult.setRetailerPrice(pbp.getRetailerPrice());
                            priceEngineResult.setUnitPriceFixed(Change.SET);
                            Dl.remote().lookup(Importer.class).store(priceEngineResult, "Set directly via PriceBlocker", Dl.local().lookup(Guardian.class).getUsername());

                        });
                    });

        }
        );
    }

}
