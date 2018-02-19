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

import java.awt.event.ActionEvent;

import javafx.scene.control.TextInputDialog;

import eu.ggnet.dwoss.price.engine.PriceEngineResult;
import eu.ggnet.dwoss.price.engine.PriceEngineResult.Change;
import eu.ggnet.dwoss.util.UserInfoException;
import eu.ggnet.saft.Dl;
import eu.ggnet.saft.Ui;
import eu.ggnet.saft.api.Reply;
import eu.ggnet.saft.core.auth.AccessableAction;
import eu.ggnet.saft.core.auth.Guardian;
import eu.ggnet.saft.core.swing.OkCancelWrap;
import eu.ggnet.saft.core.ui.AlertType;

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
            Ui.build().title("Bitte SopoNr eingeben :").dialog().eval(() -> {
                TextInputDialog dialog = new TextInputDialog();
                dialog.setContentText("Bitte SopoNr eingeben :");
                return dialog;
            }).opt().filter(r -> {
                try {
                    PriceEngineResult per = Dl.remote().lookup(Exporter.class).load(r).getPayload();
                    PriceBlockerViewCask pbp = new PriceBlockerViewCask(r, per.getProductDescription(), per.getCustomerPrice(), per.getRetailerPrice());
                    Ui.build().title("Preise fixieren").swing().eval(() -> OkCancelWrap.result(pbp))
                            .opt()
                            .filter(Ui.failure()::handle)
                            .map(Reply::getPayload)
                            .ifPresent(f -> {
                                per.setCustomerPrice(f.customerPr);
                                per.setRetailerPrice(f.retailerPr);
                                per.setUnitPriceFixed(Change.SET);
                            });
                    Dl.remote().lookup(Importer.class).store(per, "Set directly via PriceBlocker", Dl.local().lookup(Guardian.class).getUsername());
                    return false;
                } catch (UserInfoException ex) {
                    Ui.exec(() -> {
                        Ui.build().alert().message("Kein Ergebins für SopoNr: " + ex.getMessage()).show(AlertType.WARNING);
                    });
                    return false;
                }

            });
        });
    }
}
