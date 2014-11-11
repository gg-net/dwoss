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
package eu.ggnet.dwoss.stock.action;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import org.openide.util.Lookup;

import eu.ggnet.saft.core.Workspace;
import eu.ggnet.saft.core.authorisation.Guardian;
import eu.ggnet.saft.core.authorisation.AccessableAction;

import eu.ggnet.dwoss.stock.StockTransactionProcessor;

import eu.ggnet.dwoss.stock.StockTransactionCreatorViewCask;
import eu.ggnet.dwoss.common.ExceptionUtil;

import static eu.ggnet.saft.core.Client.lookup;
import static eu.ggnet.dwoss.rights.api.AtomicRight.REMOVE_SINGE_UNIT_FROM_TRANSACTION;

/**
 * Removes a unit from a Transaction.
 * <p/>
 * @author oliver.guenther
 */
public class RemoveUnitFromTransactionAction extends AccessableAction {

    public RemoveUnitFromTransactionAction() {
        super(REMOVE_SINGE_UNIT_FROM_TRANSACTION);
    }

    @Override
    @SuppressWarnings("UseSpecificCatch")
    public void actionPerformed(ActionEvent e) {
        StockTransactionCreatorViewCask dialog = new StockTransactionCreatorViewCask(lookup(Workspace.class).getMainFrame());
        dialog.setTitle("Gerät aus Transaktion heraus nehmen");
        dialog.setLocationRelativeTo(lookup(Workspace.class).getMainFrame());
        dialog.setVisible(true);
        if ( !dialog.isOk() ) return;
        try {
            lookup(StockTransactionProcessor.class).removeFromPreparedTransaction(
                    dialog.getRefurbishIds(), Lookup.getDefault().lookup(Guardian.class).getUsername(), dialog.getComment());
            JOptionPane.showMessageDialog(lookup(Workspace.class).getMainFrame(), "SopoNr: " + dialog.getRefurbishIds() + " wurde aus Transaktion entfernt.");
        } catch (Exception ex) {
            ExceptionUtil.show(lookup(Workspace.class).getMainFrame(), ex);
        }
    }
}
