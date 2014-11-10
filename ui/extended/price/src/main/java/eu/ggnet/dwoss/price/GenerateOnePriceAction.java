package eu.ggnet.dwoss.price;

import java.awt.Dialog;
import java.awt.event.ActionEvent;

import eu.ggnet.saft.core.Workspace;

import eu.ggnet.dwoss.price.Exporter;
import eu.ggnet.dwoss.price.engine.PriceEngineResult;
import eu.ggnet.dwoss.price.engine.support.PriceEngineResultFormater;

import eu.ggnet.saft.core.authorisation.AccessableAction;

import eu.ggnet.dwoss.util.HtmlDialog;

import static eu.ggnet.saft.core.Client.lookup;
import static eu.ggnet.dwoss.rights.api.AtomicRight.CREATE_ONE_PRICE;
import static javax.swing.JOptionPane.showInputDialog;
import static javax.swing.JOptionPane.showMessageDialog;

/**
 *
 * @author pascal.perau
 */
public class GenerateOnePriceAction extends AccessableAction {

    public GenerateOnePriceAction() {
        super(CREATE_ONE_PRICE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String refurbishId = showInputDialog(lookup(Workspace.class).getMainFrame(), "Bitte SopoNr eingeben :");
        if ( refurbishId == null || refurbishId.isEmpty() ) return;
        PriceEngineResult per = lookup(Exporter.class).onePrice(refurbishId);
        if ( per == null ) {
            showMessageDialog(lookup(Workspace.class).getMainFrame(), "Kein Ergebins für SopoNr: " + refurbishId);
        }
        String html = PriceEngineResultFormater.toSimpleHtml(per);
        HtmlDialog dialog = new HtmlDialog(lookup(Workspace.class).getMainFrame(), Dialog.ModalityType.MODELESS);
        dialog.setText(html);
        dialog.setLocationRelativeTo(lookup(Workspace.class).getMainFrame());
        dialog.setVisible(true);
    }
}
