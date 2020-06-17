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
package eu.ggnet.dwoss.stock.ui.cap;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import eu.ggnet.dwoss.stock.ui.*;
import eu.ggnet.saft.core.Ui;

/**
 *
 * @author oliver.guenther
 */
public class OpenCommissioningManager extends AbstractAction {

    public OpenCommissioningManager() {
        super("Kommissionsmanager");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Ui.build().title("Kommissionsmanager").swing().show(() -> {
            CommissioningManagerModel model = new CommissioningManagerModel();
            CommissioningManagerView view = new CommissioningManagerView();
            CommissioningManagerController controller = new CommissioningManagerController();
            view.setModel(model);
            view.setController(controller);
            controller.setModel(model);
            controller.setView(view);
            return view;
        });

    }
}
