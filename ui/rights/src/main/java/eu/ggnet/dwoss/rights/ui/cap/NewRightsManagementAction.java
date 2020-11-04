/*
 * Copyright (C) 2020 GG-Net GmbH
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
package eu.ggnet.dwoss.rights.ui.cap;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import eu.ggnet.dwoss.core.widget.AccessableAction;
import eu.ggnet.dwoss.rights.ui.NewRightsManagementController;
import eu.ggnet.saft.core.Ui;

import static eu.ggnet.dwoss.rights.api.AtomicRight.CREATE_UPDATE_RIGHTS;

public class NewRightsManagementAction extends AccessableAction {

    public NewRightsManagementAction() {
        super(CREATE_UPDATE_RIGHTS);
        putValue(Action.NAME, "Neu: " + CREATE_UPDATE_RIGHTS.toName());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Ui.build().fxml().show(NewRightsManagementController.class);
    }

}
