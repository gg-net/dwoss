package eu.ggnet.dwoss.redtape.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import eu.ggnet.saft.core.Workspace;

import eu.ggnet.dwoss.redtape.SalesProductViewCask;

import eu.ggnet.dwoss.util.OkCancelDialog;

import static eu.ggnet.saft.core.Client.lookup;

/**
 * @author bastian.venz
 * @author oliver.guenther
 * @author pascal.perau
 */
public class SalesProductAction extends AbstractAction {

    public SalesProductAction() {
        super("Neuwarenartikel für Verkauf verwalten");
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        SalesProductViewCask cask = new SalesProductViewCask();
        OkCancelDialog<SalesProductViewCask> dialog = new OkCancelDialog<>(lookup(Workspace.class).getMainFrame(), "Neuwarenartikel für Verkauf verwalten", cask);
        dialog.setVisible(enabled);
    }
}
