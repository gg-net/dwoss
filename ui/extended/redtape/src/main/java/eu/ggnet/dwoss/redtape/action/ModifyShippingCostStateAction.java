package eu.ggnet.dwoss.redtape.action;

import java.awt.Window;
import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import eu.ggnet.saft.core.authorisation.Guardian;

import eu.ggnet.dwoss.redtape.entity.Document;

import eu.ggnet.dwoss.redtape.RedTapeWorker;
import eu.ggnet.dwoss.redtape.state.CustomerDocument;

import eu.ggnet.dwoss.redtape.RedTapeController;
import eu.ggnet.dwoss.redtape.ShippingCostHelper;

import eu.ggnet.statemachine.StateTransition;

import static eu.ggnet.saft.core.Client.lookup;

/**
 *
 * @author bastian.venz
 */
public class ModifyShippingCostStateAction extends StateTransitionAction {

    public ModifyShippingCostStateAction(Window parent, RedTapeController controller, CustomerDocument cdoc,
                                         StateTransition<CustomerDocument> transition) {
        super(parent, controller, cdoc, transition);
        this.controller = controller;
        this.cdoc = cdoc;
        this.transition = transition;
    }

    private RedTapeController controller;

    private CustomerDocument cdoc;

    private StateTransition<CustomerDocument> transition;

    private String createWindowText() {
        StringBuilder message = new StringBuilder("Sie haben einen Versandauftrag bearbeitet.\n");
        message.append("Veränderung: ").append(transition.getDescription()).append("\n");
        message.append("Sollen die Versandkosten automatisch kalkuliert werden?");
        return message.toString();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Document document = lookup(RedTapeWorker.class).stateChange(cdoc, transition, lookup(Guardian.class).getUsername());

        int confirmDialog = JOptionPane.showConfirmDialog(parent, createWindowText(),
                "Automatische Versandkostenkalkulation", JOptionPane.YES_NO_CANCEL_OPTION);

        if ( confirmDialog == JOptionPane.YES_OPTION ) ShippingCostHelper.modifyOrAddShippingCost(document, cdoc.getShippingCondition());

        if ( confirmDialog != JOptionPane.CANCEL_OPTION )
            controller.reloadSelectionOnStateChange(lookup(RedTapeWorker.class).update(document, null, lookup(Guardian.class).getUsername()).getDossier());

    }
}
