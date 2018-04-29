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
package eu.ggnet.dwoss.redtapext.ui.cao.stateaction;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.util.Optional;

import javax.swing.JOptionPane;

import eu.ggnet.dwoss.redtape.ee.entity.Document;
import eu.ggnet.dwoss.redtapext.ee.RedTapeWorker;
import eu.ggnet.dwoss.redtapext.ee.state.CustomerDocument;
import eu.ggnet.dwoss.redtapext.ui.cao.RedTapeController;
import eu.ggnet.dwoss.redtapext.ui.cao.common.ShippingCostHelper;
import eu.ggnet.saft.core.Dl;
import eu.ggnet.saft.core.Ui;
import eu.ggnet.saft.api.Reply;
import eu.ggnet.saft.experimental.auth.Guardian;
import eu.ggnet.statemachine.StateTransition;


public class RemoveShippingCostStateAction extends DefaultStateTransitionAction {

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

        if ( confirmDialog != JOptionPane.CANCEL_OPTION ) {
            Optional.of(Dl.remote().lookup(RedTapeWorker.class).stateChange(cdoc, transition, Dl.local().lookup(Guardian.class).getUsername()))
                    .filter(Ui.failure()::handle)
                    .map(Reply::getPayload)
                    .map(Document::getDossier)
                    .ifPresent(d -> controller.reloadSelectionOnStateChange(d));
        }
    }
}
