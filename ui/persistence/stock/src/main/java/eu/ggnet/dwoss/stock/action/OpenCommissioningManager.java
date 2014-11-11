/* 
 * Copyright (C) 2014 pascal.perau
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
package eu.ggnet.dwoss.stock.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import eu.ggnet.saft.core.Workspace;

import eu.ggnet.dwoss.stock.CommissioningManagerController;
import eu.ggnet.dwoss.stock.CommissioningManagerModel;
import eu.ggnet.dwoss.stock.CommissioningManagerView;

import static eu.ggnet.saft.core.Client.lookup;

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
        CommissioningManagerModel model = new CommissioningManagerModel();
        CommissioningManagerView view = new CommissioningManagerView(lookup(Workspace.class).getMainFrame());
        CommissioningManagerController controller = new CommissioningManagerController();
        view.setModel(model);
        view.setController(controller);
        controller.setModel(model);
        controller.setView(view);
        view.setVisible(true);
    }
}
