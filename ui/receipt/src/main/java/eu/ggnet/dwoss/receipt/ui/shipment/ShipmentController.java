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

import javax.swing.JOptionPane;

import javafx.application.Platform;
import javafx.stage.Modality;

import eu.ggnet.dwoss.receipt.ui.AbstractController;
import eu.ggnet.dwoss.stock.ee.StockAgent;
import eu.ggnet.dwoss.stock.ee.entity.Shipment;
import eu.ggnet.dwoss.stock.ee.entity.StockTransaction;
import eu.ggnet.dwoss.core.widget.Dl;
import eu.ggnet.dwoss.core.widget.auth.Guardian;
import eu.ggnet.dwoss.stock.spi.ActiveStock;

public class ShipmentController extends AbstractController {

    private ShipmentModel model;

    private StockAgent stockAgent;

    public ShipmentController() {
        this.stockAgent = Dl.remote().lookup(StockAgent.class);
        if ( stockAgent == null ) throw new NullPointerException(StockAgent.class.getName() + " is null");
    }

    public ShipmentModel getModel() {
        if ( this.model == null ) { // Lazy Init
            model = new ShipmentModel(stockAgent.findAll(Shipment.class));
        }
        return model;
    }

    public void editShipment() {

        Platform.runLater(() -> {
            Shipment shipment = model.getSelected();
            if ( shipment == null ) return;
            ShipmentUpdateStage stage = new ShipmentUpdateStage(shipment);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            if ( !stage.isOk() ) return;
            shipment = stage.getShipment();
            model.remove(shipment);
            shipment = stockAgent.merge(shipment);
            model.add(shipment);
        });
    }

    /**
     * Starts the Inclusion.
     *
     * @param row
     */
    public void inclusion() {
        Shipment shipment = model.getSelected();
        if ( shipment == null ) return;
        StockTransaction stockTransaction = stockAgent.findOrCreateRollInTransaction(Dl.local().lookup(ActiveStock.class).getActiveStock().id,
                Dl.local().lookup(Guardian.class).getUsername(),
                "Roll in through Inclusion");
        ShipmentInclusionViewCask sip = new ShipmentInclusionViewCask(view, shipment, stockTransaction);
        sip.setLocationRelativeTo(view);
        sip.setVisible(true);
        if ( sip.inclusionClosed() ) shipment.setStatus(Shipment.Status.CLOSED);
        else if ( sip.inclusionAborted() ) shipment.setStatus(Shipment.Status.OPENED);
        else return;

        model.remove(shipment);
        shipment = stockAgent.merge(shipment);
        model.add(shipment);
    }

    public void createShipment() {
        Platform.runLater(() -> {
            ShipmentUpdateStage stage = new ShipmentUpdateStage(new Shipment());
            stage.showAndWait();
            if ( !stage.isOk() ) return;
            Shipment shipment = stockAgent.persist(stage.getShipment());
            model.add(shipment);
        });
    }

    public void deleteShipment() {
        Shipment shipment = model.getSelected();
        if ( shipment == null ) return;
        if ( JOptionPane.showConfirmDialog(view,
                "Shipment " + shipment.getShipmentId() + " wirklich löschen ?", "Shipment löschen",
                JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION ) return;
        stockAgent.delete(shipment);
        model.remove(shipment);
    }
}
