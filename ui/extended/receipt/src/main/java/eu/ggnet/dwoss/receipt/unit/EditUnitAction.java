package eu.ggnet.dwoss.receipt.unit;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import eu.ggnet.saft.core.Workspace;

import eu.ggnet.dwoss.receipt.UnitProcessor;

import eu.ggnet.dwoss.receipt.UiUnitSupport;
import eu.ggnet.saft.core.authorisation.AccessableAction;

import eu.ggnet.dwoss.util.UserInfoException;

import static eu.ggnet.saft.core.Client.lookup;
import static eu.ggnet.dwoss.rights.api.AtomicRight.UPDATE_UNIQUE_UNIT;

/**
 * Action to allow the Manipulation of an existing Unit.
 * <p/>
 * @author oliver.guenther
 */
public class EditUnitAction extends AccessableAction {

    /**
     * Default Constructor.
     */
    public EditUnitAction() {
        super(UPDATE_UNIQUE_UNIT);
    }

    /**
     * Action to allow the Manipulation of an existing Unit.
     * <p/>
     * @author oliver.guenther
     * @param e the event
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        String refurbishedId = JOptionPane.showInputDialog(lookup(Workspace.class).getMainFrame(), "Bitte SopoNr/Seriennummer eingeben:");
        try {
            UiUnitSupport uiUnitSupport = new UiUnitSupport(lookup(UnitProcessor.class));
            uiUnitSupport.editUnit(refurbishedId);
        } catch (UserInfoException ex) {
            JOptionPane.showMessageDialog(lookup(Workspace.class).getMainFrame(), ex.getMessage(), "Nutzerfehler", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
