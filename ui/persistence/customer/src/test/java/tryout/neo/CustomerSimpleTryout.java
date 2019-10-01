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


import eu.ggnet.saft.core.Ui;
import eu.ggnet.saft.core.UiCore;
import eu.ggnet.saft.core.Dl;

import javax.swing.JButton;
import javax.swing.JPanel;

import eu.ggnet.dwoss.customer.ee.CustomerAgent;
import eu.ggnet.dwoss.customer.ee.assist.gen.CustomerGenerator;
import eu.ggnet.dwoss.customer.ee.entity.Communication.Type;
import eu.ggnet.dwoss.customer.ee.entity.*;
import eu.ggnet.dwoss.customer.ui.neo.*;
import eu.ggnet.saft.core.ui.UiParent;

import tryout.stub.CustomerAgentStub;

/**
 *
 * @author jens.papenhagen
 */
public class CustomerSimpleTryout {

    private static CustomerGenerator gen = new CustomerGenerator();

    public static Customer makeValidCustomer() {
        Customer customer = gen.makeCustomer();
        Contact contact = gen.makeContact();

        Address address = gen.makeAddress();

        customer.getCompanies().clear();
        customer.getContacts().clear();
        customer.getFlags().clear();
        customer.getMandatorMetadata().clear();

        address.setCountry(Country.GERMANY);

        contact.getAddresses().clear();
        contact.getCommunications().clear();

        contact.getAddresses().add(address);
        contact.getCommunications().add(new Communication(Type.MOBILE, "+49 172 123456789"));

        customer.getContacts().add(contact);

        return customer;
    }

    public static void main(String[] args) {
        //stub for the new Costumer modell with generator needed
        Dl.remote().add(CustomerAgent.class, new CustomerAgentStub());

        JPanel p = new JPanel();

        JButton close = new JButton("Schliessen");
        close.addActionListener(e -> Ui.closeWindowOf(close));

        JButton consumerCustomerButton = new JButton("Consumer Customer");
        consumerCustomerButton.addActionListener(ev -> {
            Customer consumerCustomer = makeValidCustomer();

            System.out.println("IS simple: " + consumerCustomer.getSimpleViolationMessage());
            System.out.println("Consumer Customer: " + consumerCustomer.isConsumer());

            CustomerConnectorFascade.edit(consumerCustomer, UiParent.of(p),null);
        });

        JButton bussinesCustomer = new JButton("Bussines Customer");
        bussinesCustomer.addActionListener(ev -> {
            Customer bussnisCustomer = makeValidCustomer();
            Contact tempcon = bussnisCustomer.getContacts().get(0);

            Company company = gen.makeCompany();
            company.getContacts().clear();
            company.getContacts().add(tempcon);

            company.getCommunications().clear();
            bussnisCustomer.getContacts().clear();

            bussnisCustomer.getCompanies().add(company);

            System.out.println("IS simple: " + bussnisCustomer.getSimpleViolationMessage());
            System.out.println("Bussines Customer: " + bussnisCustomer.isBusiness());
            CustomerConnectorFascade.edit(bussnisCustomer, UiParent.of(p),null);
        });

        JButton nullCustomer = new JButton("Create SimpleCustomer");
        nullCustomer.addActionListener(ev -> {
            CustomerConnectorFascade.selectOrEdit(UiParent.of(p),l -> System.out.println("Stored Id: " + l));
        });

        p.add(consumerCustomerButton);
        p.add(bussinesCustomer);
        p.add(nullCustomer);
        p.add(close);

        UiCore.startSwing(() -> p);
    }



}
