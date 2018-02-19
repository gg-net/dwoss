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
package eu.ggnet.dwoss.customer.ui.cap;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import eu.ggnet.dwoss.customer.ee.CustomerAgent;
import eu.ggnet.dwoss.customer.ee.entity.Customer;
import eu.ggnet.dwoss.customer.ui.neo.CustomerEnhanceController;
import eu.ggnet.dwoss.customer.ui.neo.CustomerSimpleController;
import eu.ggnet.saft.Dl;
import eu.ggnet.saft.Ui;
import eu.ggnet.saft.api.Reply;

/**
 *
 * @author pascal.perau
 */
public class AddCustomerAction extends AbstractAction {

    public AddCustomerAction() {
        super("Neuen Kunden anlegern");
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        Ui.exec(() -> {
            Ui.build().fxml().eval(CustomerSimpleController.class).opt().map(r -> {
                Reply<Customer> reply = Dl.remote().lookup(CustomerAgent.class).store(r.simpleCustomer);
                if ( r.continueEnhance ) {
                    Ui.build().fxml().eval(() -> reply.getPayload(), CustomerEnhanceController.class).opt().ifPresent(c -> Ui.build().alert("Would store + " + c));
                    return false;
                }
                return false;
            });
        });
    }
}
