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

import eu.ggnet.saft.core.authorisation.Guardian;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import eu.ggnet.saft.core.Workspace;

import eu.ggnet.dwoss.price.Exporter;
import eu.ggnet.dwoss.price.Importer;
import eu.ggnet.dwoss.price.engine.PriceEngineResult;
import eu.ggnet.dwoss.price.engine.PriceEngineResult.Change;

import eu.ggnet.saft.core.authorisation.AccessableAction;

import eu.ggnet.dwoss.util.UserInfoException;

import eu.ggnet.dwoss.util.OkCancelDialog;
import eu.ggnet.saft.Ui;

import static eu.ggnet.saft.core.Client.lookup;
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
        try {
            String refurbishedId = JOptionPane.showInputDialog(lookup(Workspace.class).getMainFrame(), "Bitte SopoNr zur Fixierung eines Preises eingeben:");
            if ( refurbishedId == null ) return;
            PriceEngineResult per = lookup(Exporter.class).load(refurbishedId);
            PriceBlockerViewCask pbp = new PriceBlockerViewCask(refurbishedId, per.getProductDescription(), per.getCustomerPrice(), per.getRetailerPrice());
            OkCancelDialog<PriceBlockerViewCask> view = new OkCancelDialog<>(lookup(Workspace.class).getMainFrame(), "Price fixieren", pbp);
            view.setVisible(true);
            if ( view.isCancel() ) return;
            per.setCustomerPrice(pbp.getCustomerPrice());
            per.setRetailerPrice(pbp.getRetailerPrice());
            per.setUnitPriceFixed(Change.SET);
            lookup(Importer.class).store(per, "Set directly via PriceBlocker", lookup(Guardian.class).getUsername());
        } catch (UserInfoException ex) {
            Ui.handle(ex);
        }
    }

}
