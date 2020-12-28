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
package eu.ggnet.dwoss.receipt.ui.cap;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;

import eu.ggnet.dwoss.core.widget.AccessableAction;
import eu.ggnet.dwoss.receipt.ui.shipment.ShipmentController;
import eu.ggnet.dwoss.receipt.ui.shipment.ShipmentDialog;

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
        EventQueue.invokeLater(() -> {
            new ShipmentDialog(new ShipmentController()).setVisible(true);
        });
    }
}
