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
package eu.ggnet.dwoss.stock.ui;

import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import org.openide.util.Lookup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.core.common.Css;
import eu.ggnet.dwoss.core.widget.HtmlPane;
import eu.ggnet.dwoss.stock.ee.StockAgent;
import eu.ggnet.dwoss.stock.ee.StockTransactionProcessor;
import eu.ggnet.dwoss.stock.ee.entity.StockTransaction;
import eu.ggnet.dwoss.stock.ee.format.StockTransactionFormater;
import eu.ggnet.saft.core.*;
import eu.ggnet.dwoss.core.widget.auth.Guardian;


/**
 *
 * @author oliver.guenther
 */
public class StockTransactionManagerController {

    private StockTransactionManagerModel model;

    private StockTransactionManagerView view;

    private final StockAgent stockAgent;

    private final StockTransactionProcessor stp;

    private SwingWorker<Object, List<StockTransaction>> loader;

    private Logger L = LoggerFactory.getLogger(StockTransactionManagerController.class);

    public StockTransactionManagerController() {
        this(Dl.remote().lookup(StockAgent.class), Dl.remote().lookup(StockTransactionProcessor.class));
    }

    public StockTransactionManagerController(StockAgent stockAgent, StockTransactionProcessor stp) {
        this.stockAgent = stockAgent;
        this.stp = stp;
    }

    public void setModel(StockTransactionManagerModel model) {
        this.model = model;
    }

    public void setView(StockTransactionManagerView view) {
        this.view = view;
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
                    Ui.handle(ex);
                }
            }
        };
        loader.execute();
    }

    void cancelLoader() {
        if ( loader != null && !loader.isDone() ) loader.cancel(false);
    }

    void showDetails(StockTransaction transaction) {
        Ui.exec(() -> {
            Ui.build(view).fx().show(() -> Css.toHtml5WithStyle(StockTransactionFormater.toHtml(transaction)), () -> new HtmlPane());
        });
    }

    @SuppressWarnings("UseSpecificCatch")
    void cancel(StockTransaction transaction) {
        String comment = JOptionPane.showInputDialog(view, "Grund für Transaktionsabbruch:", "Transaktionsabbruch", JOptionPane.QUESTION_MESSAGE);
        if ( comment == null || comment.trim().equals("") ) return;
        try {
            stp.cancel(transaction, Dl.local().lookup(Guardian.class).getUsername(), comment);
            JOptionPane.showMessageDialog(UiCore.getMainFrame(), "Transaktion (" + transaction.getId() + " wurde abgebrochen");
            model.remove(transaction);
        } catch (Exception ex) {
            Ui.handle(ex);
        }
    }
}
