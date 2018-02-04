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

import java.awt.Dialog;
import java.awt.event.ActionEvent;

import eu.ggnet.dwoss.price.engine.PriceEngineResult;
import eu.ggnet.dwoss.price.engine.support.PriceEngineResultFormater;
import eu.ggnet.dwoss.util.HtmlDialog;
import eu.ggnet.saft.*;
import eu.ggnet.saft.core.auth.AccessableAction;

import static eu.ggnet.dwoss.rights.api.AtomicRight.CREATE_ONE_PRICE;
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
        String refurbishId = showInputDialog(UiCore.getMainFrame(), "Bitte SopoNr eingeben :");
        if ( refurbishId == null || refurbishId.isEmpty() ) return;
        PriceEngineResult per = Dl.remote().lookup(Exporter.class).onePrice(refurbishId);
        if ( per == null ) {
            Ui.build().alert("Kein Ergebins für SopoNr: " + refurbishId);
        }
        String html = PriceEngineResultFormater.toSimpleHtml(per);
        HtmlDialog dialog = new HtmlDialog(UiCore.getMainFrame(), Dialog.ModalityType.MODELESS);
        dialog.setText(html);
        dialog.setLocationRelativeTo(UiCore.getMainFrame());
        dialog.setVisible(true);
    }
}
