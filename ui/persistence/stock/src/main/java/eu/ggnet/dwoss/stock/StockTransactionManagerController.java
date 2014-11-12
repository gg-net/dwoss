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
package eu.ggnet.dwoss.stock;

import eu.ggnet.dwoss.stock.StockTransactionManagerView;

import java.awt.Dialog;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import org.openide.util.Lookup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.saft.core.Workspace;
import eu.ggnet.saft.core.authorisation.Guardian;

import eu.ggnet.dwoss.stock.StockAgent;
import eu.ggnet.dwoss.stock.StockTransactionProcessor;
import eu.ggnet.dwoss.stock.entity.StockTransaction;
import eu.ggnet.dwoss.stock.format.StockTransactionFormater;

import eu.ggnet.dwoss.common.ExceptionUtil;

import eu.ggnet.dwoss.util.HtmlDialog;

import lombok.RequiredArgsConstructor;
import lombok.Setter;

import static eu.ggnet.saft.core.Client.lookup;

/**
 *
 * @author oliver.guenther
 */
@RequiredArgsConstructor
public class StockTransactionManagerController {

    @Setter
    private StockTransactionManagerModel model;

    @Setter
    private StockTransactionManagerView view;

    private final StockAgent stockAgent;

    private final StockTransactionProcessor stockRotationOperation;

    private SwingWorker<Object, List<StockTransaction>> loader;

    private Logger L = LoggerFactory.getLogger(StockTransactionManagerController.class);

    public StockTransactionManagerController() {
        this(lookup(StockAgent.class), lookup(StockTransactionProcessor.class));
    }

    public void reload() {
        if ( loader != null && !loader.isDone() ) loader.cancel(false);
        model.clear();
        loader = new SwingWorker<Object, List<StockTransaction>>() {
            @Override
            protected Object doInBackground() throws Exception {
                List<StockTransaction> loaded;
                int last = 0;
                do {
                    L.info("loading " + model.getTransactionType() + ":" + model.getStatusType() + " from " + last + " by 3");
                    loaded = stockAgent.findStockTransactionEager(model.getTransactionType(), model.getStatusType(), last, 3);
                    publish(loaded);
                    last += 3;
                    L.debug("loaded {}, isEmpty {}", loaded, loaded.isEmpty());
                } while (!loaded.isEmpty() && !isCancelled());
                return null;
            }

            @Override
            protected void process(List<List<StockTransaction>> chunks) {
                for (List<StockTransaction> list : chunks) {
                    if ( !isCancelled() ) model.addAll(list);
                }
            }

            @Override
            protected void done() {
                try {
                    get();
                } catch (CancellationException ex) {
                    // Ignore
                } catch (InterruptedException | ExecutionException ex) {
                    ExceptionUtil.show(lookup(Workspace.class).getMainFrame(), ex);
                }
            }
        };
        loader.execute();
    }

    void cancelLoader() {
        if ( loader != null && !loader.isDone() ) loader.cancel(false);
    }

    void showDetails(StockTransaction transaction) {
        HtmlDialog detailDialog = new HtmlDialog(view, Dialog.ModalityType.MODELESS);
        detailDialog.setText(StockTransactionFormater.toHtml(transaction));
        detailDialog.setVisible(true);
    }

    @SuppressWarnings("UseSpecificCatch")
    void cancel(StockTransaction transaction) {
        String comment = JOptionPane.showInputDialog(view, "Grund für Transaktionsabbruch:", "Transaktionsabbruch", JOptionPane.QUESTION_MESSAGE);
        if ( comment == null || comment.trim().equals("") ) return;
        try {
            stockRotationOperation.cancel(transaction, Lookup.getDefault().lookup(Guardian.class).getUsername(), comment);
            JOptionPane.showMessageDialog(lookup(Workspace.class).getMainFrame(), "Transaktion (" + transaction.getId() + " wurde abgebrochen");
            model.remove(transaction);
        } catch (Exception ex) {
            ExceptionUtil.show(lookup(Workspace.class).getMainFrame(), ex);
        }
    }
}
