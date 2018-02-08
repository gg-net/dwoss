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

import java.util.Random;

import javax.swing.JButton;
import javax.swing.JPanel;

import eu.ggnet.dwoss.customer.ee.assist.gen.CustomerGenerator;
import eu.ggnet.dwoss.customer.ee.entity.Company;
import eu.ggnet.dwoss.customer.ui.neo.CompanyUpdateController;
import eu.ggnet.saft.*;

/**
 *
 * @author jens.papenhagen
 */
public class CompanyUpdateTryOut {

    //CustomerComapnyController
    public static void main(String[] args) {
        CustomerGenerator gen = new CustomerGenerator();
        Company company = gen.makeCompany();
        company.setTaxId("Steuernummer");
        gen.makeAddresses(5).forEach(a -> company.getAddresses().add(a));
        gen.makeContacts(6).forEach(c -> company.getContacts().add(c));
        company.getCommunications().add(gen.makeCommunication());
        company.getCommunications().add(gen.makeCommunication());
        company.getCommunications().add(gen.makeCommunication());
        company.getCommunications().add(gen.makeCommunication());
        company.getCommunications().get(new Random().nextInt(company.getCommunications().size() - 1)).setPrefered(true);

        JButton close = new JButton("Schliessen");
        close.addActionListener(e -> Ui.closeWindowOf(close));

        JButton editButton = new JButton("edit");
        editButton.addActionListener(ev -> {
            Ui.exec(() -> {
                Ui.build().fxml().eval(() -> company, CompanyUpdateController.class).ifPresent(System.out::println);
            });
        });
        
        JButton addButton = new JButton("add");
        addButton.addActionListener(ev -> {
            Ui.exec(() -> {
                Ui.build().fxml().eval(() -> new Company(), CompanyUpdateController.class).ifPresent(System.out::println);
            });
        });

        JPanel p = new JPanel();
        p.add(editButton);
        p.add(addButton);
        p.add(close);

        UiCore.startSwing(() -> p);
    }

}
