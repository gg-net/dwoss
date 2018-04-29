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
package eu.ggnet.dwoss.misc.ui.cap;

import eu.ggnet.saft.core.UiCore;
import eu.ggnet.saft.core.Ui;
import eu.ggnet.saft.core.Dl;

import java.awt.event.ActionEvent;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import javax.swing.SwingWorker;

import org.openide.util.Lookup;

import eu.ggnet.dwoss.common.api.values.SalesChannel;
import eu.ggnet.dwoss.misc.ee.SalesChannelHandler;
import eu.ggnet.dwoss.misc.ui.saleschannel.SalesChannelManagerDialog;
import eu.ggnet.dwoss.misc.ui.saleschannel.SalesChannelTableModel;
import eu.ggnet.dwoss.stock.ee.StockAgent;
import eu.ggnet.dwoss.stock.ee.entity.Stock;
import eu.ggnet.dwoss.stock.ee.model.SalesChannelLine;
import eu.ggnet.dwoss.common.ui.AccessableAction;
import eu.ggnet.saft.experimental.auth.Guardian;

import static eu.ggnet.dwoss.rights.api.AtomicRight.OPEN_SALES_CHANNEL_MANAGER;

/**
 * Opens the SalesChannelManager with all available units and optional executes the changes.
 * <p>
 * @author oliver.guenther
 */
public class OpenSalesChannelManagerAction extends AccessableAction {

    public OpenSalesChannelManagerAction() {
        super(OPEN_SALES_CHANNEL_MANAGER);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        new SwingWorker<List<SalesChannelLine>, Object>() {
            @Override
            protected List<SalesChannelLine> doInBackground() throws Exception {
                return Dl.remote().lookup(SalesChannelHandler.class).findAvailableUnits();
            }

            @Override
            protected void done() {
                try {
                    final SalesChannelManagerDialog dialog = new SalesChannelManagerDialog(UiCore.getMainFrame());
                    Map<SalesChannel, List<Stock>> collect = Dl.remote().lookup(StockAgent.class).findAll(Stock.class).stream().collect(Collectors.groupingBy(Stock::getPrimaryChannel));
                    Map<SalesChannel, Stock> stockToChannel = new HashMap<>();
                    // TODO: Make this better.
                    for (Entry<SalesChannel, List<Stock>> entry : collect.entrySet()) {
                        SalesChannel salesChannel = entry.getKey();
                        List<Stock> list = entry.getValue();
                        stockToChannel.put(salesChannel, list.get(0));
                    }

                    dialog.setModel(new SalesChannelTableModel(get(), stockToChannel));
                    dialog.setLocationRelativeTo(UiCore.getMainFrame());
                    dialog.setVisible(true);
                    if ( !dialog.isOk() ) return;
                    new SwingWorker<Boolean, Object>() {
                        @Override
                        protected Boolean doInBackground() throws Exception {
                            return Dl.remote().lookup(SalesChannelHandler.class)
                                    .update(dialog
                                            .getModel()
                                            .getDataModel()
                                            .stream()
                                            .filter(l -> l.hasChanged() || l.getDestination() != null)
                                            .collect(Collectors.toList()), Lookup.getDefault().lookup(Guardian.class).getUsername(),
                                            "Erzeugt duch Verkaufskanalmanager");
                        }

                        @Override
                        protected void done() {
                            try {
                                String msg = (get() ? "Verkaufskanaländerungen durchgeführt und Umfuhren vorbereitet" : "Keine Änderungen an Verkaufskanälen durchgeführt");
                                Ui.exec(() -> {
                                    Ui.build().alert().message(msg);
                                });
                            } catch (InterruptedException | ExecutionException ex) {
                                Ui.handle(ex);
                            }
                        }
                    }.execute();
                } catch (InterruptedException | ExecutionException ex) {
                    Ui.handle(ex);
                }
            }
        }.execute();
    }
}