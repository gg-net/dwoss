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

import javax.swing.JButton;
import javax.swing.JPanel;

import eu.ggnet.dwoss.customer.ee.assist.gen.CustomerGenerator;
import eu.ggnet.dwoss.customer.ee.entity.Customer;
import eu.ggnet.dwoss.customer.ui.neo.PreferedAddressLabelsController;
import eu.ggnet.saft.core.Ui;
import eu.ggnet.saft.core.UiCore;

/**
 *
 * @author jacob.weinhold
 */
public class PreferedAddressLabelsTryOut {

    public static void main(String[] args) {

        CustomerGenerator GEN = new CustomerGenerator();

        JButton close = new JButton("Schliessen");
        close.addActionListener(e -> Ui.closeWindowOf(close));

        JButton firmenKundenButton = new JButton("Lade FirmenKunde");
        firmenKundenButton.addActionListener(ev -> {

            Customer customer = new Customer();
            customer.getCompanies().add(GEN.makeCompany());
            customer.getCompanies().add(GEN.makeCompany());

            if ( !customer.isValid() ) Ui.build().alert("customer is invalid: " + customer.getViolationMessage());
            Ui.build().fxml().eval(() -> customer, PreferedAddressLabelsController.class).cf().thenAccept(System.out::println).handle(Ui.handler());
        });

        JButton endKundenButton = new JButton("Lade Endkunde");
        endKundenButton.addActionListener(ev -> {
            Customer customer = GEN.makeCustomer();
            customer.getContacts().add(GEN.makeFullContact());
            customer.getContacts().add(GEN.makeFullContact());

            if ( !customer.isValid() ) {
                Ui.exec(() -> {
                    Ui.build().alert("customer is invalid" + customer.getViolationMessage());
                });
                return;
            }
            Ui.build().fxml().eval(() -> customer, PreferedAddressLabelsController.class).cf().thenAccept(System.out::println).handle(Ui.handler());

        });

        JPanel p = new JPanel();
        p.add(firmenKundenButton);
        p.add(endKundenButton);
        p.add(close);

        UiCore.startSwing(() -> p);
    }
}
