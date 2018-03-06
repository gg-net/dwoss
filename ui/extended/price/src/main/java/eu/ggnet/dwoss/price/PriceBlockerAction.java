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

import javax.swing.JOptionPane;

import eu.ggnet.dwoss.price.engine.PriceEngineResult;
import eu.ggnet.dwoss.price.engine.PriceEngineResult.Change;
import eu.ggnet.dwoss.util.OkCancelDialog;
import eu.ggnet.dwoss.util.UserInfoException;
import eu.ggnet.saft.*;
import eu.ggnet.saft.core.auth.AccessableAction;
import eu.ggnet.saft.core.auth.Guardian;

import static eu.ggnet.dwoss.rights.api.AtomicRight.UPDATE_SET_UNIT_PRICE;

/**
 *
 * @author oliver.guenther
 */
public class PriceBlockerAction extends AccessableAction {

    public PriceBlockerAction() {
        super(UPDATE_SET_UNIT_PRICE);
    }

    /**
     * Make all actions full Saft usage.
     * Use Ui.exec -> Ui.build idiom
     * Wrap UserInfoException via ReplyUtil or replace on caller with Reply
     * Replace simple progress SwingWorker with Ui.progess().call or like this
     * Use Ui.failure::handle idiom
     * Replace HtmlDialog or HtmlPanel with HtmlPane
     * Replace OkCancelDialog with OkCancelWrap.....
     * Replace IPreClose with VetoableOk
     * Replace DateRangeChooserDialog with DateRangeChooserView
     * Example ImportImageIdsAction
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            String refurbishedId = JOptionPane.showInputDialog(UiCore.getMainFrame(), "Bitte SopoNr zur Fixierung eines Preises eingeben:");
            if ( refurbishedId == null ) return;
            PriceEngineResult per = Dl.remote().lookup(Exporter.class).load(refurbishedId);
            PriceBlockerViewCask pbp = new PriceBlockerViewCask(refurbishedId, per.getProductDescription(), per.getCustomerPrice(), per.getRetailerPrice());
            OkCancelDialog<PriceBlockerViewCask> view = new OkCancelDialog<>(UiCore.getMainFrame(), "Price fixieren", pbp);
            view.setVisible(true);
            if ( view.isCancel() ) return;
            per.setCustomerPrice(pbp.getCustomerPrice());
            per.setRetailerPrice(pbp.getRetailerPrice());
            per.setUnitPriceFixed(Change.SET);
            Dl.remote().lookup(Importer.class).store(per, "Set directly via PriceBlocker", Dl.local().lookup(Guardian.class).getUsername());
        } catch (UserInfoException ex) {
            Ui.handle(ex);
        }

//        Ui.exec(() -> {
//
//            Optional<String> sopoOptional = Ui.build().dialog().eval(
//                    () -> {
//
//                        TextInputDialog dialog = new TextInputDialog();
//                        dialog.setTitle("SopoNr Eingabe");
//                        dialog.setHeaderText("SopoNr:");
//                        dialog.setContentText("Bitte SopoNr zur Fixierung eines Preises eingeben:");
//                        return dialog;
//                    })
//                    .opt();
//
//            sopoOptional.map(sopoNr -> ReplyUtil.wrap(() -> Dl.remote().lookup(Exporter.class).load(sopoNr)))
//                    .filter(Ui.failure()::handle)
//                    .map(Reply::getPayload)
//                    .ifPresent(priceEngineResult -> {
//                        PriceBlockerViewCask pbp = new PriceBlockerViewCask(sopoOptional.get(), priceEngineResult.getProductDescription(), priceEngineResult.getCustomerPrice(), priceEngineResult.getRetailerPrice());
//                        OkCancelDialog<PriceBlockerViewCask> view = new OkCancelDialog<>(UiCore.getMainFrame(), "Price fixieren", pbp);
//                        view.setVisible(true);
//                        if ( view.isCancel() )
//                            return;
//
//                        priceEngineResult.setCustomerPrice(pbp.getCustomerPrice());
//                        priceEngineResult.setRetailerPrice(pbp.getRetailerPrice());
//                        priceEngineResult.setUnitPriceFixed(Change.SET);
//
//                        Dl.remote().lookup(Importer.class).store(priceEngineResult, "Set directly via PriceBlocker", Dl.local().lookup(Guardian.class).getUsername());
//
//                    });
//
//        }
//        );
    }

}
