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
import eu.ggnet.dwoss.customer.ee.entity.Customer.ExternalSystem;
import eu.ggnet.dwoss.customer.ee.entity.Customer.Source;
import eu.ggnet.dwoss.customer.ee.entity.MandatorMetadata;
import eu.ggnet.dwoss.customer.ui.neo.CustomerEnhanceController;
import eu.ggnet.dwoss.rules.CustomerFlag;
import eu.ggnet.saft.*;

/**
 *
 * @author jens.papenhagen
 */
public class CustomerEnhanceTryout {

    public static void main(String[] args) {

        CustomerGenerator gen = new CustomerGenerator();
        Customer customer = gen.makeCustomer();
        customer.getContacts().clear();

        customer.add(gen.makeCompany());
        customer.add(gen.makeCompany());
        customer.add(gen.makeCompany());
        customer.add(gen.makeCompany());

        customer.setSource(Source.ONEADO);
        customer.setKeyAccounter("Herr Meier");
        customer.add(CustomerFlag.ITC_CUSTOMER);
        customer.add(CustomerFlag.CS_UPDATE_CANDIDATE);
        customer.getAdditionalCustomerIds().put(ExternalSystem.SAGE, "testsage");
        customer.add(new MandatorMetadata());
        if ( customer.getViolationMessage() != null ) {
            UiAlert.show("customer is invalid" + customer.getViolationMessage());

            return;
        }
        System.out.println("customer in tryout" + customer);
        JButton close = new JButton("Schliessen");
        close.addActionListener(e -> Ui.closeWindowOf(close));

        JButton run = new JButton("OpenUi");
        run.addActionListener(ev -> {
            Ui.exec(() -> {
                Ui.build().fxml().eval(() -> customer, CustomerEnhanceController.class).ifPresent(System.out::println);
            });
        });

        JPanel p = new JPanel();
        p.add(run);
        p.add(close);

        UiCore.startSwing(() -> p);

    }
}
