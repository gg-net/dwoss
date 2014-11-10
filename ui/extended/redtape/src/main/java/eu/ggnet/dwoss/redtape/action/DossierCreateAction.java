package eu.ggnet.dwoss.redtape.action;

import eu.ggnet.dwoss.rules.DocumentType;
import eu.ggnet.dwoss.rules.CustomerFlag;
import eu.ggnet.dwoss.redtape.entity.Document;
import eu.ggnet.dwoss.redtape.entity.Dossier;
import eu.ggnet.dwoss.util.CloseType;
import eu.ggnet.dwoss.util.OkCancelDialog;
import eu.ggnet.dwoss.common.ExceptionUtil;

import java.awt.*;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import eu.ggnet.saft.core.authorisation.Guardian;

import eu.ggnet.dwoss.customer.api.CustomerMetaData;
import eu.ggnet.dwoss.customer.api.CustomerService;
import eu.ggnet.dwoss.mandator.MandatorSupporter;
import eu.ggnet.dwoss.mandator.api.value.SpecialSystemCustomers;

import eu.ggnet.dwoss.redtape.RedTapeWorker;
import eu.ggnet.dwoss.redtape.RedTapeWorker.Addresses;

import eu.ggnet.dwoss.redtape.RedTapeController;
import eu.ggnet.dwoss.redtape.document.DocumentUpdateController;
import eu.ggnet.dwoss.redtape.document.DocumentUpdateView;
import eu.ggnet.dwoss.util.UserInfoException;

import static eu.ggnet.saft.core.Client.lookup;

/**
 *
 * @author pascal.perau
 */
public class DossierCreateAction extends AbstractAction {

    private final Window parent;

    private final boolean dispatch;

    private final RedTapeController controller;

    private final CustomerMetaData customer;

    public DossierCreateAction(Window parent, boolean dispatch, RedTapeController controller, long customerId) {
        this.parent = parent;
        this.dispatch = dispatch;
        this.customer = lookup(CustomerService.class).asCustomerMetaData(customerId);
        this.controller = controller;
        SpecialSystemCustomers special = lookup(MandatorSupporter.class).loadSystemCustomers();
        if ( customer.getFlags().contains(CustomerFlag.SYSTEM_CUSTOMER) ) {
            putValue(NAME, special.get(customerId).orElse(DocumentType.BLOCK).getName());
        } else {
            putValue(NAME, (dispatch ? "Versandauftrag" : "Abholauftrag"));
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Dossier dos = lookup(RedTapeWorker.class).create(customer.getId(), dispatch, lookup(Guardian.class).getUsername());
        // This is safe, as a create will return exactly one document.
        Document doc = dos.getDocuments().iterator().next();

        //document view
        DocumentUpdateView docView = new DocumentUpdateView(doc);
        docView.setController(new DocumentUpdateController(docView, doc));
        docView.setCustomerValues(customer.getId());

        //document addresses
        Addresses addresses = lookup(RedTapeWorker.class).requestAdressesByCustomer(customer.getId());
        doc.setInvoiceAddress(addresses.getInvoice());
        doc.setShippingAddress(addresses.getShipping());

        OkCancelDialog<DocumentUpdateView> dialog = new OkCancelDialog<>(controller.getView(), Dialog.ModalityType.DOCUMENT_MODAL, "Auftrag erstellen", docView);
        dialog.setLocationRelativeTo(controller.getView());
        dialog.setVisible(true);
        if ( dialog.getCloseType() == CloseType.OK ) {
            Document updatedDoc = lookup(RedTapeWorker.class).update(doc, null, lookup(Guardian.class).getUsername());
            controller.getDossierTableController().getModel().add(updatedDoc.getDossier());
        } else {
            try {
                lookup(RedTapeWorker.class).revertCreate(doc);
            } catch (UserInfoException ex) {
                ExceptionUtil.show(parent, ex);
            }
        }
    }
}
