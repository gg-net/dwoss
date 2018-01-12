/*
 * Copyright (C) 2018 GG-Net GmbH
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
package tryout.neo.listView.popup;

import javax.swing.*;

import eu.ggnet.dwoss.customer.CustomerAgent;
import eu.ggnet.dwoss.customer.ui.neo.listView.popup.AddressEditorController;
import eu.ggnet.saft.*;

import tryout.stub.CustomerAgentStub;

/**
 *
 * @author jens.papenhagen
 */
public class AddressEditorTryout {

    public static void main(String[] args) {
        //stub for the new Costumer modell with generator needed
        Client.addSampleStub(CustomerAgent.class, new CustomerAgentStub());

        JButton close = new JButton("Schliessen");
        close.addActionListener(e -> Ui.closeWindowOf(close));

        JButton run = new JButton("OpenUi");
        run.addActionListener(ev -> {
            Ui.fxml().show(AddressEditorController.class);

        });

        JPanel p = new JPanel();
        p.add(run);
        p.add(close);

        UiCore.startSwing(() -> p);
    }
}
