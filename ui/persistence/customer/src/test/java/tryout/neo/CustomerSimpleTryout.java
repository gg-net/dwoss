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

import java.util.Locale;

import eu.ggnet.dwoss.customer.ee.entity.Customer;
import eu.ggnet.dwoss.customer.ee.entity.Communication;
import eu.ggnet.dwoss.customer.ee.entity.Contact;
import eu.ggnet.dwoss.customer.ee.entity.MandatorMetadata;

import javax.swing.*;

import eu.ggnet.dwoss.customer.ee.CustomerAgent;
import eu.ggnet.dwoss.customer.ee.assist.gen.CustomerGenerator;
import eu.ggnet.dwoss.customer.ee.entity.*;
import eu.ggnet.dwoss.customer.ee.entity.Communication.Type;
import eu.ggnet.dwoss.customer.ui.neo.CustomerSimpleController;
import eu.ggnet.dwoss.rules.CustomerFlag;
import eu.ggnet.saft.*;

import tryout.stub.CustomerAgentStub;

/**
 *
 * @author jens.papenhagen
 */
public class CustomerSimpleTryout {

    public static void main(String[] args) {
        //stub for the new Costumer modell with generator needed
        Client.addSampleStub(CustomerAgent.class, new CustomerAgentStub());

        CustomerGenerator gen = new CustomerGenerator();
        Customer customer = gen.makeCustomer();
        Company company = gen.makeCompany();
        Contact contact = gen.makeContact();
        Communication communicationEmail = new Communication();
        Address address = gen.makeAddress();

        customer.getCompanies().clear();
        customer.getContacts().clear();

        customer.add(CustomerFlag.ITC_CUSTOMER);
        customer.setKeyAccounter("Herr Meier");
        customer.add(new MandatorMetadata());

        communicationEmail.setType(Type.MOBILE);
        communicationEmail.setIdentifier("040123456789");

        address.setIsoCountry(Locale.GERMANY);

        contact.getAddresses().clear();
        contact.getCommunications().clear();

        contact.add(address);
        contact.add(communicationEmail);

        JButton close = new JButton("Schliessen");
        close.addActionListener(e -> Ui.closeWindowOf(close));

        JButton consumerCustomer = new JButton("Consumer Customer");
        consumerCustomer.addActionListener(ev -> {
            customer.add(CustomerFlag.ITC_CUSTOMER);
            customer.setKeyAccounter("Herr Meier");
            customer.add(new MandatorMetadata());

            communicationEmail.setType(Type.MOBILE);
            communicationEmail.setIdentifier("040123456789");

            address.setIsoCountry(Locale.GERMANY);

            contact.getAddresses().clear();
            contact.getCommunications().clear();

            contact.add(address);
            contact.add(communicationEmail);

            customer.add(contact);

            System.out.println("IS simple: " + customer.getSimpleViolationMessage());
            System.out.println("Consumer Customer: " + customer.isConsumer());

            Ui.exec(() -> {
                Ui.build().parent(consumerCustomer).fxml().eval(() -> customer, CustomerSimpleController.class).ifPresent(System.out::println);
            });
        });

        JButton bussinesCustomer = new JButton("Bussines Customer");
        bussinesCustomer.addActionListener(ev -> {
            Customer bc = gen.makeCustomer();
            bc.getContacts().clear();
            bc.add(CustomerFlag.ITC_CUSTOMER);
            bc.setKeyAccounter("Herr Meier");
            bc.add(new MandatorMetadata());

            communicationEmail.setType(Type.MOBILE);
            communicationEmail.setIdentifier("040123456789");

            address.setIsoCountry(Locale.GERMANY);

            company.getContacts().clear();
            company.getAddresses().clear();
            company.getCommunications().clear();

                contact.getAddresses().clear();
                contact.getCommunications().clear();

                contact.add(address);
                contact.add(communicationEmail);

            company.add(contact);
            company.add(address);
            company.add(communicationEmail);

            bc.add(company);

            System.out.println("IS simple: " + bc.getSimpleViolationMessage());
            System.out.println("Bussines Customer: " + bc.isBussines());

            Ui.exec(() -> {
                Ui.build().parent(consumerCustomer).fxml().eval(() -> bc, CustomerSimpleController.class).ifPresent(System.out::println);
            });
        });

        JPanel p = new JPanel();
        p.add(consumerCustomer);
        p.add(bussinesCustomer);
        p.add(close);

        UiCore.startSwing(() -> p);
    }

}
