package eu.ggnet.dwoss.receipt;

import java.awt.event.ActionEvent;

import eu.ggnet.dwoss.receipt.shipment.ShipmentController;
import eu.ggnet.dwoss.receipt.shipment.ShipmentDialog;
import eu.ggnet.saft.core.authorisation.AccessableAction;

import static eu.ggnet.dwoss.rights.api.AtomicRight.READ_CREATE_UPDATE_DELETE_SHIPMENTS;

/**
 *
 * @author oliver.guenther
 */
public class OpenShipmentAction extends AccessableAction {

    public OpenShipmentAction() {
        super(READ_CREATE_UPDATE_DELETE_SHIPMENTS);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        new ShipmentDialog(new ShipmentController()).setVisible(true);
    }
}
