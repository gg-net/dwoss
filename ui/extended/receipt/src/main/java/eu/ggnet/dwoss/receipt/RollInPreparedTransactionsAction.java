package eu.ggnet.dwoss.receipt;

import java.awt.event.ActionEvent;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import eu.ggnet.saft.core.Workspace;
import eu.ggnet.saft.core.authorisation.Guardian;
import eu.ggnet.saft.core.authorisation.AccessableAction;

import eu.ggnet.dwoss.stock.StockAgent;
import eu.ggnet.dwoss.stock.StockTransactionProcessor;
import eu.ggnet.dwoss.stock.entity.StockTransaction;
import eu.ggnet.dwoss.stock.entity.StockTransactionStatusType;
import eu.ggnet.dwoss.stock.entity.StockTransactionType;
import eu.ggnet.dwoss.stock.format.StockTransactionFormater;

import eu.ggnet.dwoss.common.ExceptionUtil;

import eu.ggnet.dwoss.util.HtmlPanel;
import eu.ggnet.dwoss.util.OkCancelDialog;

import static eu.ggnet.saft.core.Client.lookup;
import static eu.ggnet.dwoss.rights.api.AtomicRight.CREATE_ROLL_IN_OF_PREPARED_TRANSACTIONS;

/**
 *
 * @author oliver.guenther
 */
public class RollInPreparedTransactionsAction extends AccessableAction {

    public RollInPreparedTransactionsAction() {
        super(CREATE_ROLL_IN_OF_PREPARED_TRANSACTIONS);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final List<StockTransaction> transactions = lookup(StockAgent.class).findStockTransactionEager(StockTransactionType.ROLL_IN, StockTransactionStatusType.PREPARED);
        HtmlPanel view = new HtmlPanel();
        view.getHtmlPane().setText(StockTransactionFormater.toHtml(transactions));
        OkCancelDialog<HtmlPanel> dialog = new OkCancelDialog<>(lookup(Workspace.class).getMainFrame(), "Transactions", view);
        dialog.setLocationRelativeTo(lookup(Workspace.class).getMainFrame());
        dialog.setVisible(true);
        if ( dialog.isCancel() ) return;
        new SwingWorker<Object, Object>() {
            @Override
            protected Object doInBackground() throws Exception {
                lookup(StockTransactionProcessor.class).rollIn(transactions, lookup(Guardian.class).getUsername());
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                } catch (InterruptedException | ExecutionException ex) {
                    ExceptionUtil.show(lookup(Workspace.class).getMainFrame(), ex);
                }
            }
        }.execute();
    }
}
