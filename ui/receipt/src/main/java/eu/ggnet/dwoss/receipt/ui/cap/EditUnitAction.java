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
package eu.ggnet.dwoss.receipt.ui.cap;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import eu.ggnet.dwoss.receipt.ui.UiUnitSupport;
import eu.ggnet.dwoss.receipt.ee.UnitProcessor;
import eu.ggnet.dwoss.core.common.UserInfoException;
import eu.ggnet.saft.core.Dl;
import eu.ggnet.saft.core.UiCore;
import eu.ggnet.dwoss.core.widget.AccessableAction;

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
        String refurbishedId = JOptionPane.showInputDialog(UiCore.getMainFrame(), "Bitte SopoNr/Seriennummer eingeben:");
        try {
            UiUnitSupport uiUnitSupport = new UiUnitSupport(Dl.remote().lookup(UnitProcessor.class));
            uiUnitSupport.editUnit(refurbishedId);
        } catch (UserInfoException ex) {
            JOptionPane.showMessageDialog(UiCore.getMainFrame(), ex.getMessage(), "Nutzerfehler", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
