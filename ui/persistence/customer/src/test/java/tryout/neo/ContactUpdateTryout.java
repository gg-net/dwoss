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
import eu.ggnet.dwoss.customer.ee.entity.Contact;
import eu.ggnet.dwoss.customer.ee.entity.Customer;
import eu.ggnet.dwoss.customer.ui.neo.ContactUpdateController;
import eu.ggnet.dwoss.customer.ui.neo.CustomerConnectorFascade;
import eu.ggnet.saft.core.Ui;
import eu.ggnet.saft.core.UiCore;

/**
 *
 * @author jens.papenhagen
 */
public class ContactUpdateTryout {

    public static void main(String[] args) {
        CustomerGenerator gen = new CustomerGenerator();
        Customer customer = gen.makeCustomer();
        customer.getCompanies().clear();

        Contact contact = gen.makeContactWithId(1, 11, 21);
        customer.getContacts().add(contact);
        contact.setTitle("Dr.");
        contact.getAddresses().add(gen.makeAddressWithId(12));
        contact.getCommunications().add(gen.makeCommunicationWithId(22));

        CustomerConnectorFascade.setCustomer(customer);

        JButton close = new JButton("Schliessen");
        close.addActionListener(e -> Ui.closeWindowOf(close));

        JButton editButton = new JButton("edit");
        editButton.addActionListener(ev -> {
            Ui.exec(() -> {
                Ui.build().fxml().eval(() -> contact, ContactUpdateController.class).opt().ifPresent(System.out::println);
            });
        });

        JButton addButton = new JButton("add");
        addButton.addActionListener(ev -> {
            Ui.exec(() -> {
                Ui.build().fxml().eval(() -> new Contact(), ContactUpdateController.class).opt().ifPresent(System.out::println);
            });
        });

        JPanel p = new JPanel();
        p.add(editButton);
        p.add(addButton);
        p.add(close);

        UiCore.startSwing(() -> p);
    }
}
