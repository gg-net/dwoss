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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;

import eu.ggnet.saft.core.auth.Guardian;
import eu.ggnet.dwoss.redtape.ee.entity.Document;
import eu.ggnet.dwoss.redtape.ee.entity.Document.Directive;
import eu.ggnet.dwoss.redtape.ee.entity.Position;

import eu.ggnet.dwoss.rules.PositionType;

import eu.ggnet.dwoss.redtapext.ee.RedTapeWorker;
import eu.ggnet.dwoss.redtapext.ee.state.RedTapeStateTransition;

import eu.ggnet.dwoss.redtapext.ui.cao.RedTapeController;
import eu.ggnet.dwoss.redtapext.ui.cao.document.AfterInvoicePosition;
import eu.ggnet.dwoss.redtapext.ui.cao.document.annulation.ComplaintView;

import eu.ggnet.dwoss.rules.DocumentType;

import eu.ggnet.dwoss.util.CloseType;
import eu.ggnet.dwoss.util.OkCancelDialog;

import static eu.ggnet.saft.Client.lookup;

/**
 *
 * @author pascal.perau
 */
public class ComplaintAction extends AbstractAction {

    private final Window parent;

    private RedTapeController controller;

    private Document doc;

    public ComplaintAction(Window parent, RedTapeController controller, Document doc, RedTapeStateTransition transition) {
        this.parent = parent;
        this.controller = controller;
        this.doc = doc;
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
            if ( position.getType() != PositionType.COMMENT ) {
                AfterInvoicePosition aiPosition = new AfterInvoicePosition(position);
                if ( doc.getDossier().isDispatch() && position.getType() == PositionType.SHIPPING_COST ) aiPosition.setParticipate(true);
                creditPositions.add(aiPosition);
            }
        }
        ComplaintView view = new ComplaintView(creditPositions);
        OkCancelDialog<ComplaintView> dialog = new OkCancelDialog<>("Reklamation anmelden", view);
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
        if ( dialog.getCloseType() == CloseType.OK ) {
            doc.removeAllPositions();
            for (Position position : view.getPositions()) {
                doc.append(position);
            }
            doc.setType(DocumentType.COMPLAINT);
            doc.setDirective(Directive.WAIT_FOR_COMPLAINT_COMPLETION);
            Document d = lookup(RedTapeWorker.class).update(doc, null, lookup(Guardian.class).getUsername());
            controller.reloadSelectionOnStateChange(d.getDossier());
        }
    }
}
