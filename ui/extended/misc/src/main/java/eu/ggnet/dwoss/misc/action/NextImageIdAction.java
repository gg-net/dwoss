package eu.ggnet.dwoss.misc.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import eu.ggnet.saft.core.Workspace;

import eu.ggnet.dwoss.misc.op.listings.SalesListingProducer;

import static eu.ggnet.saft.core.Client.lookup;

/**
 *
 * @author oliver.guenther
 */
public class NextImageIdAction extends AbstractAction {

    public NextImageIdAction() {
        super("Nächte Bilder Id");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JOptionPane.showMessageDialog(lookup(Workspace.class).getMainFrame(), "Die nächste BilderId ist " + lookup(SalesListingProducer.class).nextImageId());
    }
}
