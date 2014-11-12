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
package eu.ggnet.dwoss.receipt.product;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import eu.ggnet.saft.core.Workspace;
import eu.ggnet.saft.core.authorisation.AccessableAction;

import eu.ggnet.dwoss.receipt.UiProductSupport;

import eu.ggnet.dwoss.uniqueunit.UniqueUnitAgent;
import eu.ggnet.dwoss.uniqueunit.entity.Product;

import eu.ggnet.dwoss.util.UserInfoException;

import eu.ggnet.dwoss.common.ExceptionUtil;

import static eu.ggnet.saft.core.Client.lookup;
import static eu.ggnet.dwoss.rights.api.AtomicRight.UPDATE_PRODUCT;

/**
 * Allow the modification of a Product/Part.
 * <p/>
 * @author oliver.guenther
 */
public class UpdateProductAction extends AccessableAction {

    public UpdateProductAction() {
        super(UPDATE_PRODUCT);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            String partNo = JOptionPane.showInputDialog(lookup(Workspace.class).getMainFrame(), "Bitte Artikelnummer des Herstellers eingeben:");
            Product product = lookup(UniqueUnitAgent.class).findProductByPartNo(partNo);
            if ( product == null ) {
                JOptionPane.showMessageDialog(lookup(Workspace.class).getMainFrame(), "Artikel " + partNo + " existiert nicht, bitte über Aufnahme erfassen",
                        "Fehler", JOptionPane.ERROR_MESSAGE);
            } else {
                // Hint: We need the Manufacturer of the Porduct in advance, as we initialize all Validator elements.
                // If We want to allow creation of new Products here, the workflow must be enhanced.
                new UiProductSupport().createOrEditPart(product.getTradeName().getManufacturer(), partNo, lookup(Workspace.class).getMainFrame());
            }
        } catch (UserInfoException ex) {
            ExceptionUtil.show(lookup(Workspace.class).getMainFrame(), ex);
        }
    }
}
