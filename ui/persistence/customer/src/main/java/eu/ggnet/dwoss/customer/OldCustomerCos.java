package eu.ggnet.dwoss.customer;

import eu.ggnet.dwoss.util.CloseType;
import eu.ggnet.dwoss.util.OkCancelDialog;

import java.awt.*;

import javax.swing.JOptionPane;

import org.openide.util.lookup.ServiceProvider;

import eu.ggnet.dwoss.customer.api.AddressService;
import eu.ggnet.dwoss.customer.api.CustomerCos;

import eu.ggnet.dwoss.event.AddressChange;

import eu.ggnet.dwoss.customer.priv.OldCustomer;
import eu.ggnet.dwoss.customer.priv.OldCustomerAgent;

import static eu.ggnet.saft.core.Client.lookup;

/**
 *
 * @author pascal.perau
 */
@ServiceProvider(service = CustomerCos.class)
public class OldCustomerCos implements CustomerCos {

    @Override
    public long createCustomer() {
        CustomerCreateView view = new CustomerCreateView();
        CustomerCreateController controller = new CustomerCreateController();
        view.setController(controller);
        controller.setView(view);
        // HINT: This was RedTapeView as parrent. If users complain about the location of create customer, add it to Workspace or else.
        OkCancelDialog<CustomerCreateView> dialog = new OkCancelDialog<>(null, Dialog.ModalityType.DOCUMENT_MODAL, "Neuen Kunden anlegen", view);
        dialog.setVisible(true);
        if ( dialog.isOk() ) {
            return lookup(OldCustomerAgent.class).store(view.getCustomer()).getId();
        }
        return 0;
    }

    @Override
    public boolean updateCustomer(long customerId) {
        OldCustomer customer = lookup(OldCustomerAgent.class).findById(customerId);
        CustomerUpdateViewCask ec = new CustomerUpdateViewCask();
        ec.setCustomer(customer);
        // HINT: This was RedTapeView as parrent. If users complain about the location of create customer, add it to Workspace or else.
        OkCancelDialog<CustomerUpdateViewCask> dialog = new OkCancelDialog<>(null, Dialog.ModalityType.DOCUMENT_MODAL, "Kunden editieren", ec);
        dialog.setVisible(true);

        boolean changed = false;
        if ( dialog.getCloseType() == CloseType.OK ) {
            lookup(OldCustomerAgent.class).store(ec.getCustomer());
            for (AddressChange addressChange : ec.getChangedAdresses()) {
                if ( JOptionPane.showOptionDialog(dialog, "Adresse wurde geändert, soll diese Änderung an allen Dokumenten des Kunden übernommen werden?", "Adressänderung", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null) == JOptionPane.YES_OPTION ) {
                    lookup(AddressService.class).notifyAddressChange(addressChange);
                }
                changed = true;
            }
        }
        return changed;
    }
}
