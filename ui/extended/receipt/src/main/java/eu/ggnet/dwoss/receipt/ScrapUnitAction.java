/* 
 * Copyright (C) 2014 GG-Net GmbH - Oliver Günther
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.ggnet.dwoss.receipt;

import java.awt.Window;
import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import org.openide.util.Lookup;

import eu.ggnet.saft.core.ui.Workspace;
import eu.ggnet.saft.core.auth.Guardian;

import eu.ggnet.dwoss.receipt.UnitDestroyer;

import eu.ggnet.saft.core.auth.AccessableAction;

import eu.ggnet.dwoss.uniqueunit.entity.UniqueUnit;

import eu.ggnet.dwoss.util.UserInfoException;

import static eu.ggnet.saft.Client.lookup;
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
