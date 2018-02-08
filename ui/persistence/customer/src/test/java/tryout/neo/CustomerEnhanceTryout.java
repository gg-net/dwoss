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
import eu.ggnet.dwoss.customer.ee.entity.projection.AddressLabel;
import eu.ggnet.dwoss.customer.ui.neo.CustomerEnhanceController;
import eu.ggnet.dwoss.rules.AddressType;
import eu.ggnet.dwoss.rules.CustomerFlag;
import eu.ggnet.saft.Ui;
import eu.ggnet.saft.UiCore;

/**
 *
 * @author jens.papenhagen
 */
public class CustomerEnhanceTryout {

    public static void main(String[] args) {

        CustomerGenerator gen = new CustomerGenerator();

        JButton close = new JButton("Schliessen");
        close.addActionListener(e -> Ui.closeWindowOf(close));

        JButton firmenKundenButton = new JButton("Lade FirmenKunde");

        firmenKundenButton.addActionListener(ev -> {
            Customer customer = gen.makeCustomer();
            customer.getContacts().clear();

            customer.getCompanies().add(gen.makeCompany());
            customer.getCompanies().add(gen.makeCompany());
            customer.getCompanies().add(gen.makeCompany());
            customer.getCompanies().add(gen.makeCompany());

            customer.setSource(Source.ONEADO);
            customer.setKeyAccounter("Herr Meier");
            customer.getFlags().add(CustomerFlag.ITC_CUSTOMER);
            customer.getFlags().add(CustomerFlag.CS_UPDATE_CANDIDATE);
            customer.getAdditionalCustomerIds().put(ExternalSystem.SAGE, "testsage");

            if ( customer.getViolationMessage() != null ) {
                Ui.build().alert("customer is invalid" + customer.getViolationMessage());

                return;
            }

            customer.getAddressLabels().add(new AddressLabel(gen.makeCompany(), null, gen.makeAddress(), AddressType.SHIPPING));
            Ui.exec(() -> {
                Ui.build().fxml().eval(() -> customer, CustomerEnhanceController.class).ifPresent(System.out::println);
            });
        });

        JButton endKundenButton = new JButton("Lade Endkunde");

        endKundenButton.addActionListener(ev -> {
            Customer customer = gen.makeCustomer();
            customer.getCompanies().clear();

            customer.getContacts().add(gen.makeContact());
            customer.getContacts().add(gen.makeContact());
            customer.getContacts().add(gen.makeContact());
            customer.getContacts().add(gen.makeContact());

            customer.setSource(Source.ONEADO);
            customer.getFlags().add(CustomerFlag.ITC_CUSTOMER);
            customer.getFlags().add(CustomerFlag.CS_UPDATE_CANDIDATE);
            customer.getAdditionalCustomerIds().put(ExternalSystem.SAGE, "testsage");

            if ( customer.getViolationMessage() != null ) {
                Ui.build().alert("customer is invalid" + customer.getViolationMessage());

                return;
            }
            Ui.exec(() -> {
                Ui.build().fxml().eval(() -> customer, CustomerEnhanceController.class).ifPresent(System.out::println);
            });
        });

        JPanel p = new JPanel();
        p.add(endKundenButton);
        p.add(firmenKundenButton);

        p.add(close);

        UiCore.startSwing(() -> p);

    }
}
