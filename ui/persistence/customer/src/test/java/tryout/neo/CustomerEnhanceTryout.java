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
package tryout.neo;

import javax.swing.*;

import eu.ggnet.dwoss.customer.CustomerAgent;
import eu.ggnet.dwoss.customer.assist.gen.CustomerGenerator;
import eu.ggnet.dwoss.customer.entity.Customer;
import eu.ggnet.dwoss.customer.ui.neo.CustomerEnhanceController;
import eu.ggnet.saft.*;

import tryout.stub.CustomerAgentStub;

/**
 *
 * @author jens.papenhagen
 */
public class CustomerEnhanceTryout {

    public static void main(String[] args) {
       
        CustomerGenerator gen = new CustomerGenerator();
        Customer c = gen.makeCustomer();

        JButton close = new JButton("Schliessen");
        close.addActionListener(e -> Ui.closeWindowOf(close));

        JButton run = new JButton("OpenUi");      
        run.addActionListener(ev -> {
            Ui.exec(() -> {
                Ui.build().fxml().eval(() -> c, CustomerEnhanceController.class).ifPresent(System.out::println);;
            });
        });

        JPanel p = new JPanel();
        p.add(run);
        p.add(close);

        UiCore.startSwing(() -> p);

    }
}
