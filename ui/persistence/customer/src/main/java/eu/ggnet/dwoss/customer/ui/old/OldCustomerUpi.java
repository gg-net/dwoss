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

import javax.swing.JOptionPane;

import org.openide.util.lookup.ServiceProvider;

import eu.ggnet.dwoss.customer.api.AddressService;
import eu.ggnet.dwoss.customer.ee.priv.OldCustomer;
import eu.ggnet.dwoss.customer.ee.priv.OldCustomerAgent;
import eu.ggnet.dwoss.customer.upi.CustomerUpi;
import eu.ggnet.dwoss.redtape.api.event.AddressChange;
import eu.ggnet.dwoss.util.CloseType;
import eu.ggnet.dwoss.util.OkCancelDialog;
import eu.ggnet.saft.Dl;
import eu.ggnet.saft.core.ui.UiParent;
import eu.ggnet.saft.core.ui.SwingCore;

/**
 *
 * @author pascal.perau
 */
@ServiceProvider(service = CustomerUpi.class)
public class OldCustomerUpi implements CustomerUpi {

    @Override
    public long createCustomer(UiParent parent) {

        CustomerCreateWithSearchView view = new CustomerCreateWithSearchView();
        CustomerCreateWithSearchController controller = new CustomerCreateWithSearchController();
        view.setController(controller);
        controller.setView(view);
        OkCancelDialog<CustomerCreateWithSearchView> dialog = new OkCancelDialog<>(SwingCore.windowAncestor(Optional.ofNullable(parent).map(UiParent::swingOrMain).orElse(null)).orElse(SwingCore.mainFrame()),
                Dialog.ModalityType.DOCUMENT_MODAL, "Neuen Kunden anlegen", view);
        dialog.setVisible(true);
        if ( dialog.isOk() ) {
            return Dl.remote().lookup(OldCustomerAgent.class).store(view.getCustomer()).getId();
        }
        return 0;
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
