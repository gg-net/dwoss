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
package eu.ggnet.dwoss.redtape.action;

import java.awt.Window;
import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import eu.ggnet.saft.core.authorisation.Guardian;

import eu.ggnet.dwoss.redtape.RedTapeWorker;
import eu.ggnet.dwoss.redtape.state.CustomerDocument;

import eu.ggnet.dwoss.redtape.RedTapeController;
import eu.ggnet.dwoss.redtape.ShippingCostHelper;

import eu.ggnet.statemachine.StateTransition;

import static eu.ggnet.saft.core.Client.lookup;

public class RemoveShippingCostStateAction extends StateTransitionAction {

    public RemoveShippingCostStateAction(Window parent, RedTapeController controller, CustomerDocument cdoc, StateTransition<CustomerDocument> transition) {
        super(parent, controller, cdoc, transition);
        this.controller = controller;
        this.cdoc = cdoc;
        this.transition = transition;
    }

    private RedTapeController controller;

    private CustomerDocument cdoc;

    private StateTransition<CustomerDocument> transition;

    @Override
    public void actionPerformed(ActionEvent e) {
        int confirmDialog = JOptionPane.showConfirmDialog(
                parent, "Auftrag ist nun ein Abholauftrag.\nSollen die Versandkosten entfernt werden?",
                "Automatisches Setzten der Versandkosten", JOptionPane.YES_NO_CANCEL_OPTION);

        if ( confirmDialog == JOptionPane.YES_OPTION ) ShippingCostHelper.removeShippingCost(cdoc.getDocument());

        if ( confirmDialog != JOptionPane.CANCEL_OPTION )
            controller.reloadSelectionOnStateChange(lookup(RedTapeWorker.class).stateChange(cdoc, transition, lookup(Guardian.class).getUsername()).getDossier());
    }
}
