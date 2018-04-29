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
package eu.ggnet.dwoss.rights.ui.cap;

import java.awt.event.ActionEvent;

import eu.ggnet.dwoss.rights.ui.RightsManagmentController;
import eu.ggnet.saft.Ui;
import eu.ggnet.saft.core.auth.AccessableAction;

import static eu.ggnet.dwoss.rights.api.AtomicRight.CREATE_UPDATE_RIGHTS;

/**
 *
 * @author Bastian Venz
 */
public class RightsManagmentAction extends AccessableAction {

    public RightsManagmentAction() {
        super(CREATE_UPDATE_RIGHTS);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Ui.exec(() -> {
            Ui.build().fxml().show(RightsManagmentController.class);
        });
    }

}
