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
package eu.ggnet.dwoss.redtapext.ui.cao.document.annulation;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import eu.ggnet.dwoss.mandator.MandatorSupporter;
import eu.ggnet.dwoss.redtape.entity.Position;
import eu.ggnet.dwoss.redtape.entity.PositionBuilder;
import eu.ggnet.dwoss.redtapext.ui.cao.document.AfterInvoicePosition;
import eu.ggnet.dwoss.redtapext.ui.cao.document.AfterInvoiceTablePanel;
import eu.ggnet.dwoss.rules.PositionType;
import eu.ggnet.dwoss.util.CloseType;
import eu.ggnet.dwoss.util.IPreClose;
import eu.ggnet.saft.Client;

import static eu.ggnet.dwoss.rules.PositionType.COMMENT;

/**
 *
 * @author pascal.perau
 */
public class ComplaintView extends javax.swing.JPanel implements IPreClose {

    private final List<AfterInvoicePosition> complaintPositions;

    private final AfterInvoiceTablePanel afterInvoiceTablePanel;

    @SuppressWarnings("OverridableMethodCallInConstructor")
    public ComplaintView(List<AfterInvoicePosition> complaintPositions) {
        setLayout(new BorderLayout());
        afterInvoiceTablePanel = new AfterInvoiceTablePanel();
        add(afterInvoiceTablePanel, BorderLayout.CENTER);
        this.complaintPositions = complaintPositions;
        afterInvoiceTablePanel.setTableModel(new ComplaintTableModel(complaintPositions));

    }

    public List<Position> getPositions() {
        List<Position> positions = new ArrayList<>();
        for (AfterInvoicePosition afterInvoicePosition : complaintPositions) {
            if ( afterInvoicePosition.isParticipant() ) {
                positions.add(afterInvoicePosition.getPosition());
            }
        }
        if ( afterInvoiceTablePanel.getComment() != null && !afterInvoiceTablePanel.getComment().trim().equals("") )
            positions.add(new PositionBuilder().type(PositionType.COMMENT).name("Grund/Beschreibung")
                    .bookingAccount(Client.lookup(MandatorSupporter.class).loadPostLedger().get(COMMENT).orElse(-1))
                    .description(afterInvoiceTablePanel.getComment()).build());
        return positions;
    }

    @Override
    public boolean pre(CloseType type) {
        if ( type == CloseType.OK && getPositions().isEmpty() ) {
            JOptionPane.showMessageDialog(this, "keine Positionen zur Gutschrift gewählt");
            return false;
        }
        return true;
    }
}
