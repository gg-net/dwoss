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
import eu.ggnet.dwoss.customer.ui.neo.*;
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

//    @Override
//    public long createCustomer(UiParent parent) {
//
//        CustomerCreateWithSearchView view = new CustomerCreateWithSearchView();
//        CustomerCreateWithSearchController controller = new CustomerCreateWithSearchController();
//        view.setController(controller);
//        controller.setView(view);
//        OkCancelDialog<CustomerCreateWithSearchView> dialog = new OkCancelDialog<>(SwingCore.windowAncestor(Optional.ofNullable(parent).map(UiParent::swingOrMain).orElse(null)).orElse(SwingCore.mainFrame()),
//                Dialog.ModalityType.DOCUMENT_MODAL, "Neuen Kunden anlegen", view);
//        dialog.setVisible(true);
//        if ( dialog.isOk() ) {
//            return Dl.remote().lookup(OldCustomerAgent.class).store(view.getCustomer()).getId();
//        }
//        return 0;
//    }
    @Override
    public void createCustomer(UiParent parent, Consumer<Long> csmr) {

//        Ui.exec(() -> {
//            Optional<CustomerContinue> result = Ui.build().fxml().eval(CustomerSimpleController.class).opt();
//            if ( !result.isPresent() ) return;
//            Reply<Customer> reply = Dl.remote().lookup(CustomerAgent.class).store(result.get().simpleCustomer);
//            if ( !Ui.failure().handle(reply) ) return;
//            if ( !result.get().continueEnhance ) return;
//            Ui.build().fxml().eval(() -> reply.getPayload(), CustomerEnhanceController.class)
//                    .opt().ifPresent(c -> Ui.build().alert("Would store + " + c));
//        });
        Ui.build().fxml().eval(CustomerSimpleController.class).cf()
                .thenApply((cc) -> {
                    L.info("start customer simpleController");
                    if ( cc == null ) return null;

                    L.info("try finding customer");
                    //store cutsomer
                    Reply<Customer> reply = Dl.remote().lookup(CustomerAgent.class).store(cc.simpleCustomer);
                    //handle errors
                    L.info("found customer {}", reply.getPayload());
                    if ( !Ui.failure().handle(reply) ) return null;
                    //return stored customer
                    cc.simpleCustomer = reply.getPayload().toSimple().get();

                    L.info("return customer from simple {}", cc);
                    return cc;
                })
                .thenApply((cc) -> {
                    if ( cc == null ) return null;

                    Long id = null;

                    L.info("Customer from customerSimpleController {}", cc);
                    L.info("continueEnhance: {}", cc.continueEnhance);

                    if ( cc.continueEnhance ) {
                        L.info("continue enhance");
                        Customer customer = Dl.remote().lookup(CustomerAgent.class).findById(Customer.class, cc.simpleCustomer.getId());
                        L.info("found customer {}", customer);
                        if ( customer == null ) {
                            L.info("customer is null");
                            return null;
                        } else {
                            Ui.build().fxml().eval(() -> customer, CustomerEnhanceController.class)
                                    .cf().thenApply((c) -> {
//                                        if ( c == null ) return null;

                                        L.info("Customer enhance controller update customer");

                                        //store cutsomer
                                        Dl.remote().lookup(CustomerAgent.class).update(customer);
                                        //handle errors
//                                        if ( !Ui.failure().handle(reply) ) return null;

//                                        c = reply.getPayload();
                                        return cc.simpleCustomer.getId();
                                    });
                        }

                    } else {
                        L.info("Return customer CustomerSimpleController");
                        id = cc.simpleCustomer.getId();
                    }

                    //when enhanced, open enhanced view
                    //store again
                    //not enhanced
                    //return customer id
                    L.info("last statement return null");
                    return id;
                })
                .thenAccept(csmr);

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
