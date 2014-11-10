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
import static eu.ggnet.dwoss.rights.api.AtomicRight.DELETE_UNIQUE_UNIT;
import static javax.swing.JOptionPane.*;

/**
 * Delete a Unit from the Database.
 * <p/>
 * @author oliver.guenther
 */
public class DeleteUnitAction extends AccessableAction {

    public DeleteUnitAction() {
        super(DELETE_UNIQUE_UNIT);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            Window mainFrame = lookup(Workspace.class).getMainFrame();
            UnitDestroyer deleteUnitOp = lookup(UnitDestroyer.class);
            String refurbishedId = showInputDialog(mainFrame, "SopoNr die gelöscht werden soll:");
            if ( refurbishedId == null ) return;
            UniqueUnit uniqueUnit = deleteUnitOp.verifyScarpOrDeleteAble(refurbishedId);
            if ( JOptionPane.YES_OPTION != showConfirmDialog(mainFrame,
                    "SopoNr " + refurbishedId + " wirklich löschen ?", "Löschung", YES_NO_OPTION) ) return;
            deleteUnitOp.delete(uniqueUnit, "Löschung aus UI", Lookup.getDefault().lookup(Guardian.class).getUsername());
            showMessageDialog(mainFrame, "SopoNr " + refurbishedId + " ist gelöscht.");
        } catch (UserInfoException ex) {
            JOptionPane.showMessageDialog(lookup(Workspace.class).getMainFrame(), ex.getMessage());
        }
    }
}
