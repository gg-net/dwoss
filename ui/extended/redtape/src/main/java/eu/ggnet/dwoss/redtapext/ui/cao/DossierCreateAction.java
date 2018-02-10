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
package eu.ggnet.dwoss.redtapext.ui.cao;

import java.awt.Window;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import eu.ggnet.dwoss.customer.opi.CustomerMetaData;
import eu.ggnet.dwoss.customer.opi.CustomerService;
import eu.ggnet.dwoss.mandator.api.value.SpecialSystemCustomers;
import eu.ggnet.dwoss.mandator.upi.CachedMandators;
import eu.ggnet.dwoss.redtape.ee.entity.Document;
import eu.ggnet.dwoss.redtape.ee.entity.Dossier;
import eu.ggnet.dwoss.redtapext.ee.RedTapeWorker;
import eu.ggnet.dwoss.redtapext.ee.RedTapeWorker.Addresses;
import eu.ggnet.dwoss.redtapext.ui.cao.document.DocumentUpdateController;
import eu.ggnet.dwoss.redtapext.ui.cao.document.DocumentUpdateView;
import eu.ggnet.dwoss.rules.CustomerFlag;
import eu.ggnet.dwoss.rules.DocumentType;
import eu.ggnet.dwoss.util.UserInfoException;
import eu.ggnet.saft.Dl;
import eu.ggnet.saft.Ui;
import eu.ggnet.saft.api.Reply;
import eu.ggnet.saft.core.auth.Guardian;
import eu.ggnet.saft.core.swing.OkCancelWrap;

/**
 *
 * @author pascal.perau
 */
public class DossierCreateAction extends AbstractAction {

    private final Window parent;

    private final boolean dispatch;

    private final RedTapeController controller;

    private final CustomerMetaData customer;

    @SuppressWarnings("OverridableMethodCallInConstructor")
    public DossierCreateAction(Window parent, boolean dispatch, RedTapeController controller, long customerId) {
        this.parent = parent;
        this.dispatch = dispatch;
        this.customer = Dl.remote().lookup(CustomerService.class).asCustomerMetaData(customerId);
        this.controller = controller;
        SpecialSystemCustomers special = Dl.local().lookup(CachedMandators.class).loadSystemCustomers();
        if ( customer.getFlags().contains(CustomerFlag.SYSTEM_CUSTOMER) ) {
            putValue(NAME, special.get(customerId).orElse(DocumentType.BLOCK).getName());
        } else {
            putValue(NAME, (dispatch ? "Versandauftrag" : "Abholauftrag"));
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Ui.exec(() -> {
            Dossier dos = Dl.remote().lookup(RedTapeWorker.class).create(customer.getId(), dispatch, Dl.local().lookup(Guardian.class).getUsername());
            Document doc = dos.getDocuments().iterator().next();  // This is safe, as a create will return exactly one document.

            Addresses addresses = Dl.remote().lookup(RedTapeWorker.class).requestAdressesByCustomer(customer.getId());
            doc.setInvoiceAddress(addresses.getInvoice());
            doc.setShippingAddress(addresses.getShipping());

            Ui.build().parent(controller.getView()).swing().eval(() -> {
                DocumentUpdateView docView = new DocumentUpdateView(doc);
                docView.setController(new DocumentUpdateController(docView, doc));
                docView.setCustomerValues(customer.getId());
                return OkCancelWrap.vetoResult(docView);
            }).filter(r -> handleFailure(r, doc))
                    .map(Reply::getPayload)
                    .ifPresent(this::handleSuccesses);
        });
    }

    private void handleSuccesses(Document doc) {
        Document updatedDoc = Dl.remote().lookup(RedTapeWorker.class).update(doc, null, Dl.local().lookup(Guardian.class).getUsername());
        controller.getDossierTableController().getModel().add(updatedDoc.getDossier());
    }

    private boolean handleFailure(Reply<?> reply, Document doc) {
        if ( reply.hasSucceded() ) return true;
        try {
            Dl.remote().lookup(RedTapeWorker.class).revertCreate(doc);
        } catch (UserInfoException ex) {
            Ui.handle(ex);
        }
        return false;
    }
}
