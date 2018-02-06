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
import eu.ggnet.dwoss.customer.ui.neo.ContactUpdateController;
import eu.ggnet.saft.*;


/**
 *
 * @author jens.papenhagen
 */
public class ContactUpdateTryout {

    public static void main(String[] args) {
        CustomerGenerator gen = new CustomerGenerator();
        Contact contact = gen.makeContact();
        contact.setTitle("Dr.");
        contact.getAddresses().add(gen.makeAddress());
        contact.getAddresses().add(gen.makeAddress());
        contact.getAddresses().add(gen.makeAddress());
        
        contact.getCommunications().add(gen.makeCommunication());
        contact.getCommunications().add(gen.makeCommunication());
        contact.getCommunications().add(gen.makeCommunication());
        
        JButton close = new JButton("Schliessen");
        close.addActionListener(e -> Ui.closeWindowOf(close));

        JButton run = new JButton("OpenUi");
        run.addActionListener(ev -> {
            Ui.exec(() -> {
                Ui.build().fxml().eval(() -> contact, ContactUpdateController.class).ifPresent(System.out::println);;
            });
        });

        JPanel p = new JPanel();
        p.add(run);
        p.add(close);

        UiCore.startSwing(() -> p);
    }
}
