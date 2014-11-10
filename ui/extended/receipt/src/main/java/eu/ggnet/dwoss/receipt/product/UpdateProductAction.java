package eu.ggnet.dwoss.receipt.product;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import eu.ggnet.saft.core.Workspace;
import eu.ggnet.saft.core.authorisation.AccessableAction;

import eu.ggnet.dwoss.receipt.UiProductSupport;

import eu.ggnet.dwoss.uniqueunit.UniqueUnitAgent;
import eu.ggnet.dwoss.uniqueunit.entity.Product;

import eu.ggnet.dwoss.util.UserInfoException;

import eu.ggnet.dwoss.common.ExceptionUtil;

import static eu.ggnet.saft.core.Client.lookup;
import static eu.ggnet.dwoss.rights.api.AtomicRight.UPDATE_PRODUCT;

/**
 * Allow the modification of a Product/Part.
 * <p/>
 * @author oliver.guenther
 */
public class UpdateProductAction extends AccessableAction {

    public UpdateProductAction() {
        super(UPDATE_PRODUCT);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            String partNo = JOptionPane.showInputDialog(lookup(Workspace.class).getMainFrame(), "Bitte Artikelnummer des Herstellers eingeben:");
            Product product = lookup(UniqueUnitAgent.class).findProductByPartNo(partNo);
            if ( product == null ) {
                JOptionPane.showMessageDialog(lookup(Workspace.class).getMainFrame(), "Artikel " + partNo + " existiert nicht, bitte über Aufnahme erfassen",
                        "Fehler", JOptionPane.ERROR_MESSAGE);
            } else {
                // Hint: We need the Manufacturer of the Porduct in advance, as we initialize all Validator elements.
                // If We want to allow creation of new Products here, the workflow must be enhanced.
                new UiProductSupport().createOrEditPart(product.getTradeName().getManufacturer(), partNo, lookup(Workspace.class).getMainFrame());
            }
        } catch (UserInfoException ex) {
            ExceptionUtil.show(lookup(Workspace.class).getMainFrame(), ex);
        }
    }
}
