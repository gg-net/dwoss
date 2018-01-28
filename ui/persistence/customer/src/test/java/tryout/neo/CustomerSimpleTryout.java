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

import eu.ggnet.dwoss.customer.ee.entity.Customer;
import eu.ggnet.dwoss.customer.ee.entity.Communication;
import eu.ggnet.dwoss.customer.ee.entity.Contact;
import eu.ggnet.dwoss.customer.ee.entity.MandatorMetadata;

import javax.swing.*;

import eu.ggnet.dwoss.customer.ee.assist.gen.CustomerGenerator;
import eu.ggnet.dwoss.customer.ee.entity.Communication.Type;
import eu.ggnet.dwoss.customer.ui.neo.CustomerSimpleController;
import eu.ggnet.dwoss.rules.CustomerFlag;
import eu.ggnet.saft.*;

/**
 *
 * @author jens.papenhagen
 */
public class CustomerSimpleTryout {

    public static void main(String[] args) {
        CustomerGenerator gen = new CustomerGenerator();
        Customer customer = gen.makeCustomer();
        Contact contact = gen.makeContact();
        

        customer.add(CustomerFlag.ITC_CUSTOMER);
        customer.setKeyAccounter("Herr Meier");
        customer.add(new MandatorMetadata());

        customer.getCompanies().clear();        
        customer.getContacts().clear();   
        
        Communication communicationEmail = new Communication();
        communicationEmail.setType(Type.EMAIL);
        
        contact.getAddresses().clear();
        contact.getCommunications().clear();
        contact.add(gen.makeAddress());
        contact.add(communicationEmail);
        
        
        
        customer.add(contact);        
        
        
        System.out.println("IS simple: " + customer.getSimpleViolationMessage());
        

        JButton close = new JButton("Schliessen");
        close.addActionListener(e -> Ui.closeWindowOf(close));

        JButton run = new JButton("OpenUi");

        run.addActionListener(ev -> {
            Ui.exec(() -> {
                Ui.build().fxml().eval(() -> customer, CustomerSimpleController.class).ifPresent(System.out::println);
            });
        });

        JPanel p = new JPanel();
        p.add(run);
        p.add(close);

        UiCore.startSwing(() -> p);
    }

}
