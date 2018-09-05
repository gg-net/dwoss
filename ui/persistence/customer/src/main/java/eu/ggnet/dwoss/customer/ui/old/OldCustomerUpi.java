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
package eu.ggnet.dwoss.customer.ui.old;

import java.awt.Dialog;
import java.util.Optional;
import java.util.function.Consumer;

import javax.swing.JOptionPane;

import org.openide.util.lookup.ServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.common.ui.CloseType;
import eu.ggnet.dwoss.common.ui.OkCancelDialog;
import eu.ggnet.dwoss.customer.api.AddressService;
import eu.ggnet.dwoss.customer.ee.CustomerAgent;
import eu.ggnet.dwoss.customer.ee.entity.Customer;
import eu.ggnet.dwoss.customer.ee.priv.OldCustomer;
import eu.ggnet.dwoss.customer.ee.priv.OldCustomerAgent;
import eu.ggnet.dwoss.customer.ui.neo.CustomerEnhanceController;
import eu.ggnet.dwoss.customer.ui.neo.CustomerSimpleController;
import eu.ggnet.dwoss.customer.ui.neo.CustomerSimpleController.CustomerContinue;
import eu.ggnet.dwoss.customer.upi.CustomerUpi;
import eu.ggnet.dwoss.redtape.api.event.AddressChange;
import eu.ggnet.saft.api.Reply;
import eu.ggnet.saft.core.Dl;
import eu.ggnet.saft.core.Ui;
import eu.ggnet.saft.core.ui.SwingCore;
import eu.ggnet.saft.core.ui.UiParent;

/**
 *
 * @author pascal.perau
 */
@ServiceProvider(service = CustomerUpi.class)
public class OldCustomerUpi implements CustomerUpi {

    private static final Logger L = LoggerFactory.getLogger(OldCustomerUpi.class);

    @Override
    public void createCustomer(UiParent parent, Consumer<Long> csmr) {

        Ui.exec(() -> {
            Optional<CustomerContinue> result = Ui.build().fxml().eval(CustomerSimpleController.class).opt();
            if ( !result.isPresent() ) {
                csmr.accept(0l);
                return;
            }
            final Customer customer;
            Reply<Customer> reply;
            if ( result.get().customer == null || result.get().customer.getId() < 0 ) {
                reply = Dl.remote().lookup(CustomerAgent.class).store(result.get().simpleCustomer);
                if ( !Ui.failure().handle(reply) ) {
                    csmr.accept(0l);
                    return;
                }
                customer = reply.getPayload();
            } else {
                customer = result.get().customer;
            }

            if ( !result.get().continueEnhance ) {
                csmr.accept(customer.getId());
                return;
            }
            Ui.build().fxml().eval(() -> customer, CustomerEnhanceController.class)
                    .opt().ifPresent(c -> csmr.accept(customer.getId()));
        });

    }

    @Override
    public boolean updateCustomer(UiParent parent, long customerId) {
        OldCustomer customer = Dl.remote().lookup(OldCustomerAgent.class).findById(customerId);
        CustomerEditView ec = new CustomerEditView();
        ec.setCustomer(customer);
        // HINT: This was RedTapeView as parrent. If users complain about the location of create customer, add it to Workspace or else.
        OkCancelDialog<CustomerEditView> dialog = new OkCancelDialog<>(SwingCore.windowAncestor(Optional.ofNullable(parent).map(UiParent::swingOrMain).orElse(null)).orElse(SwingCore.mainFrame()), Dialog.ModalityType.DOCUMENT_MODAL, "Kunden editieren", ec);
        dialog.setVisible(true);

        boolean changed = false;
        if ( dialog.getCloseType() == CloseType.OK ) {
            Dl.remote().lookup(OldCustomerAgent.class).store(ec.getCustomer());
            for (AddressChange addressChange : ec.getChangedAdresses()) {
                if ( JOptionPane.showOptionDialog(dialog, "Adresse wurde geändert, soll diese Änderung an allen Dokumenten des Kunden übernommen werden?", "Adressänderung", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null) == JOptionPane.YES_OPTION ) {
                    Dl.remote().lookup(AddressService.class).notifyAddressChange(addressChange);
                }
                changed = true;
            }
        }
        return changed;
    }
}
