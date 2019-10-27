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

import eu.ggnet.dwoss.common.api.values.AddressType;
import eu.ggnet.dwoss.common.api.values.CustomerFlag;
import eu.ggnet.dwoss.customer.ee.CustomerAgent;
import eu.ggnet.dwoss.customer.ee.assist.gen.CustomerGenerator;
import eu.ggnet.dwoss.customer.ee.entity.AddressLabel;
import eu.ggnet.dwoss.customer.ee.entity.Customer;
import eu.ggnet.dwoss.customer.ee.entity.Customer.ExternalSystem;
import eu.ggnet.dwoss.customer.ee.entity.Customer.Source;
import eu.ggnet.dwoss.customer.ui.neo.CustomerEnhanceController;
import eu.ggnet.dwoss.mandator.ee.Mandators;
import eu.ggnet.saft.core.*;
import eu.ggnet.saft.core.dl.RemoteLookup;
import eu.ggnet.saft.experimental.auth.Guardian;

import tryout.stub.*;

/**
 *
 * @author jens.papenhagen
 */
public class CustomerEnhanceTryout {

    public static void main(String[] args) {

        Dl.local().add(RemoteLookup.class, new RemoteLookup() {
            @Override
            public <T> boolean contains(Class<T> clazz) {
                return false;
            }

            @Override
            public <T> T lookup(Class<T> clazz) {
                return null;
            }
        });

        Dl.remote().add(Mandators.class, new MandatorsStub());
        Dl.local().add(Guardian.class, new GuardianStub());

        JButton close = new JButton("Schliessen");
        close.addActionListener(e -> Ui.closeWindowOf(close));

        JButton firmenKundenButton = new JButton("Lade FirmenKunde");

        firmenKundenButton.addActionListener(ev -> {
            Customer customer = CustomerGenerator.makeCustomer();
            customer.getContacts().clear();

            customer.getCompanies().add(CustomerGenerator.makeCompanyWithId(220l));
            customer.getCompanies().add(CustomerGenerator.makeCompanyWithId(221l));
            customer.getCompanies().add(CustomerGenerator.makeCompanyWithId(222l));
            customer.getCompanies().add(CustomerGenerator.makeCompanyWithId(223l));

            customer.setSource(Source.ONEADO);
            customer.setKeyAccounter("Herr Meier");
            customer.getFlags().add(CustomerFlag.ITC_CUSTOMER);
            customer.getFlags().add(CustomerFlag.CS_UPDATE_CANDIDATE);
            customer.getAdditionalCustomerIds().put(ExternalSystem.SAGE, "testsage");

            if ( customer.getViolationMessage() != null ) {
                Ui.exec(() -> {
                    Ui.build().alert("customer is invalid" + customer.getViolationMessage());
                });

                return;
            }

            customer.getAddressLabels().add(new AddressLabel(CustomerGenerator.makeCompany(), null, CustomerGenerator.makeAddress(), AddressType.SHIPPING));

            Dl.remote().add(CustomerAgent.class, new CustomerAgentStub(customer));
            Ui.exec(() -> {
                Ui.build().fxml().eval(() -> customer, CustomerEnhanceController.class).opt().ifPresent(System.out::println);
            });
        });

        JButton endKundenButton = new JButton("Lade Endkunde");

        endKundenButton.addActionListener(ev -> {
            Customer customer = CustomerGenerator.makeCustomer();
            customer.getContacts().clear();
            customer.getCompanies().clear();

            customer.getContacts().add(CustomerGenerator.makeContactWithId(1, 11, 21));
            customer.getContacts().add(CustomerGenerator.makeContactWithId(2, 12, 22));
            customer.getContacts().add(CustomerGenerator.makeContactWithId(3, 13, 23));
            customer.getContacts().add(CustomerGenerator.makeContactWithId(4, 14, 24));

            customer.setSource(Source.ONEADO);
            customer.getFlags().add(CustomerFlag.ITC_CUSTOMER);
            customer.getFlags().add(CustomerFlag.CS_UPDATE_CANDIDATE);
            customer.getAdditionalCustomerIds().put(ExternalSystem.SAGE, "testsage");

            if ( customer.getViolationMessage() != null ) {
                Ui.exec(() -> {
                    Ui.build().alert("customer is invalid" + customer.getViolationMessage());
                });

                return;
            }
            Dl.remote().add(CustomerAgent.class, new CustomerAgentStub(customer));
            Ui.exec(() -> {
                Ui.build().fxml().eval(() -> customer, CustomerEnhanceController.class).opt().ifPresent(System.out::println);
            });
        });

        JPanel p = new JPanel();
        p.add(endKundenButton);
        p.add(firmenKundenButton);

        p.add(close);

        UiCore.startSwing(() -> p);

    }
}
