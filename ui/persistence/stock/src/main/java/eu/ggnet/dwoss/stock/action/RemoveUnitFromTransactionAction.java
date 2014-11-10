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
