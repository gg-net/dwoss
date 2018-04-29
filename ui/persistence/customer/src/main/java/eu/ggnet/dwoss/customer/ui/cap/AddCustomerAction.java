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
import java.util.Optional;

import javax.swing.AbstractAction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.customer.ee.CustomerAgent;
import eu.ggnet.dwoss.customer.ee.entity.Customer;
import eu.ggnet.dwoss.customer.ui.neo.CustomerEnhanceController;
import eu.ggnet.dwoss.customer.ui.neo.CustomerSimpleController;
import eu.ggnet.dwoss.customer.ui.neo.CustomerSimpleController.CustomerContinue;
import eu.ggnet.saft.core.Dl;
import eu.ggnet.saft.core.Ui;
import eu.ggnet.saft.api.Reply;

/**
 *
 * @author pascal.perau
 */
public class AddCustomerAction extends AbstractAction {

    private final static Logger L = LoggerFactory.getLogger(AddCustomerAction.class);

    public AddCustomerAction() {
        super("Neuen Kunden anlegern");
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        Ui.exec(() -> {
            Optional<CustomerContinue> result = Ui.build().fxml().eval(CustomerSimpleController.class).opt();
            if ( !result.isPresent() ) return;
            Reply<Customer> reply = Dl.remote().lookup(CustomerAgent.class).store(result.get().simpleCustomer);
            if ( !Ui.failure().handle(reply) ) return;
            if ( !result.get().continueEnhance ) return;
            Ui.build().fxml().eval(() -> reply.getPayload(), CustomerEnhanceController.class)
                    .opt().ifPresent(c -> Ui.build().alert("Would store + " + c));
        });
    }

}
