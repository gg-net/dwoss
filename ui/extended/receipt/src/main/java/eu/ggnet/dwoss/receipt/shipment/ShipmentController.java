package eu.ggnet.dwoss.receipt.shipment;

import eu.ggnet.dwoss.stock.entity.Shipment;
import eu.ggnet.dwoss.stock.entity.StockTransaction;
import eu.ggnet.dwoss.stock.entity.Stock;

import javax.swing.JOptionPane;

import eu.ggnet.saft.core.Workspace;
import eu.ggnet.saft.core.authorisation.Guardian;

import eu.ggnet.dwoss.receipt.AbstractController;

import eu.ggnet.dwoss.stock.StockAgent;

import javafx.application.Platform;
import javafx.stage.Modality;

import static eu.ggnet.saft.core.Client.lookup;

public class ShipmentController extends AbstractController {

    private ShipmentModel model;

    private Stock stock;

    private StockAgent stockAgent;

    private String userName;

    /**
     * Constructor, with Service Discovery.
     *
     * @param stockId
     */
    public ShipmentController() {
        this(lookup(Workspace.class).getValue(Stock.class), lookup(Guardian.class).getUsername(), lookup(StockAgent.class));
    }

    public ShipmentController(Stock stock, String userName, StockAgent stockAgent) {
        this.stock = stock;
        this.userName = userName;
        this.stockAgent = stockAgent;
        if ( stockAgent == null ) throw new NullPointerException("ReceiptShipmentLogic is null");
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
        StockTransaction stockTransaction = stockAgent.findOrCreateRollInTransaction(stock.getId(), userName, "Roll in through Inclusion");
        ShipmentInclusionViewCask sip = new ShipmentInclusionViewCask(view, shipment, stockTransaction);
        sip.setLocationRelativeTo(view);
        sip.setVisible(true);
        if ( sip.isInclusionClosed() ) shipment.setStatus(Shipment.Status.CLOSED);
        else if ( sip.isInclusionAbort() ) shipment.setStatus(Shipment.Status.OPENED);
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
