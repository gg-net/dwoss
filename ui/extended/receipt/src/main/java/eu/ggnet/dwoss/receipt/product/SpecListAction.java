package eu.ggnet.dwoss.receipt.product;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import eu.ggnet.saft.core.Workspace;

import eu.ggnet.dwoss.util.OkCancelDialog;

import static eu.ggnet.saft.core.Client.lookup;

/**
 *
 * @author oliver.guenther
 */
public class SpecListAction extends AbstractAction {

    public SpecListAction() {
        super("Modelle verwalten");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        OkCancelDialog<SpecListPanel> dialog = new OkCancelDialog<>(lookup(Workspace.class).getMainFrame(),"Brand-Familiy-Model-Series", new SpecListPanel(new SpecListController()));
        dialog.setVisible(true);
    }

}
