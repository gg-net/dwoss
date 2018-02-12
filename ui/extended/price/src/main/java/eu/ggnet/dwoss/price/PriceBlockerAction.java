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

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import eu.ggnet.dwoss.common.ReplyUtil;
import eu.ggnet.dwoss.price.engine.PriceEngineResult;
import eu.ggnet.dwoss.price.engine.PriceEngineResult.Change;
import eu.ggnet.saft.*;
import eu.ggnet.saft.core.auth.AccessableAction;
import eu.ggnet.saft.core.auth.Guardian;
import eu.ggnet.saft.core.swing.OkCancelWrap;

import static eu.ggnet.dwoss.rights.api.AtomicRight.UPDATE_SET_UNIT_PRICE;
import static javafx.scene.control.ButtonType.CANCEL;
import static javafx.scene.control.ButtonType.OK;

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

        Ui.build().title("Bitte SopoNr zur Fixierung eines Preises eingeben").dialog().eval(() -> {
            Dialog<String> dialog = new Dialog<>();
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            TextField username = new TextField();
            username.setPromptText("SopoNr");

            grid.add(new Label("Bitte SopoNr zur Fixierung eines Preises eingeben: "), 0, 0);
            grid.add(username, 1, 0);

            dialog.getDialogPane().setContent(grid);
            dialog.getDialogPane().getButtonTypes().addAll(OK, CANCEL);
            return dialog;
        }).ifPresent(r -> {
            if ( r == null ) return;
            PriceEngineResult per = ReplyUtil.wrap(() -> Dl.remote().lookup(Exporter.class).load(r)).getPayload();
            PriceBlockerViewCask pbp = new PriceBlockerViewCask(r, per.getProductDescription(), per.getCustomerPrice(), per.getRetailerPrice());
            Ui.build().title("Preise fixieren").swing().eval(() -> OkCancelWrap.result(pbp)).ifPresent(rr -> {
                per.setCustomerPrice(rr.getPayload().customerPr);
                per.setRetailerPrice(rr.getPayload().retailerPr);
                per.setUnitPriceFixed(Change.SET);
                Dl.remote().lookup(Importer.class).store(per, "Set directly via PriceBlocker", Dl.local().lookup(Guardian.class).getUsername());

            });
        });

    }

}
