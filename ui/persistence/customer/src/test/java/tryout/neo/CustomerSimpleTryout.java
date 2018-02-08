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
import java.util.Optional;

import javax.swing.JButton;
import javax.swing.JPanel;

import eu.ggnet.dwoss.customer.ee.CustomerAgent;
import eu.ggnet.dwoss.customer.ee.assist.gen.CustomerGenerator;
import eu.ggnet.dwoss.customer.ee.entity.Communication.Type;
import eu.ggnet.dwoss.customer.ee.entity.*;
import eu.ggnet.dwoss.customer.ui.neo.CustomerEnhanceController;
import eu.ggnet.dwoss.customer.ui.neo.CustomerSimpleController;
import eu.ggnet.dwoss.customer.ui.neo.CustomerSimpleController.CustomerContinue;
import eu.ggnet.saft.*;
import eu.ggnet.saft.api.Reply;

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



        address.setIsoCountry(Locale.GERMANY);

        contact.getAddresses().clear();
        contact.getCommunications().clear();

        contact.getAddresses().add(address);
        contact.getCommunications().add(makeValidCommunication());

        customer.getContacts().add(contact);

        return customer;
    }

    public static Communication makeValidCommunication(){
        Communication communicationMobile = new Communication();
        communicationMobile.setType(Type.MOBILE);
        communicationMobile.setIdentifier("040123456789");

        return communicationMobile;
    }

    public static void main(String[] args) {
        //stub for the new Costumer modell with generator needed
        Dl.remote().add(CustomerAgent.class, new CustomerAgentStub());

        JButton close = new JButton("Schliessen");
        close.addActionListener(e -> Ui.closeWindowOf(close));

        JButton consumerCustomerButton = new JButton("Consumer Customer");
        consumerCustomerButton.addActionListener(ev -> {
            Customer consumerCustomer = makeValidCustomer();

            System.out.println("IS simple: " + consumerCustomer.getSimpleViolationMessage());
            System.out.println("Consumer Customer: " + consumerCustomer.isConsumer());

            Ui.exec(() -> {
                Optional<CustomerContinue> result = Ui.build().parent(consumerCustomerButton).fxml().eval(() -> consumerCustomer, CustomerSimpleController.class);
                if ( !result.isPresent() ) return;
                Reply<Customer> reply = Dl.remote().lookup(CustomerAgent.class).store(result.get().simpleCustomer);
                if ( !Ui.failure().handle(reply) ) return;
                if ( !result.get().continueEnhance ) return;
                Ui.build().fxml().eval(() -> reply.getPayload(), CustomerEnhanceController.class)
                        .ifPresent(c -> Ui.build().alert("Would store + " + c));
            });
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

            Ui.exec(() -> {
                Optional<CustomerContinue> result = Ui.build().parent(consumerCustomerButton).fxml().eval(() -> bussnisCustomer, CustomerSimpleController.class);
                if ( !result.isPresent() ) return;
                Reply<Customer> reply = Dl.remote().lookup(CustomerAgent.class).store(result.get().simpleCustomer);
                if ( !Ui.failure().handle(reply) ) return;
                if ( !result.get().continueEnhance ) return;
                Ui.build().fxml().eval(() -> reply.getPayload(), CustomerEnhanceController.class)
                        .ifPresent(c -> Ui.build().alert("Would store + " + c));
            });
        });

        JPanel p = new JPanel();
        p.add(consumerCustomerButton);
        p.add(bussinesCustomer);
        p.add(close);

        UiCore.startSwing(() -> p);
    }

}
