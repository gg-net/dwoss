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

import eu.ggnet.dwoss.price.engine.PriceEngineResult;
import eu.ggnet.dwoss.price.engine.support.PriceEngineResultFormater;
import eu.ggnet.dwoss.rules.Css;
import eu.ggnet.dwoss.util.HtmlPane;

import eu.ggnet.saft.*;
import eu.ggnet.saft.core.auth.AccessableAction;
import eu.ggnet.saft.core.ui.AlertType;

import static eu.ggnet.dwoss.rights.api.AtomicRight.CREATE_ONE_PRICE;
import static javafx.stage.Modality.WINDOW_MODAL;
import static javax.swing.JOptionPane.showInputDialog;

/**
 *
 * @author pascal.perau
 */
public class GenerateOnePriceAction extends AccessableAction {

    public GenerateOnePriceAction() {
        super(CREATE_ONE_PRICE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

//        Ui.exec(() -> {
//            Ui.build().fx().eval(() -> "Bitte SopoNr eingebe:",() -> new InputPane())
//                    .ifPresent(r -> {
//                        PriceEngineResult per = Dl.remote().lookup(Exporter.class).onePrice(r);
//                        if(per == null){
//                            Ui.build().alert().message("Kein Ergebins für SopoNr: " + r).show(AlertType.WARNING); 
//                            return;
//                        }
//                        Ui.build().modality(WINDOW_MODAL).title("SopoNr").fx().show(() -> Css.toHtml5WithStyle(PriceEngineResultFormater.toSimpleHtml(per)), () -> new HtmlPane());
//                    });
//        });

        String refurbishId = showInputDialog(UiCore.getMainFrame(), "Bitte SopoNr eingeben :");
        if ( refurbishId == null || refurbishId.isEmpty() ) return;
        try {
            PriceEngineResult per = Dl.remote().lookup(Exporter.class).onePrice(refurbishId);
            Ui.exec(() -> {
                Ui.build().modality(WINDOW_MODAL).title("SopoNr").fx().show(() -> Css.toHtml5WithStyle(PriceEngineResultFormater.toSimpleHtml(per)), () -> new HtmlPane());
            });
        } catch (NullPointerException ex) {
            Ui.exec(() -> {
                Ui.build().alert().message("Kein Ergebins für SopoNr: " + refurbishId).show(AlertType.WARNING);
            });
        }

    }

}
