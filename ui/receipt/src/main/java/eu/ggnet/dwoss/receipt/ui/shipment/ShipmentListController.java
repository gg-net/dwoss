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
package eu.ggnet.dwoss.receipt.ui.shipment;

import java.awt.EventQueue;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;
import javax.swing.JOptionPane;

import eu.ggnet.dwoss.core.widget.Dl;
import eu.ggnet.dwoss.core.widget.auth.Guardian;
import eu.ggnet.dwoss.core.widget.dl.RemoteDl;
import eu.ggnet.dwoss.receipt.ui.shipment.ShipmentInclusionView.In;
import eu.ggnet.dwoss.stock.ee.StockAgent;
import eu.ggnet.dwoss.stock.ee.entity.Shipment;
import eu.ggnet.dwoss.stock.ee.entity.Shipment.Status;
import eu.ggnet.dwoss.stock.ee.entity.StockTransaction;
import eu.ggnet.dwoss.stock.spi.ActiveStock;
import eu.ggnet.saft.core.Saft;
import eu.ggnet.saft.core.ui.UiParent;

import static javafx.stage.Modality.WINDOW_MODAL;

public class ShipmentListController {

    @Inject
    private Saft saft;

    @Inject
    private RemoteDl remote;

    @Inject
    private Guardian guardian;

    private ShipmentModel model;

    public ShipmentModel getModel() {
        if ( this.model == null ) { // Lazy Init
            model = new ShipmentModel(remote.lookup(StockAgent.class).findAll(Shipment.class));
        }
        return model;
    }

    public void editShipment(UiParent parent) {
        saft.build().parent(parent).title("Shipment bearbeiten").fx().eval(() -> model.getSelected(), ShipmentEditView.class).cf()
                .thenApplyAsync(model::remove, EventQueue::invokeLater)
                .thenApplyAsync(remote.lookup(StockAgent.class)::merge, saft.executorService()::submit)
                .thenApplyAsync(model::add, EventQueue::invokeLater)
                .handle(saft.handler(parent));
    }

    private StockTransaction findOrCreateStockTransaction() {
        return remote.lookup(StockAgent.class).findOrCreateRollInTransaction(
                Dl.local().lookup(ActiveStock.class).getActiveStock().id,
                guardian.getUsername(),
                "Roll in through Inclusion");
    }

    /**
     * Starts the Inclusion.
     *
     * @param parent ui parent.
     */
    public void inclusion(UiParent parent) {
        CompletableFuture
                .runAsync(() -> {
                    if ( model.getSelected() == null ) {
                        saft.build().parent(parent).alert("Kein Shipment ausgewählt");
                        throw new CancellationException("No Shipment selected");
                    }
                })
                .thenCompose((Void v) -> saft.build().parent(parent).modality(WINDOW_MODAL).swing().eval(() -> new In(model.getSelected(), findOrCreateStockTransaction()), ShipmentInclusionView.class).cf())
                .thenApplyAsync((Status st) -> {
                    Shipment shipment = model.getSelected();
                    model.remove(shipment);
                    shipment.setStatus(st);
                    return shipment;
                }, EventQueue::invokeLater)
                .thenApplyAsync(remote.lookup(StockAgent.class)::merge, saft.executorService()::submit)
                .thenApplyAsync(model::add, EventQueue::invokeLater);

//        Shipment shipment = model.getSelected();
//        if ( shipment == null ) {
//            saft.build().parent(parent).alert("Kein Shipment ausgewählt");
//            return;
//        }
//        StockTransaction stockTransaction = remote.lookup(StockAgent.class).findOrCreateRollInTransaction(Dl.local().lookup(ActiveStock.class).getActiveStock().id,
//                guardian.getUsername(),
//                "Roll in through Inclusion");
//
//        Optional<Window> optWindow = saft.core(Swing.class).unwrap(parent);
//        ShipmentInclusionViewCask sip = new ShipmentInclusionViewCask(optWindow.orElse(null), shipment, stockTransaction);
//        optWindow.ifPresent(w -> sip.setLocationRelativeTo(w));
//        sip.setVisible(true);
//        if ( sip.inclusionClosed() ) shipment.setStatus(Shipment.Status.CLOSED);
//        else if ( sip.inclusionAborted() ) shipment.setStatus(Shipment.Status.OPENED);
//        else return;
//
//        model.remove(shipment);
//        shipment = remote.lookup(StockAgent.class).merge(shipment);
//        model.add(shipment);
    }

    public void createShipment(UiParent parent) {
        saft.build().parent(parent).title("Shipment anlegen").fx().eval(ShipmentEditView.class).cf()
                .thenApply(remote.lookup(StockAgent.class)::persist)
                .thenApplyAsync(model::add, EventQueue::invokeLater)
                .handle(saft.handler(parent));
    }

    public void deleteShipment() {
        Shipment shipment = model.getSelected();
        if ( shipment == null ) return;
        if ( JOptionPane.showConfirmDialog(null,
                "Shipment " + shipment.getShipmentId() + " wirklich löschen ?", "Shipment löschen",
                JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION ) return;
        remote.lookup(StockAgent.class).delete(shipment);
        model.remove(shipment);
    }
}
