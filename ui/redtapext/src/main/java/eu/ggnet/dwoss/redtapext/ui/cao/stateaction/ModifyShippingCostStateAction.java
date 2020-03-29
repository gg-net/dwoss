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

import eu.ggnet.dwoss.redtapext.ee.RedTapeWorker;
import eu.ggnet.dwoss.redtapext.ee.state.CustomerDocument;
import eu.ggnet.dwoss.redtapext.ui.cao.RedTapeController;
import eu.ggnet.dwoss.redtapext.ui.cao.common.ShippingCostHelper;
import eu.ggnet.dwoss.core.widget.Dl;
import eu.ggnet.saft.core.Ui;
import eu.ggnet.saft.api.Reply;
import eu.ggnet.dwoss.core.widget.auth.Guardian;
import eu.ggnet.statemachine.StateTransition;


/**
 *
 * @author bastian.venz
 */
public class ModifyShippingCostStateAction extends DefaultStateTransitionAction {

    public ModifyShippingCostStateAction(Window parent, RedTapeController controller, CustomerDocument cdoc,
                                         StateTransition<CustomerDocument> transition) {
        super(parent, controller, cdoc, transition);
        this.controller = controller;
        this.cdoc = cdoc;
        this.transition = transition;
    }

    private final RedTapeController controller;

    private final CustomerDocument cdoc;

    private final StateTransition<CustomerDocument> transition;

    private String createWindowText() {
        StringBuilder message = new StringBuilder("Sie haben einen Versandauftrag bearbeitet.\n");
        message.append("Veränderung: ").append(transition.getDescription()).append("\n");
        message.append("Sollen die Versandkosten automatisch kalkuliert werden?");
        return message.toString();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Optional.of(Dl.remote().lookup(RedTapeWorker.class).stateChange(cdoc, transition, Dl.local().lookup(Guardian.class).getUsername()))
                .filter(Ui.failure()::handle)
                .map(Reply::getPayload).ifPresent(document -> {
            int confirmDialog = JOptionPane.showConfirmDialog(parent, createWindowText(),
                    "Automatische Versandkostenkalkulation", JOptionPane.YES_NO_CANCEL_OPTION);
            if ( confirmDialog == JOptionPane.YES_OPTION ) ShippingCostHelper.modifyOrAddShippingCost(document, cdoc.getShippingCondition());

            if ( confirmDialog != JOptionPane.CANCEL_OPTION )
                controller.reloadSelectionOnStateChange(Dl.remote().lookup(RedTapeWorker.class).update(document, null, Dl.local().lookup(Guardian.class).getUsername()).getDossier());
        });

    }
}
