package eu.ggnet.dwoss.receipt;

import java.awt.Window;
import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import org.openide.util.Lookup;

import eu.ggnet.saft.core.Workspace;
import eu.ggnet.saft.core.authorisation.Guardian;

import eu.ggnet.dwoss.receipt.UnitDestroyer;

import eu.ggnet.saft.core.authorisation.AccessableAction;

import eu.ggnet.dwoss.uniqueunit.entity.UniqueUnit;

import eu.ggnet.dwoss.util.UserInfoException;

import static eu.ggnet.saft.core.Client.lookup;
import static eu.ggnet.dwoss.rights.api.AtomicRight.UPDATE_UNIQUE_UNIT_TO_SCRAP_UNIT;
import static javax.swing.JOptionPane.*;

/**
 *
 * @author oliver.guenther
 */
public class ScrapUnitAction extends AccessableAction {

    public ScrapUnitAction() {
        super(UPDATE_UNIQUE_UNIT_TO_SCRAP_UNIT);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            Window mainFrame = lookup(Workspace.class).getMainFrame();
            UnitDestroyer destroyer = lookup(UnitDestroyer.class);
            String refurbishedId = showInputDialog(mainFrame, "SopoNr die verschrottet werden soll:");
            if ( refurbishedId == null ) return;
            UniqueUnit uniqueUnit = destroyer.verifyScarpOrDeleteAble(refurbishedId);
            if ( JOptionPane.YES_OPTION != showConfirmDialog(mainFrame,
                    "SopoNr " + refurbishedId + " wirklich verschrotten ?", "Verschrottung", YES_NO_OPTION) ) return;
            String reason = JOptionPane.showInputDialog(mainFrame, "Bitte grund angeben.");
            destroyer.scrap(uniqueUnit, reason, Lookup.getDefault().lookup(Guardian.class).getUsername());
            showMessageDialog(mainFrame, "SopoNr " + refurbishedId + " ist verschrottet.");
        } catch (UserInfoException ex) {
            JOptionPane.showMessageDialog(lookup(Workspace.class).getMainFrame(), ex.getMessage());
        }
    }
}
