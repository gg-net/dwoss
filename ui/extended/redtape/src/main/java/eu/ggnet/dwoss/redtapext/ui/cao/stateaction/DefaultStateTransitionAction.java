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

import javax.swing.*;

import org.apache.commons.lang3.StringUtils;

import eu.ggnet.dwoss.common.api.values.PaymentMethod;
import eu.ggnet.dwoss.common.api.values.PositionType;
import eu.ggnet.dwoss.common.ui.CloseType;
import eu.ggnet.dwoss.common.ui.OkCancelDialog;
import eu.ggnet.dwoss.redtape.ee.entity.Document;
import eu.ggnet.dwoss.redtape.ee.entity.Position;
import eu.ggnet.dwoss.redtapext.ee.RedTapeWorker;
import eu.ggnet.dwoss.redtapext.ee.state.*;
import eu.ggnet.dwoss.redtapext.ui.cao.RedTapeController;
import eu.ggnet.saft.Dl;
import eu.ggnet.saft.Ui;
import eu.ggnet.saft.api.Reply;
import eu.ggnet.saft.core.auth.Guardian;
import eu.ggnet.statemachine.StateTransition;

/**
 * Default Action for a wrapped StateTransistion.
 *
 * @author oliver.guenther
 */
public class DefaultStateTransitionAction extends AbstractAction {

    protected final Window parent;

    private final CustomerDocument cdoc;

    private final RedTapeController controller;

    private final StateTransition<CustomerDocument> transition;

    @SuppressWarnings("OverridableMethodCallInConstructor")
    public DefaultStateTransitionAction(Window parent, RedTapeController controller, CustomerDocument cdoc, StateTransition<CustomerDocument> transition) {
        this.parent = parent;
        this.cdoc = cdoc;
        this.transition = transition;
        this.controller = controller;
        putValue(Action.NAME, transition.getDescription());
        putValue(Action.SHORT_DESCRIPTION, transition.getToolTip());
        if ( ((RedTapeStateTransition)transition).getHints().contains(RedTapeStateTransition.Hint.CHANGES_PAYMENT_METHOD_TO_DIRECT_DEBIT) && cdoc.getPaymentMethod() != PaymentMethod.DIRECT_DEBIT ) {
            this.setEnabled(false);
        }
        if ( ((RedTapeStateTransition)transition).getHints().contains(RedTapeStateTransition.Hint.CHANGES_PAYMENT_METHOD_TO_INVOICE) && cdoc.getPaymentMethod() != PaymentMethod.INVOICE ) {
            this.setEnabled(false);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //TODO: All the extra checks for hints don't feel like the optimum

        //Invoice
        if ( ((RedTapeStateTransition)transition).getHints().contains(RedTapeStateTransition.Hint.CREATES_INVOICE) ) {
            int confirmInvoice = JOptionPane.showOptionDialog(
                    parent, "Eine Rechnung wird unwiederruflich erstellt. Möchten Sie fortfahren?",
                    "Rechnungserstellung", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
            if ( confirmInvoice == JOptionPane.CANCEL_OPTION ) return;
        }

        //Cancel
        if ( ((RedTapeStateTransition)transition).equals(RedTapeStateTransitions.CANCEL) ) {
            int confirmInvoice = JOptionPane.showOptionDialog(
                    parent, "Der Vorgang wird storniert.\nMöchten Sie fortfahren?",
                    "Abbrechen des Vorganges", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
            if ( confirmInvoice == JOptionPane.NO_OPTION ) return;
        }

        if ( ((RedTapeStateTransition)transition).getHints().contains(RedTapeStateTransition.Hint.ADDS_SETTLEMENT) ) {
            SettlementViewCask view = new SettlementViewCask();
            OkCancelDialog<SettlementViewCask> dialog = new OkCancelDialog<>(parent, "Zahlung hinterlegen", view);
            dialog.setVisible(true);
            if ( dialog.getCloseType() == CloseType.OK ) {
                for (Document.Settlement settlement : view.getSettlements()) {
                    cdoc.getDocument().add(settlement);
                }
            } else {
                return;
            }
        }
        if ( ((RedTapeStateTransition)transition).getHints().contains(RedTapeStateTransition.Hint.UNIT_LEAVES_STOCK) ) {
            for (Position p : cdoc.getDocument().getPositions(PositionType.PRODUCT_BATCH).values()) {
                //TODO not the best but fastest solution for now, this must be changed later
                if ( StringUtils.isBlank(p.getRefurbishedId()) ) {
                    if ( JOptionPane.showConfirmDialog(
                            parent, "Der Vorgang enthält Neuware, wurden alle Seriennummern erfasst?", "Bitte verifizieren", JOptionPane.OK_CANCEL_OPTION)
                            == JOptionPane.CANCEL_OPTION )
                        return;
                }
            }
        }

        Optional.of(Dl.remote().lookup(RedTapeWorker.class).stateChange(cdoc, transition, Dl.local().lookup(Guardian.class).getUsername()))
                .filter(Ui.failure()::handle)
                .map(Reply::getPayload)
                .map(Document::getDossier)
                .ifPresent(d -> controller.reloadSelectionOnStateChange(d));
    }
}
