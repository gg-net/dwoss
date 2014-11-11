/* 
 * Copyright (C) 2014 pascal.perau
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

import eu.ggnet.saft.core.Workspace;

import eu.ggnet.dwoss.price.Exporter;
import eu.ggnet.dwoss.price.engine.PriceEngineResult;
import eu.ggnet.dwoss.price.engine.support.PriceEngineResultFormater;

import eu.ggnet.saft.core.authorisation.AccessableAction;

import eu.ggnet.dwoss.util.HtmlDialog;

import static eu.ggnet.saft.core.Client.lookup;
import static eu.ggnet.dwoss.rights.api.AtomicRight.CREATE_ONE_PRICE;
import static javax.swing.JOptionPane.showInputDialog;
import static javax.swing.JOptionPane.showMessageDialog;

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
        String refurbishId = showInputDialog(lookup(Workspace.class).getMainFrame(), "Bitte SopoNr eingeben :");
        if ( refurbishId == null || refurbishId.isEmpty() ) return;
        PriceEngineResult per = lookup(Exporter.class).onePrice(refurbishId);
        if ( per == null ) {
            showMessageDialog(lookup(Workspace.class).getMainFrame(), "Kein Ergebins für SopoNr: " + refurbishId);
        }
        String html = PriceEngineResultFormater.toSimpleHtml(per);
        HtmlDialog dialog = new HtmlDialog(lookup(Workspace.class).getMainFrame(), Dialog.ModalityType.MODELESS);
        dialog.setText(html);
        dialog.setLocationRelativeTo(lookup(Workspace.class).getMainFrame());
        dialog.setVisible(true);
    }
}
