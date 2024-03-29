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
import java.util.*;

import javax.swing.Action;
import javax.swing.JOptionPane;

import eu.ggnet.dwoss.core.common.values.*;
import eu.ggnet.dwoss.core.widget.swing.CloseType;
import eu.ggnet.dwoss.core.widget.swing.OkCancelDialog;
import eu.ggnet.dwoss.redtape.ee.entity.Document;
import eu.ggnet.dwoss.redtape.ee.entity.Document.Directive;
import eu.ggnet.dwoss.redtape.ee.entity.Position;
import eu.ggnet.dwoss.redtapext.ee.RedTapeWorker;
import eu.ggnet.dwoss.redtapext.ee.state.RedTapeStateTransition;
import eu.ggnet.dwoss.redtapext.ui.cao.RedTapeController;
import eu.ggnet.dwoss.redtapext.ui.cao.document.AfterInvoicePosition;
import eu.ggnet.dwoss.redtapext.ui.cao.document.annulation.CreditMemoView;
import eu.ggnet.dwoss.core.widget.Dl;
import eu.ggnet.dwoss.core.widget.AccessableAction;
import eu.ggnet.dwoss.core.widget.auth.Guardian;

import static eu.ggnet.dwoss.rights.api.AtomicRight.CREATE_ANNULATION_INVOICE;

/**
 *
 * @author pascal.perau
 */
public class AnnulationInvoiceAction extends AccessableAction {

    private final Window parent;

    private Document doc;

    private RedTapeController controller;

    public AnnulationInvoiceAction(Window parent, RedTapeController controller, Document doc, RedTapeStateTransition transition) {
        super(CREATE_ANNULATION_INVOICE);
        this.parent = parent;
        this.doc = doc;
        this.controller = controller;
        putValue(Action.NAME, transition.getDescription());
        putValue(Action.SHORT_DESCRIPTION, transition.getToolTip());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Calendar release = Calendar.getInstance();
        release.setTime(doc.getHistory().getRelease());
        Calendar beforeOneYear = Calendar.getInstance();
        beforeOneYear.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR) - 1);

        if ( beforeOneYear.after(release)
                && JOptionPane.CANCEL_OPTION == JOptionPane.showConfirmDialog(parent,
                        "Der Vorgang ist über ein Jahr alt und die Garantie ist abgelaufen!\nMöchten sie fortfahren?", "Garantie Warnung", JOptionPane.OK_CANCEL_OPTION) ) {
            return;
        }

        List<AfterInvoicePosition> creditPositions = new ArrayList<>();
        for (Position position : doc.getPositions().values()) {
            if ( position.getType() != PositionType.COMMENT ) creditPositions.add(new AfterInvoicePosition(position));
        }
        CreditMemoView view = new CreditMemoView(creditPositions);
        OkCancelDialog<CreditMemoView> dialog = new OkCancelDialog<>(getValue(Action.NAME).toString(), view);
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
        if ( dialog.getCloseType() == CloseType.OK ) {
            doc.removeAllPositions();
            for (Position position : view.getPositions()) {
                position.setPrice(position.getPrice() * (-1));
                doc.append(position);
            }
            doc.setType(DocumentType.ANNULATION_INVOICE);
            doc.setDirective(Directive.BALANCE_REPAYMENT);
            doc.setCreditMemoReason(view.getReason());
            Document d = Dl.remote().lookup(RedTapeWorker.class).update(doc, view.getStockLocation(), Dl.local().lookup(Guardian.class).getUsername());
            controller.reloadSelectionOnStateChange(d.getDossier());
        }
    }
}
