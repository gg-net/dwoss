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
