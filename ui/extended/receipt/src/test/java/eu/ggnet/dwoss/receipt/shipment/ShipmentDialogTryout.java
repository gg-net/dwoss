package eu.ggnet.dwoss.receipt.shipment;

import eu.ggnet.dwoss.receipt.shipment.ShipmentDialog;
import eu.ggnet.dwoss.receipt.shipment.ShipmentController;

import javax.swing.UIManager;

import org.junit.Test;

import eu.ggnet.dwoss.stock.entity.Stock;

/**
 *
 * @author oliver.guenther
 */
public class ShipmentDialogTryout {

    @Test
    public void testShipment() throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        ShipmentController controller = new ShipmentController(new Stock(0, "Test"), "No User", new ReceiptShipmentOperationStub());
        ShipmentDialog dialog = new ShipmentDialog(new javax.swing.JFrame(), controller);
        dialog.setVisible(true);
    }

}
